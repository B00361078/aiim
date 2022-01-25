package com.aiim.app.database;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import org.junit.jupiter.api.Test;

class DatabaseConnectTest {
	
	@Test
	void testIsSingleton() {
		Connection dc1 = DatabaseConnect.getConnection();
		Connection dc2 = DatabaseConnect.getConnection();
		assertEquals(dc1, dc2, "Singleton rule broken");
	}
}
