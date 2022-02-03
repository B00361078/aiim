package com.aiim.app.util;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/* Unit test class for Session Singleton
 * Neil Campbell 14/12/2021, B00361078
 */

class SessionTest {
	
	private ResourceBundle strBundle;

	@BeforeEach
    public void setUp() {
        strBundle = ResourceBundle.getBundle("bundle");
	}

	@Test
	void testIsSingleton() {
		Session s1 = Session.createSession();
		Session s2 = Session.createSession();
		assertEquals(s1, s2, strBundle.getString("testError5"));
	}
}
