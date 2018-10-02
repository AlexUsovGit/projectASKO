import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    public  Stage window;

    public static void main(String[] args) {
        launch();
    }



    public void start( Stage window) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/MainScreenView.fxml"));
        Scene scene = new Scene(root, 1024, 724);
        window.setTitle("Test");
        window.setScene(scene);
        window.show();
    }

    public Stage getWindow() {
        return window;
    }
}
