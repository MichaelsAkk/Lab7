package Commands;

import Classes.Flat;
import Classes.Transport;
import Classes.View;
import Instruments.DBHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class ScriptUpdate implements Serializable {

    private transient HashSet<Flat> flats;
    private transient Flat flat;
    private Integer elementId;
    private transient ArrayList<String> scriptCommands;
    private int i;
    private String info;
    private transient DBHandler db;
    private Integer userId;

    public ScriptUpdate (HashSet<Flat> flats, Integer elementId, ArrayList<String> scriptCommands, int i) {
        this.flats = flats;
        this.elementId = elementId;
        this.scriptCommands = scriptCommands;
        this.i = i;
    }

    public ScriptUpdate (HashSet<Flat> flats, Integer elementId, ArrayList<String> scriptCommands, int i, DBHandler db) {
        this.flats = flats;
        this.elementId = elementId;
        this.scriptCommands = scriptCommands;
        this.i = i;
        this.db = db;
    }

    public void setUserId (Integer userId) {
        this.userId = userId;
    }

    public String getInfo() {
        return this.info;
    }

    public void execute() {
    }

    public int fieldsUpdate() {
        int control = 0;
        if (flats != null) {
            for (Flat f : flats) {
                if (f.getId().toString().equalsIgnoreCase(elementId.toString())) {
                    control = 1;
                    flat = f;
                    break;
                }
            }
        }
        if (control == 0) { info = "Ошибка ввода: элемента с данным id не существует в коллекции"; return 0; }
        else if (userId!=flat.getUserId()) {
            info = "Невозможно обновить: объект не принадлежит вам";
            return 0;
        }
        else {
            String result;
            Integer id = null;
            String name;
            Integer x = null;
            Integer y = null;
            Integer houseYear = -1;
            String houseName = null;
            View view = null;
            Transport transport = null;
            Float area = -1.0f;
            int numberOfRooms = -1;
            boolean new1 = false;
            long numberOfFlatsOnFloor = -1;
            long numberOfLifts = -1;

            result = scriptCommands.get(i+1);
            if (result.equalsIgnoreCase("null") || result.equalsIgnoreCase("")) {
                info = "Ошибка ввода: поле \"name\" содержит null";
                return 0;
            } else if (result.equalsIgnoreCase("")) {
                info = "Ошибка ввода: поле \"name\" не может быть пустым";
                return 0;
            }
            else if (result.equalsIgnoreCase("-")) { name = flat.getName(); }
            else name = result;

            result = scriptCommands.get(i+2);
            try {
                if (result.equalsIgnoreCase("null") || result.equalsIgnoreCase("")) {
                    info = "Ошибка ввода: поле \"x\" содержит null";
                    return 0;
                }
                else if (result.equalsIgnoreCase("-")) { x = flat.getCoordinates().getX(); }
                else {
                    x = Integer.parseInt(result);
                    if (x <= -474) {
                        info = "Ошибка ввода: поле \"x\" должно быть больше -474";
                        return 0;
                    }
                }
            } catch (NumberFormatException e) {
                info = "Ошибка ввода: поле \"x\" не является целым числом";
                return 0;
            }

            result = scriptCommands.get(i+3);
            try {
                if (result.equalsIgnoreCase("null") || result.equalsIgnoreCase("")) {
                    info = "Ошибка ввода: поле \"y\" содержит null";
                    return 0;
                }
                else if (result.equalsIgnoreCase("-")) { y = flat.getCoordinates().getY(); }
                else {
                    y = Integer.parseInt(result);
                }
            } catch (NumberFormatException e) {
                info = "Ошибка ввода: поле \"y\" не является целым числом";
                return 0;
            }

            result = scriptCommands.get(i+4);
            try {
                if (result.equalsIgnoreCase("null") || result.equalsIgnoreCase("")) { info = "Ошибка ввода: поле \"area\" содержит null";
                    return 0;}
                else if (result.equalsIgnoreCase("-")) area = flat.getArea();
                else {
                    area = Float.parseFloat(result);
                    if (area <= 0) {
                        info = "Ошибка ввода: поле \"area\" должно быть больше 0";
                        return 0;
                    }
                }
            } catch (NumberFormatException e) {
                info = "Ошибка ввода: поле \"area\" должно быть числом (дробная часть указывается после символа \".\")";
                return 0;
            }

            result = scriptCommands.get(i+5);
            try {
                if (result.equalsIgnoreCase("-")) numberOfRooms = flat.getNumberOfRooms();
                else {
                    numberOfRooms = Integer.parseInt(result);
                    if (numberOfRooms <= 0) {
                        info = "Ошибка ввода: поле \"numberOfRooms\" должно быть целым положительным числом";
                        return 0;
                    }
                }
            } catch (NumberFormatException e) {
                info = "Ошибка ввода: поле \"numberOfRooms\" не является целым числом";
                return 0;
            }

            result = scriptCommands.get(i+6);
            if (result.equalsIgnoreCase("-")) new1 = flat.getNew();
            else if (!result.equalsIgnoreCase("true") && !result.equalsIgnoreCase("false")) {
                info = "Ошибка ввода: поле \"new\" должно быть либо \"true\", либо \"false\" ";
                return 0;
            } else if (result.equalsIgnoreCase("true")) new1 = true;
            else new1 = false;

            result = scriptCommands.get(i+7);
            try {
                if (result.equalsIgnoreCase("-")) { view = flat.getView(); }
                else if (result.equalsIgnoreCase("null") || result.equalsIgnoreCase("")) {
                    info = "Ошибка ввода: поле \"view\" содержит null";
                    return 0;
                } else {
                    view = View.valueOf(result.toUpperCase());
                }
            } catch (IllegalArgumentException e) {
                info = "Ошибка ввода: поле \"view\" не содержит значение из указанного списка";
                return 0;
            }

            result = scriptCommands.get(i+8);
            try {
                if (result.equalsIgnoreCase("-")) transport = flat.getTransport();
                else if (result.equalsIgnoreCase("null") || result.equalsIgnoreCase("")) transport = null;
                else {
                    transport = Transport.valueOf(result.toUpperCase());
                }
            } catch (IllegalArgumentException e) {
                info = "Ошибка ввода: поле \"transport\" не содержит значение из указанного списка";
                return 0;
            }

            result = scriptCommands.get(i+9);
            if (result.equalsIgnoreCase("-")) houseName = flat.getHouse().getName();
            else if (result.equalsIgnoreCase("null") || result.equalsIgnoreCase("")) {
                info = "Ошибка ввода: поле \"House.name\" содержит null";
                return 0;
            } else houseName = result;

            result = scriptCommands.get(i+10);
            try {
                if (result.equalsIgnoreCase("-")) houseYear = flat.getHouse().getYear();
                else if (result.equalsIgnoreCase("null") || result.equalsIgnoreCase("")) houseYear = null;
                else {
                    houseYear = Integer.parseInt(result);
                    if (houseYear <= 0) {
                        info = "Ошибка ввода: поле \"House.year\" не является целым положительным числом";
                        return 0;
                    }
                }
            } catch (NumberFormatException e) {
                info = "Ошибка ввода: поле \"House.year\" не является целым числом";
                return 0;
            }

            result = scriptCommands.get(i+11);
            try {
                if (result.equalsIgnoreCase("-")) numberOfFlatsOnFloor = flat.getHouse().getNumberOfFlatsOnFloor();
                else {
                    numberOfFlatsOnFloor = Long.parseLong(result);
                    if (numberOfFlatsOnFloor <= 0) {
                        info = "Ошибка ввода: поле \"House.NumberOfFlatsOnFloor\" не является целым положительным числом";
                        return 0;
                    }
                }
            } catch (NumberFormatException e) {
                info = "Ошибка ввода: поле \"House.NumberOfFlatsOnFloor\" не является целым числом";
                return 0;
            }

            result = scriptCommands.get(i+12);
            try {
                if (result.equalsIgnoreCase("-")) numberOfLifts = flat.getHouse().getNumberOfLifts();
                else {
                    numberOfLifts = Long.parseLong(result);
                    if (numberOfLifts <= 0) {
                        info = "Ошибка ввода: поле \"House.NumberOfLifts\" не является целым положительным числом";
                        return 0;
                    }
                }
            } catch (NumberFormatException e) {
                info = "Ошибка ввода: поле \"House.NumberOfLifts\" не является целым числом";
                return 0;
            }

            String transportString, yearString;
            if (transport == null) transportString = "NULL";
            else transportString = "'"+transport.toString()+"'";
            if (houseYear == null) yearString = "NULL";
            else yearString = "'"+houseYear.toString()+"'";

            if (db.executeUpdate("UPDATE flats SET flat_name = '"+name+"', x = "+x+", y = "+y+", " +
                    "area = "+area+", number_of_rooms = "+numberOfRooms+", flat_new = "+new1+", " +
                    "flat_view = '"+view.toString()+"', flat_transport = "+transportString+", " +
                    "house_name = '"+houseName+"', house_year = "+yearString+", number_of_flats_on_floor = "+numberOfFlatsOnFloor+", " +
                    "number_of_lifts = "+numberOfLifts+" WHERE flat_id = "+elementId) == -1) {

                return 0;
            }
            else {
                flats.clear();
                db.getData(flats, "SELECT * FROM flats;");
                flats = null;
                flat = null;
            }

            return 1;
        }
    }
}
