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
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class clientes extends Application {
    private Label statusLabel;
    private Label helpLabel;
    private Stage mainStage;

    private Label nombreAsterisco;
    private Label apellidoAsterisco;
    private Label telefonoAsterisco;
    private Label emailAsterisco;
    private Label direccionAsterisco;

    public clientes(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public clientes() {}

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Crear Cliente");

        helpLabel = new Label("Por favor, ingrese los datos del cliente.");
        helpLabel.setFont(new Font("Arial", 20));
        helpLabel.setTextFill(Color.web("#0078D7"));
        helpLabel.setStyle("-fx-font-weight: bold;");

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

        Label direccionLabel = createStyledLabel("Dirección:");
        TextField direccionField = createStyledTextField();
        direccionAsterisco = createAsteriskLabel();

        statusLabel = new Label("");
        statusLabel.setFont(new Font("Arial", 16));
        statusLabel.setTextFill(Color.web("#ff0000"));

        // Navegación con Enter
        nombreField.setOnKeyPressed(event -> { if (event.getCode() == KeyCode.ENTER) apellidoField.requestFocus(); });
        apellidoField.setOnKeyPressed(event -> { if (event.getCode() == KeyCode.ENTER) telefonoField.requestFocus(); });
        telefonoField.setOnKeyPressed(event -> { if (event.getCode() == KeyCode.ENTER) emailField.requestFocus(); });
        emailField.setOnKeyPressed(event -> { if (event.getCode() == KeyCode.ENTER) direccionField.requestFocus(); });
        direccionField.setOnKeyPressed(event -> { if (event.getCode() == KeyCode.ENTER) { /*btnGuardar.fire();*/ } });

        Button btnGuardar = createStyledButton("Guardar");
        Button btnCancelar = createStyledButton("Cancelar");
        Button btnCatalogo = createStyledButton("Catálogo de Clientes");

        btnGuardar.setOnAction(e -> {
            boolean error = false;
            // Ocultar todos los asteriscos primero
            nombreAsterisco.setVisible(false);
            apellidoAsterisco.setVisible(false);
            telefonoAsterisco.setVisible(false);
            emailAsterisco.setVisible(false);
            direccionAsterisco.setVisible(false);

            String nombre = nombreField.getText().trim();
            String apellido = apellidoField.getText().trim();
            String telefono = telefonoField.getText().trim();
            String email = emailField.getText().trim();
            String direccion = direccionField.getText().trim();

            if (nombre.isEmpty()) { nombreAsterisco.setVisible(true); error = true; }
            if (apellido.isEmpty()) { apellidoAsterisco.setVisible(true); error = true; }
            if (telefono.isEmpty()) { telefonoAsterisco.setVisible(true); error = true; }
            if (email.isEmpty()) { emailAsterisco.setVisible(true); error = true; }
            if (direccion.isEmpty()) { direccionAsterisco.setVisible(true); error = true; }

            if (error) {
                showError("Debe completar todos los campos obligatorios.");
                return;
            }
            if (telefonoYaRegistrado(telefono)) {
                showError("El número de teléfono ya está registrado para otro cliente.");
                telefonoAsterisco.setVisible(true);
                return;
            }
            // clientDAO.saveClient(nombre, apellido, telefono, email, direccion);
            showSuccess("Cliente guardado con éxito.");
        });

        btnCancelar.setOnAction(e -> {
            primaryStage.close();
            if (mainStage != null) mainStage.show();
        });

        btnCatalogo.setOnAction(e -> {
            CatalogoClientes catalogo = new CatalogoClientes();
            catalogo.start(new Stage());
        });

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(7.0);
        grid.setVgap(15.0);
        grid.setPadding(new Insets(30.0));
        grid.setStyle("-fx-background-color: #f0f0f0; -fx-border-radius: 10; -fx-background-radius: 10;");

        int row = 0;
        grid.add(helpLabel, 0, row, 3, 1); row++;
        grid.add(nombreLabel, 0, row); grid.add(nombreField, 1, row); grid.add(nombreAsterisco, 2, row); row++;
        grid.add(apellidoLabel, 0, row); grid.add(apellidoField, 1, row); grid.add(apellidoAsterisco, 2, row); row++;
        grid.add(telefonoLabel, 0, row); grid.add(telefonoField, 1, row); grid.add(telefonoAsterisco, 2, row); row++;
        grid.add(emailLabel, 0, row); grid.add(emailField, 1, row); grid.add(emailAsterisco, 2, row); row++;
        grid.add(direccionLabel, 0, row); grid.add(direccionField, 1, row); grid.add(direccionAsterisco, 2, row); row++;
        grid.add(statusLabel, 0, row, 3, 1); row++;

        HBox buttonLayout = new HBox(15.0);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(15.0));
        buttonLayout.getChildren().addAll(btnGuardar, btnCancelar, btnCatalogo);

        grid.add(buttonLayout, 0, row, 3, 1);

        Scene scene = new Scene(grid, 700.0, 600.0);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Label createAsteriskLabel() {
        Label label = new Label("*");
        label.setFont(new Font("Arial", 22));
        label.setTextFill(Color.RED);
        label.setVisible(false);
        return label;
    }

    private void showError(String mensaje) {
        statusLabel.setText(mensaje);
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ff0000;");
    }
    private void showSuccess(String mensaje) {
        statusLabel.setText(mensaje);
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #28a745;");
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setFont(new Font("Arial", 18));
        label.setTextFill(Color.web("#333"));
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    private TextField createStyledTextField() {
        TextField textField = new TextField();
        textField.setMinSize(250.0, 40.0);
        textField.setFont(new Font("Arial", 16));
        textField.setStyle("-fx-padding: 10; -fx-background-color: #fff; -fx-border-color: #ccc; -fx-border-radius: 5;");
        textField.setEffect(new DropShadow(2, Color.GRAY));
        return textField;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setMinSize(200.0, 50.0);
        button.setFont(new Font("Arial", 16));
        button.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        button.setEffect(new DropShadow(5, Color.BLACK));
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: #005EA6; -fx-text-fill: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        });
        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        });
        return button;
    }

    private boolean telefonoYaRegistrado(String telefono) {
        boolean existe = false;
        String sql = "SELECT 1 FROM CLIENTES WHERE telefono = ?";
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, telefono);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    existe = true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Error de conexión a la base de datos.");
        }
        return existe;
    }

    public static void main(String[] args) {
        launch(args);
    }
}