package example;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.tio.utils.hutool.FileUtil;
import org.tio.websocket.client.WebSocket;
import org.tio.websocket.client.WsClient;
import org.tio.websocket.client.config.WsClientConfig;
import org.tio.websocket.common.WsPacket;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

class EchoClient {

  static class SentItem {
    boolean recv;
    long sendAt;
    long recvAt;

    public SentItem(boolean recv, long sendAt, long recvAt) {
      this.recv = recv;
      this.sendAt = sendAt;
      this.recvAt = recvAt;
    }
  }

  // 简单基准测试
  public static void main(String[] args) throws Exception {
    int total = 1000;
    int msgCount=100;

    List<WebSocket> wsList = new ArrayList<>(total);
    List<ConcurrentHashMap<Long, SentItem>> sentList = new ArrayList<>(total);

    Subject<Object> complete = PublishSubject.create().toSerialized();

    complete
        .buffer(total)
        .subscribe(
            x -> {
              List<SentItem> sentItemList =
                  sentList.stream()
                      .flatMap(sent -> sent.values().stream())
                      .collect(Collectors.toList());
              Boolean all =
                  sentItemList.stream().reduce(true, (p, item) -> p && item.recv, (a, b) -> b);
              if (all) {
                System.out.println("all ok");
              }
              Callable<LongStream> durationStream =
                  () ->
                      sentItemList.stream()
                          .filter(item -> item.recv)
                          .mapToLong(item -> (item.recvAt - item.sendAt));
              System.out.printf("成功：%d\n", durationStream.call().count());
              System.out.printf("最大时差：%d\n", durationStream.call().max().getAsLong());
              System.out.printf("最小时差：%d\n", durationStream.call().min().getAsLong());
              System.out.printf("平均时差：%.2f\n", durationStream.call().average().getAsDouble());
            });

    //    ExecutorService pool = Executors.newFixedThreadPool(100);
    //    List<Future<?>> submitList = new ArrayList<>();

    for (int i = 0; i < total; i++) {
      int finalI = i;
      //      Future<?> submit =
      //          pool.submit(
      //              () -> {
      WsClient client = null;
      try {
        client = WsClient.create("ws://127.0.0.1:7777");
        WebSocket ws = client.getWs();
        ConcurrentHashMap<Long, SentItem> sent = new ConcurrentHashMap<>();
        ws.addOnOpen(
            e -> {
              Observable.interval(20, TimeUnit.MILLISECONDS)
                  .take(msgCount)
                  .subscribe(
                      n -> {
                        sent.put(n, new SentItem(false, System.currentTimeMillis(), -1));
                        ws.send(String.valueOf(n));
                      },
                      err -> {
                        err.printStackTrace();
                        System.out.println("error close No." + finalI);
                        ws.close(1002, err.toString());
                      },
                      () -> {
                        System.out.println("close No." + finalI);
                        ws.close();
                        Observable.timer(3, TimeUnit.SECONDS)
                            .subscribe(
                                x -> {
                                  System.out.printf(
                                      "\t\t\t\tNo.%d conn count: %d\n",
                                      finalI, WsClient.clientCount());
                                });
                      });
            });
        ws.addOnMessage(
            e -> {
              long n = Long.parseLong(e.data.getWsBodyText());
              sent.compute(
                  n,
                  (k, v) -> {
                    v.recv = true;
                    v.recvAt = System.currentTimeMillis();
                    return v;
                  });
            });
        ws.addOnClose(
            e -> {
              complete.onNext(new Object());
            });
        ws.addOnThrows(
            e -> {
              e.printStackTrace();
            });
        ws.connect();
        wsList.add(ws);
        sentList.add(sent);
      } catch (Exception e) {
        e.printStackTrace();
      }
      //              });
      //      submitList.add(submit);
    }
    //
    //    for (Future<?> future : submitList) {
    //      future.get();
    //    }
  }

