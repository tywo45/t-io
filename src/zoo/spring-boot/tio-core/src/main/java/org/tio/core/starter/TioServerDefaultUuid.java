package org.tio.core.starter;

import org.tio.core.intf.TioUuid;
import org.tio.utils.hutool.Snowflake;

/**
 * @author fanpan26
 * */
public class TioServerDefaultUuid implements TioUuid {
    private Snowflake snowflake;


    public TioServerDefaultUuid(long workerId, long dataCenterId) {
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
