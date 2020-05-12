import java.io.Serializable;
import java.util.Date;

public class Games implements Serializable {
    private int id;

    private int id_universe;
    private int id_genre;
    private String name;
    private String description;
    private String number_players;
    private Date release_date;
    private int rating;

    private String image;
    private String rules;
    private String link;

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

    @Override
    public String toString() {
        return "Games{" +
                "id=" + id +
                ", id_universe=" + id_universe +
                ", id_genre=" + id_genre +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", number_players='" + number_players + '\'' +
                ", release_date=" + release_date +
                ", rating=" + rating +
                ", image='" + image + '\'' +
                ", rules='" + rules + '\'' +
                ", link='" + link + '\'' +
                '}';
    }

    public String get1() {
        return ""+ name +"\n"+
                 image +"\n"+
                link
                ;
    }
}
