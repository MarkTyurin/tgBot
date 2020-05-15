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
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
              //  rowInline.add(new InlineKeyboardButton().setText("Update message text").setCallbackData("update_msg_text"));

               // rowInline.add(new InlineKeyboardButton().setText("ok").setCallbackData("/ok"));

                rowInline.add(new InlineKeyboardButton().setText("Поиск по жанру").setCallbackData("/genre"));

                rowInline.add(new InlineKeyboardButton().setText("Поиск по вселенной").setCallbackData("/universe"));
                rowInline.add(new InlineKeyboardButton().setText("Внести аккаунт в базу данных").setCallbackData("/user"));
                // Set the keyboard to the markup
                rowsInline.add(rowInline);

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

            SendMessage newM2 = new SendMessage()
                    .setText(String.join(" " , data));
            execute(newM2);

            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            // if (call_data.equals("update_msg_text")) {
            switch (data[0]) {
                case "/start": {
                    String answer = "Updated message text";
                    EditMessageText new_message = new EditMessageText()
                            .setChatId(chat_id)
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
                    int id_user = message1.getFrom().getId();
                    Statement statement = null;
                    String sql;

                    sql = "INSERT INTO user_games (id_user, id_game) VALUES ("+id_user+",'"+id_game+"')";
                    try {
                        statement = Db.connecti.createStatement();
                        statement.executeQuery(sql);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace(); }
                    break;
                }

                case "/user": {
                    SendMessage newM2 = new SendMessage()
                            .setText("dsfsdfdsds");
                    execute(newM2);

                    int id_user = message1.getFrom().getId();
                    String  nam = message1.getFrom().getFirstName();
                    SendMessage newM = new SendMessage()
                            .setText(nam +"dsfsdfdsds"+ id_user);
                    execute(newM);
                    Statement statement = null;
                    String sql;
                    sql = "INSERT INTO users (tg_id,nickname) VALUES  ("+id_user+", '"+nam+"')";
                    try {
                        statement = Db.connecti.createStatement();
                       statement.executeQuery(sql);
                        sendMsg(message1, "аккаунт добавлен");
                 } catch (SQLException throwables) {
                        throwables.printStackTrace(); }
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
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    rowsInline.add(rowInline);
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    List<Genre> genres = run.query(Db.connecti, "SELECT * FROM Genre", h);
                    for (Genre genre : genres) {
                        rowInline.add(new InlineKeyboardButton().setText(genre.getName()).setCallbackData("/find_genre," + genre.getName()));
                    }
                    EditMessageText new_message = new EditMessageText()
                            .setChatId(chat_id)
                            .setMessageId(toIntExact(message_id))
                            .setText("Выбор жанра")
                            .setReplyMarkup(markupInline);
                    try {
                        execute(new_message);
                        break;
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

                case "/find_genre": {
                    String genre = data[1];

                    //rowInline.add(new InlineKeyboardButton().setText( genre.getName()).setCallbackData("/find_genre"));

                    QueryRunner run = new QueryRunner();
                    ResultSetHandler<List<Games>> h = new BeanListHandler<Games>(Games.class);
                    Statement statement = null;
                    String sql;
                    int id_genre = 1000;
                    sql = "SELECT id FROM Genre Where name  ='" + genre + "'";
                    try {
                        // connection = DriverManager.getConnection(DB_URL, USER, PASS);
                        statement = Db.connecti.createStatement();
                        ResultSet resultSet = statement.executeQuery(sql);
                        while (resultSet.next())
                            id_genre = resultSet.getInt("id");

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    rowsInline.add(rowInline);
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);

                    SendMessage msg2 = new SendMessage()
                            .setChatId(chat_id)

                            .setReplyMarkup(markupInline);

                    List<Games> games = run.query(Db.connecti, "SELECT * FROM Games where id_genre =" + id_genre, h);
                    for (Games game : games) {
                       msg2.setText(game.toString());
                        //rowInline.add(new InlineKeyboardButton().setText( genre.getName()).setCallbackData("/find_genre"));
                        rowInline.add(new InlineKeyboardButton().setText("Добавить в мой список").setCallbackData("/add,"+game.getId()));
                        execute(msg2);
                    }
                    break;


                }


                case "/universe": {
                    QueryRunner run = new QueryRunner();
                    ResultSetHandler<List<Universe>> h = new BeanListHandler<Universe>(Universe.class);
                    ///Кнопки под сообщением
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    rowsInline.add(rowInline);
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    List<Universe> universes = run.query(Db.connecti, "SELECT * FROM Universe", h);
                    for (Universe universe : universes) {
                        rowInline.add(new InlineKeyboardButton().setText(universe.getName()).setCallbackData("/find_universe," + universe.getName()));
                    }
                    EditMessageText new_message = new EditMessageText()
                            .setChatId(chat_id)
                            .setMessageId(toIntExact(message_id))
                            .setText("Выбор вселенной")
                            .setReplyMarkup(markupInline);
                    try {
                        execute(new_message);
                        break;
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

                case "/find_universe": {
                    String uni = data[1];

                    //rowInline.add(new InlineKeyboardButton().setText( genre.getName()).setCallbackData("/find_genre"));

                    QueryRunner run = new QueryRunner();
                    ResultSetHandler<List<Games>> h = new BeanListHandler<Games>(Games.class);
                    Statement statement = null;
                    String sql;
                    int id_uni = 1000;
                    sql = "SELECT id FROM Universe Where name  ='" + uni + "'";
                    try {
                        // connection = DriverManager.getConnection(DB_URL, USER, PASS);
                        statement = Db.connecti.createStatement();
                        ResultSet resultSet = statement.executeQuery(sql);
                        while (resultSet.next())
                            id_uni = resultSet.getInt("id");

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    rowsInline.add(rowInline);
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    rowInline.add(new InlineKeyboardButton().setText("Добавить в мой список").setCallbackData("/add" ));




                    List<Games> games = run.query(Db.connecti, "SELECT * FROM Games where id_universe =" + id_uni, h);
                    for (Games game : games) {
                        SendMessage msg2 = new SendMessage()
                                .setChatId(chat_id)
                                .setText(game.toString())
                          .setReplyMarkup(markupInline);
                        //rowInline.add(new InlineKeyboardButton().setText( genre.getName()).setCallbackData("/find_genre"));
                        execute(msg2);
                    }
                    break;


                }


            }


        }
        /*
        if(update.hasMessage()){
            if(update.getMessage().hasText()){
                if(update.getMessage().getText().equals("Hello")){
                    try {
                        execute(sendInlineKeyBoardMessage(update.getMessage().getChatId()));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if(update.hasCallbackQuery()){
            try {
                execute(new SendMessage().setText(
                        update.getCallbackQuery().getData())
                        .setChatId(update.getCallbackQuery().getMessage().getChatId()));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        /*      Message message = update.getMessage();
        if (message != null && message.hasText()) {
            String strMessage = message.getText();
            strMessage =  Commands.getCommands(getMessage(strMessage), getCommand(strMessage, getMessage(strMessage)));
            if (!strMessage.equals("")) {
                sendMsg(message, strMessage);
            }
        }*/
    }

    private String getCommand(String command, String message) {
        return command.split(" ")[0];
    }

    private String getMessage(String message) {
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
        keyboardFirstRow.add(new KeyboardButton("/ok"));
        keyboardFirstRow.add(new KeyboardButton("/help"));
        keyboardFirstRow.add(new KeyboardButton("/add account"));

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


    private void setInline() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        buttons1.add(new InlineKeyboardButton().setText("Кнопка").setCallbackData("17"));
        buttons.add(buttons1);

        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);
    }

    public static SendMessage sendInlineKeyBoardMessage(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Тык");
        inlineKeyboardButton1.setCallbackData("Button \"Тык\" has been pressed");
        inlineKeyboardButton2.setText("Тык2");
        inlineKeyboardButton2.setCallbackData("Button \"Тык2\" has been pressed");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Fi4a").setCallbackData("CallFi4a"));
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return new SendMessage().setChatId(chatId).setText("Пример").setReplyMarkup(inlineKeyboardMarkup);
    }
}
