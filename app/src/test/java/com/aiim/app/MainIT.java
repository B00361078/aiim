package com.aiim.app;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.ResourceBundle;

import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import com.aiim.app.controller.ViewController;
import com.aiim.app.view.ViewNames;

import javafx.scene.Node;
import javafx.stage.Stage;

@TestMethodOrder(OrderAnnotation.class)

class MainIT extends ApplicationTest {
	
	private String SYSADMINUSER;
	private String SYSADMINPASS;
	private ResourceBundle strBundle = ResourceBundle.getBundle("bundle");
	private String AGENTUSER;
	private String AGENTPASS;
	private String OWNERUSER;
	private String OWNERPASS;
	
	@BeforeEach
	public void setup() {
		SYSADMINUSER = "tstuser3";
		SYSADMINPASS = "testing3";
		OWNERUSER = "tstuser2";
		OWNERPASS = "testing2";
		AGENTUSER = "tstuser1";
		AGENTPASS = "testing1";
    }
	
	@Override
	public void start (Stage stage) throws Exception {
		ViewController.createInstance().setCurrentStage(stage);
		ViewController.createInstance().switchToView(ViewNames.LOGIN);
	}
	
	@Order(1)
	@Test void testSwitchToLoginPage() throws IOException {
		assertEquals("/com/aiim/app/view/login.fxml", ViewController.createInstance().getResource(), strBundle.getString("testError1"));
	}
	
	@Order(2)
	@Test
	public void testSuccessfulLoginLogout() throws IOException {
	    clickOn("#usernameField").write(SYSADMINUSER);
	    clickOn("#passwordField").write(SYSADMINPASS);
	    clickOn("#loginBtn");
	    assertEquals("/com/aiim/app/view/dashboard.fxml", ViewController.createInstance().getResource(), strBundle.getString("testError1"));
	    clickOn("#menuButton");
	    clickOn("#menuItemLogout");
	    assertEquals("/com/aiim/app/view/login.fxml", ViewController.createInstance().getResource(), strBundle.getString("testError1"));
	}
	
	@Order(3)
	@Test
	public void testUnSuccessfulLogin() throws IOException, InterruptedException {
	    clickOn("#usernameField").write(SYSADMINUSER+"x");
	    clickOn("#passwordField").write(SYSADMINPASS);
	    clickOn("#loginBtn");
	    FxAssert.verifyThat("OK", NodeMatchers.isVisible());
	}
	
	@Order(4)
	@Test
	public void testSettingsAccessible() throws IOException, InterruptedException {
	    clickOn("#usernameField").write(SYSADMINUSER);
	    clickOn("#passwordField").write(SYSADMINPASS);
	    clickOn("#loginBtn");
	    clickOn("#settingsBtn");
	    clickOn("#assignOn");
	    FxAssert.verifyThat("OK", NodeMatchers.isVisible());
	    clickOn("Cancel");
	    //clickOn("OK");
	    clickOn("#backBtn");
	    clickOn("#radioRaised");
	    clickOn("#radioProg");
	    clickOn("#radioClosed");
	    clickOn("#menuButton");
	    clickOn("#menuItemLogout");
	}
	
	@Order(5)
	@Test
	public void testRaiseIncidentAccessible() throws IOException, InterruptedException {
	    clickOn("#usernameField").write(AGENTUSER);
	    clickOn("#passwordField").write(AGENTPASS);
	    clickOn("#loginBtn");
	    clickOn("#raiseNewBtn");
	    clickOn("#details").write("testing here");
	    clickOn("#cancelBtn");
	    clickOn("#menuButton");
	    clickOn("#menuItemLogout");
	}
	
	@Order(7)
	@Test
	public void testAmendTicketTable() throws IOException, InterruptedException {
		clickOn("#usernameField").write(OWNERUSER);
	    clickOn("#passwordField").write(OWNERPASS);
	    clickOn("#loginBtn");
	    doubleClickOn("#ticketTable");
	    clickOn("#menuButton");
	    clickOn("#menuItemLogout");
	}
	
	public <T extends Node> T lookupById(final String controlId) {
	    return (T) lookup(controlId).queryAll().iterator().next();
	}
	
}
