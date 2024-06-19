package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;

public class ConnectionThread extends Thread {
    private Socket socket;
    private PrintWriter writer;

    public ConnectionThread(String address, int port) throws IOException {
        socket = new Socket(address, port);
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(input)
            );
            writer = new PrintWriter(output, true);

            String rawMessage;

            while ((rawMessage = reader.readLine()) != null) {
                Message message = new ObjectMapper()
                        .readValue(rawMessage, Message.class);

                switch (message.type) {
                    case Broadcast -> System.out.println(message.content);
                    case Login -> System.out.println(message.content + "- Joined ");
                    case Logout -> System.out.println(message.content + "- Left ");
                    case OnlineUsers -> System.out.println("Online users: " + message.content);
                    case Private -> System.out.println("Private from" + message.content + ": " + message.content);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
             try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(Message message) throws JsonProcessingException {
        String rawMessage = new ObjectMapper()
                .writeValueAsString(message);
        writer.println(rawMessage);
    }

    public void login(String login) throws JsonProcessingException {
        Message message = new Message(MessageType.Login, login);
        send(message);
    }


    public void requestOnlineUsers() throws JsonProcessingException {
        send(new Message(MessageType.OnlineUsers, ""));
    }


    public void logout(String login) throws JsonProcessingException {
        Message message = new Message(MessageType.Logout, login);
        send(message);
    }

}