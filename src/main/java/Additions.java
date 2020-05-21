import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Additions implements Serializable {
    private int id;
    private int id_game;
    private String name;
    private String description;
    private String image;

    @Override
    public String toString() {
        return
               "*"+  name +"*"+ '\'' +
                "*Описание:*" + description + '\'' +
                    image ;
    }
}
