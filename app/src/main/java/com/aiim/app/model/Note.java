package com.aiim.app.model;

/* The following class builds a Note object when notes are created (Builder design pattern). Part of MVC design Pattern as model class.
 * Neil Campbell 06/01/2022, B00361078
 */

public class Note {
     String noteID;
     String author;
     String ticketRef;
     String noteMessage;
     String dateCreated;
   

    private Note() {}

    public static class Builder {
        public String noteID;
        public String author;
        public String ticketRef;
        public String noteMessage; 
        public String dateCreated; 

        public Builder setNoteID(String noteID) {
            this.noteID = noteID;
            return this;
        }

        public Builder setAuthor(String author) {
            this.author = author;
            return this;
        }

        public Builder setTicketRef(String ticketRef) {
            this.ticketRef = ticketRef;
            return this;
        }

        public Builder setNoteMessage(String noteMessage) {
            this.noteMessage = noteMessage;
            return this;
        }
        public Builder setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
            return this;
        }
        
        // methods for mapping to table view
        public String getNoteID() {
            return noteID;
          }
        public String getAuthor() {
            return author;
          }
        public String getTicketRef() {
            return ticketRef;
          }
        public String getNoteMessage() {
            return noteMessage;
          }
        public String getDateCreated() {
            return dateCreated;
          }
        
		public Note build() {
            Note note = new Note();
            setNoteID(this.noteID);
            setAuthor(this.author);
            setTicketRef(this.ticketRef);
            setNoteMessage(this.noteMessage);
            setDateCreated(this.dateCreated);
            return note;
        }
    }
}
