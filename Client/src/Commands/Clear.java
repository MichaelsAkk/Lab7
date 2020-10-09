package Commands;

import Classes.Flat;
import Instruments.DBHandler;

import java.io.Serializable;
import java.util.HashSet;

public class Clear implements Serializable {
    private transient HashSet<Flat> flats;
    private String info;
    private transient DBHandler db;
    private Integer userId;

    public Clear() { info=null; }

    public Clear (HashSet<Flat> flats) {
        info = null;
        this.flats = flats;
    }

    public Clear (HashSet<Flat> flats, DBHandler db) {
        info = null;
        this.flats = flats;
        this.db = db;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void execute() {
        if (db.executeUpdate("DELETE FROM flats WHERE user_id = "+ userId) == 0) {
            info = "Не удалось очистить коллекцию";
        } else {
            flats.clear();
            db.getData(flats, "SELECT * FROM flats;");
            info = "Коллекция успешно очищена от ваших объектов";
            flats = null;
        }
    }

    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "clear";
    }
}
