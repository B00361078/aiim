package com.aiim.app.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

class AppUtilTest {
	
	private AppUtil appUtil;
	private ResourceBundle strBundle;
	
	@Before
    public void onceExecutedBeforeAll() {
        appUtil = new AppUtil();
        strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
    }
	
	@Test
	public void testGetModeResultForAssignMode() throws Exception {
		strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
		appUtil = new AppUtil();
		String mode = "assignMode";
		String result = appUtil.getMode(mode);
		assertTrue(result.contains("ON") | result.contains("OFF"), strBundle.getString("testError2"));
	}
	
	

}
