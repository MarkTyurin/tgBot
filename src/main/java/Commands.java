import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
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



            case "/ok": {
                Connection c;

                c = DriverManager
                        .getConnection(DB_URL, USER, PASS);
                c.setAutoCommit(false);

                QueryRunner run = new QueryRunner();

// Use the BeanListHandler implementation to convert all
// ResultSet rows into a List of Person JavaBeans.
                ResultSetHandler<List<Games>> h = new BeanListHandler<Games>(Games.class);
                String str="";
// Execute the SQL statement and return the results in a List of
// Person objects generated by the BeanListHandler.
                List<Games> games = run.query(c, "SELECT * FROM Games", h);
                for (Games game : games) {
                  str+=  game.toString() ;
                }
                c.close();
                return " "+ str+"//";
            }
            case "/coin":
                return "И выпадает: " + (new Random().nextInt(2) == 0 ? "Орёл" : "Решка");
            case "/random":
                int max = Arrays.stream(message.split(" ")).mapToInt(Integer::parseInt).max().getAsInt(),
                        min = Arrays.stream(message.split(" ")).mapToInt(Integer::parseInt).min().getAsInt();
                int rand = new Random().nextInt(max - min + 1) + min;
                return "Случайный результат между " + min + " и " + max + ": " + rand;
            case "/find":
            {
                Connection c;
                c = DriverManager
                        .getConnection(DB_URL, USER, PASS);
                c.setAutoCommit(false);
                QueryRunner run = new QueryRunner();

                ResultSetHandler<List<Games>> h = new BeanListHandler<Games>(Games.class);
                String str="";
                String name = message;
                List<Games> games = run.query(c, "SELECT * FROM Games where name like %"+name+"%", h);
                for (Games game : games) {
                    str+=  game.toString() ;
                }
                c.close();
                return " "+ str+"//";



            }
            case "/help":
                return String.join("\n", TextCommands.help);


        }

        return "";
    }


}
