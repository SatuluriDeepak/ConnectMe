package models;

public class ModelChat {
    String Message,Senderid,Receiver,TimeStamp;
    boolean SeenStatus;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getSenderid() {
        return Senderid;
    }

    public void setSenderid(String senderid) {
        Senderid = senderid;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public boolean isSeenStatus() {
        return SeenStatus;
    }

    public void setSeenStatus(boolean seenStatus) {
        SeenStatus = seenStatus;
    }

    public ModelChat(String message, String senderid, String receiver, String timeStamp, boolean seenStatus) {
        this.Message = message;
        this.Senderid = senderid;
        this.Receiver = receiver;
        this.TimeStamp = timeStamp;
        this.SeenStatus = seenStatus;
    }

    public ModelChat() {
    }
}
