package ru.gb.client.gui;

import java.util.function.Consumer;

public class ClientChatFrame {
    private final ChatFrame chatFrame;

    public ClientChatFrame(Consumer<String> messageConsumer) {
        this.chatFrame = new ChatFrame("Client Chat v1.1", messageConsumer);
    }

    public void append(String message) {
        chatFrame.getChatArea().append(message);
        chatFrame.getChatArea().append("\n");
    }
}
