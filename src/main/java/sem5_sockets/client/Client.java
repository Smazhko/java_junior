package sem5_sockets.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;

public class Client {

  public static final int SERVER_PORT = 8181;
  public static final String SERVER_HOST = "localhost";

  public static void main(String[] args) {
    try {
      // Создаём сервер-сокет - канал для связи с сервером. всю инфу отправляем туда
      Socket serverSocket = new Socket(SERVER_HOST, SERVER_PORT);
      System.out.println("SYSTEM : Подключение к серверу (tcp://" + SERVER_HOST + ":" + SERVER_PORT + ") ...");

      // Читаем с сервера приветственное сообщение
      Scanner msgFromServ = new Scanner(serverSocket.getInputStream());
      String msg = msgFromServ.nextLine();
      System.out.println("\u001B[37mСервер : " + msg + "\u001B[0m");

      // Отправили сгенерированный идентификатор UUID на сервер с помощью безымянного PrintWriter
      UUID myId = UUID.randomUUID();
      System.out.println("SYSTEM : Отправляем серверу ID: " + myId);
      new PrintWriter(serverSocket.getOutputStream(), true).println(myId);

      // Создаём и запускаем два потока:
      // 1. чтобы слушать сервер,
      // 2. чтобы на него что-то отправлять
      new Thread(new ServerReader(serverSocket)).start();
      new Thread(new ServerWriter(serverSocket)).start();
    } catch (IOException e) {
      System.err.println("SYSTEM : Не удалось подключиться к серверу! Ошибка: " + e.getMessage());
      e.printStackTrace();
      System.out.println("Перезапустите приложение!");
      System.exit(0);
    }
  }

}



