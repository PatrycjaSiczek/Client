package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Client {
    public void start(String address, int port) {
       try {
           ConnectionThread thread = new ConnectionThread(address, port);
           thread.start();
           BufferedReader reader = new BufferedReader(
                   new InputStreamReader(System.in)
           );
           System.out.println("Login: ");
           String login = reader.readLine();
            thread.login(login);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    thread.logout(login);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }));


           String rawMessage;

           while((rawMessage = reader.readLine()) != null) {

               Message message = new Message(
                       MessageType.Broadcast, rawMessage);
               thread.send(message);
           }

               if (rawMessage.equals("/online")) {
                   thread.requestOnlineUsers();
               } else {
                   thread.send(new Message(MessageType.Broadcast, rawMessage));
               }

           if (rawMessage.startsWith("/w ")) {
               int firstSpaceIndex = rawMessage.indexOf(' ', 2);
               if (firstSpaceIndex == -1) {
                   System.out.println("Use: /w recipient message");
           }
               String recipient = rawMessage.substring(3, firstSpaceIndex);
               String messageContent = rawMessage.substring(firstSpaceIndex + 1);
               Message message = new Message(MessageType.Private, messageContent, recipient);
               thread.send(message);
           } else {
               Message message = new Message(MessageType.Broadcast, rawMessage);
               thread.send(message);
           }

       } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

