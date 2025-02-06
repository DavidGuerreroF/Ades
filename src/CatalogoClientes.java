import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

// Clase Cliente para representar los detalles de un cliente
class Cliente {
    private int id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String direccion;

    public Cliente(int id, String nombre, String apellido, String telefono, String email, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getDireccion() {
        return direccion;
    }

    // Representación en String para mostrar en el ListView
    @Override
    public String toString() {
        return "ID: " + id + " | Nombre: " + nombre + " " + apellido +
                "\nTel: " + telefono + " | Email: " + email + "\nDir: " + direccion;
    }
}

public class CatalogoClientes extends Application {

    private Label statusLabel;
    private ListView<Cliente> clienteListView;
    private TextField searchField;

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Catálogo de Clientes");

        // Crear lista para mostrar los clientes
        clienteListView = new ListView<>();
        clienteListView.setMinSize(800, 400); // Ajuste del tamaño del ListView
        clienteListView.setStyle("-fx-font-size: 16px;"); // Aumentar el tamaño del texto en el ListView

        // Crear el label de estado
        statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #444;");

        // Crear barra de búsqueda
        searchField = new TextField();
        searchField.setPromptText("Buscar por teléfono...");
        searchField.setMinSize(300, 40);  // Tamaño de la barra de búsqueda
        searchField.setStyle("-fx-font-size: 16px;");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterClientList(newValue));

        // Crear los botones
        Button btnBorrarCliente = createStyledButton("Borrar Cliente");
        Button btnModificarCliente = createStyledButton("Modificar Cliente");

        // Acción para el botón "Borrar Cliente"
        btnBorrarCliente.setOnAction((e) -> {
            Cliente selectedClient = clienteListView.getSelectionModel().getSelectedItem();
            if (selectedClient != null) {
                // Eliminar el cliente de la base de datos usando el DAO (se asume que clientDAO existe)
                clientDAO.deleteClient(selectedClient.getId());
                updateStatus("Cliente borrado con éxito.");
                refreshClientList(); // Actualizar la lista después de borrar el cliente
            } else {
                updateStatus("Por favor, seleccione un cliente para borrar.");
            }
        });

        // Acción para el botón "Modificar Cliente"
        btnModificarCliente.setOnAction((e) -> {
            Cliente selectedClient = clienteListView.getSelectionModel().getSelectedItem();
            if (selectedClient != null) {
                Modificarcliente.mostrar(selectedClient); // Llamar al formulario de modificación
                refreshClientList(); // Actualizar la lista después de modificar el cliente
            } else {
                updateStatus("Por favor, seleccione un cliente para modificar.");
            }
        });

        // Crear un contenedor para los botones (HBox)
        HBox buttonContainer = new HBox(20); // Espaciado entre botones
        buttonContainer.setAlignment(Pos.CENTER); // Alinear los botones al centro
        buttonContainer.getChildren().addAll(btnBorrarCliente, btnModificarCliente); // Agregar botones al contenedor

        // Layout principal
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15.0);
        grid.setVgap(20.0); // Más espaciado entre los elementos
        grid.setPadding(new Insets(30.0));
        grid.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #d0d0d0; -fx-border-width: 2px;");

        // Añadir componentes al layout
        //Label titleLabel = new Label("Catálogo de Clientes");
        //titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0078D7;");

        //grid.add(titleLabel, 0, 0); // Título en la parte superior
        grid.add(searchField, 0, 1); // Barra de búsqueda en la parte superior
        grid.add(clienteListView, 0, 2); // ListView en el centro
        grid.add(buttonContainer, 0, 3); // Botones debajo de la lista
        grid.add(statusLabel, 0, 4); // Mensaje de estado en la parte inferior

        // Mostrar la escena
        Scene scene = new Scene(grid, 1000, 600); // Tamaño más amplio de la ventana
        primaryStage.setScene(scene);
        primaryStage.show();

        // Cargar la lista de clientes
        refreshClientList();
    }

    // Actualizar el mensaje de estado
    private void updateStatus(String action) {
        this.statusLabel.setText(action);
    }

    // Método para refrescar la lista de clientes
    private void refreshClientList() {
        List<Cliente> clients = clientDAO.getAllClients(); // Obtener todos los clientes desde la base de datos
        clienteListView.getItems().clear();
        clienteListView.getItems().addAll(clients);
    }

    // Método para filtrar la lista de clientes por teléfono
    private void filterClientList(String query) {
        List<Cliente> filteredClients = clientDAO.getAllClients().stream()
                .filter(cliente -> cliente.getTelefono().contains(query))
                .collect(Collectors.toList());

        clienteListView.getItems().clear();
        clienteListView.getItems().addAll(filteredClients);
    }

    // Método para crear un botón estilizado
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setMinSize(200.0, 60.0); // Botón más grande
        button.setStyle("-fx-font-size: 16px; -fx-background-color: #0078D7; -fx-text-fill: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        button.setOnMouseEntered((e) -> {
            button.setStyle("-fx-font-size: 16px; -fx-background-color: #005EA6; -fx-text-fill: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        });
        button.setOnMouseExited((e) -> {
            button.setStyle("-fx-font-size: 16px; -fx-background-color: #0078D7; -fx-text-fill: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        });
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
