import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Random;

public class Commands {
    static final String DB_URL = "jdbc:postgresql://ec2-54-247-89-181.eu-west-1.compute.amazonaws.com:5432/d4k73hbn3que92";
    static final String USER = "akuaihbrfdperl";
    static final String PASS = "e240eb73da4d572576a41ee28fe9dab1ace5ec37bb29532e3489618f84607bd0";
    public static String getCommands(String message, String command) throws SQLException {

        message = message.trim();
        switch (command) {
            case "/start":
                break;
            case "/removeSpaces":
                return message.replaceAll(" ", "");


            case "/ok":
            {  Connection c;
                Statement stmt;
                c = DriverManager
                        .getConnection(DB_URL, USER, PASS);
                c.setAutoCommit(false);
                stmt = c.createStatement();
                ResultSetHandler<Games> resultHandler = new BeanHandler<Games>(Games.class);
                QueryRunner queryRunner = new QueryRunner();
                Games emp = new Games();
                try {
                     emp = queryRunner.query(c, "SELECT * FROM Games",
                            resultHandler );
                    //Display values
                    System.out.print(emp.toString());

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                return " "+ emp.toString() ;
            }
            case "/coin":
                return "И выпадает: " + (new Random().nextInt(2) == 0 ? "Орёл" : "Решка");
            case "/random":
                int max = Arrays.stream(message.split(" ")).mapToInt(Integer::parseInt).max().getAsInt(),
                        min = Arrays.stream(message.split(" ")).mapToInt(Integer::parseInt).min().getAsInt();
                int rand = new Random().nextInt(max - min + 1) + min;
                return "Случайный результат между " + min + " и " + max + ": " + rand;
            case "/magicBall":

            case "/help":
                return String.join("\n", TextCommands.help);


        }
        return "";
    }


}
