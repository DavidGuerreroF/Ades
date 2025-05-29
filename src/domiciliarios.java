import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;

public class domiciliarios extends Application {

    private Label statusLabel; // Label para mostrar el estado
    private Label helpLabel; // Label para el mensaje de ayuda
    private Stage mainStage; // Referencia al menú principal

    private Label nombreAsterisco;
    private Label apellidoAsterisco;
    private Label telefonoAsterisco;
    private Label emailAsterisco;

    // Constructor que recibe el Stage del menú principal
    public domiciliarios(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public domiciliarios() {}
    Button btnCrear = createStyledButton("Crear");
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Crear Domiciliario");

        // Crear el label de ayuda
        helpLabel = new Label("Por favor, ingrese los datos del domiciliario.");
        helpLabel.setFont(new Font("Arial", 20));
        helpLabel.setTextFill(Color.web("#0078D7"));
        helpLabel.setStyle("-fx-font-weight: bold;");

        // Crear etiquetas y campos de texto
        Label nombreLabel = createStyledLabel("Nombre:");
        TextField nombreField = createStyledTextField();
        nombreAsterisco = createAsteriskLabel();

        Label apellidoLabel = createStyledLabel("Apellido:");
        TextField apellidoField = createStyledTextField();
        apellidoAsterisco = createAsteriskLabel();

        Label telefonoLabel = createStyledLabel("Teléfono:");
        TextField telefonoField = createStyledTextField();
        telefonoAsterisco = createAsteriskLabel();

        Label emailLabel = createStyledLabel("Email:");
        TextField emailField = createStyledTextField();
        emailAsterisco = createAsteriskLabel();

        // Navegación con Enter
        nombreField.setOnKeyPressed(event -> { if (event.getCode() == KeyCode.ENTER) apellidoField.requestFocus(); });
        apellidoField.setOnKeyPressed(event -> { if (event.getCode() == KeyCode.ENTER) telefonoField.requestFocus(); });
        telefonoField.setOnKeyPressed(event -> { if (event.getCode() == KeyCode.ENTER) emailField.requestFocus(); });
        emailField.setOnKeyPressed(event -> { if (event.getCode() == KeyCode.ENTER) { btnCrear.fire(); } });

        // Botones

        btnCrear.setOnAction(e -> {
            boolean error = false;
            // Ocultar todos los asteriscos primero
            nombreAsterisco.setVisible(false);
            apellidoAsterisco.setVisible(false);
            telefonoAsterisco.setVisible(false);
            emailAsterisco.setVisible(false);

            String nombre = nombreField.getText().trim();
            String apellido = apellidoField.getText().trim();
            String telefono = telefonoField.getText().trim();
            String email = emailField.getText().trim();

            // Validación de campos obligatorios
            if (nombre.isEmpty()) { nombreAsterisco.setVisible(true); error = true; }
            if (apellido.isEmpty()) { apellidoAsterisco.setVisible(true); error = true; }
            if (telefono.isEmpty()) { telefonoAsterisco.setVisible(true); error = true; }
            if (email.isEmpty()) { emailAsterisco.setVisible(true); error = true; }

            if (error) {
                showError("Debe completar todos los campos obligatorios.");
                return;
            }

            // Validar que el teléfono y el correo no estén repetidos
            if (telefonoYaRegistrado(telefono)) {
                showError("El número de teléfono ya está registrado para otro domiciliario.");
                telefonoAsterisco.setVisible(true);
                return;
            }
            if (emailYaRegistrado(email)) {
                showError("El correo ya está registrado para otro domiciliario.");
                emailAsterisco.setVisible(true);
                return;
            }

            // Guardar domiciliario
            try {
                crearDomiciliario(nombre, apellido, telefono, email);
                showSuccess("Domiciliario creado con éxito.");
                // Limpiar los campos después de guardar
                nombreField.clear();
                apellidoField.clear();
                telefonoField.clear();
                emailField.clear();
            } catch (SQLException ex) {
                showError("Error al crear el domiciliario: " + ex.getMessage());
            }
        });

        Button btnCatalogo = createStyledButton("Catálogo de Domiciliarios");
        btnCatalogo.setOnAction(e -> {
            CatalogoDomiciliario catalogo = new CatalogoDomiciliario();
            catalogo.start(new Stage());
        });

        Button btnCancelar = createStyledButton("Cancelar");
        btnCancelar.setOnAction(e -> {
            primaryStage.close();
            if (mainStage != null) mainStage.show();
        });

        // Crear un layout en cuadrícula
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(30));

        // Agregar el label de ayuda al layout
        grid.add(helpLabel, 0, 0, 3, 1); // Ocupa tres columnas

        // Agregar elementos al layout con asteriscos en la columna 2
        int row = 1;
        grid.add(nombreLabel, 0, row); grid.add(nombreField, 1, row); grid.add(nombreAsterisco, 2, row); row++;
        grid.add(apellidoLabel, 0, row); grid.add(apellidoField, 1, row); grid.add(apellidoAsterisco, 2, row); row++;
        grid.add(telefonoLabel, 0, row); grid.add(telefonoField, 1, row); grid.add(telefonoAsterisco, 2, row); row++;
        grid.add(emailLabel, 0, row); grid.add(emailField, 1, row); grid.add(emailAsterisco, 2, row); row++;

        // Crear el label de estado
        statusLabel = new Label("");
        statusLabel.setFont(new Font("Arial", 16));
        statusLabel.setTextFill(Color.web("#333"));
        grid.add(statusLabel, 0, row, 3, 1); row++;

        // Disposición de botones
        VBox buttonBox = new VBox(10, btnCrear, btnCatalogo, btnCancelar);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 0, row, 3, 1);

        // Crear la escena y mostrarla
        Scene scene = new Scene(grid, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Etiqueta de asterisco rojo
    private Label createAsteriskLabel() {
        Label label = new Label("*");
        label.setFont(new Font("Arial", 22));
        label.setTextFill(Color.RED);
        label.setVisible(false);
        return label;
    }

    // Mostrar error en statusLabel
    private void showError(String mensaje) {
        statusLabel.setText(mensaje);
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ff0000;");
    }

    // Mostrar éxito en statusLabel
    private void showSuccess(String mensaje) {
        statusLabel.setText(mensaje);
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #28a745;");
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

    // Métodos para validar teléfono y correo únicos
    private boolean telefonoYaRegistrado(String telefono) {
        String sql = "SELECT 1 FROM domiciliarios WHERE telefono = ?";
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, telefono);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Error de conexión a la base de datos.");
        }
        return false;
    }

    private boolean emailYaRegistrado(String email) {
        String sql = "SELECT 1 FROM domiciliarios WHERE email = ?";
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Error de conexión a la base de datos.");
        }
        return false;
    }

    // Métodos auxiliares para crear etiquetas y campos de texto
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setFont(new Font("Arial", 18));
        label.setTextFill(Color.web("#333"));
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    private TextField createStyledTextField() {
        TextField textField = new TextField();
        textField.setMinSize(250, 40); // Aumentar el tamaño mínimo del campo de texto
        textField.setFont(new Font("Arial", 16));
        textField.setStyle("-fx-padding: 10;");
        textField.setEffect(new DropShadow(2, Color.GRAY));
        return textField;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setMinSize(200, 50);
        button.setFont(new Font("Arial", 16));
        button.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        button.setEffect(new DropShadow(5, Color.BLACK));
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #005EA6; -fx-text-fill: white; -fx-border-radius: 10; -fx-background-radius: 10;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-border-radius: 10; -fx-background-radius: 10;"));
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}