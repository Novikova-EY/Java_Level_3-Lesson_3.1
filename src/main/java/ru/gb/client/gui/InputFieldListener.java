package ru.gb.client.gui;



import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class InputFieldListener implements ActionListener {
    private final JTextArea textArea;
    private final JTextField inputField;
    private final StringBuilder stringBuilder = new StringBuilder();
    private final Consumer<String> consumer;

    public InputFieldListener(JTextArea textArea, JTextField inputField, Consumer<String> consumer) {
        this.textArea = textArea;
        this.inputField = inputField;
        this.consumer = consumer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inputField.getText().isBlank()) {
            return;
        }

        String newMessage = inputField.getText();

        stringBuilder.append(textArea.getText())
                .append(newMessage)
                .append("\n");
        textArea.setText(stringBuilder.toString());

        consumer.accept(newMessage);

        inputField.setText("");
        stringBuilder.setLength(0);
    }
}
