package Opgave_BMIServer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Application {
    @Override
    public void start(Stage primaryStage){
        // Text area for at vise resultatet
        TextArea ta = new TextArea();
        ta.setEditable(false);


        Scene scene = new Scene(new ScrollPane(ta), 450, 200);
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.show();
        //laver en ny tråd til arbejdet
        new Thread( () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8000);
                // lytter efter conection request
                Socket socket = serverSocket.accept();

                // skabe datainput og dataoutput til serveren
                DataInputStream inputFromClient = new DataInputStream(
                        socket.getInputStream());
                DataOutputStream outputToClient = new DataOutputStream(
                        socket.getOutputStream());

                while (true) {
                    String weightStatus;
                    // få vægt fra klienten
                    double weight = inputFromClient.readDouble();

                    // få højde fra klienten
                    double height = inputFromClient.readDouble();

                    double bmi = weight / (height * height);
                    //tjekke vægt status
                    if (bmi < 18.5)
                        weightStatus = "Underweight";
                    else if (bmi < 25)
                        weightStatus = "Normal";
                    else if (bmi < 30)
                        weightStatus = "Overweight";
                    else
                        weightStatus = "Obese";

                    outputToClient.writeUTF("BMI is " + bmi + ". " + weightStatus);
                    // siger at dette skal forgå på vores javafx tråd
                    Platform.runLater(() -> {
                        ta.appendText("Weight: "
                                + weight + '\n');
                        ta.appendText("Height: " + height + '\n');
                        ta.appendText("BMI is " + bmi + ". " + weightStatus + '\n');
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
