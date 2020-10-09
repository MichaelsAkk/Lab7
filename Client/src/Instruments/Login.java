package Instruments;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login implements Serializable {
    private String login;
    private String pwd;
    private Integer userId;
    private boolean reg;
    private transient DBHandler db;

    public Login() {}

    public Login(String login, String pwd) {
        this.login = login;
        this.pwd = pwd;
    }

    public void setDB(DBHandler db) {
        this.db = db;
    }

    public void setData(String login, String pwd) {
        this.login = login;
        this.pwd = pwd;
        this.userId = 0;
    }

    public void setUserId (int userId) {
        this.userId = userId;
    }

    public void setReg (boolean reg) {
        this.reg = reg;
    }

    public boolean getReg () {
        return this.reg;
    }

    public String getLogin () {
        return this.login;
    }

    public String getPwd () {
        return this.pwd;
    }

    public int getUserId () {
        return this.userId;
    }

    public void execute() {

        if (reg == false) {
            ResultSet rs = db.executeQuery("SELECT * FROM users WHERE login='" + login + "' and pwd='" + pwd +"'");
            int count = 0;
            while (true) {
                try {
                    if (!rs.next()) break;
                    try {
                        userId = rs.getInt(1);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                count++;
            }
            if (count == 0) {
                userId = 0;
            }
        } else {
            ResultSet rs = db.executeQuery("SELECT * FROM users WHERE login='" + login + "'");
            int count = 0;
            while (true) {
                try {
                    if (!rs.next()) break;
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                count++;
            }
            if (count != 0) {
                userId = 0;
            } else {
                db.executeUpdate("INSERT INTO users (login, pwd) values ('" + login + "', '" + pwd + "')");
                reg = false;
            }
        }
    }

    public String toStrings() {
        if (reg == true) {
            return "Зарегистрировать пользователя под логином " + login;
        } else {
            return "Выполнить вход пользователя под логином " + login;
        }
    }
}
