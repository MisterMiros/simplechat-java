package com.example.simplechat.client;

import com.example.simplechat.client.services.chat.ChatService;
import com.example.simplechat.client.services.chat.Message;
import com.example.simplechat.client.services.chat.MessageEventHandler;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {

    private TextArea    chatArea;
    private TextField   senderField;
    private TextField   messageField;
    private Button      sendButton;
    private ChatService chatService;

    private class SendHandler implements EventHandler<MouseEvent> {

        private TextField   senderField;
        private TextField   messageField;
        private TextArea    chatArea;
        private ChatService chatService;

        public SendHandler(TextField senderField, TextField messageField, TextArea chatArea, ChatService chatService) {
            this.senderField  = senderField;
            this.messageField = messageField;
            this.chatArea     = chatArea;
            this.chatService  = chatService;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            String sender  = senderField.getText();
            String message = messageField.getText();
            chatService.sendMessage(sender, message);
        }
    }

    private class ReceiveHandler implements MessageEventHandler {

        private TextArea chatArea;

        public ReceiveHandler(TextArea chatArea) {
            this.chatArea = chatArea;
        }

        @Override
        public void handle(Message message) {
            String sender = message.sender;
            String text   = message.text;
            chatArea.appendText(String.format("%s: %s\n", sender, text));
        }
    }

    @Override
    public void start(Stage stage) {
        chatService = new ChatService("ws://localhost:8080/websocket");

        SplitPane mainPane = new SplitPane();
        mainPane.orientationProperty().setValue(Orientation.VERTICAL);

        StackPane chatPane = new StackPane();
        chatArea = new TextArea();
        chatArea.editableProperty().setValue(false);
        chatPane.getChildren().add(chatArea);
        chatService.addHandler(new ReceiveHandler(chatArea));

        FlowPane controlsPane = new FlowPane();
        controlsPane.rowValignmentProperty().setValue(VPos.CENTER);
        controlsPane.alignmentProperty().setValue(Pos.CENTER);
        Label senderLabel = new Label("Sender:");
        controlsPane.getChildren().add(senderLabel);
        senderField = new TextField("Sender");
        senderField.setPrefWidth(50);
        controlsPane.getChildren().add(senderField);
        Label messageLabel = new Label("Message:");
        controlsPane.getChildren().add(messageLabel);
        messageField = new TextField("Hello, World!");
        controlsPane.getChildren().add(messageField);
        sendButton = new Button("Send");
        sendButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new SendHandler(senderField, messageField, chatArea, chatService));
        controlsPane.getChildren().add(sendButton);

        mainPane.getItems().add(chatPane);
        mainPane.getItems().add(controlsPane);

        Scene scene = new Scene(mainPane, 640, 480);

        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}