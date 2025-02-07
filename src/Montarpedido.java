import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;

public class Montarpedido extends Application {
    private TextField nombreCliente;
    private TextField apellidoCliente;
    private TextField telefonoCliente;
    private TextField direccionCliente;
    private DatePicker fechaPedido;
    private Button btnCrearPedido;
    private Button btnCancelar;
    private TextField valorPedido; // Campo para ingresar el valor en float
    private TextField menuDiaField; // Campo para ingresar el menú del día manualmente
    private ComboBox<String> comboDomiciliario; // ComboBox para seleccionar domiciliarios
    private ObservableList<String> listaDomiciliarios; // Lista de domiciliarios
    private Button btnClientes;
    private Button btnDomiciliarios;
    private Button btnAgregarMenu;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Inicializamos los controles de la interfaz
        nombreCliente = new TextField();
        apellidoCliente = new TextField();
        telefonoCliente = new TextField();
        direccionCliente = new TextField();
        fechaPedido = new DatePicker(LocalDate.now()); // Establecer fecha actual
        fechaPedido.setEditable(false); // Hacer que la fecha no sea editable por el usuario
        valorPedido = new TextField(); // Campo para valor
        valorPedido.setPromptText("Ingrese el valor del pedido");

        menuDiaField = new TextField(); // Campo para ingresar el menú del día manualmente
        menuDiaField.setPromptText("Ingrese el menú del día");
        menuDiaField.setMinHeight(100); // Aumentar el tamaño del campo de menú del día

        btnAgregarMenu = new Button("Agregar Menú"); // Botón para agregar menú del día
        comboDomiciliario = new ComboBox<>(); // ComboBox para seleccionar domiciliarios
        listaDomiciliarios = FXCollections.observableArrayList(); // Lista para almacenar domiciliarios
        cargarDomiciliarios(); // Cargar domiciliarios disponibles

        btnCrearPedido = new Button("Crear Pedido");
        btnCancelar = new Button("Cancelar");
        btnClientes = new Button("Ir a Clientes"); // Botón para ir a Clientes.java
        btnDomiciliarios = new Button("Ir a Domiciliarios"); // Botón para ir a Domiciliarios.java

