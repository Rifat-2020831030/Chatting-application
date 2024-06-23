package server.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private FileTransferHandler fileTransferHandler;
    public static String targetUser;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            fileTransferHandler = new FileTransferHandler(socket);

            out.println("Enter your username:");
            username = in.readLine();
            MainServer.addActiveUser(username, this);
            MainServer.broadcast(username + " has joined the chat.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = in.readLine();
                if (message == null) {
                    break;
                }
                if (message.startsWith("/join ")) {
                    String groupName = message.substring(6);
                    MainServer.addClientToGroup(groupName, this);
                } else if (message.startsWith("/msg ")) {
                    String targetUser = message.substring(5, message.indexOf(' ', 5));
                    String msgContent = message.substring(message.indexOf(' ', 5) + 1);
                    // MainServer.sendMessageToUser(msgContent);
                    // MainServer.sendMessageToUser(targetUser, msgContent);
                } else if (message.startsWith("/file ")) {
                    fileTransferHandler.receiveFile("uploads/");
                } else {
                    MainServer.broadcast(username + ": " + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MainServer.removeClient(this);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getUsername() {
        return username;
    }
}

