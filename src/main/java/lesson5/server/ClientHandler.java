package lesson5.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;


public class ClientHandler implements Runnable {

  private final Socket clientSocket;
  private final PrintWriter out;
  private final Map<String, ClientHandler> clients;

  public ClientHandler(Socket clientSocket, Map<String, ClientHandler> clients) throws IOException {
    this.clientSocket = clientSocket;
    this.out = new PrintWriter(clientSocket.getOutputStream(), true);
    this.clients = clients;
  }

  public Socket getClientSocket() {
    return clientSocket;
  }

  @Override
  public void run() {
    try (Scanner in = new Scanner(clientSocket.getInputStream())) {
      while (true) {
        if (clientSocket.isClosed()) {
          System.out.println("Клиент " + clientSocket + "отключился");
          break;
        }

        String input = in.nextLine();
        System.out.println("Получено сообщение от клиента " + clientSocket + ": " + input);

        String toClientId = null;
        if (input.startsWith("@")) {
          String[] parts = input.split("\\s+");
          if (parts.length > 0) {
            toClientId = parts[0].substring(1);
          }
        }

        if (toClientId == null) {
          clients.values().forEach(it -> it.send(input));
        } else {
          ClientHandler toClient = clients.get(toClientId);
          if (toClient != null) {
            toClient.send(input.replace("@" + toClientId + " ", ""));
          } else {
            System.err.println("Не найден клиент с идентфиикатором: " + toClientId);
          }
        }

        out.println("Cообщение [" + input + "] получено");
        if (Objects.equals("exit", input)) {
          System.out.println("Клиент отключился");
          break;
        }
      }
    } catch (IOException e) {
      System.err.println("Произошла ошибка при взаимодействии с клиентом " + clientSocket + ": " + e.getMessage());
    }

    // FIXME: При отключении клиента нужно удалять его из Map и оповещать остальных
    try {
      clientSocket.close();
    } catch (IOException e) {
      System.err.println("Ошибка при отключении клиента " + clientSocket + ": " + e.getMessage());
    }
  }

  public void send(String msg) {
    out.println(msg);
  }

}
