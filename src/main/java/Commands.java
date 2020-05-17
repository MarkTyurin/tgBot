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


    public static String getCommands(String message, String command) throws SQLException {

        message = message.trim();
        switch (command) {
            case "/start":
                break;
            case "/removeSpaces":
                return message.replaceAll(" ", "");


            case "/hi": {

                return " __Вас__ **приветствует** бот-помошник. ";
            }
            case "/coin":
                return "И выпадает: " + (new Random().nextInt(2) == 0 ? "Орёл" : "Решка");
            case "/random":
                int max = Arrays.stream(message.split(" ")).mapToInt(Integer::parseInt).max().getAsInt(),
                        min = Arrays.stream(message.split(" ")).mapToInt(Integer::parseInt).min().getAsInt();
                int rand = new Random().nextInt(max - min + 1) + min;
                return "Случайный результат между " + min + " и " + max + ": " + rand;
            case "/find": {

                QueryRunner run = new QueryRunner();

                ResultSetHandler<List<Games>> h = new BeanListHandler<Games>(Games.class);
                String str = "";
                String name = message;
                List<Games> games = run.query(Db.connecti, "SELECT * FROM Games where name like %" + name + "%", h);
                for (Games game : games) {
                    str += game.toString();
                }

                return " " + str + "//";


            }
            case "/help":
                return String.join("\n", TextCommands.help);


        }

        return "";
    }


}
