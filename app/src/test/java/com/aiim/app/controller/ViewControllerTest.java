package com.aiim.app.controller;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/* Unit test class for ViewController Singleton
 * Neil Campbell 14/12/2021, B00361078
 */

class ViewControllerTest {
	
	private ResourceBundle strBundle;

	@BeforeEach
    public void setUp() {
        strBundle = ResourceBundle.getBundle("bundle");
	}

	@Test
	void testIsSingleton() {
		ViewController vc1 = ViewController.createInstance();
		ViewController vc2 = ViewController.createInstance();
		assertEquals(vc1, vc2, strBundle.getString("testError5"));
	}
}
