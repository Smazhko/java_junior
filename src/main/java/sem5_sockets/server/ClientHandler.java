package sem5_sockets.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

// этот класс обеспечивает связь сервера с ОДНИМ клиентом в отдельном потоке.
// этот класс и есть поток
public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final String clientId;
    private final PrintWriter msgrToClient;          // поток данных на клиента
    private final Map<String, ClientHandler> clients; // список всех клиентов
    private final char PUB_MSG_PRFX = '$';
    private final char PRIV_MSG_PRFX = '@';
    private final char SYS_MSG_PRFX = '%';

    // Конструктор. Кидает IOException потому что тут заводим новый PrinterWriter
    // у клиентского сокета есть методы - getInputStream и getOutputStream - для входящих и исходящих потоков
    public ClientHandler(Socket clientSocket, String clientId, Map<String, ClientHandler> clients) throws IOException {
        this.clientSocket = clientSocket;
        this.clientId = clientId;
        this.msgrToClient = new PrintWriter(clientSocket.getOutputStream(), false);
        this.clients = clients;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    @Override
    public void run() {
        // отправляем пользователю список подключенных клиентов
        send(SYS_MSG_PRFX + "Список доступных клиентов:" + getAllClients());

        // оповещаем всех о подключении нового клиента
        if (clients.size() > 1)
            sendAll(SYS_MSG_PRFX + "Подключился новый клиент: " + clientSocket + ", id = " + clientId, false);

        try (Scanner in = new Scanner(clientSocket.getInputStream())) {
            // в бесконечном цикле ожидаем сообщений от клиента
            while (true) {
                // если сокет клиента закрыт, то выводим сообщение
                if (clientSocket.isClosed()) {
                    break;
                }

                // ожидаем сообщения от клиента
                if (in.hasNext()){
                    String clientMsg = in.nextLine();
                    System.out.println("Получено сообщение от клиента " + clientId + ": " + clientMsg);

                    // если сообщение от клиента /exit, то разрываем коннект
                    if (clientMsg.equals("/exit")) {
                        break;
                    }

                    if (clientMsg.equals("/all")) {
                        send(SYS_MSG_PRFX + "Список доступных клиентов:" + getAllClients());
                        continue;
                    }

                    // если сообщение было помечено "КОМУ" через @ (PRIV_MSG) -
                    // разбиваем строку по пробелам и берём первое слово, которое содержит UUID
                    String toClientId = null;
                    if (clientMsg.charAt(0) == PRIV_MSG_PRFX) {
                        String[] parts = clientMsg.split("\\s+");
                        if (parts.length > 0) {
                            toClientId = parts[0].substring(1);
                        }
                    }

                    // Отправляем сообщение:
                    // Если адресата нет, то отправляем всем, в том числе и себе.
                    // Если адресат есть, то пытаемся получить поток адресата из мапы.
                    // Если в мапе такой адресат есть, то
                    // удаляем из сообщения @ + КОМУ, чтобы осталось только сообщение,
                    // и вызываем на потоке адресата send(АДРЕС + СООБЩЕНИЕ)
                    // также дублируем ПМ сообщение на свой поток, чтоб оно оформленное вылезло в консоли
                    if (toClientId == null) {
                        System.out.println("Рассылка ВСЕМ от " + clientId + ": " + clientMsg);
                        sendAll(PUB_MSG_PRFX + clientId + " : " + clientMsg, true);
                    } else {
                        ClientHandler toClient = clients.get(toClientId);
                        if (toClient != null) {
                            String pm = clientMsg.replace(PRIV_MSG_PRFX + toClientId + " ", "");
                            System.out.println("Отправляем ЛС: " + clientId + " -> " + toClientId + ": " + clientMsg);
                            toClient.send(PRIV_MSG_PRFX + clientId + ": " + pm);
                            send(PRIV_MSG_PRFX + "-> " + toClientId + ": " + pm);
                        } else {
                            System.err.println("Не найден клиент с идентификатором: " + toClientId);
                        }
                    }

                    send(SYS_MSG_PRFX + "Сообщение [" + clientMsg + "] отправлено.");

                }
                else break;
            }
        } catch (IOException e) {
            System.err.println("Произошла ошибка при взаимодействии с клиентом " + clientSocket + ": " + e.getMessage());
        }


        // отключаем клиента
        // удаляем клиента из мапы
        // выводим лог
        // сообщаем всем, что клиент отключился
        // отправляем всем список доступных клиентов
        try {
            clientSocket.close();
            clients.remove(clientId);
            System.out.println("Клиент " + clientSocket + " отключился.");
            sendAll(SYS_MSG_PRFX + "Клиент отключился: " + clientSocket + ", id = " + clientId, false);
            sendAll(SYS_MSG_PRFX + "Обновлённый список доступных клиентов:" + getAllClients(), false);
        } catch (IOException e) {
            System.err.println("Ошибка при отключении клиента " + clientSocket + ": " + e.getMessage());
        }
    }

    // получаем строку - список доступных клиентов, включая самого клиента
    private String getAllClients() {
        if (clients.size() == 1)
            return " пуст. Вы - единственный!";
        else
            return "\n" + clients.entrySet().stream()
                        //.filter(entry -> !entry.getKey().equals(clientId)) // исключить себя
                        .map(it -> "         id = " + it.getKey() + ", client = " + it.getValue().getClientSocket())
                        .collect(Collectors.joining("\n"));
    }

    public void send(String msg) {
        msgrToClient.println(msg);
        msgrToClient.flush();
    }

    public void sendAll(String msg, boolean selfSend) {
        for (ClientHandler client : clients.values()) {
            if (!selfSend && client.equals(this)) continue;
            client.send(msg);
        }
    }

}
