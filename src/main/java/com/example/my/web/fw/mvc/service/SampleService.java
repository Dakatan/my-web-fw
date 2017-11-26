package com.example.my.web.fw.mvc.service;

import com.example.my.web.fw.annotation.BeanComponent;

@BeanComponent
public class SampleService {
  public String hello() {
    return "hello";
  }
}
