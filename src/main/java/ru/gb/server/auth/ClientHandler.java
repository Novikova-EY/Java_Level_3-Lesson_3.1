package ru.gb.server.auth;

import ru.gb.server.Server;

import java.io.*;
import java.net.Socket;

import java.sql.SQLException;
import java.util.ArrayList;

public class ClientHandler {
    private File file;
    private final Server server;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private String name;
    private String nicknameCheck;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());


            new Thread(new Runnable() {
                @Override
                public void run() {
                    listen();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("SWW", e);
        }
    }

    private void listen() {
        try {
            doAuth();
            readMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doAuth() throws IOException {
        while (true) {
            String input = in.readUTF();
            if (input.startsWith("-auth")) {
                String[] credentials = input.split("\\s");
                nicknameCheck = null;
                try {
                    nicknameCheck = ClientsBD.getNickByLoginPass(credentials[1], credentials[2]);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (nicknameCheck != null) {
                    if (server.isNicknameFree(nicknameCheck)) {
                        name = nicknameCheck;
                        server.subscribe(this);
                        server.broadcast(name + " logged in.");

                        StringBuilder sb = new StringBuilder();
                        String fn = null;
                        try {
                            fn = sb.append("history_").append(ClientsBD.getLoginByNickname(name)).append(".txt").toString();
                            file = new File(fn);
                            file.createNewFile();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        takeClientHistoryFile(file.getName());

                        return;
                    } else {
                        sendMessage("Current user is already logged-in.");
                    }
                } else {
                    sendMessage("Unknown user. Incorrect login/password");
                }
            } else {
                sendMessage("Invalid authentication request.");
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void readMessage() throws IOException {
        while (true) {
            String message = in.readUTF();
            if (message.startsWith("/")) {
                if (message.equals("/end")) {
                    server.unsubscribe(this);
                }
                if (message.startsWith("/w ")) {
                    String[] tokens = message.split("\\s");
                    String nick = tokens[1];
                    String msg = message.substring(4 + nick.length());
                    server.sendMsgToClient(this, nick, msg);
                }
                if (message.startsWith("/change ")) {
                    String[] tokens1 = message.split("\\s");
                    String newNickname = tokens1[1];
                    String oldNickname = getName();
                    try {
                        server.changeNick(getName(), newNickname);
                        server.broadcast(oldNickname + " change nickname on " + newNickname);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                server.broadcast(name + ": " + message);
                writeClientHistoryFile(name + ": " + message);
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeClientHistoryFile(String message) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getName(), true))) {
            writer.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void takeClientHistoryFile(String filename) {
        final ArrayList arrayList = new ArrayList();
        String str;
        try (BufferedReader reader = new BufferedReader(new FileReader(file.getName()))) {
            while ((str = reader.readLine()) != null) {
                arrayList.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int n = 100;
        if (arrayList.size() <= n) {
            for (int i = 0; i < arrayList.size(); i++) {
                server.broadcast((String) arrayList.get(i));
            }
        } else {
            for (int i = (arrayList.size() - n); i < arrayList.size(); i++) {
                server.broadcast((String) arrayList.get(i));
            }
        }
    }
}

