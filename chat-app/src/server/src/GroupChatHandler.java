package server.src;

import java.util.*;

public class GroupChatHandler {
    public static HashSet<ClientHandler> groupMembers = new HashSet<>();
    
    public synchronized void addClient(ClientHandler client) {
        groupMembers.add(client);
    }

    public synchronized void broadcastToGroup(String message) {
        for (ClientHandler client : groupMembers) {
            client.sendMessage(message);
        }
    }
}
