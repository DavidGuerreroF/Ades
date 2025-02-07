import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.*;

public class domiciliarios extends Application {

    private Label statusLabel; // Label para mostrar el estado
    private Label helpLabel; // Label para el mensaje de ayuda
    private Stage mainStage; // Referencia al menú principal

    // Constructor que recibe el Stage del menú principal
    public domiciliarios(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public domiciliarios() {

    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Crear Domiciliario");

        // Crear el label de ayuda
        helpLabel = new Label("Por favor, ingrese los datos del domiciliario.");
        helpLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #0078D7; -fx-font-weight: bold;");

        // Crear etiquetas y campos de texto
        Label nombreLabel = createStyledLabel("Nombre:");
        TextField nombreField = createStyledTextField();

        Label apellidoLabel = createStyledLabel("Apellido:");
        TextField apellidoField = createStyledTextField();

        Label telefonoLabel = createStyledLabel("Teléfono:");
        TextField telefonoField = createStyledTextField();

        Label emailLabel = createStyledLabel("Email:");
        TextField emailField = createStyledTextField();

        // Botones
        Button btnCrear = createStyledButton("Crear");
        btnCrear.setOnAction(e -> {
            // Validación de campos
            if (validateFields(nombreField, apellidoField, telefonoField, emailField)) {
                try {
                    crearDomiciliario(nombreField.getText(), apellidoField.getText(), telefonoField.getText(), emailField.getText());
                    updateStatus("CREADO CON ÉXITO");
                } catch (SQLException ex) {
                    updateStatus("Error al crear el domiciliario: " + ex.getMessage());
                }
            } else {
                updateStatus("Debe completar todos los campos.");
            }
        });

        Button btnCatalogo = createStyledButton("Catálogo de Domiciliarios");
        btnCatalogo.setOnAction(e -> {
            // Lógica para abrir el catálogo de domiciliarios
            CatalogoDomiciliario catalogo = new CatalogoDomiciliario(); // Crear instancia de CatalogoDomiciliarios
            catalogo.start(new Stage()); // Abrir la ventana del catálogo
        });

        Button btnCancelar = createStyledButton("Cancelar");
        btnCancelar.setOnAction(e -> {
            primaryStage.close(); // Cierra la ventana actual
            mainStage.show(); // Muestra el menú principal
        });

        // Crear un layout en cuadrícula
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(30));

        // Agregar el label de ayuda al layout
        grid.add(helpLabel, 0, 0, 2, 1); // Ocupa dos columnas

        // Agregar elementos al layout
        grid.add(nombreLabel, 0, 1);
        grid.add(nombreField, 1, 1);
        grid.add(apellidoLabel, 0, 2);
        grid.add(apellidoField, 1, 2);
        grid.add(telefonoLabel, 0, 3);
        grid.add(telefonoField, 1, 3);
        grid.add(emailLabel, 0, 4);
        grid.add(emailField, 1, 4);

        // Disposición de botones
        grid.add(btnCrear, 0, 5);
        grid.add(btnCatalogo, 1, 5);
        grid.add(btnCancelar, 0, 6, 2, 1); // Botón "Cancelar" ocupa 2 columnas

        // Crear el label de estado
        statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        grid.add(statusLabel, 0, 7, 2, 1); // Añadir el label de estado

        // Crear la escena y mostrarla
        Scene scene = new Scene(grid, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Método para actualizar el label de estado
    private void updateStatus(String action) {
        statusLabel.setText(action);
    }

    // Método para crear un domiciliario
    private void crearDomiciliario(String nombre, String apellido, String telefono, String email) throws SQLException {
        String query = "INSERT INTO domiciliarios (nombre, apellido, telefono, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setString(3, telefono);
            stmt.setString(4, email);
            stmt.executeUpdate();
        }
    }

    // Método para validar que los campos no estén vacíos
    private boolean validateFields(TextField nombre, TextField apellido, TextField telefono, TextField email) {
        return !nombre.getText().isEmpty() && !apellido.getText().isEmpty() && !telefono.getText().isEmpty() && !email.getText().isEmpty();
    }

    // Métodos auxiliares para crear etiquetas y campos de texto
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        return label;
    }

    private TextField createStyledTextField() {
        TextField textField = new TextField();
        textField.setMinSize(200, 40); // Aumentar el tamaño mínimo del campo de texto
        textField.setStyle("-fx-font-size: 16px; -fx-padding: 5;");
        return textField;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setMinSize(150, 50);
        button.setStyle("-fx-font-size: 16px; -fx-background-color: #0078D7; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-font-size: 16px; -fx-background-color: #005EA6; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-font-size: 16px; -fx-background-color: #0078D7; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;"));
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
