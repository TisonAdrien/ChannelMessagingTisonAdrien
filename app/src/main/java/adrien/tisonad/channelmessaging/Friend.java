package adrien.tisonad.channelmessaging;

import java.util.UUID;

/**
 * Created by tisonad on 30/01/2017.
 */
public class Friend {
    private UUID userID;
    private String username;
    private String imageUrl;

    public Friend(UUID userID, String username, String imageUrl) {
        this.userID = userID;
        this.username = username;
        this.imageUrl = imageUrl;
    }

    public UUID getUserID() {

        return userID;
    }

    public Friend() {
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
