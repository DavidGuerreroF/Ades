import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.io.*;
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
    private TextField valorPedido;
    private TextField menuDiaField;
    private ComboBox<String> comboDomiciliario;
    private ObservableList<String> listaDomiciliarios;
    private Button btnClientes;
    private Button btnDomiciliarios;
    private Button btnAgregarMenu;
    private Button btnImprimir;
    private Label labelAyuda;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        inicializarControles();
        cargarDomiciliarios();

        GridPane root = crearInterfaz();
        configurarBotones(primaryStage);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("Montar Pedido");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void inicializarControles() {
        nombreCliente = new TextField();
        apellidoCliente = new TextField();
        telefonoCliente = new TextField();
        direccionCliente = new TextField();
        fechaPedido = new DatePicker(LocalDate.now());
        fechaPedido.setEditable(false);
        valorPedido = new TextField();
        valorPedido.setPromptText("Ingrese el valor de pedido");
        nombreCliente.setPromptText("Ingrese Nombre de cliente");
        apellidoCliente.setPromptText("Ingrese Apellido de cliente");
        telefonoCliente.setPromptText("Ingrese telefono de cliente");
        direccionCliente.setPromptText("Ingrese direccion de cliente");

        menuDiaField = new TextField();
        menuDiaField.setPromptText("Ingrese el menu asi:(Sopa,Plato,Jugo)");
        menuDiaField.setMinHeight(100);

        comboDomiciliario = new ComboBox<>();
        listaDomiciliarios = FXCollections.observableArrayList();

        btnCrearPedido = new Button("Crear Pedido");
        btnCancelar = new Button("Cancelar");
        btnClientes = new Button("Ir a Clientes");
        btnDomiciliarios = new Button("Ir a Domiciliarios");
        btnImprimir = new Button("Imprimir Pedido");
        btnAgregarMenu = new Button("Agregar Menú");
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

    private GridPane crearInterfaz() {
        GridPane root = new GridPane();
        root.setVgap(15);
        root.setHgap(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30px;");
        root.setBackground(crearFondo());

        root.add(crearCampo("Teléfono", telefonoCliente), 0, 1);
        root.add(crearCampo("Nombre", nombreCliente), 0, 2);
        root.add(crearCampo("Menú del Día", menuDiaField), 0, 3);
        root.add(crearCampo("Fecha del Pedido", fechaPedido), 0, 4);
        root.add(crearCampo("Apellido", apellidoCliente), 1, 1);
        root.add(crearCampo("Dirección", direccionCliente), 1, 2);
        root.add(crearCampo("Domiciliario", comboDomiciliario), 1, 3);
        root.add(crearCampo("Valor", valorPedido), 1, 4);

        HBox botones = new HBox(30);
        botones.setAlignment(Pos.CENTER);
        botones.getChildren().addAll(btnCrearPedido, btnCancelar, btnClientes, btnDomiciliarios, btnImprimir);
        root.add(botones, 0, 9, 2, 1);

        labelAyuda = new Label("AYUDA ADES:  SI EL CLIENTE/DOMICILIARIO ES NUEVO CREELO POSTERIORMENTE, SOLO DEBE INGRESAR TELEFONO Y DAR ENTER PARA CONTINUAR");
        labelAyuda.setStyle("-fx-font-size: 12px; -fx-text-fill: #333; -fx-font-weight: normal; -fx-alignment: center;");

        // Añadir el mensaje de ayuda al pie de la ventana
        HBox ayudaBox = new HBox(labelAyuda);
        ayudaBox.setAlignment(Pos.CENTER);
        ayudaBox.setStyle("-fx-padding: 10px;");
        root.add(ayudaBox, 0, 10, 2, 1); // Ubicar el mensaje de ayuda debajo de los botones
        estiloBotones();
        return root;
    }

    private Background crearFondo() {
        Image fondo = new Image("file:///C:/PROYECTO/images/Pedidos.png");
        BackgroundImage backgroundImage = new BackgroundImage(fondo, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        return new Background(backgroundImage);
    }

    private HBox crearCampo(String etiqueta, Control control) {
        Label label = new Label(etiqueta);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333;");
        HBox hbox = new HBox(15, label, control);
        hbox.setAlignment(Pos.CENTER_LEFT);
        control.setMinSize(300, 40);
        control.setStyle("-fx-font-size: 16px; -fx-padding: 5px; -fx-border-radius: 10px; -fx-background-color: #FFFFFF; -fx-border-color: #ccc;");
        return hbox;
    }

    private void estiloBotones() {
        String botonEstilo = "-fx-background-color: #0294b5; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-font-size: 16px; -fx-border-radius: 25px;";
        btnCrearPedido.setStyle(botonEstilo);
        btnCrearPedido.setPrefWidth(250);
        btnCancelar.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-font-size: 16px; -fx-border-radius: 25px;");
        btnCancelar.setPrefWidth(250);
        btnClientes.setStyle(botonEstilo);
        btnClientes.setPrefWidth(250);
        btnDomiciliarios.setStyle(botonEstilo);
        btnDomiciliarios.setPrefWidth(250);
        btnImprimir.setStyle(botonEstilo);
        btnImprimir.setPrefWidth(250);
    }

    private void configurarBotones(Stage primaryStage) {
        telefonoCliente.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                cargarDatosPorTelefono(telefonoCliente.getText());
            }
        });

        btnCrearPedido.setOnAction(event -> crearPedido());
        btnCancelar.setOnAction(event -> cancelarPedido(primaryStage));
        btnClientes.setOnAction(event -> irAClientes());
        btnDomiciliarios.setOnAction(event -> irADomiciliarios());
        btnImprimir.setOnAction(event -> imprimirPedido());
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
                mostrarAlerta(Alert.AlertType.ERROR, "No se encontró un cliente con ese número de teléfono.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo, mensaje);
        alert.show();
    }

    private void crearPedido() {
        try (Connection connection = conexionDB.getConnection()) {
            String telefono = telefonoCliente.getText();
            String nombre = nombreCliente.getText();
            String apellido = nombreCliente.getText();
            String direccion = direccionCliente.getText();
            String fecha = fechaPedido.getValue().toString();

            float valor = obtenerValorPedido();
            if (valor == -1) return;

            int idCliente = obtenerIdPorTelefono(telefono, connection);
            if (idCliente == -1) {
                mostrarAlerta(Alert.AlertType.ERROR, "No se encontró un cliente con ese número de teléfono.");
                return;
            }

            int idDomiciliario = obtenerIdDomiciliario(comboDomiciliario.getValue(), connection);
            if (idDomiciliario == -1) {
                mostrarAlerta(Alert.AlertType.ERROR, "Debe seleccionar un domiciliario.");
                return;
            }

            java.sql.Date fechaSQL = java.sql.Date.valueOf(fecha);
            String query = "INSERT INTO PEDIDOS (id_cliente, id_domiciliario, fecha_pedido, estado, direccion_entrega, telefono_cliente, valor, plato_seleccionado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idDomiciliario);
            stmt.setDate(3, fechaSQL);
            stmt.setString(4, "Pendiente");
            stmt.setString(5, direccion);
            stmt.setString(6, telefono);
            stmt.setFloat(7, valor);
            stmt.setString(8, menuDiaField.getText());

            stmt.executeUpdate();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Pedido creado exitosamente");
            crearArchivoPedido(idCliente, nombre, telefono, direccion, fecha, valor);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al crear el pedido: " + e.getMessage());
        }
    }

    private float obtenerValorPedido() {
        try {
            return Float.parseFloat(valorPedido.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "El valor ingresado no es válido.");
            return -1;
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

    private void crearArchivoPedido(int idCliente, String nombre, String telefono, String direccion, String fecha, float valor) {
        File archivoPedido = new File("pedido_" + idCliente + "_" + System.currentTimeMillis() + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoPedido))) {
            writer.write("Restaurante el Buen sabor\n NIT:?????\n Telefono: ???????\n");
            writer.write("Nombre: " + nombre + "\n");
            writer.write("Telefono Cliente: " + telefono + "\n");
            writer.write("Dirección Cliente: " + direccion + "\n");
            writer.write("Fecha del Pedido: " + fecha + "\n");
            writer.write("Valor: " + "$"+ valor + "\n");
            writer.write("Orden: " + menuDiaField.getText() + "\n");
            writer.write("Domiciliario: " + comboDomiciliario.getValue() + "\n");
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al escribir el archivo de texto: " + e.getMessage());
            return;
        }

        abrirArchivoPedido(archivoPedido);
    }

    private void abrirArchivoPedido(File archivoPedido) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(archivoPedido);
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al abrir el archivo de texto: " + e.getMessage());
            }
        }
    }

    private void cancelarPedido(Stage stage) {
        try {
            despachos despachosApp = new despachos();
            Stage nuevaVentana = new Stage();
            despachosApp.start(nuevaVentana);
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al regresar al menú principal: " + e.getMessage());
        }
    }

    private void irAClientes() {
        try {
            clientes clientesApp = new clientes();
            Stage nuevaVentana = new Stage();
            clientesApp.start(nuevaVentana);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al abrir la ventana de clientes: " + e.getMessage());
        }
    }

    private void irADomiciliarios() {
        try {
            domiciliarios domiciliariosApp = new domiciliarios();
            Stage nuevaVentana = new Stage();
            domiciliariosApp.start(nuevaVentana);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al abrir la ventana de domiciliarios: " + e.getMessage());
        }
    }

    private void imprimirPedido() {
        try {
            Imprimir imprimirApp = new Imprimir();
            Stage nuevaVentana = new Stage();
            imprimirApp.start(nuevaVentana);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al abrir la ventana de impresión: " + e.getMessage());
        }
    }
}