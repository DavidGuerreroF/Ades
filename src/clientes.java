import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class clientes extends Application {
    private Label statusLabel;
    private Label helpLabel;
    private Stage mainStage;

    public clientes(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public clientes() {

    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Crear Cliente");

        // Crear el label de ayuda
        helpLabel = new Label("Por favor, ingrese los datos del cliente.");
        helpLabel.setFont(new Font("Arial", 20));
        helpLabel.setTextFill(Color.web("#0078D7"));
        helpLabel.setStyle("-fx-font-weight: bold;");
       // helpLabel.setEffect(new DropShadow(2, Color.BLACK));

        // Crear los labels y campos de texto
        Label nombreLabel = createStyledLabel("Nombre:");
        TextField nombreField = createStyledTextField();
        Label apellidoLabel = createStyledLabel("Apellido:");
        TextField apellidoField = createStyledTextField();
        Label telefonoLabel = createStyledLabel("Teléfono:");
        TextField telefonoField = createStyledTextField();
        Label emailLabel = createStyledLabel("Email:");
        TextField emailField = createStyledTextField();
        Label direccionLabel = createStyledLabel("Dirección:");
        TextField direccionField = createStyledTextField();

        statusLabel = new Label("");
        statusLabel.setFont(new Font("Arial", 16));
        statusLabel.setTextFill(Color.web("#333"));

        // Crear los botones
        Button btnGuardar = createStyledButton("Guardar");
        Button btnCancelar = createStyledButton("Cancelar");
        Button btnCatalogo = createStyledButton("Catálogo de Clientes");

        // Acción para el botón "Guardar"
        btnGuardar.setOnAction(e -> {
            String nombre = nombreField.getText().trim();
            String apellido = apellidoField.getText().trim();
            String telefono = telefonoField.getText().trim();
            String email = emailField.getText().trim();
            String direccion = direccionField.getText().trim();

            // Verificar si algún campo está vacío
            if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || email.isEmpty() || direccion.isEmpty()) {
                updateStatus("Debe completar todos los campos.");
                statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ff0000;");
            } else {
                // Llamada al DAO para guardar el cliente en la base de datos
                clientDAO.saveClient(nombre, apellido, telefono, email, direccion);
                updateStatus("Cliente guardado con éxito.");
                statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #28a745;");
            }
        });

        // Acción para el botón "Cancelar"
        btnCancelar.setOnAction(e -> {
            primaryStage.close();
            mainStage.show();
        });

        // Acción para el botón "Catálogo de Clientes"
        btnCatalogo.setOnAction(e -> {
            CatalogoClientes catalogo = new CatalogoClientes();
            catalogo.start(new Stage());
        });

        // Configuración del layout principal
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15.0);
        grid.setVgap(15.0);
        grid.setPadding(new Insets(30.0));
        grid.setStyle("-fx-background-color: #f0f0f0; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Agregar los campos al layout
        grid.add(helpLabel, 0, 0, 2, 1);
        grid.add(nombreLabel, 0, 1);
        grid.add(nombreField, 1, 1);
        grid.add(apellidoLabel, 0, 2);
        grid.add(apellidoField, 1, 2);
        grid.add(telefonoLabel, 0, 3);
        grid.add(telefonoField, 1, 3);
        grid.add(emailLabel, 0, 4);
        grid.add(emailField, 1, 4);
        grid.add(direccionLabel, 0, 5);
        grid.add(direccionField, 1, 5);
        grid.add(statusLabel, 0, 7, 2, 1);

        // Crear un contenedor horizontal para los botones
        HBox buttonLayout = new HBox(15.0); // Espaciado entre botones
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(15.0));
        buttonLayout.getChildren().addAll(btnGuardar, btnCancelar, btnCatalogo);

        // Agregar el contenedor de botones al layout principal
        grid.add(buttonLayout, 0, 6, 2, 1);

        // Mostrar la escena
        Scene scene = new Scene(grid, 700.0, 600.0);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Actualizar el mensaje de estado
    private void updateStatus(String action) {
        statusLabel.setText(action);
    }

    // Crear un label estilizado
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setFont(new Font("Arial", 18));
        label.setTextFill(Color.web("#333"));
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    // Crear un campo de texto estilizado
    private TextField createStyledTextField() {
        TextField textField = new TextField();
        textField.setMinSize(250.0, 40.0);
        textField.setFont(new Font("Arial", 16));
        textField.setStyle("-fx-padding: 10; -fx-background-color: #fff; -fx-border-color: #ccc; -fx-border-radius: 5;");
        textField.setEffect(new DropShadow(2, Color.GRAY));
        return textField;
    }

    // Crear un botón estilizado
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

    public static void main(String[] args) {
        launch(args);
    }
}