import java.util.Arrays;
import java.util.Random;

public class Commands {

    public static String getCommands(String message, String command) {

        message = message.trim();
        switch (command) {
            case "/start":
                break;
            case "/removeSpaces":
                return message.replaceAll(" ", "");


            case "/ok":

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
