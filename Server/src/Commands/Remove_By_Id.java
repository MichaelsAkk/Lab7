package Commands;

import Classes.Flat;
import Instruments.DBHandler;

import java.io.Serializable;
import java.util.HashSet;

public class Remove_By_Id implements Serializable {
    private transient HashSet<Flat> flats;
    private Integer id;
    private String info;
    private transient DBHandler db;
    private Integer userId;

   public Remove_By_Id () {
       this.info = null;
   }

    public Remove_By_Id(HashSet<Flat> flats, Integer id) {
        this.flats = flats;
        this.id = id;
    }

    public Remove_By_Id(HashSet<Flat> flats, Integer id, DBHandler db) {
        this.flats = flats;
        this.id = id;
        this.db = db;
    }

    public Remove_By_Id(Integer id) {
        this.id = id;
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
       int rows = db.executeUpdate("DELETE FROM flats WHERE flat_id = " + id + " and user_id = " + userId);
       if (rows == 0){
           info = "Элемента с данным id либо не существует в коллекции, либо он принадлежит не вам";
       }
       else {

           try {
               flats.stream().filter(f -> f.getId().toString().equalsIgnoreCase(id.toString())).limit(1).forEach(f -> flats.remove(f));
           } catch (Exception e) {
           }

           info = "Элемент успешно удален";
           flats = null;
           id = null;
       }
    }

    public String toStrings() {
        return "remove_by_id " + id;
    }
}
