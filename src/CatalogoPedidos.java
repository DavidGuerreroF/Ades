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
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.sql.*;

public class CatalogoPedidos extends Application {
    private TableView<Pedido> tablaPedidos;
    private ObservableList<Pedido> listaPedidos;
    private Button btnCancelar, btnDespachar, btnVolver;

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

        TableColumn<Pedido, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());

        TableColumn<Pedido, Double> colValor = new TableColumn<>("Valor");
        colValor.setCellValueFactory(cellData -> cellData.getValue().valorProperty().asObject());

        TableColumn<Pedido, String> colPlatoSeleccionado = new TableColumn<>("Menu Seleccionado");
        colPlatoSeleccionado.setCellValueFactory(cellData -> cellData.getValue().platoSeleccionadoProperty());

        tablaPedidos.getColumns().addAll(colId, colCliente, colTelefono, colDireccion, colDomiciliario, colFecha, colEstado, colValor, colPlatoSeleccionado);
        tablaPedidos.setItems(listaPedidos);
        tablaPedidos.setPrefWidth(800);
        tablaPedidos.setPrefHeight(400);
        tablaPedidos.setStyle("-fx-border-color: #2d3b48; -fx-border-width: 1px;");

        // Botones de acción con diseño mejorado
        btnCancelar = new Button("Cancelar Pedido");
        btnCancelar.setStyle(
                "-fx-background-color: #FF6347; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-cursor: hand;"
        );
        btnCancelar.setOnAction(event -> cancelarPedido());

        btnDespachar = new Button("Marcar como Despachado");
        btnDespachar.setStyle(
                "-fx-background-color: #32CD32; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-cursor: hand;"
        );
        btnDespachar.setOnAction(event -> marcarDespachado());

        btnVolver = new Button("Volver al Menú Principal");
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

        HBox botones = new HBox(20);
        botones.setAlignment(Pos.CENTER);
        botones.getChildren().addAll(btnDespachar, btnCancelar, btnVolver);

        // Layout principal
        VBox root = new VBox(20, tablaPedidos, botones);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f4f4f9; -fx-padding: 20px;");

        // Configuración de la escena
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Catálogo de Pedidos");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Cargar pedidos desde la base de datos
        cargarPedidos();
    }

    // Método para cargar los pedidos desde la base de datos
    private void cargarPedidos() {
        try (Connection connection = conexionDB.getConnection()) {
            // Consulta SQL que une PEDIDOS, CLIENTES, DOMICILIARIOS, y MENU_DIA
            String query = "SELECT p.id, c.nombre AS cliente, c.telefono, c.direccion, d.nombre AS domiciliario, p.fecha_pedido, p.estado, p.valor, plato_seleccionado " +
                    "FROM PEDIDOS p " +
                    "JOIN CLIENTES c ON p.id_cliente = c.id " +
                    "JOIN DOMICILIARIOS d ON p.id_domiciliario = d.id " +
                    "JOIN MENU_DIA m ON p.id_menu_dia = m.id";

            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String cliente = rs.getString("cliente");
                String telefono = rs.getString("telefono");
                String direccion = rs.getString("direccion");
                String domiciliario = rs.getString("domiciliario");
                String fechaPedido = rs.getString("fecha_pedido");
                String estado = rs.getString("estado");
                double valor = rs.getDouble("valor");
                String platoSeleccionado = rs.getString("plato_seleccionado"); // Asumiendo que 'plato1' es el plato seleccionado

                listaPedidos.add(new Pedido(id, cliente, telefono, direccion, domiciliario, fechaPedido, estado, valor, platoSeleccionado));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al cargar los pedidos: " + e.getMessage());
            alert.show();
        }
    }

    // Método para cancelar un pedido
    private void cancelarPedido() {
        Pedido selectedPedido = tablaPedidos.getSelectionModel().getSelectedItem();
        if (selectedPedido != null) {
            try (Connection connection = conexionDB.getConnection()) {
                String query = "UPDATE PEDIDOS SET estado = ? WHERE id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, "Cancelado");
                stmt.setInt(2, selectedPedido.getId());
                stmt.executeUpdate();

                // Actualizar la vista
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

    // Método para marcar un pedido como despachado
    private void marcarDespachado() {
        Pedido selectedPedido = tablaPedidos.getSelectionModel().getSelectedItem();
        if (selectedPedido != null) {
            try (Connection connection = conexionDB.getConnection()) {
                String query = "UPDATE PEDIDOS SET estado = ? WHERE id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, "Despachado");
                stmt.setInt(2, selectedPedido.getId());
                stmt.executeUpdate();

                // Actualizar la vista
                selectedPedido.setEstado("Despachado");
                tablaPedidos.refresh();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Pedido marcado como despachado.");
                alert.show();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error al marcar el pedido como despachado: " + e.getMessage());
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, seleccione un pedido para marcar como despachado.");
            alert.show();
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
        private final StringProperty estado;
        private final DoubleProperty valor;
        private final StringProperty platoSeleccionado;

        public Pedido(int id, String cliente, String telefono, String direccion, String domiciliario, String fechaPedido, String estado, double valor, String platoSeleccionado) {
            this.id = new SimpleIntegerProperty(id);
            this.cliente = new SimpleStringProperty(cliente);
            this.telefono = new SimpleStringProperty(telefono);
            this.direccion = new SimpleStringProperty(direccion);
            this.domiciliario = new SimpleStringProperty(domiciliario);
            this.fechaPedido = new SimpleStringProperty(fechaPedido);
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
    }
}
