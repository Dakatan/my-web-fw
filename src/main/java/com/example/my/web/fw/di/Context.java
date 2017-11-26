package com.example.my.web.fw.di;

import com.example.my.web.fw.annotation.Injection;
import com.example.my.web.fw.annotation.BeanComponent;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Context {
  private static final Map<String, Class<?>> typeMap = new HashMap<>();
  private static final Map<Class<?>, Object> beanMap = new HashMap<>();

  static {
    autoRegister();
    autoInjection();
  }

  public static Collection<Class<?>> getRegisteredBeanTypes() {
    return beanMap.keySet();
  }

  public static Object getBean(String name) {
    Class<?> clazz = typeMap.get(name);
    if(clazz == null) {
      throw new NullPointerException("Class is null.");
    }
    Object cache = beanMap.get(clazz);
    if(cache != null) {
      return cache;
    }
    try {
      Object instance = clazz.newInstance();
      beanMap.put(clazz, instance);
      return instance;
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T getBean(Class<T> clazz) {
    Object instance = beanMap.get(clazz);
    if(instance != null) {
      return (T) instance;
    }
    return (T) getBean(clazz.getName());
  }

  private static void register(String name, Class<?> clazz) {
    if(name == null || clazz == null) {
      throw new RuntimeException("name or class is null.");
    }
    if(typeMap.containsKey(name) || beanMap.containsKey(clazz)) {
      throw new RuntimeException("bean already registered.");
    }

    Object bean;
    try {
      bean = clazz.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    typeMap.put(name, clazz);
    beanMap.put(clazz, bean);
  }

  private static void register(Class<?> clazz) {
    if(clazz == null) {
      throw new NullPointerException("clazz is null");
    }
    String className = clazz.getName();
    register(className.substring(0, 1).toLowerCase() + className.substring(1), clazz);
  }

  private static void autoRegister() {
    URL url = Context.class.getResource("/" + Context.class.getName().replace(".", "/") + ".class");
    Path classPath;
    try {
       classPath = new File(url.toURI()).toPath().resolve("../../../../../../..");
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
    try {
      Files.walk(classPath)
              .filter(p -> !Files.isDirectory(p))
              .filter(p -> p.toString().endsWith(".class"))
              .map(p -> classPath.relativize(p))
              .map(p -> p.toString().replace(File.separatorChar, '.'))
              .map(s -> s.substring(0, s.length() - 6))
              .forEach(s -> {
                try {
                  Class<?> clazz = Class.forName(s);
                  if(clazz.isAnnotationPresent(BeanComponent.class)) {
                    register(clazz);
                  }
                } catch (ClassNotFoundException e) {
                  throw new RuntimeException(e);
                }
              });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void inject(Class<?> clazz) {
    Object instance = getBean(clazz);
    injectInternal(instance);
  }

  private static void autoInjection() {
    for(Class<?> clazz : beanMap.keySet()) {
      inject(clazz);
    }
  }

  private static void injectInternal(Object instance) {
    Arrays.stream(instance.getClass().getDeclaredFields())
            .filter(f -> f.isAnnotationPresent(Injection.class))
            .forEach(f -> {
              f.setAccessible(true);
              try {
                f.set(instance, getBean(f.getType()));
              } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
              }
            });
  }
}