        // Acción al presionar Enter en el campo de teléfono
        telefonoCliente.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                cargarDatosPorTelefono(telefonoCliente.getText());
            }
        });

        // Configuración de la interfaz
        GridPane root = new GridPane();
        root.setVgap(15);
        root.setHgap(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 30px;");

        // Campos de formulario
        root.add(crearCampo("Nombre", nombreCliente), 0, 2);
        root.add(crearCampo("Apellido", apellidoCliente), 0, 3);
        root.add(crearCampo("Teléfono", telefonoCliente), 0, 1);
        root.add(crearCampo("Dirección", direccionCliente), 0, 4);
        root.add(crearCampo("Fecha del Pedido", fechaPedido), 0, 5);
        root.add(crearCampo("Valor", valorPedido), 0, 6); // Agregar el campo valor
        root.add(crearCampo("Menú del Día", menuDiaField), 0, 7); // Campo para menú del día
        root.add(crearCampo("Domiciliario", comboDomiciliario), 0, 8); // ComboBox para domiciliarios

        // Botones
        HBox botones = new HBox(30);
        botones.setAlignment(Pos.CENTER);
        botones.getChildren().addAll(btnCrearPedido, btnCancelar, btnClientes, btnDomiciliarios);
        root.add(botones, 0, 9, 2, 1);

        // Estilo de botones
        btnCrearPedido.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-font-size: 16px; -fx-border-radius: 25px;");
        btnCrearPedido.setPrefWidth(250);
        btnCancelar.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-font-size: 16px; -fx-border-radius: 25px;");
        btnCancelar.setPrefWidth(250);
        btnClientes.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-font-size: 16px; -fx-border-radius: 25px;");
        btnClientes.setPrefWidth(250);
        btnDomiciliarios.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-font-size: 16px; -fx-border-radius: 25px;");
        btnDomiciliarios.setPrefWidth(250);

        // Acción del botón de "Crear Pedido"
        btnCrearPedido.setOnAction(event -> crearPedido());

        // Acción del botón de "Cancelar"
        btnCancelar.setOnAction(event -> cancelarPedido(primaryStage));

        // Acción del botón de "Ir a Clientes"
        btnClientes.setOnAction(event -> irAClientes(primaryStage));

        // Acción del botón de "Ir a Domiciliarios"
        btnDomiciliarios.setOnAction(event -> irADomiciliarios(primaryStage));

        // Configuración de la escena
        Scene scene = new Scene(root, 1000, 600); // Ajustado para los cambios
        primaryStage.setTitle("Montar Pedido");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Método para crear el campo con la etiqueta y el componente asociado
    private HBox crearCampo(String etiqueta, Control control) {
        Label label = new Label(etiqueta);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333;");
        HBox hbox = new HBox(15, label, control);
        hbox.setAlignment(Pos.CENTER_LEFT);
        control.setMinSize(300, 40); // Aumentar tamaño de los campos
        control.setStyle("-fx-font-size: 16px; -fx-padding: 5px; -fx-border-radius: 10px; -fx-background-color: #FFFFFF; -fx-border-color: #ccc;");
        return hbox;
    }

    private void cargarDatosPorTelefono(String telefono) {
        try (Connection connection = conexionDB.getConnection()) {
            String query = "SELECT nombre, apellido, direccion FROM CLIENTES WHERE telefono = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, telefono);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nombreCliente.setText(rs.getString("nombre"));
                apellidoCliente.setText(rs.getString("apellido"));
                direccionCliente.setText(rs.getString("direccion"));
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No se encontró un cliente con ese número de teléfono.");
                alert.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarDomiciliarios() {
        try (Connection connection = conexionDB.getConnection()) {
            String query = "SELECT nombre FROM DOMICILIARIOS";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                listaDomiciliarios.add(rs.getString("nombre"));
            }
            comboDomiciliario.setItems(listaDomiciliarios);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void crearPedido() {
        try (Connection connection = conexionDB.getConnection()) {
            String telefono = telefonoCliente.getText();
            String nombre = nombreCliente.getText();
            String apellido = nombreCliente.getText();
            String direccion = direccionCliente.getText();
            String fecha = fechaPedido.getValue().toString();

            // Obtener valor del pedido
            float valor = 0;
            try {
                valor = Float.parseFloat(valorPedido.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "El valor ingresado no es válido.");
                alert.show();
                return;
            }

            // Obtener el ID del cliente por teléfono
            int idCliente = obtenerIdPorTelefono(telefono, connection);
            if (idCliente == -1) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No se encontró un cliente con ese número de teléfono.");
                alert.show();
                return;
            }

            // Obtener el ID del domiciliario seleccionado
            int idDomiciliario = obtenerIdDomiciliario(comboDomiciliario.getValue(), connection);
            if (idDomiciliario == -1) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Debe seleccionar un domiciliario.");
                alert.show();
                return;
            }

            java.sql.Date fechaSQL = java.sql.Date.valueOf(fecha);

            // Consulta para insertar el pedido
            String query = "INSERT INTO PEDIDOS (id_cliente, id_domiciliario, fecha_pedido, estado, direccion_entrega, telefono_cliente, valor, plato_seleccionado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idDomiciliario);
            stmt.setDate(3, fechaSQL);
            stmt.setString(4, "Pendiente");
            stmt.setString(5, direccion);
            stmt.setString(6, telefono);
            stmt.setFloat(7, valor);
            stmt.setString(8, menuDiaField.getText()); // Agregar el plato ingresado

            stmt.executeUpdate();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Pedido creado exitosamente");
            alert.show();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al crear el pedido: " + e.getMessage());
            alert.show();
        }
    }

    private int obtenerIdPorTelefono(String telefono, Connection connection) throws SQLException {
        String query = "SELECT id FROM CLIENTES WHERE telefono = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, telefono);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }
        return -1;
    }

    private int obtenerIdDomiciliario(String domiciliarioSeleccionado, Connection connection) throws SQLException {
        String query = "SELECT id FROM DOMICILIARIOS WHERE nombre = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, domiciliarioSeleccionado);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }
        return -1;
    }

    private void cancelarPedido(Stage stage) {
        // Cargar el menú principal (despachos.java)
        try {
            // Crear la nueva ventana de despachos
            despachos despachosApp = new despachos();
            Stage nuevaVentana = new Stage(); // Nueva ventana para el menú principal
            despachosApp.start(nuevaVentana); // Inicializa la ventana de despachos

            // Cerrar la ventana actual
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al regresar al menú principal: " + e.getMessage());
            alert.show();
        }
    }

    private void irAClientes(Stage stage) {
        // Cargar la ventana de Clientes (clientes.java)
        try {
            // Crear la nueva ventana de clientes
            clientes clientesApp = new clientes();
            Stage nuevaVentana = new Stage(); // Nueva ventana para clientes
            clientesApp.start(nuevaVentana); // Inicializa la ventana de clientes

            // Cerrar la ventana actual
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al abrir la ventana de clientes: " + e.getMessage());
            alert.show();
        }
    }

    private void irADomiciliarios(Stage stage) {
        // Cargar la ventana de Domiciliarios (domiciliarios.java)
        try {
            // Crear la nueva ventana de domiciliarios
            domiciliarios domiciliariosApp = new domiciliarios();
            Stage nuevaVentana = new Stage(); // Nueva ventana para domiciliarios
            domiciliariosApp.start(nuevaVentana); // Inicializa la ventana de domiciliarios

            // Cerrar la ventana actual
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al abrir la ventana de domiciliarios: " + e.getMessage());
            alert.show();
        }
    }

}