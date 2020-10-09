package Instruments;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import Classes.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBHandler {
    private static final String user = "s285666";
    private static final String pwd = "gxk574";
    private static final String driver = "org.postgresql.Driver";
    private static final String dbHost = "pg";
    private static final String dbPort = "5432";
    private static final String dbName = "studs";
    private static final String url = "jdbc:postgresql://"+dbHost+":"+dbPort+"/"+dbName;
    private Connection con;
    static final Logger logger = LoggerFactory.getLogger(DBHandler.class);

    public void getConnection () {

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            logger.info("Не удалось подключить драйвер");
            e.printStackTrace();
        }
        logger.info("Драйвер подключен");

        try {
            con = DriverManager.getConnection(url, user, pwd);
        } catch (SQLException throwables) {
            logger.info("Не удалось установить соединение с БД");
            throwables.printStackTrace();
        }
        logger.info("Установлено соединение с " + dbName);

    }

    public ResultSet executeQuery (String query) {
        Statement stmt;
        ResultSet rs;

        ReadWriteLock lock = new ReentrantReadWriteLock();
        Lock writeLock = lock.writeLock();

        try {
            writeLock.lock();
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException throwables) {
            logger.info("Не удалось выполнить запрос");
            throwables.printStackTrace();
            return null;
        } finally {
            writeLock.unlock();
        }
        logger.info("Запрос выполнен");

        return rs;
    }

    public int executeUpdate (String query) {
        Statement stmt;
        int rows;

        ReadWriteLock lock = new ReentrantReadWriteLock();
        Lock writeLock = lock.writeLock();

        try {
            writeLock.lock();
            stmt = con.createStatement();
            rows = stmt.executeUpdate(query);
        } catch (SQLException throwables) {
            logger.info("Не удалось выполнить запрос");
            throwables.printStackTrace();
            return 0;
        } finally {
            writeLock.unlock();
        }
        logger.info("Запрос выполнен");

        return rows;
    }

    public void getData (HashSet<Flat> flats, String query) {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        Lock writeLock = lock.writeLock();

        try {
            writeLock.lock();

            ResultSet rs = executeQuery(query);
            Integer houseYear;
            int k = 1;

            while (true) {
                try {
                    if (!rs.next()) break;

                    if (rs.getInt(14) == 0) houseYear = null;
                    else houseYear = rs.getInt(14);

                    if (rs.getString(12) != null)
                        flats.add(new Flat(rs.getInt(1), rs.getString(2), new Coordinates(rs.getInt(3),
                                rs.getInt(4)), LocalDate.of(rs.getInt(5), rs.getInt(6), rs.getInt(7)),
                                rs.getFloat(8), rs.getInt(9),
                                rs.getBoolean(10), View.valueOf(rs.getString(11).toUpperCase()),
                                Transport.valueOf(rs.getString(12).toUpperCase()),
                                new House(rs.getString(13), houseYear, rs.getLong(15),
                                        rs.getLong(16)), rs.getInt(17)));
                    else
                        flats.add(new Flat(rs.getInt(1), rs.getString(2), new Coordinates(rs.getInt(3),
                                rs.getInt(4)), LocalDate.of(rs.getInt(5), rs.getInt(6), rs.getInt(7)),
                                rs.getFloat(8), rs.getInt(9),
                                rs.getBoolean(10), View.valueOf(rs.getString(11).toUpperCase()),
                                null,
                                new House(rs.getString(13), houseYear,
                                        rs.getLong(15), rs.getLong(16)), rs.getInt(17)));
                } catch (Exception throwables) {
                    logger.info("Не удалось прочитать данные");
                    k = 0;
                    throwables.printStackTrace();
                }
            }

            if (k == 1)
                logger.info("Данные получены");
        } finally {
            writeLock.unlock();
        }
    }
}
