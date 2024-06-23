package client.src;

import java.io.*;
import java.net.*;

import javafx.application.Platform;
import server.src.FileTransferHandler;
import server.src.MainServer;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private DataOutputStream dataOut;
    private BufferedReader userIn;
    private UIHandler uiHandler;

    public ChatClient(String serverAddress, UIHandler uiHandler) throws IOException {
        this.uiHandler = uiHandler;
        socket = new Socket(serverAddress, 12345);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        dataOut = new DataOutputStream(socket.getOutputStream());
        userIn = new BufferedReader(new InputStreamReader(System.in));

        Platform.runLater(() -> uiHandler.showMessage("Connected to the server."));

        String prompt = in.readLine();
        Platform.runLater(() -> uiHandler.showMessage(prompt));
        String username = uiHandler.getUsernameFromUser();
        out.println(username);
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void sendFile(File file) throws IOException {
        out.println("/file ");
        FileTransferHandler fileTransferHandler = new FileTransferHandler(socket);
        fileTransferHandler.sendFile(file.getAbsolutePath());
    }

    public void start() {
        new Thread(new IncomingReader()).start();
    }

    private class IncomingReader implements Runnable {
        String message;
        @Override
        public void run() {
            try {
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/activeUsers ")) {
                        Platform.runLater(() -> uiHandler.updateActiveUsers(message.substring(10).split(" ")));
                        // Platform.runLater(() -> uiHandler.updateActiveUsers(message.substring(13).split(" ")));
                    } else {
                        Platform.runLater(() -> uiHandler.showMessage(message));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}