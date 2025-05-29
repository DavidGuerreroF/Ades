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
import java.net.URLEncoder;
import java.sql.*;
import java.time.LocalDate;

public class Montarpedido extends Application {
    private TextField nombreCliente;
    private TextField apellidoCliente;
    private TextField telefonoCliente;
    private TextField direccionCliente;
    private DatePicker fechaPedido;
    private DatePicker fechaCobro;
    private CheckBox pagoInmediatoCheck;
    private Button btnCrearPedido;
    private Button btnCancelar;
    private TextField valorPedido;
    private TextField menuDiaField;
    private TextField dineroRecibidoField;
    private ComboBox<String> comboDomiciliario;
    private ObservableList<String> listaDomiciliarios;
    private Button btnClientes;
    private Button btnDomiciliarios;
    private Button btnAgregarMenu;
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
        fechaCobro = new DatePicker();
        fechaCobro.setPromptText("Seleccione la fecha de cobro");

        pagoInmediatoCheck = new CheckBox("Pago Inmediato");
        pagoInmediatoCheck.setStyle("-fx-font-size: 14px;");

        valorPedido = new TextField();
        valorPedido.setPromptText("Ingrese el valor del pedido");
        nombreCliente.setPromptText("Ingrese el nombre del cliente");
        apellidoCliente.setPromptText("Ingrese el apellido del cliente");
        telefonoCliente.setPromptText("Ingrese el teléfono del cliente");
        direccionCliente.setPromptText("Ingrese la dirección del cliente");

        menuDiaField = new TextField();
        menuDiaField.setPromptText("Ingrese el menú del día (Sopa, Plato, Jugo)");
        menuDiaField.setMinHeight(100);

        comboDomiciliario = new ComboBox<>();
        listaDomiciliarios = FXCollections.observableArrayList();

        btnCrearPedido = new Button("Crear Pedido");
        btnCancelar = new Button("Cancelar");
        btnClientes = new Button("Ir a Clientes");
        btnDomiciliarios = new Button("Ir a Domiciliarios");
        btnAgregarMenu = new Button("Agregar Menú");

        dineroRecibidoField = new TextField();
        dineroRecibidoField.setPromptText("Dinero recibido");
        dineroRecibidoField.setDisable(true);

