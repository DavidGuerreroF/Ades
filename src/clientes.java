import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Crear Cliente");
        this.helpLabel = new Label("Por favor, ingrese los datos del cliente.");
        this.helpLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #0078D7; -fx-font-weight: bold;");

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

        this.statusLabel = new Label("");
        this.statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Crear los botones
        Button btnGuardar = createStyledButton("Guardar");
        Button btnCancelar = createStyledButton("Cancelar");
        Button btnCatalogo = createStyledButton("Catálogo de Clientes");

        // Acción para el botón "Guardar"
        btnGuardar.setOnAction((e) -> {
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
        btnCancelar.setOnAction((e) -> {
            primaryStage.close();
            this.mainStage.show();
        });

        // Acción para el botón "Catálogo de Clientes"
        btnCatalogo.setOnAction((e) -> {
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
        grid.add(this.statusLabel, 0, 7, 2, 1);

        // Crear un contenedor horizontal para los botones
        HBox buttonLayout = new HBox(15.0); // Espaciado entre botones
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(15.0));
        buttonLayout.getChildren().addAll(btnGuardar, btnCancelar, btnCatalogo);

        // Agregar el contenedor de botones al layout principal
        grid.add(buttonLayout, 0, 6, 2, 1);

        // Mostrar la escena
        Scene scene = new Scene(grid, 600.0, 500.0);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Actualizar el mensaje de estado
    private void updateStatus(String action) {
        this.statusLabel.setText(action);
    }

    // Crear un label estilizado
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        return label;
    }

    // Crear un campo de texto estilizado
    private TextField createStyledTextField() {
        TextField textField = new TextField();
        textField.setMinSize(200.0, 40.0);
        textField.setStyle("-fx-font-size: 16px; -fx-padding: 5; -fx-background-color: #fff; -fx-border-color: #ccc; -fx-border-radius: 5;");
        return textField;
    }

    // Crear un botón estilizado
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setMinSize(150.0, 50.0);
        button.setStyle("-fx-font-size: 16px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");
        button.setOnMouseEntered((e) -> {
            button.setStyle("-fx-font-size: 16px; -fx-background-color: #45a049; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");
        });
        button.setOnMouseExited((e) -> {
            button.setStyle("-fx-font-size: 16px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");
        });
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
