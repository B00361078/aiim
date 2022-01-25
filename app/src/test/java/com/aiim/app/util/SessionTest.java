package com.aiim.app.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SessionTest {

	@Test
	void testIsSingleton() {
		Session s1 = Session.createSession();
		Session s2 = Session.createSession();
		assertEquals(s1, s2, "Singleton rule broken");
	}
}
