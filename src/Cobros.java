import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.sql.*;
import java.time.LocalDate;
import java.io.*;

public class Cobros extends Application {
    private TableView<Pedido> tablaPedidos;
    private ObservableList<Pedido> listaPedidos;
    private Button btnCobrar, btnVolver, btnFiltrar, btnExportarNotepad;
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

        TableColumn<Pedido, String> colFechaCobro = new TableColumn<>("Fecha de Cobro"); // Nueva columna
        colFechaCobro.setCellValueFactory(cellData -> cellData.getValue().fechaCobroProperty());

        TableColumn<Pedido, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());

        TableColumn<Pedido, Double> colValor = new TableColumn<>("Valor Pendiente");
        colValor.setCellValueFactory(cellData -> cellData.getValue().valorProperty().asObject());

        TableColumn<Pedido, String> colPlatoSeleccionado = new TableColumn<>("Menu Seleccionado");
        colPlatoSeleccionado.setCellValueFactory(cellData -> cellData.getValue().platoSeleccionadoProperty());

        tablaPedidos.getColumns().addAll(colId, colCliente, colTelefono, colDireccion, colDomiciliario, colFecha, colFechaCobro, colEstado, colValor, colPlatoSeleccionado);
        tablaPedidos.setItems(listaPedidos);
        tablaPedidos.setPrefWidth(800);
        tablaPedidos.setPrefHeight(400);
        tablaPedidos.setStyle("-fx-border-color: #2d3b48; -fx-border-width: 1px;");

        // Botones de acción con diseño mejorado
        btnCobrar = new Button("Cobrar"); // Nuevo botón para marcar como cobrado
        btnCobrar.setStyle(
                "-fx-background-color: #FFD700; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-cursor: hand;"
        );
        btnCobrar.setOnAction(event -> mostrarOpcionesCobro());

        btnVolver = new Button("Volver");
        btnVolver.setStyle(
                "-fx-background-color: #4682B4; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-cursor: hand;"
        );
        btnVolver.setOnAction(event -> volverAlMenuPrincipal(primaryStage));

        btnExportarNotepad = new Button("Exportar a Notepad"); // Nuevo botón para exportar a Notepad
        btnExportarNotepad.setStyle(
                "-fx-background-color: #32CD32; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-cursor: hand;"
        );
        btnExportarNotepad.setOnAction(event -> exportarANotepad());

        // Filtros de fecha
        datePickerInicio = new DatePicker();
        datePickerInicio.setPromptText("Fecha Inicio");

        datePickerFin = new DatePicker();
        datePickerFin.setPromptText("Fecha Fin");

        btnFiltrar = new Button("Filtrar");
        btnFiltrar.setStyle(
                "-fx-background-color: #1E90FF; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-cursor: hand;"
        );
        btnFiltrar.setOnAction(event -> filtrarPedidos());

        HBox filtros = new HBox(10, new Label("Filtrar por Fechas:"), datePickerInicio, datePickerFin, btnFiltrar);
        filtros.setAlignment(Pos.CENTER);

        HBox botones = new HBox(20);
        botones.setAlignment(Pos.CENTER);
        botones.getChildren().addAll(btnCobrar, btnVolver, btnExportarNotepad);

        // Layout principal
        VBox root = new VBox(20, filtros, tablaPedidos, botones);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f4f4f9; -fx-padding: 20px;");

        // Configuración de la escena
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Cobros de Pedidos");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Cargar pedidos desde la base de datos
        cargarPedidos();
    }

    // Método para cargar los pedidos desde la base de datos
    private void cargarPedidos() {
        listaPedidos.clear();
        try (Connection connection = conexionDB.getConnection()) {
            // Consulta SQL que une PEDIDOS, CLIENTES, DOMICILIARIOS y filtra los pedidos en estado "Pendiente"
            String query = "SELECT p.id, c.nombre AS cliente, c.telefono, c.direccion, d.nombre AS domiciliario, p.fecha_pedido, p.fechacobro, p.estado, p.valor, p.plato_seleccionado " +
                    "FROM PEDIDOS p " +
                    "JOIN CLIENTES c ON p.id_cliente = c.id " +
                    "JOIN DOMICILIARIOS d ON p.id_domiciliario = d.id " +
                    "WHERE p.estado = 'Pendiente'";

            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String cliente = rs.getString("cliente");
                String telefono = rs.getString("telefono");
                String direccion = rs.getString("direccion");
                String domiciliario = rs.getString("domiciliario");
                String fechaPedido = rs.getString("fecha_pedido");
                String fechaCobro = rs.getString("fechacobro"); // Obtener fecha de cobro si existe
                String estado = rs.getString("estado");
                double valor = rs.getDouble("valor");
                String platoSeleccionado = rs.getString("plato_seleccionado");

                listaPedidos.add(new Pedido(id, cliente, telefono, direccion, domiciliario, fechaPedido, fechaCobro, estado, valor, platoSeleccionado));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al cargar los pedidos: " + e.getMessage());
            alert.show();
        }
    }

    // Método para cargar los pedidos filtrados por fechas desde la base de datos
    private void filtrarPedidos() {
        LocalDate fechaInicio = datePickerInicio.getValue();
        LocalDate fechaFin = datePickerFin.getValue();

        if (fechaInicio != null && fechaFin != null) {
            listaPedidos.clear();
            try (Connection connection = conexionDB.getConnection()) {
                // Consulta SQL que une PEDIDOS, CLIENTES, DOMICILIARIOS y filtra por fechas y estado "Pendiente"
                String query = "SELECT p.id, c.nombre AS cliente, c.telefono, c.direccion, d.nombre AS domiciliario, p.fecha_pedido, p.fechacobro, p.estado, p.valor, p.plato_seleccionado " +
                        "FROM PEDIDOS p " +
                        "JOIN CLIENTES c ON p.id_cliente = c.id " +
                        "JOIN DOMICILIARIOS d ON p.id_domiciliario = d.id " +
                        "WHERE p.estado = 'Pendiente' AND p.fecha_pedido BETWEEN ? AND ?";

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
                    String fechaCobro = rs.getString("fechacobro"); // Obtener fecha de cobro si existe
                    String estado = rs.getString("estado");
                    double valor = rs.getDouble("valor");
                    String platoSeleccionado = rs.getString("plato_seleccionado");

                    listaPedidos.add(new Pedido(id, cliente, telefono, direccion, domiciliario, fechaPedido, fechaCobro, estado, valor, platoSeleccionado));
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

    // Método para mostrar opciones de cobro
    private void mostrarOpcionesCobro() {
        Pedido selectedPedido = tablaPedidos.getSelectionModel().getSelectedItem();
        if (selectedPedido != null) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Opciones de Cobro");

            ButtonType btnPagoTotal = new ButtonType("Pago Total", ButtonBar.ButtonData.OK_DONE);
            ButtonType btnPagoParcial = new ButtonType("Pago Parcial", ButtonBar.ButtonData.OTHER);

            dialog.getDialogPane().getButtonTypes().addAll(btnPagoTotal, btnPagoParcial, ButtonType.CANCEL);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == btnPagoTotal) {
                    marcarCobrado(selectedPedido, true, 0);
                } else if (dialogButton == btnPagoParcial) {
                    mostrarDialogoPagoParcial(selectedPedido);
                }
                return null;
            });

            dialog.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, seleccione un pedido para marcar como cobrado.");
            alert.show();
        }
    }

    // Método para mostrar el diálogo de pago parcial
    private void mostrarDialogoPagoParcial(Pedido pedido) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Pago Parcial");

        Label lblMonto = new Label("Monto a Pagar:");
        TextField txtMonto = new TextField();

        VBox vbox = new VBox(10, lblMonto, txtMonto);
        dialog.getDialogPane().setContent(vbox);

        ButtonType btnPagar = new ButtonType("Pagar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnPagar, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnPagar) {
                double monto = Double.parseDouble(txtMonto.getText());
                marcarCobrado(pedido, false, monto);
            }
            return null;
        });

        dialog.showAndWait();
    }

    // Método para marcar un pedido como cobrado
    private void marcarCobrado(Pedido pedido, boolean pagoTotal, double montoParcial) {
        try (Connection connection = conexionDB.getConnection()) {
            String query;
            if (pagoTotal) {
                query = "UPDATE PEDIDOS SET estado = ?, fechacobro = ? WHERE id = ?";
            } else {
                query = "UPDATE PEDIDOS SET valor = valor - ? WHERE id = ?";
            }
            PreparedStatement stmt = connection.prepareStatement(query);
            if (pagoTotal) {
                stmt.setString(1, "Cobrado");
                stmt.setDate(2, Date.valueOf(LocalDate.now())); // Establecer la fecha actual como fecha de cobro
                stmt.setInt(3, pedido.getId());
            } else {
                stmt.setDouble(1, montoParcial);
                stmt.setInt(2, pedido.getId());
            }
            stmt.executeUpdate();

            // Actualizar la vista
            if (pagoTotal) {
                pedido.setEstado("Cobrado");
                pedido.setFechaCobro(LocalDate.now().toString()); // Actualizar la fecha de cobro en el objeto Pedido
            } else {
                pedido.setValor(pedido.getValor() - montoParcial);
            }
            tablaPedidos.refresh();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, pagoTotal ? "Pedido marcado como cobrado." : "Pago parcial registrado.");
            alert.show();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al marcar el pedido como cobrado: " + e.getMessage());
            alert.show();
        }
    }

    // Método para exportar los pedidos a un archivo de texto y abrirlo en Notepad
    private void exportarANotepad() {
        // Crear el contenido del archivo de texto
        StringBuilder contenido = new StringBuilder();
        contenido.append("No.Pedido\tCliente\tTeléfono\tDirección\tDomiciliario\tFecha Pedido\tFecha de Cobro\tEstado\tValor\tMenu Seleccionado\n");
        for (Pedido pedido : listaPedidos) {
            contenido.append(pedido.getId()).append("\t")
                    .append(pedido.getCliente()).append("\t")
                    .append(pedido.getTelefono()).append("\t")
                    .append(pedido.getDireccion()).append("\t")
                    .append(pedido.getDomiciliario()).append("\t")
                    .append(pedido.getFechaPedido()).append("\t")
                    .append(pedido.getFechaCobro()).append("\t")
                    .append(pedido.getEstado()).append("\t")
                    .append(pedido.getValor()).append("\t")
                    .append(pedido.getPlatoSeleccionado()).append("\n");
        }

        // Mostrar el diálogo de guardar archivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Archivo de Texto");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Texto", "*.txt"));
        File file = fileChooser.showSaveDialog(null);

        // Guardar el archivo y abrirlo en Notepad
        if (file != null) {
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                fileOut.write(contenido.toString().getBytes());
                fileOut.close();

                // Abrir el archivo en Notepad
                ProcessBuilder processBuilder = new ProcessBuilder("notepad.exe", file.getAbsolutePath());
                processBuilder.start();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Archivo de texto exportado exitosamente.");
                alert.show();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error al exportar el archivo de texto: " + e.getMessage());
                alert.show();
            }
        }
    }

    // Método para volver al menú principal (abrir clase Despachos.java)
    private void volverAlMenuPrincipal(Stage primaryStage) {
        primaryStage.hide(); // Cierra la ventana actual
        try {
            // Crear una nueva instancia de la clase Despachos
            despachos despachos = new despachos();
            // Llamar al método start de Despachos, pasando un nuevo Stage
            despachos.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Clase Pedido
    public static class Pedido {
        private final IntegerProperty id;
        private final StringProperty cliente;
        private final StringProperty telefono;
        private final StringProperty direccion;
        private final StringProperty domiciliario;
        private final StringProperty fechaPedido;
        private final StringProperty fechaCobro; // Propiedad para la fecha de cobro
        private final StringProperty estado;
        private final DoubleProperty valor;
        private final StringProperty platoSeleccionado;

        public Pedido(int id, String cliente, String telefono, String direccion, String domiciliario, String fechaPedido, String fechaCobro, String estado, double valor, String platoSeleccionado) {
            this.id = new SimpleIntegerProperty(id);
            this.cliente = new SimpleStringProperty(cliente);
            this.telefono = new SimpleStringProperty(telefono);
            this.direccion = new SimpleStringProperty(direccion);
            this.domiciliario = new SimpleStringProperty(domiciliario);
            this.fechaPedido = new SimpleStringProperty(fechaPedido);
            this.fechaCobro = new SimpleStringProperty(fechaCobro); // Inicializar la propiedad
            this.estado = new SimpleStringProperty(estado);
            this.valor = new SimpleDoubleProperty(valor);
            this.platoSeleccionado = new SimpleStringProperty(platoSeleccionado);
        }

        // Getters y Setters
        public int getId() {
            return id.get();
        }

        public String getCliente() {
            return cliente.get();
        }

        public String getTelefono() {
            return telefono.get();
        }

        public String getDireccion() {
            return direccion.get();
        }

        public String getDomiciliario() {
            return domiciliario.get();
        }

        public String getFechaPedido() {
            return fechaPedido.get();
        }

        public String getFechaCobro() {
            return fechaCobro.get();
        }

        public String getEstado() {
            return estado.get();
        }

        public double getValor() {
            return valor.get();
        }

        public String getPlatoSeleccionado() {
            return platoSeleccionado.get();
        }

        // Properties
        public IntegerProperty idProperty() {
            return id;
        }

        public StringProperty clienteProperty() {
            return cliente;
        }

        public StringProperty telefonoProperty() {
            return telefono;
        }

        public StringProperty direccionProperty() {
            return direccion;
        }

        public StringProperty domiciliarioProperty() {
            return domiciliario;
        }

        public StringProperty fechaPedidoProperty() {
            return fechaPedido;
        }

        public StringProperty fechaCobroProperty() {
            return fechaCobro;
        }

        public StringProperty estadoProperty() {
            return estado;
        }

        public DoubleProperty valorProperty() {
            return valor;
        }

        public StringProperty platoSeleccionadoProperty() {
            return platoSeleccionado;
        }

        public void setEstado(String estado) {
            this.estado.set(estado);
        }

        public void setFechaCobro(String fechaCobro) {
            this.fechaCobro.set(fechaCobro);
        }

        public void setValor(double v) {
        }
    }
}