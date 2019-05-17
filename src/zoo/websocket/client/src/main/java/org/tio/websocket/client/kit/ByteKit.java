package org.tio.websocket.client.kit;

import java.util.Arrays;

public class ByteKit {
  public static byte[][] split(byte[] raw, int partSize) {
    int length =
        raw.length % partSize == 0 ? raw.length / partSize : ((int) (raw.length / partSize)) + 1;
    byte[][] parts = new byte[length][];
    int start = 0;
    for (int i = 0; i < length; i++) {
      int end = Integer.min(raw.length, start + partSize);
      parts[i] = Arrays.copyOfRange(raw, start, end);
      start = end;
    }
    return parts;
  }
}
