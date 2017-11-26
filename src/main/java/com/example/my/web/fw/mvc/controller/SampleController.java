package com.example.my.web.fw.mvc.controller;

import com.example.my.web.fw.annotation.BeanComponent;
import com.example.my.web.fw.annotation.Injection;
import com.example.my.web.fw.annotation.RequestPath;
import com.example.my.web.fw.mvc.service.SampleService;

@RequestPath("")
@BeanComponent
public class SampleController {

  @Injection
  private SampleService sampleService;

  @RequestPath("sample")
  public String doService() {
    return "<p>" + sampleService.hello() + "</p>";
  }
}
