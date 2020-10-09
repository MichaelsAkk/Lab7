package Commands;

import Classes.Flat;
import Instruments.DBHandler;

import java.io.Serializable;
import java.util.HashSet;

public class Remove_Lower implements Serializable {
    private transient HashSet<Flat> flats;
    private int numberOfRooms;
    private String info;
    private transient DBHandler db;
    private Integer userId;

    public Remove_Lower() {
        this.info = null;
    }

    public Remove_Lower(HashSet<Flat> flats, int numberOfRooms) {
        this.flats = flats;
        this.numberOfRooms = numberOfRooms;
    }

    public Remove_Lower(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public Remove_Lower(HashSet<Flat> flats, int numberOfRooms, DBHandler db) {
        this.flats = flats;
        this.numberOfRooms = numberOfRooms;
        this.db = db;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getInfo() {
        return this.info;
    }

    public void setFlats(HashSet<Flat> flats, DBHandler db) {

        this.flats = flats;
        this.db = db;
    }

    public void execute() {
        int rows = db.executeUpdate("DELETE FROM flats WHERE number_of_rooms <"+numberOfRooms+" and user_id = "+ userId);

        if ((rows % 10) == 1 && (rows % 100) != 11)
            info = "Был удален " + (rows) + " элемент";
        else if (((rows % 10) == 2 || (rows % 10) == 3 || (rows % 10) == 4) && ((rows % 100) != 12
                || (rows % 10) != 13 || (rows % 10) != 14))
            info = "Было удалено " + (rows) + " элемента";
        else info = "Было удалено " + (rows) + " элементов";
        flats.clear();
        db.getData(flats, "SELECT * FROM flats;");
        flats = null;
    }

    public String toStrings() {
        return "remove_lower "+ numberOfRooms;
    }
}
