package org.tio.websocket.client.kit;

public class ObjKit {
  public static class Box<T> {
    public T value;

    public Box(T v) {
      value = v;
    }
  }

  public static  <T> Box<T> box(T obj) {
    return new Box<>(obj);
  }
}
