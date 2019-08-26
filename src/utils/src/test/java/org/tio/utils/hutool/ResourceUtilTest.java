package org.tio.utils.hutool;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link ResourceUtil} 单元测试
 * @author looly
 *
 */
public class ResourceUtilTest {

	@Test
	public void getResourceAsStreamTest() {
		InputStream resourceAsStream = ResourceUtil.getResourceAsStream("classpath:config/tio-quartz.properties");
		Assert.assertNotNull(resourceAsStream);
		try {
			resourceAsStream.close();
		} catch (IOException e) {
			//ignore
		}
	}
}
