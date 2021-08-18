/* Luis Ferrufino
 * G#00997076
 * 11/16/19
 * CS 450-002
 * HW#6
 */
import java.sql.*;  //Import the java SQL library
import java.text.SimpleDateFormat;
import java.util.Date;

class Program {    //Create a new class to encapsulate the program

 
  public static void SQLError (Exception e) {  //Our function for handling SQL errors
  
	System.out.println("ORACLE error detected:");
	e.printStackTrace();	
  }

  public static void main (String args[]) {  //The main function


    try {                                        //Keep an eye open for errors
       
      String driverName = "oracle.jdbc.driver.OracleDriver";
      //String driverName = "com.mysql.jdbc.Driver";
      Class.forName(driverName);
    
      System.out.println("Connecting to Oracle...");  
    
      String url = "jdbc:oracle:thin:@artemis.vsnet.gmu.edu:1521/vse18c.vsnet.gmu.edu";
      Connection conn = DriverManager.getConnection(url,"lferrufi","ooftahyx");

      System.out.println("Connected!");
       
      Statement stmt = conn.createStatement();   //Create a new statement
       
      //Now we execute our query and store the results in the myresults object:
      ResultSet myresults = stmt.executeQuery("select * from Student S");
      
      while ( myresults.next() ) {
      
        int sUno = myresults.getInt("Uno");
        String sname = myresults.getString("Sname");
        String major = myresults.getString("Major");
        String status = myresults.getString("Status");
        Date today = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d yyyy");
        System.out.println("Date mailed:                   " + simpleDateFormat.format(today));
        System.out.println("Semester:                      " + "Fall 2019");
        System.out.println("Student name:                  " + sname);
        System.out.println("Student identification number: " + sUno); 
        System.out.println("Student major:                 " + major);
        System.out.println("Student status:                " + status);
        System.out.println("                               ");
        Statement stmt2 = conn.createStatement();   //Create a new statement
        ResultSet myresults2 = stmt2.executeQuery("select * from Enroll E where E.Uno = " + sUno);
       
        while ( myresults2.next() ) {

          String dcode = myresults2.getString("Dcode");
          int cno = myresults2.getInt("Cno");
          int sno = myresults2.getInt("Sno"); 
          Statement stmt3 = conn.createStatement();
          ResultSet myresults3 = stmt3.executeQuery("select C.Dcode, C.Cno, C.Title, C.Credits, "
                                                 + "E.Sno, F.Fname from Enroll E, Course C, " 
                                                 + "Faculty F, Section S where C.Dcode = S.Dcode "
                                                 + "and C.Cno = S.Cno and S.Instructor = F.Uno "
                                                 + "and E.Dcode = S.Dcode and E.Cno = S.Cno and "
                                                 + "E.Sno = S.Sno and E.Uno = " + sUno + " and "
                                                 + "E.Dcode = \'" + dcode + "\' and E.Cno = " 
                                                 + cno + " and E.Sno = " + sno);
          Statement stmt4 = conn.createStatement();
          ResultSet myresults4 = stmt4.executeQuery("select M.Bldg, M.Room, M.Day, M.Mbegin, "
                                                  + "M.Mend from Enroll E, Meeting M where "
                                                  + "E.Dcode = M.Dcode and E.Cno = M.Cno and "
                                                  + "E.Sno = M.Sno and E.Dcode = \'" + dcode 
                                                  + "\' and E.Cno = " + cno + " and E.Sno = "
                                                  + sno + " and E.Uno = " + sUno);
         
          while ( myresults3.next() ) {

            System.out.println("        Course identification--Course title--------------"
                             + "Number of credits--Section number--Instructor name--Meetings");
            System.out.format("        %-21s--%-24s--%-17d--%-14d--%-15s--", dcode + " " + cno, myresults3.getString("Title"),
                             myresults3.getInt("Credits"), myresults3.getInt("Sno"), myresults3.getString("Fname"));
            String temp = "";

            while ( myresults4.next() ) {
 
              temp += myresults4.getString("Bldg") + " " + myresults4.getInt("Room") + " on "
                                  + myresults4.getString("Day") + " from " + myresults4.getInt("Mbegin")
                                  + " to " + myresults4.getInt("Mend") + ", ";
            }
            System.out.print(temp.substring(0, temp.length() - 2) + "\n\n");
          }
          myresults3.close();
          stmt3.close();
          myresults4.close();
          stmt4.close();
        }
        myresults2.close();
        stmt2.close();
        Statement stmt5 = conn.createStatement();
        ResultSet myresults5 = stmt5.executeQuery("select sum(C.Credits) from Enroll E, Course C "
                                                    + "where E.Dcode = C.Dcode and E.Cno = C.Cno and "
                                                    + "E.Uno = " + sUno);
        myresults5.next();
        int credits = myresults5.getInt("sum(C.Credits)");
        myresults5.close(); 
        stmt5.close();
        System.out.println("        Total number of Credits: " + credits);
        System.out.print("        Amount due             : ");

        if ( status.equals("undergraduate") ) {

          if ( credits <= 11 ) System.out.println("$" + ( credits * 523 ) + ".00");
          else System.out.println("$6,282.00");
        } else {

          if ( credits <= 8 ) System.out.println("$" + ( credits * 762 ) + ".00");
          else System.out.println("$6,858.00");
        }
        if ( credits == 0 ) System.out.println("        You aren't enrolled this semester.");
        System.out.println("------------------------------------------------------------------"
                         + "------------------------------------------------------------------");
      }
      conn.close();  // Close our connection.

    } catch (Exception e) {
	  
	   SQLError(e);
    } //if any error occurred in the try..catch block, call the SQLError function
  }
}  
