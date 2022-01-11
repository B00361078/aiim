package com.aiim.app.util;

import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ntp.TimeStamp;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;

import com.aiim.app.model.DataSetIter;
import com.aiim.app.model.Network;

import javafx.concurrent.Task;

class AppUtilTest {
	
	private AppUtil appUtil;
	private ResourceBundle strBundle;
	private String currentDirectory;
	
	@BeforeEach
    public void setUp() {
        appUtil = new AppUtil();
        strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
        currentDirectory = Paths.get("").toAbsolutePath().toString();
	}
	
	@Test
	public void testGetModeResultForAssignMode() throws Exception {
		String mode = "assignMode";
		String result = appUtil.getMode(mode);
		assertTrue(result.contains("ON") | result.contains("OFF"), strBundle.getString("testError2"));
	}
	
	@Test
	public void testGetModeResultForTrainMode() throws Exception {
		String mode = "trainMode";
		String result = appUtil.getMode(mode);
		assertTrue(result.contains("ON") | result.contains("OFF"), strBundle.getString("testError2"));
	}
	
	@Test
	public void testIsAutoAssignedPos() {
		String prediction = "General";
		int expectedResult = 0;
		int result = appUtil.isAutoAssigned(prediction);
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void testIsAutoAssignedNeg() {
		String prediction = "Guidewire";
		int expectedResult = 0;
		System.out.println(appUtil);
		int result = appUtil.isAutoAssigned(prediction);
		assertNotEquals(expectedResult, result);
	}
	
	 private class TestTask extends Task {

		private TestTask() {;
        }

        @Override
        protected String call() throws Exception {
            return null;
        }
	}
	
	@Test
	public void testStartThread() {
		String name = "testThread";
		Task task = new TestTask();
		Thread testThread = appUtil.startThread(task, name);
		assertTrue(testThread.isAlive(), strBundle.getString("testError3"));
		testThread.interrupt();
	}
	
	@Test
	public void testGetDate() {
		Timestamp timestamp = appUtil.getDate();
		assertSame(strBundle.getString("testError4"), timestamp.getClass(), Timestamp.class);
	}
	
	@Test
	public void testAppendToFile() throws IOException {
		String filename = "test.txt";
		String details = "This is a test.";
		appUtil.appendToFile(filename, details);
		File file = new File(currentDirectory+"/files/"+filename);
		assertTrue(file.length() > 0);
		FileUtils.forceDelete(FileUtils.getFile(currentDirectory+"/files/"+filename));
	}
	
	
	
	

}
