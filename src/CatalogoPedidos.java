import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.sql.*;
import java.time.LocalDate;
import java.io.*;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

public class CatalogoPedidos extends Application {
    private TableView<Pedido> tablaPedidos;
    private ObservableList<Pedido> listaPedidos;
    private Button btnCancelar, btnVolver, btnFiltrar, btnImprimirPedido;
    private DatePicker datePickerInicio, datePickerFin;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Inicializamos los controles de la interfaz
        tablaPedidos = new TableView<>();
        listaPedidos = FXCollections.observableArrayList();

        // Configuración de la tabla
        TableColumn<Pedido, Integer> colId = new TableColumn<>("No.Pedido");
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        TableColumn<Pedido, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(cellData -> cellData.getValue().clienteProperty());

        TableColumn<Pedido, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty());

        TableColumn<Pedido, String> colDireccion = new TableColumn<>("Dirección");
        colDireccion.setCellValueFactory(cellData -> cellData.getValue().direccionProperty());

        TableColumn<Pedido, String> colDomiciliario = new TableColumn<>("Domiciliario");
        colDomiciliario.setCellValueFactory(cellData -> cellData.getValue().domiciliarioProperty());

        TableColumn<Pedido, String> colFecha = new TableColumn<>("Fecha Pedido");
        colFecha.setCellValueFactory(cellData -> cellData.getValue().fechaPedidoProperty());

        TableColumn<Pedido, String> colfechacobro = new TableColumn<>("Fecha de Cobro");
        colfechacobro.setCellValueFactory(cellData -> cellData.getValue().fechacobroProperty());

        TableColumn<Pedido, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());

        TableColumn<Pedido, Double> colValor = new TableColumn<>("Valor");
        colValor.setCellValueFactory(cellData -> cellData.getValue().valorProperty().asObject());

        TableColumn<Pedido, String> colPlatoSeleccionado = new TableColumn<>("Menu Seleccionado");
        colPlatoSeleccionado.setCellValueFactory(cellData -> cellData.getValue().platoSeleccionadoProperty());

        tablaPedidos.getColumns().addAll(colId, colCliente, colTelefono, colDireccion, colDomiciliario, colFecha, colfechacobro, colEstado, colValor, colPlatoSeleccionado);
        tablaPedidos.setItems(listaPedidos);
        tablaPedidos.setPrefWidth(800);
        tablaPedidos.setPrefHeight(400);
        tablaPedidos.setStyle("-fx-border-color: #2d3b48; -fx-border-width: 1px; -fx-background-radius: 10;");

        // Botones de acción con diseño mejorado (estilos directos)
        btnCancelar = new Button("Cancelar Pedido");
        btnCancelar.setStyle(
                "-fx-background-color: #ff5e57;" +
                        "-fx-text-fill: #fff;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 24;" +
                        "-fx-font-size: 15px;" +
                        "-fx-border-color: #d32f2f;" +
                        "-fx-border-width: 2;"
        );
        btnCancelar.setOnMouseEntered(e -> btnCancelar.setStyle(
                "-fx-background-color: #ff2e1f;" +
                        "-fx-text-fill: #fff;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 24;" +
                        "-fx-font-size: 15px;" +
                        "-fx-border-color: #b71c1c;" +
                        "-fx-border-width: 2;"
        ));
        btnCancelar.setOnMouseExited(e -> btnCancelar.setStyle(
                "-fx-background-color: #ff5e57;" +
                        "-fx-text-fill: #fff;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 24;" +
                        "-fx-font-size: 15px;" +
                        "-fx-border-color: #d32f2f;" +
                        "-fx-border-width: 2;"
        ));
        btnCancelar.setOnAction(event -> cancelarPedido());

        btnVolver = new Button("Volver");
        btnVolver.setStyle(
                "-fx-background-color: #607d8b;" +
                        "-fx-text-fill: #fff;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 24;" +
                        "-fx-font-size: 15px;" +
                        "-fx-border-color: #37474f;" +
                        "-fx-border-width: 2;"
        );
        btnVolver.setOnMouseEntered(e -> btnVolver.setStyle(
                "-fx-background-color: #455a64;" +
                        "-fx-text-fill: #fff;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 24;" +
                        "-fx-font-size: 15px;" +
                        "-fx-border-color: #263238;" +
                        "-fx-border-width: 2;"
        ));
        btnVolver.setOnMouseExited(e -> btnVolver.setStyle(
                "-fx-background-color: #607d8b;" +
                        "-fx-text-fill: #fff;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 24;" +
                        "-fx-font-size: 15px;" +
                        "-fx-border-color: #37474f;" +
                        "-fx-border-width: 2;"
        ));
        btnVolver.setOnAction(event -> volverAlMenuPrincipal(primaryStage));

        btnImprimirPedido = new Button("Imprimir a PDF");
        btnImprimirPedido.setStyle(
                "-fx-background-color: #3adb76;" +
                        "-fx-text-fill: #fff;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 24;" +
                        "-fx-font-size: 15px;" +
                        "-fx-border-color: #1baf5e;" +
                        "-fx-border-width: 2;"
        );
        btnImprimirPedido.setOnMouseEntered(e -> btnImprimirPedido.setStyle(
                "-fx-background-color: #26a65b;" +
                        "-fx-text-fill: #fff;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 24;" +
                        "-fx-font-size: 15px;" +
                        "-fx-border-color: #0e8040;" +
                        "-fx-border-width: 2;"
        ));
        btnImprimirPedido.setOnMouseExited(e -> btnImprimirPedido.setStyle(
                "-fx-background-color: #3adb76;" +
                        "-fx-text-fill: #fff;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 24;" +
                        "-fx-font-size: 15px;" +
                        "-fx-border-color: #1baf5e;" +
                        "-fx-border-width: 2;"
        ));
        btnImprimirPedido.setOnAction(event -> imprimirPedidoPDF());

        datePickerInicio = new DatePicker();
        datePickerInicio.setPromptText("Fecha Inicio");

        datePickerFin = new DatePicker();
        datePickerFin.setPromptText("Fecha Fin");

        btnFiltrar = new Button("Filtrar");
        btnFiltrar.setStyle(
                "-fx-background-color: #2196f3;" +
                        "-fx-text-fill: #fff;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 18;" +
                        "-fx-font-size: 14px;" +
                        "-fx-border-color: #1565c0;" +
                        "-fx-border-width: 2;"
        );
        btnFiltrar.setOnMouseEntered(e -> btnFiltrar.setStyle(
                "-fx-background-color: #0d47a1;" +
                        "-fx-text-fill: #fff;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 18;" +
                        "-fx-font-size: 14px;" +
                        "-fx-border-color: #002171;" +
                        "-fx-border-width: 2;"
        ));
        btnFiltrar.setOnMouseExited(e -> btnFiltrar.setStyle(
                "-fx-background-color: #2196f3;" +
                        "-fx-text-fill: #fff;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 18;" +
                        "-fx-font-size: 14px;" +
                        "-fx-border-color: #1565c0;" +
                        "-fx-border-width: 2;"
        ));
        btnFiltrar.setOnAction(event -> filtrarPedidos());

        HBox filtros = new HBox(10, new Label("Filtrar por Fechas:"), datePickerInicio, datePickerFin, btnFiltrar);
        filtros.setAlignment(Pos.CENTER);

        HBox botones = new HBox(20);
        botones.setAlignment(Pos.CENTER);
        botones.getChildren().addAll(btnCancelar, btnImprimirPedido, btnVolver);

        VBox root = new VBox(20, filtros, tablaPedidos, botones);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f4f4f9 80%, #dde6ed 100%); -fx-padding: 20px; -fx-background-radius: 12;");

        Scene scene = new Scene(root, 900, 650);
        primaryStage.setTitle("Catálogo de Pedidos");
        primaryStage.setScene(scene);
        primaryStage.show();

        cargarPedidos();
    }


    // Método para imprimir un pedido a PDF
    private void imprimirPedidoPDF() {
        Pedido selectedPedido = tablaPedidos.getSelectionModel().getSelectedItem();
        if (selectedPedido != null) {
            try {
                // Ruta del archivo PDF
                String pdfPath = "pedido_" + selectedPedido.getId() + ".pdf";

                // Crear el PDF
                PdfWriter writer = new PdfWriter(pdfPath);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Agregar contenido al PDF
                document.add(new Paragraph("Detalles del Pedido"));
                document.add(new Paragraph("No.Pedido: " + selectedPedido.getId()));
                document.add(new Paragraph("Cliente: " + selectedPedido.getCliente()));
                document.add(new Paragraph("Teléfono: " + selectedPedido.getTelefono()));
                document.add(new Paragraph("Dirección: " + selectedPedido.getDireccion()));
                document.add(new Paragraph("Domiciliario: " + selectedPedido.getDomiciliario()));
                document.add(new Paragraph("Fecha del Pedido: " + selectedPedido.getFechaPedido()));
                document.add(new Paragraph("Fecha de Cobro: " + selectedPedido.getfechacobro()));
                document.add(new Paragraph("Estado: " + selectedPedido.getEstado()));
                document.add(new Paragraph("Valor: $" + selectedPedido.getValor()));
                document.add(new Paragraph("Plato Seleccionado: " + selectedPedido.getPlatoSeleccionado()));

                // Cerrar el documento
                document.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Pedido exportado a PDF exitosamente: " + pdfPath);
                alert.show();

                // Abrir el PDF automáticamente
                new ProcessBuilder("cmd", "/c", pdfPath).start();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error al generar el PDF: " + e.getMessage());
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, seleccione un pedido para imprimir.");
            alert.show();
        }
    }

    // Otros métodos como cargarPedidos, filtrarPedidos, cancelarPedido, etc.
    private void cargarPedidos() {
        listaPedidos.clear();
        try (Connection connection = conexionDB.getConnection()) {
            String query = "SELECT p.id, c.nombre AS cliente, c.telefono, c.direccion, d.nombre AS domiciliario, p.fecha_pedido, p.fechacobro, p.estado, p.valor, p.plato_seleccionado " +
                    "FROM PEDIDOS p " +
                    "JOIN CLIENTES c ON p.id_cliente = c.id " +
                    "JOIN DOMICILIARIOS d ON p.id_domiciliario = d.id";

            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String cliente = rs.getString("cliente");
                String telefono = rs.getString("telefono");
                String direccion = rs.getString("direccion");
                String domiciliario = rs.getString("domiciliario");
                String fechaPedido = rs.getString("fecha_pedido");
                String fechacobro = rs.getString("fechacobro");
                String estado = rs.getString("estado");
                double valor = rs.getDouble("valor");
                String platoSeleccionado = rs.getString("plato_seleccionado");

                listaPedidos.add(new Pedido(id, cliente, telefono, direccion, domiciliario, fechaPedido, fechacobro, estado, valor, platoSeleccionado));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al cargar los pedidos: " + e.getMessage());
            alert.show();
        }
    }

    private void filtrarPedidos() {
        LocalDate fechaInicio = datePickerInicio.getValue();
        LocalDate fechaFin = datePickerFin.getValue();

        if (fechaInicio != null && fechaFin != null) {
            listaPedidos.clear();
            try (Connection connection = conexionDB.getConnection()) {
                String query = "SELECT p.id, c.nombre AS cliente, c.telefono, c.direccion, d.nombre AS domiciliario, p.fecha_pedido, p.fechacobro, p.estado, p.valor, p.plato_seleccionado " +
                        "FROM PEDIDOS p " +
                        "JOIN CLIENTES c ON p.id_cliente = c.id " +
                        "JOIN DOMICILIARIOS d ON p.id_domiciliario = d.id " +
                        "WHERE p.fecha_pedido BETWEEN ? AND ?";

                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setDate(1, Date.valueOf(fechaInicio));
                stmt.setDate(2, Date.valueOf(fechaFin));
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String cliente = rs.getString("cliente");
                    String telefono = rs.getString("telefono");
                    String direccion = rs.getString("direccion");
                    String domiciliario = rs.getString("domiciliario");
                    String fechaPedido = rs.getString("fecha_pedido");
                    String fechacobro = rs.getString("fechacobro");
                    String estado = rs.getString("estado");
                    double valor = rs.getDouble("valor");
                    String platoSeleccionado = rs.getString("plato_seleccionado");

                    listaPedidos.add(new Pedido(id, cliente, telefono, direccion, domiciliario, fechaPedido, fechacobro, estado, valor, platoSeleccionado));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error al filtrar los pedidos: " + e.getMessage());
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, seleccione las fechas de inicio y fin para filtrar.");
            alert.show();
        }
    }

    private void cancelarPedido() {
        Pedido selectedPedido = tablaPedidos.getSelectionModel().getSelectedItem();
        if (selectedPedido != null) {
            try (Connection connection = conexionDB.getConnection()) {
                String query = "UPDATE PEDIDOS SET estado = ? WHERE id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, "Cancelado");
                stmt.setInt(2, selectedPedido.getId());
                stmt.executeUpdate();

                selectedPedido.setEstado("Cancelado");
                tablaPedidos.refresh();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Pedido cancelado exitosamente.");
                alert.show();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error al cancelar el pedido: " + e.getMessage());
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, seleccione un pedido para cancelar.");
            alert.show();
        }
    }

    private void volverAlMenuPrincipal(Stage primaryStage) {
        primaryStage.hide();
        try {
            despachos despachos = new despachos();
            despachos.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Pedido {
        private final IntegerProperty id;
        private final StringProperty cliente;
        private final StringProperty telefono;
        private final StringProperty direccion;
        private final StringProperty domiciliario;
        private final StringProperty fechaPedido;
        private final StringProperty fechacobro;
        private final StringProperty estado;
        private final DoubleProperty valor;
        private final StringProperty platoSeleccionado;

        public Pedido(int id, String cliente, String telefono, String direccion, String domiciliario, String fechaPedido, String fechacobro, String estado, double valor, String platoSeleccionado) {
            this.id = new SimpleIntegerProperty(id);
            this.cliente = new SimpleStringProperty(cliente);
            this.telefono = new SimpleStringProperty(telefono);
            this.direccion = new SimpleStringProperty(direccion);
            this.domiciliario = new SimpleStringProperty(domiciliario);
            this.fechaPedido = new SimpleStringProperty(fechaPedido);
            this.fechacobro = new SimpleStringProperty(fechacobro);
            this.estado = new SimpleStringProperty(estado);
            this.valor = new SimpleDoubleProperty(valor);
            this.platoSeleccionado = new SimpleStringProperty(platoSeleccionado);
        }

        public int getId() { return id.get(); }
        public String getCliente() { return cliente.get(); }
        public String getTelefono() { return telefono.get(); }
        public String getDireccion() { return direccion.get(); }
        public String getDomiciliario() { return domiciliario.get(); }
        public String getFechaPedido() { return fechaPedido.get(); }
        public String getfechacobro() { return fechacobro.get(); }
        public String getEstado() { return estado.get(); }
        public double getValor() { return valor.get(); }
        public String getPlatoSeleccionado() { return platoSeleccionado.get(); }

        public IntegerProperty idProperty() { return id; }
        public StringProperty clienteProperty() { return cliente; }
        public StringProperty telefonoProperty() { return telefono; }
        public StringProperty direccionProperty() { return direccion; }
        public StringProperty domiciliarioProperty() { return domiciliario; }
        public StringProperty fechaPedidoProperty() { return fechaPedido; }
        public StringProperty fechacobroProperty() { return fechacobro; }
        public StringProperty estadoProperty() { return estado; }
        public DoubleProperty valorProperty() { return valor; }
        public StringProperty platoSeleccionadoProperty() { return platoSeleccionado; }

        public void setEstado(String estado) { this.estado.set(estado); }
    }
}