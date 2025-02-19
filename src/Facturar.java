import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Facturar extends Application {
    private Label errorMessage, changeLabel;
    private ComboBox<String> pedidoComboBox;
    private TextField lunchCountField, priceField, moneyReceivedField, waiterField, clientField, addressField;
    private DatePicker datePicker;
    private double fixedPrice = 14500.0;
    private Stage facturarStage;

    public Facturar(CatalogoPedidos.Pedido selectedPedido) {
    }

    public Facturar() {

    }

    @Override
    public void start(Stage primaryStage) {
        facturarStage = primaryStage;  // Guardar la referencia al Stage de Facturar

        Label titleLabel = new Label("A D E S  F A C T U R A");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #0294b5;");

        // ComboBox para pedidos pendientes
        pedidoComboBox = new ComboBox<>();
        pedidoComboBox.setPromptText("Seleccione un pedido pendiente");
        List<String> pedidosPendientes = getPedidosPendientesFromDatabase();
        pedidosPendientes.add("Manual"); // Agregar opción "Ninguno"
        pedidoComboBox.getItems().addAll(pedidosPendientes);
        pedidoComboBox.setOnAction(e -> populatePedidoDetails(pedidoComboBox.getValue()));

        waiterField = createTextField("Domiciliario:", "Ingrese el domiciliario");
        clientField = createTextField("Cliente:", "Ingrese el cliente");
        addressField = createTextField("Dirección:", "Ingrese la dirección");

        lunchCountField = createTextField("Cantidad de Almuerzos:", "Ingrese cantidad de almuerzos");

        Label priceLabel = new Label("Precio Unitario:");
        priceField = new TextField(String.valueOf(fixedPrice));
        priceField.setStyle("-fx-font-size: 18px;");

        Label dateLabel = new Label("Fecha:");
        datePicker = new DatePicker(LocalDate.now());

        Label moneyReceivedLabel = new Label("Dinero Recibido:");
        moneyReceivedField = new TextField();
        moneyReceivedField.setStyle("-fx-font-size: 18px;");

        changeLabel = new Label("Cambio a Devolver: $0.00");
        changeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: green;");

        Button totalizeButton = new Button("Totalizar");
        totalizeButton.setStyle("-fx-font-size: 18px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 15px 25px; -fx-background-radius: 20px;");
        totalizeButton.setOnAction(e -> generateInvoice());

        Button newInvoiceButton = new Button("Nueva Factura");
        newInvoiceButton.setStyle("-fx-font-size: 18px; -fx-background-color: #0294b5; -fx-text-fill: white; -fx-padding: 15px 25px; -fx-background-radius: 20px;");
        newInvoiceButton.setOnAction(e -> resetFields());

        Button backButton = new Button("Volver");
        backButton.setStyle("-fx-font-size: 18px; -fx-background-color: #0294b5; -fx-text-fill: white; -fx-padding: 15px 25px; -fx-background-radius: 20px;");
        backButton.setOnAction(e -> goBackToMainMenu(primaryStage));

        errorMessage = new Label();
        errorMessage.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setPadding(new Insets(40, 40, 40, 40));

        gridPane.add(titleLabel, 0, 0, 2, 1);
        gridPane.add(new Label("Pedido:"), 0, 1);
        gridPane.add(pedidoComboBox, 1, 1);
        addToGrid(gridPane, waiterField, 2);
        addToGrid(gridPane, clientField, 3);
        addToGrid(gridPane, addressField, 4);
        addToGrid(gridPane, lunchCountField, 5);
        gridPane.add(priceLabel, 0, 6);
        gridPane.add(priceField, 1, 6);
        gridPane.add(dateLabel, 0, 7);
        gridPane.add(datePicker, 1, 7);
        gridPane.add(moneyReceivedLabel, 0, 8);
        gridPane.add(moneyReceivedField, 1, 8);
        gridPane.add(changeLabel, 0, 9, 2, 1);
        gridPane.add(totalizeButton, 0, 10, 2, 1);
        gridPane.add(newInvoiceButton, 0, 10, 2, 1);
        gridPane.add(backButton, 0, 10, 2, 1);
        gridPane.add(errorMessage, 0, 13, 2, 1);

        GridPane.setHalignment(totalizeButton, HPos.CENTER);
        GridPane.setHalignment(newInvoiceButton, HPos.RIGHT);
        GridPane.setHalignment(backButton, HPos.LEFT);
        GridPane.setHalignment(errorMessage, HPos.CENTER);
        GridPane.setHalignment(changeLabel, HPos.CENTER);

        Scene scene = new Scene(gridPane, 800, 800);
        scene.setFill(Color.web("#f4f4f4"));
        primaryStage.setTitle("Facturación");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TextField createTextField(String labelText, String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setStyle("-fx-font-size: 18px; -fx-padding: 10px;");
        return textField;
    }

    private void addToGrid(GridPane gridPane, TextField field, int row) {
        gridPane.add(new Label(field.getPromptText()), 0, row);
        gridPane.add(field, 1, row);
    }

    private void populatePedidoDetails(String pedidoInfo) {
        if (pedidoInfo != null && !pedidoInfo.equals("Ninguno")) {
            String[] details = pedidoInfo.split(" - ");
            String pedidoId = details[0];
            try (Connection connection = conexionDB.getConnection()) {
                String query = "SELECT c.nombre AS cliente, c.telefono AS telefono, c.direccion AS direccion, d.nombre AS domiciliario, p.fecha_pedido AS fecha, p.valor AS valor, p.plato_seleccionado AS menu " +
                        "FROM PEDIDOS p " +
                        "JOIN CLIENTES c ON p.id_cliente = c.id " +
                        "JOIN DOMICILIARIOS d ON p.id_domiciliario = d.id " +
                        "WHERE p.id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, Integer.parseInt(pedidoId));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    waiterField.setText(rs.getString("domiciliario"));
                    clientField.setText(rs.getString("cliente"));
                    addressField.setText(rs.getString("direccion"));
                    lunchCountField.setText("1"); // Asumimos que cada pedido tiene un almuerzo
                    priceField.setText(String.valueOf(rs.getDouble("valor")));
                    datePicker.setValue(LocalDate.parse(rs.getString("fecha")));
                    waiterField.setEditable(false);
                    clientField.setEditable(false);
                    addressField.setEditable(false);
                    lunchCountField.setEditable(false);
                    priceField.setEditable(false);
                    datePicker.setEditable(false);
                }
            } catch (SQLException e) {
                errorMessage.setText("Error al cargar detalles del pedido: " + e.getMessage());
            }
        } else {
            waiterField.setEditable(true);
            clientField.setEditable(true);
            addressField.setEditable(true);
            lunchCountField.setEditable(true);
            priceField.setEditable(true);
            datePicker.setEditable(true);
            waiterField.clear();
            clientField.clear();
            addressField.clear();
            lunchCountField.clear();
            priceField.setText(String.valueOf(fixedPrice));
            datePicker.setValue(LocalDate.now());
        }
    }

    private void generateInvoice() {
        try {
            String pedidoInfo = pedidoComboBox.getValue();
            if (pedidoInfo == null) {
                errorMessage.setText("Seleccione un pedido.");
                return;
            }

            String waiter = waiterField.getText();
            String client = clientField.getText();
            String address = addressField.getText();
            int lunchCount = Integer.parseInt(lunchCountField.getText());
            LocalDate date = datePicker.getValue();
            fixedPrice = Double.parseDouble(priceField.getText());
            double total = lunchCount * fixedPrice;
            double moneyReceived = Double.parseDouble(moneyReceivedField.getText());
            double change = moneyReceived - total;

            String invoice = "Restaurante el buen sabor\n Nit:8622525\n Telefono:417669" +
                    "\n" +
                    "----------------------------------------\n" +
                    "----------------------------------------\n" +
                    "Fecha: " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                    "Domiciliario: " + waiter + "\n" +
                    "Cliente: " + client + "\n" +
                    "Dirección: " + address + "\n" +
                    "Cantidad de Almuerzos: " + lunchCount + "\n" +
                    "Valor Unitario: $" + fixedPrice + "\n" +
                    "Total: $" + total + "\n" +
                    "Dinero Recibido: $" + moneyReceived + "\n" +
                    "Cambio a Devolver: $" + change + "\n" +
                    "----------------------------------------\n" +
                    "----------------------------------------\n" +
                    "ESTE DOCUMENTO NO ES VALIDO COMO FACTURA\n ELECTRONICA\n" +
                    "CONSTANCIA DE ENTREGA\n" +
                    "Todos los derechos reservados. 2025 \n" +
                    "Ades Software\n";

            try (FileWriter fileWriter = new FileWriter("factura.txt")) {
                fileWriter.write(invoice);
            }

            insertInvoiceIntoDatabase(invoice, total);

            if (!pedidoInfo.equals("Manual")) {
                String[] details = pedidoInfo.split(" - ");
                String pedidoId = details[0];
                updatePedidoStatus(Integer.parseInt(pedidoId), "Facturado");
            }

            errorMessage.setStyle("-fx-text-fill: green;");
            errorMessage.setText("Factura generada correctamente.");
            changeLabel.setText("Cambio a Devolver: $" + change);

            Runtime.getRuntime().exec("notepad factura.txt");
        } catch (NumberFormatException | IOException e) {
            errorMessage.setText("Error: Verifique los datos ingresados.");
        }
    }

    private String getClientAddressFromDatabase(String clientName) {
        String address = "";
        String query = "SELECT direccion FROM clientes WHERE CONCAT(nombre, ' ', apellido) = ?";
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, clientName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                address = rs.getString("direccion");
            }
        } catch (SQLException e) {
            errorMessage.setText("Error al cargar la dirección del cliente: " + e.getMessage());
        }
        return address;
    }

    private void insertInvoiceIntoDatabase(String invoiceDetails, double totalAmount) {
        String query = "INSERT INTO facturas (detalle_factura, valor_factura) VALUES (?, ?)";
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, invoiceDetails);
            pstmt.setDouble(2, totalAmount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            errorMessage.setText("Error al guardar la factura en la base de datos: " + e.getMessage());
        }
    }

    private void updatePedidoStatus(int pedidoId, String newStatus) {
        String query = "UPDATE pedidos SET estado = ? WHERE id = ?";
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, pedidoId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            errorMessage.setText("Error al actualizar el estado del pedido: " + e.getMessage());
        }
    }

    private void resetFields() {
        pedidoComboBox.setValue(null);
        waiterField.clear();
        clientField.clear();
        addressField.clear();
        lunchCountField.clear();
        priceField.setText(String.valueOf(fixedPrice));
        datePicker.setValue(LocalDate.now());
        moneyReceivedField.clear();
        changeLabel.setText("Cambio a Devolver: $0.00");
        errorMessage.setText("");
    }

    private void goBackToMainMenu(Stage primaryStage) {
        // Crear una nueva instancia de la clase Despachos
        despachos despachosWindow = new despachos();

        // Cerrar la ventana actual (Facturar)
        facturarStage.close();

        // Llamar al método start() de Despachos (esto mostrará la ventana Despachos)
        despachosWindow.start(new Stage());
    }

    private List<String> getPedidosPendientesFromDatabase() {
        List<String> pedidosPendientes = new ArrayList<>();
        String query = "SELECT p.id, CONCAT(d.nombre, ' ', d.apellido) AS domiciliario, CONCAT(c.nombre, ' ', c.apellido) AS cliente " +
                "FROM pedidos p " +
                "JOIN domiciliarios d ON p.id_domiciliario = d.id " +
                "JOIN clientes c ON p.id_cliente = c.id " +
                "WHERE p.estado = 'Pendiente'";
        try (Connection conn = conexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String domiciliario = rs.getString("domiciliario");
                String cliente = rs.getString("cliente");
                pedidosPendientes.add(id + " - " + domiciliario + " - " + cliente);
            }
        } catch (SQLException e) {
            errorMessage.setText("Error al cargar los pedidos pendientes: " + e.getMessage());
        }
        return pedidosPendientes;
    }

    public static void main(String[] args) {
        launch(args);
    }
}