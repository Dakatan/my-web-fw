package com.example.my.web.fw.server;

import java.lang.reflect.Method;

public class HandleMethod {
  private final Class<?> instanceType;
  private final Method method;

  public HandleMethod(Class<?> instanceType, Method method) {
    this.instanceType = instanceType;
    this.method = method;
  }

  public Class<?> getInstanceType() {
    return instanceType;
  }

  public Method getMethod() {
    return method;
  }
}
