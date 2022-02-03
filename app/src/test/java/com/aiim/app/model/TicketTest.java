package com.aiim.app.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.aiim.app.model.Ticket.Builder;

/* Unit test class for Ticket Builder
 * Neil Campbell 14/12/2021, B00361078
 */

class TicketTest {

	@Test
	void test() {
		Builder ticket = new Ticket.Builder()
				.setTicketID("TESTTICKET")
	    		.setStatus("Raised")
	    		.setDate("26/01/2022")
	    		.setAssignedTeam("TEAM1000");
		
		assertEquals("TESTTICKET", ticket.ticketID);
	}

}
