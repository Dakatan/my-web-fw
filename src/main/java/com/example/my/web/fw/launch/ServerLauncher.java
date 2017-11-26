package com.example.my.web.fw.launch;

import com.example.my.web.fw.server.Server;

public class ServerLauncher {
  public static void main(String[] args) {
    Server server;
    if(args.length > 0) {
      int port = Integer.valueOf(args[0]);
      server = new Server(port);
    } else {
      server = new Server();
    }

    server.start();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop()));
  }
}
