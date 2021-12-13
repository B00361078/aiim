package com.aiim.app.model;


/* The following class builds a Client object when clients are to be created (Builder design pattern). Part of MVC design Pattern as model class.
 * Neil Campbell 07/05/2021, B00361078
 */

public class Ticket {
     String ticketID;
     String status;
     String date;
     String assignedTeam;
   

    private Ticket() {}

    public static class Builder {
        public String ticketID;
        public String status;
        public String date;
        public String assignedTeam; 

        public Builder setTicketID(String ticketID) {
            this.ticketID = ticketID;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setDate(String date) {
            this.date = date;
            return this;
        }

        public Builder setAssignedTeam(String assignedTeam) {
            this.assignedTeam = assignedTeam;
            return this;
        }

        
        // methods for mapping to table view
        public String getTicketID() {
            return ticketID;
          }
        public String getStatus() {
            return status;
          }
        public String getDate() {
            return date;
          }
        public String getAssignedTeam() {
            return assignedTeam;
          }
        
		public Ticket build() {
            Ticket ticket = new Ticket();
            setTicketID(this.ticketID);
            setStatus(this.status);
            setDate(this.date);
            setAssignedTeam(this.assignedTeam);
            return ticket;
        }
    }

	public Object getInProgressState() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setStatus(Object inProgressState) {
		// TODO Auto-generated method stub
		
	}

	public Object getClosedState() {
		// TODO Auto-generated method stub
		return null;
	}
}
