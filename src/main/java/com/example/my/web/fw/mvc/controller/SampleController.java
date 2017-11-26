package com.example.my.web.fw.mvc.controller;

import com.example.my.web.fw.annotation.BeanComponent;
import com.example.my.web.fw.annotation.Injection;
import com.example.my.web.fw.annotation.Path;
import com.example.my.web.fw.mvc.service.SampleService;

@Path("")
@BeanComponent
public class SampleController {

  @Injection
  private SampleService sampleService;

  @Path("sample")
  public String doService() {
    return "<p>" + sampleService.hello() + "</p>";
  }
}
