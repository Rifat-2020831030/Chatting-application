package server.src;

import java.io.*;
import java.net.*;
import java.util.*;

public class MainServer {
    private static final int PORT = 12345;
    public static HashSet<ClientHandler> clientHandlers = new HashSet<>();
    private static HashMap<String, ClientHandler> activeUsers = new HashMap<>();
    private static HashMap<String, GroupChatHandler> groupChats = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Server is running...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
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

    // Methods to manage clients and groups
    public static synchronized void broadcast(String message) {
        for (ClientHandler client : clientHandlers) {
            client.sendMessage(message);
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
        broadcastActiveUsers();
    }

    public static synchronized void addClientToGroup(String groupName, ClientHandler client) {
        groupChats.computeIfAbsent(groupName, k -> new GroupChatHandler()).addClient(client);
    }

    public static synchronized void removeClient(ClientHandler client) {
        clientHandlers.remove(client);
        activeUsers.remove(client.getUsername());
        broadcastActiveUsers();
    }
}
