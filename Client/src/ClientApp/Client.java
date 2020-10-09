package ClientApp;

import Instruments.Login;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Client {
    static ObjectInputStream in;
    static ObjectOutputStream out;
    static InetAddress ia;
    public static final int port = 1238;
    static SocketChannel channel;
    static Object command, answer;
    static String userCommand;
    static ByteBuffer buffer = ByteBuffer.allocate(10000);
    static int userId;
    static Login log;

    public static void main(String[] args) {

        try {
            ia = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Client app запущено");

        int k = 0;
        while (k==0) {
            String login, pwd;
            try {
                System.out.print("Введите логин: ");
                login = reader.readLine();
                if (login.equalsIgnoreCase("exit")) {
                    reader.close();
                    System.out.println("Работа Client app завешрена");
                    System.exit(0);
                }
                System.out.print("Введите пароль: ");
                pwd = reader.readLine();
                MessageDigest sha = MessageDigest.getInstance("SHA-224");
                byte[] hashPWD = sha.digest(pwd.getBytes());
                StringBuilder builder = new StringBuilder();
                for (byte b : hashPWD) {
                    builder.append(String.format("%02X ",b));
                }
                log = new Login(login, builder.toString());
            } catch (IOException e) {
                System.err.println("Ошибка ввода");
            } catch (NoSuchAlgorithmException e) {
                System.err.println("Данный алгоритм хеширования не поддерживается");
            }
            log.setReg(false);
            try {
                channel = SocketChannel.open(new InetSocketAddress(ia, port));
                channel.configureBlocking(false);
                sendCommand(log);
                buffer.clear();
                buffer.position(0);
                buffer.limit(10000);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                answer = getAnswer();
                log = (Login) answer;
                out.close();
                channel.close();
                if (log.getUserId() == 0) {
                    System.out.println("Неверно указан логин или пароль");
                    System.out.println("Хотите зарегистрироваться под данным логином? (y/n)");
                    if (reader.readLine().equalsIgnoreCase("y")) {
                        channel = SocketChannel.open(new InetSocketAddress(ia, port));
                        channel.configureBlocking(false);
                        log.setReg(true);
                        sendCommand(log);
                        buffer.clear();
                        buffer.position(0);
                        buffer.limit(10000);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                        }
                        answer = getAnswer();
                        log = (Login) answer;
                        out.close();
                        channel.close();
                        if (log.getReg()==false) {
                            System.out.println("Добро пожаловать, " + log.getLogin() + "!");
                            userId = log.getUserId();
                            k++;
                        } else {
                            System.out.println("Не удалось зарегистрировать, данный логин уже занят другим пользователем");
                            log = null;
                        }
                    } else {
                        log = null;
                    }
                } else {
                    System.out.println("Добро пожаловать, " + log.getLogin() + "!");
                    userId = log.getUserId();
                    k++;
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Сервер временно недоступен");
            }
        }

        System.out.println("Система готова к работе. Для просмотра списка команд введите \"help\".");


        while (true) {
            try {
                userCommand = reader.readLine();
                ClientUserCommands.check(userCommand); // Проверка команды на валидность
                if (ClientUserCommands.getStatus()==1) {  // Если команда введена корректно
                    command = ClientUserCommands.getCommand(userCommand, reader, userId);  // getCommand возвращает введенную пользователем команду в виде объекта
                    if (command != null) {
                        channel = SocketChannel.open(new InetSocketAddress(ia, port));
                        channel.configureBlocking(false);
                        sendCommand(command);
                        buffer.clear();
                        buffer.position(0);
                        buffer.limit(10000);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                        }
                        answer = getAnswer();

                        if ((command = ProcessingAnswer.print(answer, userCommand)) != null) {

                            out.close();
                            channel.close();
                            channel = SocketChannel.open(new InetSocketAddress(ia, port));
                            channel.configureBlocking(false);
                            sendCommand(command);
                            buffer.clear();
                            buffer.position(0);
                            buffer.limit(10000);

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                            }
                            answer = getAnswer();

                            ProcessingAnswer.print(answer, userCommand);   // Получает на вход ответ сервера (getAnswer()) и обрабатывает его
                        }

                        out.close();
                        channel.close();
                    }
                }
            } catch (IOException e) {
                System.err.println("Сервер временно недоступен");
            }
            catch (ClassNotFoundException e) {
                System.err.println("Сервер временно недоступен");
            }
        }

    }

    /**
     * Отправлят команду на сервер
     * @param com отправляемая команда
     */

    public static void sendCommand (Object com) {
        try {
            if(channel == null){
                System.err.println("Соединение не создано");
                return;
            }
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            out = new ObjectOutputStream(byteStream);
            out.writeObject(com);
            channel.write(ByteBuffer.wrap(byteStream.toByteArray()));

            System.out.println("Отправлено на сервер");
        } catch (IOException e) {
            System.err.println("Не удалось отправить на сервер");
        }
    }

    /**
     * Получает ответ от сервера
     * @return объект в виде обработанной команды
     * @throws IOException
     */
    public static Object getAnswer () throws IOException, ClassNotFoundException {
        if(channel == null){
            System.err.println("Соединение не создано");
            return null;
        }

        channel.read(buffer);

        try {
            ObjectInputStream objStream = new ObjectInputStream(new ByteArrayInputStream(buffer.array()));
            return objStream.readObject();
        }catch(StreamCorruptedException e){
            return null;
        }
    }
}