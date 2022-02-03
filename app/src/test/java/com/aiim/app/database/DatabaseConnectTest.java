package com.aiim.app.database;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.util.ResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/* Unit test class for DatabaseConnect Singleton
 * Neil Campbell 14/12/2021, B00361078
 */

class DatabaseConnectTest {
	
	private ResourceBundle strBundle;

	@BeforeEach
    public void setUp() {
        strBundle = ResourceBundle.getBundle("bundle");
	}
	
	@Test
	void testIsSingleton() {
		Connection dc1 = DatabaseConnect.getConnection();
		Connection dc2 = DatabaseConnect.getConnection();
		assertEquals(dc1, dc2, strBundle.getString("testError5"));
	}
}
