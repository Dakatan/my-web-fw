package com.example.my.web.fw.server;

import org.junit.Ignore;
import org.junit.Test;

public class ServerTest {

  @Ignore
  @Test
  public void test1() {
    Server server = new Server(9999);
    server.start();
  }
}
