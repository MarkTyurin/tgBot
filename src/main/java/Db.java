import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {

    static final String DB_URL = "jdbc:postgresql://ec2-54-247-89-181.eu-west-1.compute.amazonaws.com:5432/d4k73hbn3que92";
    static final String USER = "akuaihbrfdperl";
    static final String PASS = "e240eb73da4d572576a41ee28fe9dab1ace5ec37bb29532e3489618f84607bd0";

    public static Connection connecti;
    static {
        try {
            connecti = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}