        telefonoCliente.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,10}")) {
                return change;
            }
            return null;
        }));

        valorPedido.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*(\\.\\d{0,2})?")) {
                return change;
            }
            return null;
        }));

        dineroRecibidoField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*(\\.\\d{0,2})?")) {
                return change;
            }
            return null;
        }));

        // Listener para pago inmediato
        pagoInmediatoCheck.setOnAction(e -> {
            boolean selected = pagoInmediatoCheck.isSelected();
            fechaCobro.setDisable(selected);
            dineroRecibidoField.setDisable(!selected);
            if(!selected) dineroRecibidoField.clear();
        });
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
        root.add(crearCampo("Fecha de Cobro", fechaCobro), 0, 5);
        root.add(crearCampo("Pago Inmediato", pagoInmediatoCheck), 0, 6);
        root.add(crearCampo("Dinero recibido", dineroRecibidoField), 1, 5);
        root.add(crearCampo("Apellido", apellidoCliente), 1, 1);
        root.add(crearCampo("Dirección", direccionCliente), 1, 2);
        root.add(crearCampo("Domiciliario", comboDomiciliario), 1, 3);
        root.add(crearCampo("Valor", valorPedido), 1, 4);

        HBox botones = new HBox(30);
        botones.setAlignment(Pos.CENTER);
        botones.getChildren().addAll(btnCrearPedido, btnCancelar, btnClientes, btnDomiciliarios );
        root.add(botones, 0, 9, 2, 1);

        labelAyuda = new Label("AYUDA ADES: Si el cliente/domiciliario es nuevo, créelo posteriormente. Solo debe ingresar el teléfono y dar Enter para continuar.");
        labelAyuda.setStyle("-fx-font-size: 12px; -fx-text-fill: #333; -fx-font-weight: normal; -fx-alignment: center;");

        HBox ayudaBox = new HBox(labelAyuda);
        ayudaBox.setAlignment(Pos.CENTER);
        ayudaBox.setStyle("-fx-padding: 10px;");
        root.add(ayudaBox, 0, 10, 2, 1);
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
                mostrarAlerta(Alert.AlertType.ERROR, "No se encontró un cliente con ese número de teléfono, Crearlo primero por el atajo clientes.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    }

    private void crearPedido() {
        try (Connection connection = conexionDB.getConnection()) {
            String telefono = telefonoCliente.getText();
            String nombre = nombreCliente.getText();
            String apellido = apellidoCliente.getText();
            String direccion = direccionCliente.getText();
            String fechaPedidoStr = fechaPedido.getValue().toString();
            String estado = pagoInmediatoCheck.isSelected() ? "Cobrado" : "Pendiente";

            float valor = obtenerValorPedido();
            if (valor == -1) return;

            // Validación pago inmediato
            if (pagoInmediatoCheck.isSelected()) {
                float dineroRecibido;
                try {
                    dineroRecibido = Float.parseFloat(dineroRecibidoField.getText());
                } catch (NumberFormatException ex) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Debe ingresar un valor válido en Dinero recibido.");
                    return;
                }
                if (dineroRecibido < valor) {
                    mostrarAlerta(Alert.AlertType.ERROR, "El dinero recibido es menor al valor del pedido.");
                    return;
                }
                if (dineroRecibido > valor) {
                    float vueltas = dineroRecibido - valor;
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Pedido Cobrado: Debe dar vueltas al cliente por: $" + String.format("%.2f", vueltas));
                }
            }

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

            java.sql.Date fechaPedidoSQL = java.sql.Date.valueOf(fechaPedidoStr);
            java.sql.Date fechaCobroSQL = pagoInmediatoCheck.isSelected() ? null : java.sql.Date.valueOf(fechaCobro.getValue().toString());

            String query = "INSERT INTO PEDIDOS (id_cliente, id_domiciliario, fecha_pedido, estado, direccion_entrega, telefono_cliente, valor, plato_seleccionado, fechacobro) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idDomiciliario);
            stmt.setDate(3, fechaPedidoSQL);
            stmt.setString(4, estado);
            stmt.setString(5, direccion);
            stmt.setString(6, telefono);
            stmt.setFloat(7, valor);
            stmt.setString(8, menuDiaField.getText());
            stmt.setDate(9, fechaCobroSQL);

            stmt.executeUpdate();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Pedido creado exitosamente");

            // --- Envío WhatsApp al domiciliario ---
            String nombreDomiciliario = comboDomiciliario.getValue();
            String telefonoDomiciliario = obtenerTelefonoDomiciliario(nombreDomiciliario, connection);

            // Se asume todos los números son de Colombia y se les antepone el indicativo si hace falta
            if (telefonoDomiciliario != null && !telefonoDomiciliario.isEmpty()) {
                String telColombia = normalizarTelefonoColombia(telefonoDomiciliario);
                String mensaje = "Nuevo pedido:\nCliente: " + nombre + " " + apellido +
                        "\nTel: " + telefono +
                        "\nDirección: " + direccion +
                        "\nMenú: " + menuDiaField.getText() +
                        "\nValor: $" + valorPedido.getText() +
                        "\nFecha: " + fechaPedido.getValue() +
                        "\nPago: " + (pagoInmediatoCheck.isSelected() ? "Inmediato" : "Contra entrega o por cobrar");
                enviarWhatsappADomiciliario(telColombia, mensaje);
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "El domiciliario no tiene número de WhatsApp registrado.");
            }
            // --- Fin WhatsApp ---

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al crear el pedido: " + e.getMessage());
        }
    }

    // Si el número no empieza por +57, se lo antepone automáticamente
    private String normalizarTelefonoColombia(String numero) {
        String tel = numero.replaceAll("\\D", ""); // quita todo menos dígitos
        if (tel.length() == 10) { // celular colombiano común
            return "57" + tel;
        } else if (tel.startsWith("57") && tel.length() == 12) { // ya tiene indicativo sin +
            return tel;
        } else if (tel.startsWith("57") && tel.length() == 11) { // casos raros
            return tel;
        } else if (tel.startsWith("+57")) {
            return tel.replace("+", "");
        }
        return tel;
    }

    private String obtenerTelefonoDomiciliario(String nombreDomiciliario, Connection connection) throws SQLException {
        String query = "SELECT telefono FROM DOMICILIARIOS WHERE nombre = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nombreDomiciliario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("telefono");
            }
        }
        return null;
    }

    private void enviarWhatsappADomiciliario(String telefonoDomiciliario, String mensaje) {
        try {
            String url = "https://wa.me/" + telefonoDomiciliario + "?text=" + URLEncoder.encode(mensaje, "UTF-8");
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo abrir WhatsApp: " + e.getMessage());
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
            writer.write("Valor: " + "$" + valor + "\n");
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

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo, mensaje);
        alert.showAndWait();
    }
}