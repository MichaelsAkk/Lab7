package ServerApp;

import Classes.Flat;
import Commands.*;
import Instruments.DBHandler;
import Instruments.Login;
import Instruments.ScriptInfo;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ProcessingRequest {
    private static Help help;
    private static Clear clear;
    private static Show show;
    private static Info info;
    private static Add add;
    private static Update update;
    private static Remove_By_Id rmi;
    private static Remove_Greater rg;
    private static Remove_Lower rl;
    private static History his;
    private static Average_Of_Number_Of_Rooms avg;
    private static Filter_By_Number_Of_Rooms filt;
    private static Print_Ascending pr;
    private static Execute_Script es;
    private static Login log;

    public static Object getResult(Object request, HashSet<Flat> flats, LocalDateTime today, InetAddress address, ArrayList<String> scripts, DBHandler db) {

        ReadWriteLock lock = new ReentrantReadWriteLock();
        Lock readLock = lock.readLock();

        log = new Login();
        if (request.getClass()==log.getClass()) {
            log = (Login) request;
            log.setDB(db);
            Server.logger.info("Получен запрос от "+address.getHostName()+": \"" + log.toStrings() + "\"");
            log.execute();
            return log;
        }
        log = null;

        help = new Help();
        if (request.getClass()==help.getClass()) {
            Server.logger.info("Получен запрос от "+address.getHostName()+": \"" + help.toString() + "\"");
            help.execute();
            return help;
        }
        help = null;

        clear = new Clear(flats, db);
        if (request.getClass()==clear.getClass()) {
            Server.logger.info("Получен запрос от "+address.getHostName()+": \"" + clear.toString() + "\"");
            clear.execute();
            return clear;
        }
        clear = null;

        show = new Show(flats);
        if (request.getClass()==show.getClass()) {
            Server.logger.info("Получен запрос от "+address.getHostName()+": \"" + show.toString() + "\"");
            try {
                readLock.lock();
                show.execute();
            } finally {
                readLock.unlock();
            }
            return show;
        }
        show = null;

        info = new Info(flats, today);
        if (request.getClass()==info.getClass()) {
            Server.logger.info("Получен запрос от "+address.getHostName()+": \"" + info.toString() + "\"");
            try {
                readLock.lock();
                info.execute();
            } finally {
                readLock.unlock();
            }
            return info;
        }
        info = null;

        add = new Add();
        if (request.getClass()==add.getClass()) {
            add = (Add) request;
            add.setFlats(flats, db);
            Server.logger.info("Получен запрос от "+address.getHostName()+": \"" + add.toStrings() + "\"");
            add.execute();
            return add;
        }
        add = null;

        update = new Update();
        if (request.getClass()==update.getClass()) {
            update = (Update) request;
            update.setFlats(flats, db);
            if (update.getName()==null) {
                Server.logger.info("Получен запрос от " + address.getHostName() + ": \"" + update.toStringsNoArguments() + "\"");
                update.check();
                return update;
            } else {
                Server.logger.info("Получен запрос от " + address.getHostName() + ": \"" + update.toStrings() + "\"");
                update.execute();
                return update;
            }

        }
        update = null;

        rmi = new Remove_By_Id();
        if (request.getClass()==rmi.getClass()) {
            rmi = (Remove_By_Id) request;
            rmi.setFlats(flats, db);
            Server.logger.info("Получен запрос от " + address.getHostName() + ": \"" + rmi.toStrings() + "\"");
            rmi.execute();
            return rmi;
        }
        rmi = null;

        rg = new Remove_Greater();
        if (request.getClass()==rg.getClass()) {
            rg = (Remove_Greater) request;
            rg.setFlats(flats, db);
            Server.logger.info("Получен запрос от " + address.getHostName() + ": \"" + rg.toStrings() + "\"");
            rg.execute();
            return rg;
        }
        rg = null;

        rl = new Remove_Lower();
        if (request.getClass()==rl.getClass()) {
            rl = (Remove_Lower) request;
            rl.setFlats(flats, db);
            Server.logger.info("Получен запрос от " + address.getHostName() + ": \"" + rl.toStrings() + "\"");
            rl.execute();
            return rl;
        }
        rl = null;

        his = new History();
        if (request.getClass()==his.getClass()) {
            his = (History) request;
            Server.logger.info("Получен запрос от " + address.getHostName() + ": \"" + his.toStrings() + "\"");
            his.execute();
            return his;
        }
        his = null;

        avg = new Average_Of_Number_Of_Rooms();
        if (request.getClass()==avg.getClass()) {
            avg = (Average_Of_Number_Of_Rooms) request;
            Server.logger.info("Получен запрос от " + address.getHostName() + ": \"" + avg.toStrings() + "\"");
            avg.setFlats(flats);
            try {
                readLock.lock();
                avg.execute();
            } finally {
                readLock.unlock();
            }
            return avg;
        }
        avg = null;

        filt = new Filter_By_Number_Of_Rooms();
        if (request.getClass()==filt.getClass()) {
            filt = (Filter_By_Number_Of_Rooms) request;
            Server.logger.info("Получен запрос от " + address.getHostName() + ": \"" + filt.toStrings() + "\"");
            filt.setFlats(flats);
            try {
                readLock.lock();
                filt.execute();
            } finally {
                readLock.unlock();
            }
            return filt;
        }
        filt = null;

        pr = new Print_Ascending();
        if (request.getClass()==pr.getClass()) {
            pr = (Print_Ascending) request;
            Server.logger.info("Получен запрос от "+address.getHostName()+": \"" + pr.toStrings() + "\"");
            pr.setFlats(flats);
            try {
                readLock.lock();
                pr.execute();
            } finally {
                readLock.unlock();
            }
            return pr;
        }
        pr = null;

        es = new Execute_Script();
        if (request.getClass()==es.getClass()) {
            es = (Execute_Script) request;
            Server.logger.info("Получен запрос от "+address.getHostName()+": \"" + es.toStrings() + "\"");
            ScriptInfo.setStartInfo("");
            es.setFields(flats, today, db);
            es.execute();
            return es;
        }
        es = null;

        return null;
    }
}
