import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.StrictMath.toIntExact;

public class Bot extends TelegramLongPollingBot {
    public String user_username;
    public long u_id;

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

    }


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) //для получения обновлений через лонг пул
    //лонг пул - очередь запросов.
    {

        Message message1 = update.getMessage();


        if (message1 != null && message1.hasText()) {
            String strMessage = message1.getText();
            strMessage = Commands.getCommands(getMessage(strMessage), getCommand(strMessage, getMessage(strMessage)));
            if (!strMessage.equals("")) {
                sendMsg(message1, strMessage);
            }


            // sendMsg(message1,   "user_id= "+u_id + "user_n="+user_username+" user_n2="+user_first_name);

            QueryRunner run = new QueryRunner();

            ResultSetHandler<List<Games>> h = new BeanListHandler<Games>(Games.class);

            String name = message1.getText();
            List<Games> games = run.query(Db.connecti, "SELECT * FROM Games where name like '%" + name + "%'", h);
            for (Games game : games) {
                sendMsg(message1, game.toString());
            }
        }


        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            if (update.getMessage().getText().equals("/start")) {
                SendMessage message = new SendMessage() // Create a message object object
                        .setChatId(chat_id)
                        .setText("Что нужно сделать?");
                user_username = update.getMessage().getChat().getUserName();
                u_id = update.getMessage().getChat().getId();
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
                List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
                List<InlineKeyboardButton> rowInline4 = new ArrayList<>();

                rowInline.add(new InlineKeyboardButton().setText("Поиск по жанру").setCallbackData("/genre"));
                rowInline2.add(new InlineKeyboardButton().setText("Поиск по вселенной").setCallbackData("/universe"));
                rowInline3.add(new InlineKeyboardButton().setText("Внести аккаунт в базу данных").setCallbackData("/user"));
                rowInline4.add(new InlineKeyboardButton().setText("Вывести список моих игр").setCallbackData("/user_games"));
                // Set the keyboard to the markup
                rowsInline.add(rowInline);
                rowsInline.add(rowInline2);
                rowsInline.add(rowInline3);
                rowsInline.add(rowInline4);

                // Add it to the message
                markupInline.setKeyboard(rowsInline);
                message.setReplyMarkup(markupInline);
                try {
                    execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {

            }

        } else if (update.hasCallbackQuery()) {
            // Set variables
            String call_data = update.getCallbackQuery().getData();
            String[] data;
            data = call_data.split(",");
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            ///ИСКАТЬ ВАРИАНТ ПОЛУЧЕНИЯ АЙДИ И ИМЕНИ ЧЕРЕЗ АПИ
            //   long user_id =  message1.getFrom().getId();
            //  String user_name =  message1.getFrom().getFirstName();
            //  String user_name2 =  message1.getFrom().getUserName();

            // if (call_data.equals("update_msg_text")) {
            switch (data[0]) {
                case "/start": {
                    String answer = "Updated message text";
                    EditMessageText new_message = new EditMessageText()
                            .setChatId(chat_id)
                            .enableMarkdown(true)
                            .setMessageId(toIntExact(message_id))
                            .setText(answer);
                    try {
                        execute(new_message);
                        break;
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                case "/add": {
                    String id_game = data[1];
                    Statement statement = null;
                    String sql;
                    SendMessage newM2 = new SendMessage()
                            .enableMarkdown(true)
                            .setChatId(chat_id);
                    int i = 454;
                    sql = "INSERT INTO user_games (id_user, id_game) VALUES (" + u_id + ",'" + id_game + "')";
                    try {
                        statement = Db.connecti.createStatement();
                        statement.executeUpdate(sql);
                        i=0;
                        newM2.setText("Игра добавлена.");
                        execute(newM2);

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();

                    }
                    if (i != 0) {
                        newM2.setText("Не удалось добавить игру.");
                        execute(newM2);
                    }
                    break;

                }

                case "/user": {
                    SendMessage newM2 = new SendMessage()
                            .enableMarkdown(true)
                            .setChatId(chat_id);
                    Statement statement = null;
                    String sql;
                    int i = 43;
                    sql = "INSERT INTO users (id, tg_id,nickname) VALUES  (" + u_id + "," + u_id + ", '" + user_username + "')";
                    try {
                        statement = Db.connecti.createStatement();
                        statement.executeUpdate(sql);
                        i = 0;
                        newM2.setText("Аккаунт добавлен.");
                        execute(newM2);

                    } catch (SQLException throwables) {

                        throwables.printStackTrace();
                    }
                    if (i != 0) {
                        newM2.setText("Не удалось добавить аккаунт.");
                        execute(newM2);
                    }

                    break;
                }
                case "/ok": {
                    QueryRunner run = new QueryRunner();
                    Message message = update.getCallbackQuery().getMessage();
                    ResultSetHandler<List<Games>> h = new BeanListHandler<Games>(Games.class);
                    List<Games> games = run.query(Db.connecti, "SELECT * FROM Games", h);
                    for (Games game : games) {
                        sendMsg(message, game.toString());
                    }
                    break;
                }
                case "/genre": {
                    QueryRunner run = new QueryRunner();
                    ResultSetHandler<List<Genre>> h = new BeanListHandler<Genre>(Genre.class);
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    List<Genre> genres = run.query(Db.connecti, "SELECT * FROM Genre", h);
                    for (Genre genre : genres) {
                        List<InlineKeyboardButton> rowInline = new ArrayList<>();
                        rowInline.add(new InlineKeyboardButton().setText(genre.getName()).setCallbackData("/find_genre," + genre.getId()));
                        rowsInline.add(rowInline);

                    }
                    EditMessageText new_message = new EditMessageText()
                            .setChatId(chat_id)
                            .setMessageId(toIntExact(message_id))
                            .setText("Выбор жанра")
                            .enableMarkdown(true)
                            .setReplyMarkup(markupInline);
                    try {
                        execute(new_message);
                        break;
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }


                case "/find_genre": {
                    String id_genre = data[1];
                    QueryRunner run = new QueryRunner();
                    ResultSetHandler<List<Games>> h = new BeanListHandler<Games>(Games.class);
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    int i = 0, j = 0, k = 3;
                    EditMessageText new_message = new EditMessageText()
                            .setChatId(chat_id)
                            .setMessageId(toIntExact(message_id))
                            .setText("Нажмите на заинтересовашую игру")
                            .enableMarkdown(true)
                            .setReplyMarkup(markupInline);

                    List<Games> games = run.query(Db.connecti, "SELECT * FROM Games where id_genre =" + id_genre, h);
                    for (Games game : games) {
                        if (i >= j && i < k) {
                            List<InlineKeyboardButton> rowInline = new ArrayList<>();
                            rowInline.add(new InlineKeyboardButton().setText(game.getName()).setCallbackData("/send_game," + game.getId()+",genre"));
                            rowsInline.add(rowInline);
                        }
                        i++;

                    }
                    List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
                    if (j >= 3)
                        rowInline2.add(new InlineKeyboardButton().setText("Назад").setCallbackData("/back," + id_genre + "," + k + "," + j + ",Genre"));
                    if (k < games.size())
                        rowInline2.add(new InlineKeyboardButton().setText("Вперед").setCallbackData("/forward," + id_genre + "," + k + "," + j + ",Genre"));
                    rowsInline.add(rowInline2);
                    execute(new_message);
                    break;


                }


                case "/back": {
                    int kk = Integer.parseInt(data[2]);
                    int jj = Integer.parseInt(data[3]);
                    String id = data[1];
                    String nametable = data[4];
                    QueryRunner run = new QueryRunner();
                    ResultSetHandler<List<Games>> h = new BeanListHandler<Games>(Games.class);


                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    markupInline.setKeyboard(rowsInline);
                    int i = 0, j = jj - 3, k = kk - 3;
                    EditMessageText new_message = new EditMessageText()
                            .setChatId(chat_id)
                            .setMessageId(toIntExact(message_id))
                            .setText("Нажмите на заинтересовашую игру")
                            .enableMarkdown(true)
                            .setReplyMarkup(markupInline);

                    List<Games> games = new ArrayList<>();
                    if (nametable.equals("Genre"))
                        games = run.query(Db.connecti, "SELECT * FROM Games where id_genre =" + id, h);
                    else
                        games = run.query(Db.connecti, "SELECT * FROM Games where id_universe =" + id, h);
                    for (Games game : games) {
                        if (i >= j && i < k) {
                            List<InlineKeyboardButton> rowInline = new ArrayList<>();
                            rowInline.add(new InlineKeyboardButton().setText(game.getName()).setCallbackData("/send_game," + game.getId()+",back"));
                            rowsInline.add(rowInline);
                        }
                        i++;

                    }
                    List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
                    if (j >= 3)
                        rowInline2.add(new InlineKeyboardButton().setText("Назад").setCallbackData("/back," + id + "," + k + "," + j + "," + nametable));
                    if (k < games.size())
                        rowInline2.add(new InlineKeyboardButton().setText("Вперед").setCallbackData("/forward," + id + "," + k + "," + j + "," + nametable));

                    rowsInline.add(rowInline2);
                    execute(new_message);
                    break;
                }


                case "/forward": {
                    int kk = Integer.parseInt(data[2]);
                    int jj = Integer.parseInt(data[3]);
                    String id = data[1];
                    String nametable = data[4];
                    QueryRunner run = new QueryRunner();
                    ResultSetHandler<List<Games>> h = new BeanListHandler<Games>(Games.class);


                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    markupInline.setKeyboard(rowsInline);
                    int i = 0, j = jj + 3, k = kk + 3;
                    EditMessageText new_message = new EditMessageText()
                            .setChatId(chat_id)
                            .setMessageId(toIntExact(message_id))
                            .setText("Нажмите на заинтересовашую игру")
                            .enableMarkdown(true)
                            .setReplyMarkup(markupInline);

                    List<Games> games = new ArrayList<>();
                    if (nametable.equals("Genre"))
                        games = run.query(Db.connecti, "SELECT * FROM Games where id_genre =" + id, h);
                    else
                        games = run.query(Db.connecti, "SELECT * FROM Games where id_universe =" + id, h);
                    for (Games game : games) {
                        if (i >= j && i < k) {
                            List<InlineKeyboardButton> rowInline = new ArrayList<>();
                            rowInline.add(new InlineKeyboardButton().setText(game.getName()).setCallbackData("/send_game," + game.getId()+",forward"));
                            rowsInline.add(rowInline);
                        }
                        i++;

                    }
                    List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
                    if (j >= 3)
                        rowInline2.add(new InlineKeyboardButton().setText("Назад").setCallbackData("/back," + id + "," + k + "," + j + "," + nametable));
                    if (k < games.size())
                        rowInline2.add(new InlineKeyboardButton().setText("Вперед").setCallbackData("/forward," + id + "," + k + "," + j + "," + nametable));

                    rowsInline.add(rowInline2);
                    execute(new_message);
                    break;


                }

                case "/send_game": {
                    String id_game = data[1];
                    String what = data[2];

                    QueryRunner run = new QueryRunner();
                    ResultSetHandler<List<Games>> h = new BeanListHandler<Games>(Games.class);
                    Statement statement = null;
                    String sql;

                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
                    rowsInline.add(rowInline);
                    rowsInline.add(rowInline2);
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    List<Games> games = run.query(Db.connecti, "SELECT * FROM Games where id =" + id_game, h);
                    for (Games game : games) {
                        SendMessage msg2 = new SendMessage()
                                .setChatId(chat_id)
                                .enableMarkdown(true)
                                .setText(game.toString())
                                .setReplyMarkup(markupInline);
                        rowInline.clear();
                        if(what.equals("user"))
                            rowInline.add(new InlineKeyboardButton().setText("Удалить из списка.").setCallbackData("/del," + game.getId() + "," + u_id));
                        else
                            rowInline.add(new InlineKeyboardButton().setText("Добавить в мой список").setCallbackData("/add," + game.getId()));

                        rowInline2.add(new InlineKeyboardButton().setText("Поиск дополнений").setCallbackData("/find_add," + game.getId()));

                        execute(msg2);
                    }
                    break;
                }

                case "/send_add": {
                    String id_add = data[1];
                    QueryRunner run = new QueryRunner();
                    ResultSetHandler<List<Additions>> h = new BeanListHandler<Additions>(Additions.class);
                    List<Additions> adds = run.query(Db.connecti, "SELECT * FROM Additions where id =" + id_add, h);
                    for (Additions add : adds) {
                        SendMessage msg2 = new SendMessage()
                                .setChatId(chat_id)
                                .enableMarkdown(true)
                                .setText(add.toString());
                        execute(msg2);
                    }
                    break;


                }

                case "/find_add": {
                    String id = data[1];
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    QueryRunner run = new QueryRunner();
                    ResultSetHandler<List<Additions>> h = new BeanListHandler<Additions>(Additions.class);
                    SendMessage msg2 = new SendMessage()
                            .setChatId(chat_id)
                            .setText("Список дополнений")
                            .setReplyMarkup(markupInline)
                            .enableMarkdown(true);
                    List<Additions> adds = run.query(Db.connecti, "SELECT * FROM additions Where id_game  =" + id, h);
                    if (adds.size() < 1)
                        msg2.setText("Дополнений для данной игры нет");
                    for (Additions add : adds) {
                        List<InlineKeyboardButton> rowInline = new ArrayList<>();
                        rowInline.add(new InlineKeyboardButton().setText(add.getName()).setCallbackData("/send_add," + add.getId()));
                        rowsInline.add(rowInline);
                    }
                    execute(msg2);
                    break;
                }

                case "/universe": {
                    QueryRunner run = new QueryRunner();
                    ResultSetHandler<List<Universe>> h = new BeanListHandler<Universe>(Universe.class);
                    ///Кнопки под сообщением
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    List<Universe> universes = run.query(Db.connecti, "SELECT * FROM Universe", h);
                    for (Universe universe : universes) {
                        List<InlineKeyboardButton> rowInline = new ArrayList<>();

                        rowInline.add(new InlineKeyboardButton().setText(universe.getName()).setCallbackData("/find_universe," + universe.getId()));
                        rowsInline.add(rowInline);
                    }
                    EditMessageText new_message = new EditMessageText()
                            .setChatId(chat_id)
                            .setMessageId(toIntExact(message_id))
                            .setText("Выбор вселенной")
                            .enableMarkdown(true)
                            .setReplyMarkup(markupInline);
                    try {
                        execute(new_message);
                        break;
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

                case "/find_universe": {
                    String id_uni = data[1];
                    QueryRunner run = new QueryRunner();
                    ResultSetHandler<List<Games>> h = new BeanListHandler<Games>(Games.class);
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    int i = 0, j = 0, k = 3;
                    EditMessageText new_message = new EditMessageText()
                            .setChatId(chat_id)
                            .setMessageId(toIntExact(message_id))
                            .setText("Нажмите на заинтересовашую игру")
                            .enableMarkdown(true)
                            .setReplyMarkup(markupInline);


                    List<Games> games = run.query(Db.connecti, "SELECT * FROM Games where id_universe =" + id_uni, h);
                    for (Games game : games) {
                        if (i >= j && i < k) {
                            List<InlineKeyboardButton> rowInline = new ArrayList<>();
                            rowInline.add(new InlineKeyboardButton().setText(game.getName()).setCallbackData("/send_game," + game.getId()+",universe"));
                            rowsInline.add(rowInline);
                        }
                        i++;

                    }
                    List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
                    if (j >= 5)
                        rowInline2.add(new InlineKeyboardButton().setText("Назад").setCallbackData("/back," + id_uni + "," + k + "," + j + "," + "Universe"));
                    if (k < games.size())
                        rowInline2.add(new InlineKeyboardButton().setText("Вперед").setCallbackData("/forward," + id_uni + "," + k + "," + j + "," + "Universe"));
                    rowsInline.add(rowInline2);
                    execute(new_message);
                    break;


                }

                case "/user_games": {
                    Statement statement = null;
                    String sql;
                    int id_game = 1000;
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();


                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    QueryRunner run = new QueryRunner();
                    ResultSetHandler<List<Games>> h = new BeanListHandler<Games>(Games.class);
                    SendMessage msg2 = new SendMessage()
                            .setChatId(chat_id)
                            .setText("Ваши игры: ")
                            .setReplyMarkup(markupInline)
                            .enableMarkdown(true);
                    sql = "SELECT id_game FROM user_games Where id_user  =" + u_id;
                    try {
                        statement = Db.connecti.createStatement();
                        ResultSet resultSet = statement.executeQuery(sql);
                        while (resultSet.next()) {
                            id_game = resultSet.getInt("id_game");
                            List<Games> games = run.query(Db.connecti, "SELECT * FROM games Where id  =" + id_game, h);
                            for (Games game : games) {


                                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                                rowInline.add(new InlineKeyboardButton().setText(game.getName()).setCallbackData("/send_game," + game.getId()+",user"));

                                rowsInline.add(rowInline);

                            }
                        }

                        execute(msg2);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    break;


                }


                case "/del": {
                    Statement statement = null;
                    String sql;
                    int id_game = Integer.parseInt(data[1]);
                    int user_id = Integer.parseInt(data[2]);
                    String name = "aaa";
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    rowsInline.add(rowInline);
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    QueryRunner run = new QueryRunner();
                    ResultSetHandler<List<Games>> h = new BeanListHandler<Games>(Games.class);

                    sql = "SELECT name FROM games Where  id =" + id_game;
                    try {
                        statement = Db.connecti.createStatement();
                        ResultSet resultSet = statement.executeQuery(sql);
                        while (resultSet.next()) {
                            name = resultSet.getString("name");

                            statement.executeUpdate("DELETE FROM user_games Where id_game =" + id_game + " and id_user=" + user_id);

                            SendMessage msg2 = new SendMessage()
                                    .setChatId(chat_id)
                                    .setText("Игра *" + name + "* удалена из вашего списка.")
                                    .setReplyMarkup(markupInline)
                                    .enableMarkdown(true);
                            execute(msg2);

                        }

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    break;


                }


            }


        }

    }

    private String getCommand(String command, String message) {
        return command.split(" ")[0];
    }

    public String getMessage(String message) {

        return Arrays.stream(message.split(" ")).skip(1).collect(Collectors.joining(" "));
    }

    public synchronized void setButtons(SendMessage sendMessage) {
        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        //Разметка клавиатуры
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        //Вывод клавиатуры всем пользователям
        replyKeyboardMarkup.setSelective(true);
        //Подгонка под количество кнопок
        replyKeyboardMarkup.setResizeKeyboard(true);
        //Не скрывать клавиатуру
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        //Лист кнопок
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        //Первая строка
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        //Добавляем кнопки
        keyboardFirstRow.add(new KeyboardButton("/coin"));
        keyboardFirstRow.add(new KeyboardButton("/hi"));
        keyboardFirstRow.add(new KeyboardButton("/help"));
        keyboardFirstRow.add(new KeyboardButton("/start"));

        //Добавляем кнопки в массив
        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    //Отправка сообщения
    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);


        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiRequestException e) {
            sendMessage.setText("Вы ввели слишком много сообщений! Подождите пару минут");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public String getBotUsername() //Получение имени бота
    {
        return "MyTest_TeleBot";
    }

    public String getBotToken() //Получение токена бота
    {
        return "1216338158:AAFQUTpEJe7fkD9VFN3wnAxd5YjzV2q1a9M";
    }

}