  public static void main4(String[] args) throws Exception {
    WsClient client =
        WsClient.create(
            "ws://127.0.0.1:7777?id=77",
            new WsClientConfig(
                e -> {
                  System.out.println("on open");
                },
                e -> {
                  System.out.println(String.format("on message: %s", e.data));
                },
                e -> {
                  System.out.println(
                      String.format("on close: %d %s %s", e.code, e.reason, e.wasClean));
                },
                e -> {
                  System.out.println(String.format("on error: %s", e.msg));
                },
                e -> {
                  e.printStackTrace();
                }));
    WebSocket ws = client.connect();
    ws.addOnOpen(
        e -> {
          Observable.timer(5, TimeUnit.SECONDS)
              .subscribe(
                  (i) -> {
                    System.out.println("close");
                    ws.close(1001, "user has leave");
                  });
        });
    ws.addOnClose(
        e -> {
          String s = e.code + e.reason;
        });
  }

  // echo 大 byte array
  public static void main3(String[] args) throws Exception {
    int len = 1024 * 899 * 15;
    ByteBuffer buf = ByteBuffer.allocate(len);
    for (int i = 0; i < len; i++) {
      buf.put((byte) (Byte.MAX_VALUE * Math.random()));
    }
    WsClient client =
        WsClient.create(
            "ws://127.0.0.1:7777?id=77",
            new WsClientConfig(
                null,
                e -> {
                  byte[] ret = e.data.getBody();
                  boolean equals = Arrays.equals(buf.array(), ret);
                  System.out.println(equals ? "ok" : "fail");
                },
                null,
                null,
                e -> {
                  e.printStackTrace();
                }));
    WebSocket ws = client.connect();
    ws.send(buf);
  }

  // echo，10次，917KB大文本，次次加一份，大文本测试
  public static void main2(String[] args) {
    try {
      String text = FileUtil.readString(FileUtil.file("E:\\镇魂.txt"));
      int size = 10;
      Map<Integer, String> sent = new ConcurrentHashMap<>();
      Map<Integer, String> recv = new ConcurrentHashMap<>();
      WsClient echo =
          WsClient.create(
              "ws://127.0.0.1:7777/echo",
              new WsClientConfig(
                  e -> {
                    System.out.println("emit open");
                  },
                  e -> {
                    WsPacket data = e.data;
                    int i = Integer.parseInt(data.getWsBodyText().split("/")[0]);
                    System.out.println("recv No." + i);
                    recv.put(i, data.getWsBodyText());
                    if (i == size - 1) {
                      boolean all = true;
                      for (int j = 0; j < size; j++) {
                        all = all && sent.get(j).equals(recv.get(j));
                      }
                      if (all) {
                        System.out.println("All sent success.");
                      } else {
                        System.out.println("All sent fail.");
                      }
                    }
                  },
                  e -> {
                    System.out.println(
                        String.format("emit close: %d, %s, %s", e.code, e.reason, e.wasClean));
                  },
                  e -> {
                    System.out.println(String.format("emit error: %s", e.msg));
                  },
                  Throwable::printStackTrace));
      WebSocket ws = echo.connect();

      for (int i = 0; i < size; i++) {
        StringBuilder sb = new StringBuilder(i + "/" + text);
        for (int j = 0; j < i; j++) {
          sb.append(text);
        }
        ws.send(sb.toString());
        sent.put(i, sb.toString());
        System.out.println("send No." + i);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // echo, 10_0000条短文本测试
  public static void main1(String[] args) {
    try {
      Map<Integer, Boolean> sent = new ConcurrentHashMap<>();
      int total = 100000;

      Subject<Object> complete = PublishSubject.create().toSerialized();
      complete
          .buffer(total)
          .subscribe(
              x -> {
                Boolean all = sent.values().stream().reduce(true, (p, c) -> p && c);
                if (all) {
                  System.out.println("All sent success! ");
                }
              });

      WsClient echo =
          WsClient.create(
              "wss://localhost/echo.ws",
              new WsClientConfig(
                  e -> {
                    System.out.println("emit open");
                  },
                  e -> {
                    WsPacket data = e.data;
                    int i = Integer.parseInt(data.getWsBodyText());
                    sent.put(i, true);
                    System.out.println("recv " + i);
                    complete.onNext(i);
                  },
                  e -> {
                    System.out.println(
                        String.format("emit close: %d, %s, %s", e.code, e.reason, e.wasClean));
                  },
                  e -> {
                    System.out.println(String.format("emit error: %s", e.msg));
                  },
                  e -> {
                    e.printStackTrace();
                  }));
      WebSocket ws = echo.connect();

      for (int i = 0; i < total; i++) {
        ws.send(String.format("%d", i));
        sent.put(i, false);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
