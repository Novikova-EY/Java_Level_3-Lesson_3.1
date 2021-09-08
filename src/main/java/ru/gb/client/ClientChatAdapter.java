package ru.gb.client;

import ru.gb.client.gui.ClientChatFrame;
import ru.gb.client.network.BasicChatNetwork;

import java.util.function.Consumer;

public class ClientChatAdapter {
    private final BasicChatNetwork network;
    private final ClientChatFrame frame;

    public ClientChatAdapter(String host, int port) {
        this.network = new BasicChatNetwork(host, port);
        this.frame = new ClientChatFrame(sender());
        receive();
    }

    private Consumer<String> sender() {
        return new Consumer<String>() {
            @Override
            public void accept(String message) {
                network.send(message);
            }
        };
    }

    private void receive() {
        while (true) {
            String message = network.receive();
            frame.append(message);
        }
    }
}
