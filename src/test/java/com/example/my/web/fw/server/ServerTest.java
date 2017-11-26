package com.example.my.web.fw.server;

import com.example.my.web.fw.di.Context;
import org.junit.Ignore;
import org.junit.Test;

public class ServerTest {

  @Ignore
  @Test
  public void test1() {
    Context.init();
    Server server = new Server(9999);
    server.start();
  }
}
