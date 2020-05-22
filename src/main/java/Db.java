import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {

    static final String DB_URL = "jdbc:postgresql://ec2-54-247-103-43.eu-west-1.compute.amazonaws.com:5432/d9g3ga3d10tldu";
    static final String USER = "qrbolgzpyhhdit";
    static final String PASS = "860e503455c28abbef5a7f12617bb16ae201f96e3b81ed4c0846e57700087cae";

    public static Connection connecti;
    static {
        try {
            connecti = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}
