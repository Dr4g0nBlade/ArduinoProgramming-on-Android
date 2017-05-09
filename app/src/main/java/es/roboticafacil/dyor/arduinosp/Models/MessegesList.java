package es.roboticafacil.dyor.tabbed.Models;

import java.util.List;

public class MessegesList {

    private List<ChatMessage> messages;


    public MessegesList(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
}
