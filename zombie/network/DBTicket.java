// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

public class DBTicket
{
    private String author;
    private String message;
    private int ticketID;
    private boolean viewed;
    private DBTicket answer;
    private boolean isAnswer;
    
    public DBTicket(final String author, final String message, final int ticketID) {
        this.author = null;
        this.message = "";
        this.ticketID = 0;
        this.viewed = false;
        this.answer = null;
        this.isAnswer = false;
        this.author = author;
        this.message = message;
        this.ticketID = ticketID;
        this.viewed = this.viewed;
    }
    
    public String getAuthor() {
        return this.author;
    }
    
    public void setAuthor(final String author) {
        this.author = author;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
    
    public int getTicketID() {
        return this.ticketID;
    }
    
    public void setTicketID(final int ticketID) {
        this.ticketID = ticketID;
    }
    
    public boolean isViewed() {
        return this.viewed;
    }
    
    public void setViewed(final boolean viewed) {
        this.viewed = viewed;
    }
    
    public DBTicket getAnswer() {
        return this.answer;
    }
    
    public void setAnswer(final DBTicket answer) {
        this.answer = answer;
    }
    
    public boolean isAnswer() {
        return this.isAnswer;
    }
    
    public void setIsAnswer(final boolean isAnswer) {
        this.isAnswer = isAnswer;
    }
}
