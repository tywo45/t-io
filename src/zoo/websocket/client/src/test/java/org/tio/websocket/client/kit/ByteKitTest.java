package org.tio.websocket.client.kit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ByteKitTest {

  @Test
  public void split() {
    byte[] bytes1 = new byte[12781];
    for (int i = 0; i < bytes1.length; i++) {
      bytes1[i] = (byte) (256 * Math.random());
    }

    byte[][] parts1 = ByteKit.split(bytes1, 1);
    assertEquals(parts1.length, bytes1.length);
    Byte[] combine1 = Arrays.stream(parts1).map(it -> it[0]).toArray(Byte[]::new);
    for (int i = 0; i < combine1.length; i++) {
      assertEquals((byte) combine1[i], bytes1[i]);
    }

    byte[][] parts2 = ByteKit.split(bytes1, 11);
    assertEquals(
        parts2.length, bytes1.length % 11 == 0 ? bytes1.length / 11 : bytes1.length / 11 + 1);
    Byte[] combine2 =
        Arrays.stream(parts2)
            .flatMap(
                part -> {
                  List<Byte> bytes = new ArrayList<>();
                  for (byte b : part) {
                    bytes.add(b);
                  }
                  return bytes.stream();
                })
            .toArray(Byte[]::new);
    for (int i = 0; i < combine2.length; i++) {
      assertEquals((byte) combine2[i], bytes1[i]);
    }
  }
}
