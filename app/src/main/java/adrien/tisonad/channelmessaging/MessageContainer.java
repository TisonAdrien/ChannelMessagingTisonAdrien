package adrien.tisonad.channelmessaging;

import java.util.List;

/**
 * Created by tisonad on 27/01/2017.
 */
public class MessageContainer {
    private List<Message> messages;

    public MessageContainer(List<Message> messages) {
        this.messages = messages;
    }

    public List<Message> getMessages() {

        return messages;
    }

    @Override
    public String toString() {
        return "MessageContainer{" +
                "messages=" + messages +
                '}';
    }


    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
