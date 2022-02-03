package com.aiim.app.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.aiim.app.model.Note.Builder;

class NoteTest {

	@Test
	void test() {
		Builder note = new Note.Builder()
			.setNoteID("TESTNOTE")
			.setNoteMessage("message")
			.setDateCreated("26/01/2022")
			.setTicketRef("TICKET1")
			.setAuthor("tstuser1");
		assertEquals("TESTNOTE", note.noteID);
	}
}
