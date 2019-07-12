package org.tio.utils.quartz;

import java.util.HashMap;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.SystemTimer;
import org.tio.utils.hutool.DateUtil;

/**
 * @author tanyaowu 
 * 2017年10月8日 下午4:58:34
 */
public abstract class AbstractJobWithLog implements Job {
	private Logger log = LoggerFactory.getLogger(AbstractJobWithLog.class);

	protected int runCount = 0;

	private static Map<Class<?>, Integer> map = new HashMap<>();

	private Integer getAndAddRunCount(Class<?> clazz) {
		Integer ret = map.get(clazz);
		if (ret == null) {
			map.put(clazz, 1);
			runCount = 1;
			return 1;
		} else {
			ret++;
			map.put(clazz, ret);
			runCount = ret;
			return ret;
		}
	}

	/**
	 * 
	 * @author: tanyaowu
	 */
	public AbstractJobWithLog() {
		log = LoggerFactory.getLogger(AbstractJobWithLog.class);
	}

	/** 
	 * @param context
	 * @throws JobExecutionException
	 * @author: tanyaowu
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		getAndAddRunCount(this.getClass());
		log.info("第{}次执行定时任务[{}]， 本次id:{}, 本次执行时间:{}, 上次执行时间:{}, 下次执行时间:{}", runCount, this.getClass().getName(), context.getFireInstanceId(),
				DateUtil.formatDateTime(context.getFireTime()), DateUtil.formatDateTime(context.getPreviousFireTime()), DateUtil.formatDateTime(context.getNextFireTime()));
		long start = SystemTimer.currTime;
		try {
			run(context);
		} catch (JobExecutionException e) {
			throw e;
		} catch (Throwable e) {
			log.error(e.toString(), e);
		}
		long end = SystemTimer.currTime;
		long iv = end - start;
		log.info("第{}次执行定时任务[{}]完毕， 本次id:{}, 本次执行时间:{}, 耗时:{}ms", runCount, this.getClass().getName(), context.getFireInstanceId(), DateUtil.formatDateTime(context.getFireTime()),
		        iv);
	}

	/**
	 * 在这里写上业务逻辑
	 * @param context
	 * @throws Exception
	 * @author: tanyaowu
	 */
	public abstract void run(JobExecutionContext context) throws Exception;

}
