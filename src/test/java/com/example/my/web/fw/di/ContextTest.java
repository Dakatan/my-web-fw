package com.example.my.web.fw.di;

import com.example.my.web.fw.mvc.controller.SampleController;
import org.junit.Test;

public class ContextTest {

  @Test
  public void test1() {
    SampleController controller = Context.getBean(SampleController.class);
    String str = controller.doService();
    System.out.println(str);
  }
}
