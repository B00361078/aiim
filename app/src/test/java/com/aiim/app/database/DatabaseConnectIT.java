package com.aiim.app.database;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DatabaseConnectIT {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@Test
	void testDBSingleton() { 
		Connection con1 = DatabaseConnect.getConnection();
		Connection con2 = DatabaseConnect.getConnection();
		assertSame(con1, con2);
	}

}
