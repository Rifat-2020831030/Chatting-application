package server.src;

import java.io.*;
import java.net.Socket;

public class FileTransferHandler {
    private Socket socket;

    public FileTransferHandler(Socket socket) {
        this.socket = socket;
    }

    public void sendFile(String filePath) throws IOException {
        File file = new File(filePath);
        try (FileInputStream fileInputStream = new FileInputStream(file);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

            dataOutputStream.writeUTF(file.getName());
            dataOutputStream.writeLong(file.length());

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytesRead);
            }
            dataOutputStream.flush();
        }
    }

    public void receiveFile(String savePath) throws IOException {
        try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {
            String fileName = dataInputStream.readUTF();
            long fileSize = dataInputStream.readLong();
            File file = new File(savePath + File.separator + fileName);

            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalRead = 0;
                while (totalRead < fileSize && (bytesRead = dataInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }
            }
        }
    }
}
