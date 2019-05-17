package org.tio.websocket.client.kit;

import java.lang.reflect.Field;

public class ReflectKit {
  public static void setField(Object target, String field, Object value)
      throws NoSuchFieldException, IllegalAccessException {
    Field field1 = target.getClass().getDeclaredField(field);
    field1.setAccessible(true);
    field1.set(target, value);
  }
}
