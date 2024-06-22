package server.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private FileTransferHandler fileTransferHandler;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            fileTransferHandler = new FileTransferHandler(socket);

            // Prompt for username
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
            // Handle client messages and commands
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
                    // Send private message to targetUser
                    // Assuming targetUser is the username of the target client
                    for (ClientHandler client : MainServer.clientHandlers) {
                        if (client.username.equals(targetUser)) {
                            client.sendMessage(username + " (private): " + msgContent);
                            break;
                        }
                    }
                } 
                else if (message.startsWith("/file ")) {
                    fileTransferHandler.receiveFile("uploads/");
                }
                // else if (message.startsWith("/file ")) {
                //     // Extract file name from the message
                //     String fileName = message.substring(6);
                //     // Assuming the file path or other necessary information is included in the message
                //     // For example: "/file path/to/file.txt"
                //     // Handle file transfer
                //     handleFileTransfer(fileName);
                // }
                 else {
                    MainServer.broadcast(username + ": " + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close resources and clean up
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleFileTransfer(String filePath) {
        // Create a new FileTransferHandler instance
        FileTransferHandler fileTransferHandler = new FileTransferHandler(socket);
        try {
            // Assuming you want to send the file to the client
            fileTransferHandler.sendFile(filePath);
            // If receiving a file, you would call fileTransferHandler.receiveFile(savePath) instead
        } catch (IOException e) {
            System.err.println("Error transferring file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getUsername() {
        return username;
    }
}
