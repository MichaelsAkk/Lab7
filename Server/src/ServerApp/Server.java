package ServerApp;

import Classes.Flat;
import Commands.Exit;
import Instruments.DBHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ForkJoinPool;

public class Server {

    static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    public static final int port = 1238;
    private static HashSet<Flat> flats;
    private static ArrayList<String> scripts, history;
    private static LocalDateTime today;
    private static DBHandler db;
    private static ForkJoinPool pool;

    public static void setHistory(String command) {
        if (history != null) {
            if (history.size() < 8) {
                String[] parts = command.split(" ");
                history.add(parts[0]);
            } else {
                history.remove(0);
                String[] parts = command.split(" ");
                history.add(parts[0]);
            }
        } else {
            history = new ArrayList<>();
            String[] parts = command.split(" ");
            history.add(parts[0]);
        }
    }

    public static HashSet<Flat> getFlats () {
        return flats;
    }

    public static ArrayList<String> getHistory () {
        return history;
    }

    public static ArrayList<String> getScripts () {
        return scripts;
    }

    public static void main(String[] args) {

        flats = new HashSet<Flat>();
        today = LocalDateTime.now();
        history = new ArrayList<>();
        scripts = new ArrayList<>();
        pool = new ForkJoinPool(2);

        try {
            ServerSocket socket = new ServerSocket (port);
            logger.info("Server app запущено");
            db = new DBHandler();
            db.getConnection();
            db.getData(flats, "SELECT * FROM flats;");
            ServerUser serverUser = new ServerUser();
            serverUser.start();              // Запускает отдельный поток для работы внутри сервера
            while (true) {
                Socket client = socket.accept();
                pool.execute(new User(client));
            }
        } catch (UnknownHostException ex) {
            System.err.println("Хост не определен");
        } catch (IOException ex) {
            System.err.println("Соединение разорвано");
            ex.printStackTrace();
        }
    }

    public static void handleUserCommand (String userCommand, HashSet<Flat> flats, BufferedReader reader) {
        if (userCommand.equalsIgnoreCase("")) {}
        else if (userCommand.equalsIgnoreCase("exit")) {
            Exit exit = new Exit(reader);
            System.out.println("Работа Server app завершена");
            exit.execute();
        }
        else if (ProcessingUserRequest.result(userCommand, flats, today, db) == 0) {
            System.out.println("Данная команда не поддерживается сервером");
        }
    }

    public static void readRequest (Socket client) {
        try {
            in = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            System.err.println("Соединение разорвано");
            e.printStackTrace();
        }
    }

    public static void handleRequest (Socket client) {
        try {
            out = new ObjectOutputStream(client.getOutputStream());

            out.writeObject(ProcessingRequest.getResult(in.readObject(), flats, today, client.getInetAddress(), scripts, db));

            logger.info("Ответ отправлен");

            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            System.err.println("Соединение разорвано");
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            System.err.println("Ошибка чтения");
        }
    }
}

class ServerUser extends Thread {

    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String userCommand = null;
        while (true) {
            try {
                userCommand = reader.readLine();
            } catch (IOException e) {
                System.err.println("Ошибка ввода");
            }
            Server.handleUserCommand(userCommand, Server.getFlats(), reader);
        }
    }
}

class User extends Thread {
    private Socket client;

    public User () {}

    public User (Socket client) {

        this.client = client;
    }

    @Override
    public void run() {
        Server.logger.info("Установлено соединение с {}", client.getInetAddress().getHostName());
        Server.readRequest(client);
        ProcessingUser user = new ProcessingUser(client);
        user.start();
    }
}

class ProcessingUser extends Thread {
    private Socket client;

    public ProcessingUser () {}

    public ProcessingUser (Socket client) {

        this.client = client;
    }

    @Override
    public void run() {
        Server.handleRequest(client);
    }
}
