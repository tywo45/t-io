package org.tio.utils.thread.pool;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu
 */
public class TioCallerRunsPolicy extends CallerRunsPolicy {
	private static Logger log = LoggerFactory.getLogger(TioCallerRunsPolicy.class);

	public TioCallerRunsPolicy() {
	}

	public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
		log.error(r.getClass().getSimpleName());
		super.rejectedExecution(r, e);
	}

}
