package Opgave_BMIServer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Client extends Application {

    DataOutputStream toServer = null;
    DataInputStream fromServer = null;

    @Override
    public void start(Stage primaryStage){
        //GUI
        Label weight = new Label("Weight in kg ");
        Label height= new Label("Height in m ");
        TextField tf = new TextField();
        TextField tf2 = new TextField();
        TextArea ta = new TextArea();
        Button button = new Button("Beregn");
        ta.setEditable(false);
        // Hbox til at sætte label og textfield sammen.
        HBox hBox = new HBox();
        hBox.getChildren().addAll(weight, tf);
        // Hbox til at sætte label, textfield sammen og knap sammen.
        HBox hBox2 = new HBox();
        hBox2.getChildren().addAll(height, tf2, button);
        hBox2.setSpacing(5);
        // Vbox til at sætte alt sammen.
        VBox vBox = new VBox();
        vBox.getChildren().addAll(hBox, hBox2, ta);
        // scenen
        Scene scene = new Scene(vBox, 450, 200);
        primaryStage.setTitle("Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    // få vægten fra vores label
                    double weight = Double.parseDouble(tf.getText());
                    double height = Double.parseDouble(tf2.getText());
                    //sende vægt og højde videre til serveren
                    toServer.writeDouble(weight);
                    toServer.writeDouble(height);
                    toServer.flush();

                    String bmi = fromServer.readUTF();
                    //tekst i textArea
                    ta.appendText("Weight " + weight + '\n');
                    ta.appendText("Height: " + height + '\n');
                    ta.appendText(bmi + '\n');
                //error handling
                } catch (NumberFormatException e) {
                    ta.appendText("Du skal skrive tal, ikke bogstaver.");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        try {
            Socket socket = new Socket("localhost", 8000);
            // Laver en input stream for at få data fra serveren
            fromServer = new DataInputStream(socket.getInputStream());

            //  Laver en output stream for at sende data til serveren
            toServer = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        launch(args);
    }
}
