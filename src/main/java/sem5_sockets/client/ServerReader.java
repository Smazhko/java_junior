package sem5_sockets.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

// класс - поток чтения сообщений от сервера
class ServerReader implements Runnable {
    private final Socket serverSocket;
    private final char PUB_MSG = '$';
    private final char PRIV_FROM_MSG = '@';
    private final char SYS_MSG = '%';

    // конструктор читателя сервера
    public ServerReader(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try (Scanner msgrFromServer = new Scanner(serverSocket.getInputStream())) {
            // в бесконечном цикле - ждём сообщение от сервера
            while (msgrFromServer.hasNext()) {
                String serverMsg = msgrFromServer.nextLine();
                // если сообщение начинается с @, значит это ПМ от кого-то - выводим без пометок.
                if (!serverMsg.isEmpty()) {
                    switch (serverMsg.charAt(0)) {
                        case PUB_MSG ->
                                System.out.println("\u001B[93m" + serverMsg.substring(1) + "\u001B[0m");                // green
                        case PRIV_FROM_MSG ->
                                System.out.println("\u001B[95m" + serverMsg.substring(1) + "\u001B[0m");                // pink
                        case SYS_MSG ->
                                System.out.println("\u001B[37m" + "Сервер : " + serverMsg.substring(1) + "\u001B[0m");   // grey
                        default -> System.out.println("\u001B[37m" + serverMsg + "\u001B[0m");
                    }
                }

            }
        } catch (IOException e) {
            System.err.println("SYSTEM : Ошибка при чтении с сервера: " + e.getMessage());
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("SYSTEM : Ошибка при отключении от сервера: " + e.getMessage());
        }
    }

}
