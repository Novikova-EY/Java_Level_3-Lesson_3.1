package ru.gb.client.gui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ChatFrame extends JFrame {
    private final JPanel top;
    private final JPanel bottom;

    public ChatFrame(String title, Consumer<String> messageConsumer) {
        setTitle(title);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(0, 0, 500, 600);

        setLayout(new BorderLayout());

        top = createTop();
        bottom = createBottom(messageConsumer);

        add(top, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    public JTextArea getChatArea() {
        return (JTextArea) top.getComponent(0);
    }

    private JPanel createTop() {
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JPanel top = new JPanel();
        top.setLayout(new BorderLayout());
        top.add(chatArea, BorderLayout.CENTER);
        return top;
    }

    private JPanel createBottom(Consumer<String> messageConsumer) {
        JTextField inputField = new JTextField();

        JButton submit = new JButton("Submit");
        submit.addActionListener(
                new InputFieldListener(
                        (JTextArea) top.getComponent(0),
                        inputField,
                        messageConsumer)
        );

        JPanel bottom = new JPanel();
        bottom.setLayout(new BorderLayout());
        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(submit, BorderLayout.EAST);
        return bottom;
    }
}
