package sem5_sockets.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

// существуют общепринятые (но необязательные) порты для различных сервисов
    // http -> 8080
    // https -> 443
    // smtp -> 25
    // ...

public class Server {
    public static final int PORT = 8181;

    public static void main(String[] args) {
        // создаём мапу, чтобы хранить список пользователей с их идентификаторами
        final Map<String, ClientHandler> clients = new HashMap<>();

        // созздаём сервер-сокет - канал, через который будет приниматься и отправляться вся инфо
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен на порту " + PORT + ".\nОжидаем подключения клиентов...");


            // создаём бесконечный цикл ожидания новых пользователей
            while (true) {
                try {
                    // создаём новый обычный сокет для каждого нового подключившегося пользователя
                    // таким образом создаётся мост: (сервер-сокет) <---> (сокет пользователя)
                    // на этой строке программа "замирает" в ожидании подключения - "слушает" порт 8181.
                    // как только новый клиент постучится, программа "размораживается" и выполняется далее по коду
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Подключился новый клиент: " + clientSocket);

                    // создаём писателя, который будет отправлять поток байт клиенту.
                    // autoflush - строка автоматом будет отправляться в тот момент, как она будет сформирована,
                    // а также буфер писателя будет автоматически очищаться.
                    PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
                    clientOut.println("Подключение успешно (ваш адрес: "
                            + clientSocket + "). Ожидание идентификатора...");

                    // ожидаем от пользователя его ID
                    Scanner clientIn = new Scanner(clientSocket.getInputStream());
                    String clientId = clientIn.nextLine();
                    System.out.println("Получение ID успешно! Сокет - " + clientSocket + ", ID - " + clientId);

                    // создаём нового обработчика клиента - он будет отдельным потоком,
                    // который будет общаться только со своим клиентом
                    ClientHandler clientHandler = new ClientHandler(clientSocket, clientId, clients);
                    new Thread(clientHandler).start();

                    // кладём нового клиента в список подключившихся
                    clients.put(clientId, clientHandler);
                } catch (IOException e) {
                    System.err.println("Произошла ошибка при взаимодействии с клиентом: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось начать прослушивать порт " + PORT, e);
        }
    }

}

