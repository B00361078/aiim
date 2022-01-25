package com.aiim.app.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ViewControllerTest {

	@Test
	void testIsSingleton() {
		ViewController vc1 = ViewController.createInstance();
		ViewController vc2 = ViewController.createInstance();
		assertEquals(vc1, vc2, "Singleton rule broken");
	}
}
