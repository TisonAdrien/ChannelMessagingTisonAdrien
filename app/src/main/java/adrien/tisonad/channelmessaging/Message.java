package adrien.tisonad.channelmessaging;

import java.net.URI;

/**
 * Created by tisonad on 27/01/2017.
 */
public class Message {
    private int userID;
    private String username;
    private String messageImageUrl;

    public String getMessageImageUrl() {
        return messageImageUrl;
    }

    public void setMessageImageUrl(String messageImageUrl) {
        this.messageImageUrl = messageImageUrl;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Message{" +
                "userID=" + userID +
                ", message='" + message + '\'' +
                ", date='" + date + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", messageImageUrl='" + messageImageUrl + '\'' +
                '}';
    }

    public Message(int userID, String message, String date, String imageUrl,String messageImageUrl) {
        this.userID = userID;
        this.message = message;
        this.date = date;
        this.imageUrl = imageUrl;
        this.messageImageUrl = messageImageUrl;
    }

    public int getUserID() {

        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String message;
    private String date;
    private String imageUrl;
}
