package server.src;

import java.io.*;
import java.net.Socket;

public class FileTransferHandler {

    private Socket socket;

    public FileTransferHandler(Socket socket) {
        this.socket = socket;
    }

    public void sendFile(String filePath) throws IOException {
        // Open file and socket's output stream
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        // Send file metadata (name and size)
        dataOutputStream.writeUTF(file.getName());
        dataOutputStream.writeLong(file.length());

        // Send file bytes
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            dataOutputStream.write(buffer, 0, bytesRead);
        }

        // Close resources
        fileInputStream.close();
        dataOutputStream.flush();
    }

    public void receiveFile(String savePath) throws IOException {
        // Open socket's input stream
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

        // Read file metadata (name and size)
        String fileName = dataInputStream.readUTF();
        long fileSize = dataInputStream.readLong();
        File file = new File(savePath + File.separator + fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        // Read file bytes and write to file
        byte[] buffer = new byte[4096];
        int bytesRead;
        long totalRead = 0;
        while (totalRead < fileSize && (bytesRead = dataInputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
            totalRead += bytesRead;
        }

        // Close resources
        fileOutputStream.close();
    }
}