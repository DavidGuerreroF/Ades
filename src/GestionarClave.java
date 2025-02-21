import javafx.geometry.Pos; // Para alinear los elementos dentro del GridPane
import javafx.scene.Scene; // Para definir la escena de la ventana
import javafx.scene.control.Button; // Para usar botones en la interfaz
import javafx.scene.control.Label; // Para mostrar texto o mensajes
import javafx.scene.control.TextField; // Para entradas de texto
import javafx.scene.layout.GridPane; // Para organizar los elementos en una cuadrícula
import javafx.stage.Stage; // Para crear y gestionar la ventana
import java.sql.SQLException; // Para manejar errores al trabajar con la base de datos

public class GestionarClave {
    public void start(Stage stage) {
        Label usernameLabel = new Label("Usuario:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Clave:");
        TextField passwordField = new TextField();

        Label feedbackLabel = new Label();

        Button saveButton = new Button("Crear Usuario");
        saveButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                feedbackLabel.setText("Esta a punto de crear un nuevo usuario (clave de Minimo 6 caracteres)");
            } else if (password.length() < 6) {
                feedbackLabel.setText("La clave debe tener al menos 6 caracteres.");
            } else {
                try {
                    usuarioDAO.insertarUsuario(username, password);
                    feedbackLabel.setText("Usuario registrado exitosamente.");
                } catch (SQLException ex) {
                    feedbackLabel.setText("Error al guardar el usuario: " + ex.getMessage());
                }
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(saveButton, 0, 2, 2, 1);
        gridPane.add(feedbackLabel, 0, 3, 2, 1);

        Scene scene = new Scene(gridPane, 300, 200);
        stage.setScene(scene);
        stage.setTitle("Crear Operador");
        stage.show();
    }
}
