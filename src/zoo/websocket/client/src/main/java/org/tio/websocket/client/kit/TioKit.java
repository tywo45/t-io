package org.tio.websocket.client.kit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.PacketSendMode;
import org.tio.core.intf.Packet;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TioKit {
  private static Logger log = LoggerFactory.getLogger(TioKit.class);

  public static Boolean bSend(
    ChannelContext channelContext, Packet packet, int timeout, TimeUnit timeUnit) {
    if (channelContext == null) {
      return false;
    }
    CountDownLatch countDownLatch = new CountDownLatch(1);
    PacketSendMode packetSendMode = PacketSendMode.SINGLE_BLOCK;
    try {
      if (packet == null || channelContext == null) {
        if (countDownLatch != null) {
          countDownLatch.countDown();
        }
        return false;
      }

      if (channelContext.isVirtual) {
        if (countDownLatch != null) {
          countDownLatch.countDown();
        }
        return true;
      }

      if (channelContext.isClosed || channelContext.isRemoved) {
        if (countDownLatch != null) {
          countDownLatch.countDown();
        }
        if (channelContext != null) {
          log.info(
              "can't send data, {}, isClosed:{}, isRemoved:{}",
              channelContext,
              channelContext.isClosed,
              channelContext.isRemoved);
        }
        return false;
      }

      boolean isSingleBlock =
          countDownLatch != null && packetSendMode == PacketSendMode.SINGLE_BLOCK;

      boolean isAdded = false;
      if (countDownLatch != null) {
        Packet.Meta meta = new Packet.Meta();
        meta.setCountDownLatch(countDownLatch);
        packet.setMeta(meta);
      }

      if (channelContext.tioConfig.useQueueSend) {
        isAdded = channelContext.sendRunnable.addMsg(packet);
      } else {
        isAdded = channelContext.sendRunnable.sendPacket(packet);
      }

      if (!isAdded) {
        if (countDownLatch != null) {
          countDownLatch.countDown();
        }
        return false;
      }
      if (channelContext.tioConfig.useQueueSend) {
        channelContext.sendRunnable.execute();
      }

      if (isSingleBlock) {
        try {
          Boolean awaitFlag = countDownLatch.await(timeout, timeUnit);
          if (!awaitFlag) {
            log.error(
                "{}, 阻塞发送超时, timeout:{}s, packet:{}",
                channelContext,
                timeUnit.toSeconds(timeout),
                packet.logstr());
          }
        } catch (InterruptedException e) {
          log.error(e.toString(), e);
        }

        Boolean isSentSuccess = packet.getMeta().getIsSentSuccess();
        return isSentSuccess;
      } else {
        return true;
      }
    } catch (Throwable e) {
      log.error(channelContext + ", " + e.toString(), e);
      return false;
    }
  }
}
