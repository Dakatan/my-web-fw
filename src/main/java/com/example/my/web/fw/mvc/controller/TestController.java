package com.example.my.web.fw.mvc.controller;

import com.example.my.web.fw.annotation.BeanComponent;
import com.example.my.web.fw.annotation.Injection;
import com.example.my.web.fw.annotation.RequestPath;
import com.example.my.web.fw.mvc.service.SampleService;

@RequestPath("")
@BeanComponent
public class TestController {

  @Injection
  private SampleService sampleService;

  @RequestPath("test")
  public String doService() {
    return "<h1>" + sampleService.hello() + "</h1>";
  }
}
