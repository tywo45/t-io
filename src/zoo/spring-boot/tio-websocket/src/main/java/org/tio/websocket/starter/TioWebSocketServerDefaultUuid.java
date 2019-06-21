package org.tio.websocket.starter;

import org.tio.core.intf.TioUuid;
import org.tio.utils.hutool.Snowflake;

/**
 * @author fanpan26
 * */
public class TioWebSocketServerDefaultUuid implements TioUuid {
    private Snowflake snowflake;


    public TioWebSocketServerDefaultUuid(long workerId, long dataCenterId) {
        snowflake = new Snowflake(workerId, dataCenterId);
    }

    /**
     * @return new uuid
     * @author tanyaowu
     */
    @Override
    public String uuid() {
        return snowflake.nextId() + "";
    }
}
