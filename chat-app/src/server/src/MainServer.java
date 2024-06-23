package server.src;

import java.io.*;
import java.net.*;
import java.util.*;

public class MainServer {
    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers = new HashSet<>();
    private static Map<String, ClientHandler> activeUsers = new HashMap<>();
    private static Map<String, Set<ClientHandler>> groupChats = new HashMap<>();
    
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running...");
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // implement sendMessageToUser method
    public static synchronized void sendMessageToUser(String user, String message) {
        // search for user handler in activeUsers
        ClientHandler userHandler = activeUsers.get(user);
        if (userHandler != null) {
            // If the user was found, send the message
            userHandler.sendMessage(message);
        } else {
            // Optionally, handle the case where the user is not found
            System.out.println("User " + user + " not found.");
        }
    }

    // method to broadcast message to all clients
    public static synchronized void broadcast(String message) {
        for (ClientHandler client : clientHandlers) {
            client.sendMessage(message);
        }
    }

    public static synchronized void broadcastToGroup(String groupName, String message) {
        Set<ClientHandler> groupMembers = groupChats.get(groupName);
        if (groupMembers != null) {
            for (ClientHandler client : groupMembers) {
                client.sendMessage(message);
            }
        }
    }

    public static synchronized void broadcastActiveUsers() {
        StringBuilder userList = new StringBuilder("/activeUsers ");
        for (String username : activeUsers.keySet()) {
            userList.append(username).append(" ");
        }
        broadcast(userList.toString().trim());
       
    }

    public static synchronized void addActiveUser(String username, ClientHandler client) {
        activeUsers.put(username, client);
        activeUsers.putIfAbsent(username, client);
        broadcastActiveUsers();
    }

    public static synchronized void addClientToGroup(String groupName, ClientHandler client) {
        groupChats.computeIfAbsent(groupName, k -> new HashSet<>()).add(client);
        broadcastToGroup(groupName, client.getUsername() + " has joined the group " + groupName + ".");
    }

    public static synchronized void removeClient(ClientHandler client) {
        clientHandlers.remove(client);
        activeUsers.remove(client.getUsername());
        for (Set<ClientHandler> group : groupChats.values()) {
            group.remove(client);
        }
        broadcastActiveUsers();
        broadcast(client.getUsername() + " has left the chat.");
    }
}
