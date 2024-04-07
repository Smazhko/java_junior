package lesson5.client;

import lesson5.server.Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;

public class Client {

  public static void main(String[] args) {
    try {
      // создаём сервер-сокет - канал для связи с сервером. всю инфу отправляем туда
      Socket serverSocket = new Socket("localhost", Server.PORT);
      System.out.println("Подключение к серверу успешно (tcp://localhost:" + Server.PORT + ")");

      // Читаем с сервера приветственное сообщение
      Scanner msgFromServ = new Scanner(serverSocket.getInputStream());
      String msg = msgFromServ.nextLine();
      System.out.println("Сервер : " + msg);

      // Отправили идентификатор на сервер
      new PrintWriter(serverSocket.getOutputStream(), true).println(UUID.randomUUID());

      new Thread(new ServerReader(serverSocket)).start();
      new Thread(new ServerWriter(serverSocket)).start();
    } catch (IOException e) {
      throw new RuntimeException("Не удалось подключиться к серверу: " + e.getMessage(), e);
    }
  }

}



