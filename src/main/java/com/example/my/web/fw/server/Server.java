package com.example.my.web.fw.server;

import com.example.my.web.fw.annotation.Path;
import com.example.my.web.fw.di.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
  private static final int DEFAULT_PORT = 8080;
  private static final int DEFAULT_THREAD_POOL_SIZE = 10;
  private final int port;
  private final Map<String, HandleMethod> handleMap = new HashMap<>();
  private ServerSocket serverSocket;
  private ExecutorService executor = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

  public Server() {
    this(DEFAULT_PORT);
  }

  public Server(int port) {
    this.port = port;
    Context.getRegisteredBeanTypes().stream()
            .filter(c -> c.isAnnotationPresent(Path.class))
            .forEach(c -> {
              String rootPath = c.getAnnotation(Path.class).value();
              Arrays.stream(c.getMethods())
                      .filter(m -> m.isAnnotationPresent(Path.class))
                      .forEach(m -> {
                        String path = rootPath + "/" + m.getAnnotation(Path.class).value();
                        handleMap.put(path, new HandleMethod(c, m));
                      });
            });
  }

  public void start() {
    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    while (true) {
      try {
        Socket socket = serverSocket.accept();
        executor.submit(() -> handle(socket));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void handle(Socket socket) {
    try(BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
      String firstLine = br.readLine();
      String[] firstLineArray = firstLine.split(" ");
      String httpMethod = firstLineArray[0];
      String requestPath = firstLineArray[1];
      String protocol = firstLineArray[2];
      HandleMethod handler = handleMap.get(requestPath);

      for(String line = br.readLine(); line != null && !line.isEmpty(); line = br.readLine()) {}
      try(PrintWriter pw = new PrintWriter(socket.getOutputStream())) {
        try {
          handleInternal(pw, handler);
        } catch (InvocationTargetException | IllegalAccessException e) {
          pw.println("HTTP1.0 500 Internal Server Error");
          pw.println("Content-Type: text/html");
          pw.println();
          pw.println("505 Internal Server Error");
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void handleInternal(PrintWriter pw, HandleMethod handler) throws InvocationTargetException, IllegalAccessException {
    if(handler == null) {
      pw.println("HTTP/1.0 404 Not Found");
      pw.println("Content-Type: text/html");
      pw.println();
      pw.println("404 Not Found");
      return;
    }
    Object bean = Context.getBean(handler.getInstanceType());
    Object output = handler.getMethod().invoke(bean);
    pw.println("HTTP/1.0 200 OK");
    pw.println("Content-Type: text/html");
    pw.println();
    pw.println(output);
  }
}
