package client.src;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import server.src.ClientHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class UIHandler extends Application {
    private ChatClient client;
    private TextArea messageArea;
    private ListView<String> activeUsersList;
    private String username;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Chat Application");

        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);
        root.setStyle("-fx-background-color: #333;");

        messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setWrapText(true);
        messageArea.setStyle("-fx-control-inner-background: #444; -fx-text-fill: #eee;");
        messageArea.setFont(Font.font("Arial", 14));

        TextField inputField = new TextField();
        inputField.setPromptText("Enter your message...");
        inputField.setStyle("-fx-background-color: #555; -fx-text-fill: #eee;");
        inputField.setFont(Font.font("Arial", 14));

        TextField inputField2 = new TextField();
        inputField.setPromptText("Enter target user: ");
        inputField.setStyle("-fx-background-color: #555; -fx-text-fill: #eee;");
        inputField.setFont(Font.font("Arial", 14));

        Button sendFileButton = new Button("Send File");
        sendFileButton.setStyle("-fx-background-color: #555; -fx-text-fill: #eee;");
        sendFileButton.setFont(Font.font("Arial", 14));

        activeUsersList = new ListView<>();
        activeUsersList.setStyle("-fx-background-color: #555; -fx-text-fill: #eee;");
        activeUsersList.setPrefHeight(150);

        inputField.setOnAction(event -> {
            String message = inputField.getText();
            client.sendMessage(message);
            inputField.clear();
        });

        inputField2.setOnAction(event -> {
            String targetUser = inputField2.getText();
            ClientHandler.targetUser = targetUser;
            inputField2.clear();
        });

        sendFileButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    client.sendFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button createGroupButton = new Button("Create Group Chat");
        createGroupButton.setStyle("-fx-background-color: #555; -fx-text-fill: #eee;");
        createGroupButton.setFont(Font.font("Arial", 14));
        createGroupButton.setOnAction(event -> {
            String groupName = getGroupNameFromUser();
            if (!groupName.isEmpty()) {
                client.sendMessage("/join " + groupName);
            }
        });

        root.getChildren().addAll(messageArea, inputField2, inputField, sendFileButton, createGroupButton, new Label("Active Users:"), activeUsersList);

        Scene scene = new Scene(root, 500, 500, Color.web("#333"));
        primaryStage.setScene(scene);
        primaryStage.show();

        String serverAddress = "localhost";
        client = new ChatClient(serverAddress, this);
        client.start();
    }

    public void showMessage(String message) {
        Platform.runLater(() -> messageArea.appendText(message + "\n"));
    }

    public void updateActiveUsers(String[] users) {
        Platform.runLater(() -> {
            activeUsersList.getItems().clear();
            activeUsersList.getItems().addAll(users);
        });
    }

    public String getUsernameFromUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Username");
        dialog.setHeaderText("Enter your username:");
        Optional<String> result = dialog.showAndWait();
        username = result.orElse("Anonymous");
        return username;
    }

    public String getGroupNameFromUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Group Chat");
        dialog.setHeaderText("Enter the group name:");
        Optional<String> result = dialog.showAndWait();
        return result.orElse("");
    }

    public String targetUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Username");
        dialog.setHeaderText("Enter target username:");
        Optional<String> result = dialog.showAndWait();
        ClientHandler.targetUser = result.orElse("Anonymous");
        return ClientHandler.targetUser;
    }

    public static void main(String[] args) {
        launch(args);
    }
}