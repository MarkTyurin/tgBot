import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.Serializable;
import java.sql.*;
import java.util.Date;
import java.util.List;

public class Games implements Serializable {
    static final String DB_URL = "jdbc:postgresql://ec2-54-247-89-181.eu-west-1.compute.amazonaws.com:5432/d4k73hbn3que92";
    static final String USER = "akuaihbrfdperl";
    static final String PASS = "e240eb73da4d572576a41ee28fe9dab1ace5ec37bb29532e3489618f84607bd0";
    private int id;
    private int rating;
    private int id_universe;
    private int id_genre;
    private String name;
    private String description;
    private String number_players;
    private String image;
    private String rules;
    private String link;
    private Date release_date;
    private  String genre;
    private  String universe;
    public void setId(int id) {
        this.id = id;
    }

    public void setId_universe(int id_universe) {
        this.id_universe = id_universe;
    }

    public void setId_genre(int id_genre) {
        this.id_genre = id_genre;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNumber_players(String number_players) {
        this.number_players = number_players;
    }

    public void setRelease_date(Date release_date) {
        this.release_date = release_date;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public int getRating() {
        return rating;
    }

    public int getId_universe() {
        return id_universe;
    }

    public int getId_genre() {
        return id_genre;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getNumber_players() {
        return number_players;
    }

    public String getImage() {
        return image;
    }

    public String getRules() {
        return rules;
    }

    public String getLink() {
        return link;
    }

    public Date getRelease_date() {
        return release_date;
    }

    public String getGenre() {
        return genre;
    }

    public String getUniverse() {
        return universe;
    }

    @Override
    public String toString() {
        Statement statement = null;

        String sql;
        sql = "SELECT name FROM Genre Where id =  " + id_genre;
        try {
           // connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = Db.connecti.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next())
                genre = resultSet.getString("name") ;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        sql = "SELECT name FROM universe Where id =  " + id_universe;
        try {
           // connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = Db.connecti.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next())
                universe = resultSet.getString("name") ;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return  name +
                "\nВселенная: " + universe +
                "\nЖанр: " + genre +
                "\nКраткое описание: " +description +
                "\nКоличество игроков: " + number_players +
                "\nДата выхода: " + release_date +
                "\nРейтинг: " + rating +
                "\nСкачать правила: " + rules +
                "\n" + link;
    }

    public String get1() {
        return ""+ name +"\n"+
                 image +"\n"+
                link
                ;
    }
}
