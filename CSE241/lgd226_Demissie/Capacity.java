import java.sql.*;
import java.util.*;

public class Capacity{
    public static String getUserLogin(Scanner scan, String field){
        System.out.print("Enter your " + field + " : ");
        return scan.nextLine();
    }

    public static int validateYear(Scanner scan, Connection conn){
        int year = 0;
        try{
            System.out.print("Year(yyyy)or 0 to exit: ");
            year = scan.nextInt();
            if(year == 0){
                conn.close();
                System.exit(0);
            }
            if(year <= 999 || year >= 10000){
                System.out.println("Please input an integer."); 
                year = -1;
                return year;
            }
            PreparedStatement pStmt = conn.prepareStatement("select year from takes where year = ?");
            pStmt.setInt(1, year);
            int rs = pStmt.executeUpdate();
            if(rs == 0){
                System.out.println("YEAR not in database.");
                year = -1;
            }
        }catch(InputMismatchException e){
            System.out.println("Please input an integer.");
        }catch(SQLException e){
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return year;
    }

    public static int validateCourse_Id(Scanner scan, Connection conn){
        int course_id = 0;
        try{
            System.out.print("Input course ID as 3 digit integer: ");
            course_id = scan.nextInt();
            PreparedStatement pStmt = conn.prepareStatement("select course_id from takes where course_id = ?");
            pStmt.setInt(1, course_id);
            int rs = pStmt.executeUpdate();
            if(rs == 0){
                System.out.println("COURSE_ID not in database.");
                course_id = -1;
            }
        }catch(InputMismatchException e){
            System.out.println("Please input an integer.");
        }catch(SQLException e){
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return course_id;
    }

    public static int validateSection_Id(Scanner scan, Connection conn){
        int sec_id = 0;
        try{
            System.out.print("Input section ID as integer: ");
            sec_id = scan.nextInt();
            PreparedStatement pStmt = conn.prepareStatement("select sec_id from takes where sec_id = ?");
            pStmt.setInt(1, sec_id);
            int rs = pStmt.executeUpdate();
            if(rs == 0){
                System.out.println("SECTION_ID not in database.");
                sec_id = -1;
            }
        }catch(InputMismatchException e){
            System.out.println("Please input an integer.");
            sec_id = -1;
        }catch(SQLException e){
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return sec_id;
    }

    public static String validateSemester(Scanner scan, Connection conn){
        String semester = null;
        scan.nextLine();
        try{
            System.out.print("Semester(string): ");
            semester = scan.next().toUpperCase();
            String rightOne = null;
            switch(semester){
                case "FALL":
                    rightOne = "Fall";
                    break;
                case "SPRING":
                    rightOne = "Spring";
                    break;
                case "WINTER":
                    rightOne = "Winter";
                    break;
                case "SUMMER":
                    rightOne = "Summer";
                    break;
                default:
                    System.out.println("Please enter \"fall\", \"spring\", \"winter\", or \"summer\"."); 
                    semester = null;
                    return semester;
            }
        }catch(InputMismatchException e){
            System.out.println("Please input an integer.");
        }
        return semester;
    }

    public static void main (String [] args){
        Scanner scan = new Scanner(System.in);
        String dbURL = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";
        String userName = "";
        String password = "";

        Connection conn = null;    
        boolean quit = false;       

        try{
            userName = getUserLogin(scan, "user name");
            password = getUserLogin(scan, "password");

            conn = DriverManager.getConnection(dbURL, userName, password);

            while(!quit){
                int year = validateYear(scan, conn);
                if(year == -1)
                    continue;
                
                String semester = validateSemester(scan, conn);
                if(semester == null)
                    continue;

                int course_id = validateCourse_Id(scan, conn);
                if(course_id == -1)
                    continue;

                int section_id = validateSection_Id(scan, conn);
                if(section_id == -1)
                    continue;

                scan.nextLine();

                PreparedStatement pStmt = conn.prepareStatement("select capacity from classroom inner join section on section.building = classroom.building and section.room_number = classroom.room_number where year = ? and course_id = ? and sec_id = ?");
                pStmt.setInt(1, year);
                pStmt.setInt(2, course_id);
                pStmt.setInt(3, section_id);
                ResultSet rs = pStmt.executeQuery();
                

                
                PreparedStatement pStmt2 = conn.prepareStatement("select count(*) from takes where year = ? and course_id = ? and sec_id = ?");  
                pStmt2.setInt(1, year);
                pStmt2.setInt(2, course_id);
                pStmt2.setInt(3, section_id);
                ResultSet rs2 = pStmt2.executeQuery();

                rs.next();
                rs2.next();
                
                int capacity = rs.getInt(1);
                int registered = rs2.getInt(1);
                int difference = capacity - registered;

                if(difference >= 0){
                    System.out.println("There are " + difference + " seats open.");
                }else{
                    System.out.println("There are " + difference + " overenrolled seats.");
                }

            }

            conn.close();
        }catch (SQLException e){
            System.out.println("SQL Exception: " + e.getMessage());
        }

        finally{
            try{
                if (conn != null || !conn.isClosed()){
                    conn.close();
                }
            }catch (SQLException e){
                System.out.println("SQL Exception: " + e.getMessage());
            }
        }      
    }
}

//java -cp .:ojdbc11.jar Capacity