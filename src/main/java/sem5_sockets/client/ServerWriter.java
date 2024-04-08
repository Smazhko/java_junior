package sem5_sockets.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

class ServerWriter implements Runnable {
    private final Socket serverSocket;

    // конструктор
    public ServerWriter(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        Scanner consoleReader = new Scanner(System.in);

        // создаём автозакрываемый поток вывода на сервер-сокет - через него мы будем отправлять сообщения на сервер
        try (PrintWriter msgToServer = new PrintWriter(serverSocket.getOutputStream(), true)) {

            // в бесконечном цикле читаем консоль
            while (true) {
                String msgFromConsole = consoleReader.nextLine();

                if (!serverSocket.isClosed()) {
                    msgToServer.println(msgFromConsole); //отправляем сообщение на сервер
                    if (msgFromConsole.equals("/exit")) break;
                }
                else {
                    System.err.println("SYSTEM : Соединение с сервером прервано!");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("SYSTEM : Ошибка при отправке сообщения на сервер: " + e.getMessage());
        }

        // при выходе из цикла чтения с консоли - разрываем связь с сервером
        try {
            System.out.println("SYSTEM : Отключаемся...");
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("SYSTEM : Ошибка при отключении от сервера: " + e.getMessage());
        }
    }

}