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
    private ComboBox<String> waiterComboBox, clientComboBox;
    private TextField lunchCountField, priceField, moneyReceivedField;
    private DatePicker datePicker;
    private double fixedPrice = 14500.0;
    private Stage facturarStage;

    @Override
    public void start(Stage primaryStage) {
        facturarStage = primaryStage;  // Guardar la referencia al Stage de Facturar

        Label titleLabel = new Label("A D E S");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #0294b5;");

        // ComboBox para domiciliarios
        waiterComboBox = new ComboBox<>();
        waiterComboBox.setPromptText("Seleccione un domiciliario");
        waiterComboBox.getItems().addAll(getDomiciliariosFromDatabase());

        // Botón para abrir domiciliarios
        Button openWaiterButton = new Button("Crear Domic.");
        openWaiterButton.setStyle("-fx-font-size: 12px;");
        openWaiterButton.setOnAction(e -> openDomiciliariosWindow());

        // ComboBox para clientes
        clientComboBox = new ComboBox<>();
        clientComboBox.setPromptText("Seleccione un cliente");
        clientComboBox.getItems().addAll(getClientesFromDatabase());

        // Botón para abrir clientes
        Button openClientButton = new Button("Crear Cliente.");
        openClientButton.setStyle("-fx-font-size: 12px;");
        openClientButton.setOnAction(e -> openClientesWindow());

        lunchCountField = createTextField("Almuerzos:", "Ingrese cantidad de almuerzos");

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
        gridPane.add(new Label("Domiciliario:"), 0, 1);
        gridPane.add(waiterComboBox, 1, 1);
        gridPane.add(openWaiterButton, 2, 1);
        gridPane.add(new Label("Cliente:"), 0, 2);
        gridPane.add(clientComboBox, 1, 2);
        gridPane.add(openClientButton, 2, 2);
        addToGrid(gridPane, lunchCountField, 3);
        gridPane.add(priceLabel, 0, 4);
        gridPane.add(priceField, 1, 4);
        gridPane.add(dateLabel, 0, 5);
        gridPane.add(datePicker, 1, 5);
        gridPane.add(moneyReceivedLabel, 0, 6);
        gridPane.add(moneyReceivedField, 1, 6);
        gridPane.add(changeLabel, 0, 7, 2, 1);
        gridPane.add(totalizeButton, 0, 8, 2, 1);
        gridPane.add(newInvoiceButton, 0, 8, 4, 1);
        gridPane.add(backButton, 0, 8, 1, 1);
        gridPane.add(errorMessage, 0, 11, 2, 1);

        GridPane.setHalignment(totalizeButton, HPos.CENTER);
        GridPane.setHalignment(newInvoiceButton, HPos.RIGHT);
        GridPane.setHalignment(backButton, HPos.LEFT);
        GridPane.setHalignment(errorMessage, HPos.CENTER);
        GridPane.setHalignment(changeLabel, HPos.CENTER);

        Scene scene = new Scene(gridPane, 700, 600);
        scene.setFill(Color.web("#f4f4f4"));
        primaryStage.setTitle("Facturación Restaurante");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openDomiciliariosWindow() {
        try {
            // Cerrar la ventana actual (primaryStage)
            facturarStage.hide();

            // Crear una nueva instancia de la clase domiciliarios
            domiciliarios domiciliariosWindow = new domiciliarios(facturarStage);
            domiciliariosWindow.start(new Stage()); // Iniciar el nuevo Stage
        } catch (Exception e) {
            errorMessage.setText("Error al abrir Domiciliarios: " + e.getMessage());
        }
    }

    private void openClientesWindow() {
        try {
            // Cerrar la ventana actual (primaryStage)
            facturarStage.hide();

            // Crear una nueva instancia de la clase clientes
            clientes clientesWindow = new clientes(facturarStage);
            clientesWindow.start(new Stage()); // Iniciar el nuevo Stage
        } catch (Exception e) {
            errorMessage.setText("Error al abrir Clientes: " + e.getMessage());
        }
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

    private void generateInvoice() {
        try {
            String waiter = waiterComboBox.getValue();
            String client = clientComboBox.getValue();
            int lunchCount = Integer.parseInt(lunchCountField.getText());
            LocalDate date = datePicker.getValue();
            fixedPrice = Double.parseDouble(priceField.getText());
            double total = lunchCount * fixedPrice;
            double moneyReceived = Double.parseDouble(moneyReceivedField.getText());
            double change = moneyReceived - total;

            // Obtener dirección del cliente desde la base de datos
            String clientAddress = getClientAddressFromDatabase(client);

            String invoice = "Restaurante el buen sabor\n Nit:8622525\n Telefono:417669" +
                    "\n" +
                    "----------------------------------------\n" +
                    "Fecha: " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                    "Domiciliario: " + waiter + "\n" +
                    "Cliente: " + client + "\n" +
                    "Dirección: " + clientAddress + "\n" +
                    "Cantidad de Almuerzos: " + lunchCount + "\n" +
                    "Valor Unitario: $" + fixedPrice + "\n" +
                    "Total: $" + total + "\n" +
                    "Dinero Recibido: $" + moneyReceived + "\n" +
                    "Cambio a Devolver: $" + change + "\n" +
                    "----------------------------------------\n" +
                    "ESTE DOCUMENTO NO ES VALIDO COMO FACTURA ELECTRONICA\n" +
                    "CONSTANCIA DE ENTREGA\n" +
                    "SOFTWARE REALIZADO POR DAVID GUERRERO\n" +
                    "ADES SOFTWARE\n";

            try (FileWriter fileWriter = new FileWriter("factura.txt")) {
                fileWriter.write(invoice);
            }

            insertInvoiceIntoDatabase(invoice, total);

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

    private void resetFields() {
        waiterComboBox.setValue(null);
        clientComboBox.setValue(null);
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

    private List<String> getDomiciliariosFromDatabase() {
        List<String> domiciliarios = new ArrayList<>();
        String query = "SELECT CONCAT(nombre, ' ', apellido) AS nombre_completo FROM domiciliarios";
        try (Connection conn = conexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                domiciliarios.add(rs.getString("nombre_completo"));
            }
        } catch (SQLException e) {
            errorMessage.setText("Error al cargar los domiciliarios: " + e.getMessage());
        }
        return domiciliarios;
    }

    private List<String> getClientesFromDatabase() {
        List<String> clientes = new ArrayList<>();
        String query = "SELECT CONCAT(nombre, ' ', apellido) AS nombre_completo FROM clientes";
        try (Connection conn = conexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                clientes.add(rs.getString("nombre_completo"));
            }
        } catch (SQLException e) {
            errorMessage.setText("Error al cargar los clientes: " + e.getMessage());
        }
        return clientes;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
