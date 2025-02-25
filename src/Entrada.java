import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entrada extends Application {

    public static class Producto {
        private int id;
        private int codigo;
        private String descripcion;
        private float costo;
        private int cantidad;
        private String observaciones;

        public Producto(int id, int codigo, String descripcion, float costo, int cantidad, String observaciones) {
            this.id = id;
            this.codigo = codigo;
            this.descripcion = descripcion;
            this.costo = costo;
            this.cantidad = cantidad;
            this.observaciones = observaciones;
        }

        @Override
        public String toString() {
            return "ID: " + id + ", Código: " + codigo + ", Descripción: " + descripcion + ", Costo: " + costo +
                    ", Cantidad: " + cantidad + ", Observaciones: " + observaciones;
        }
    }

    public List<Producto> listarProductos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Productos";

        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int codigo = rs.getInt("codigo");
                String descripcion = rs.getString("descripcion");
                float costo = rs.getFloat("costo");
                int cantidad = rs.getInt("cantidad");
                String observaciones = rs.getString("observaciones");

                Producto producto = new Producto(id, codigo, descripcion, costo, cantidad, observaciones);
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Entradas de Inventario");

        // Crear el ListView
        ListView<String> listView = new ListView<>();
        List<Producto> productos = listarProductos();
        for (Producto producto : productos) {
            listView.getItems().add(producto.toString());
        }

        // Permitir selección múltiple
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Crear los botones
        Button btnEntrada = new Button("Registrar Entrada");
        Button btnVolver = new Button("Volver al Menú Principal");

        // Estilizar los botones
        String buttonStyle = "-fx-background-color: #0294b5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;";
        btnEntrada.setStyle(buttonStyle);
        btnVolver.setStyle(buttonStyle);

        // Crear el layout para los botones
        HBox buttonLayout = new HBox(10);
        buttonLayout.getChildren().addAll(btnEntrada, btnVolver);
        buttonLayout.setAlignment(Pos.CENTER);

        // Crear el layout principal
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(new Label("Productos:"), listView, buttonLayout);

        // Manejar el evento de registrar entrada
        btnEntrada.setOnAction(e -> {
            List<String> selectedItems = listView.getSelectionModel().getSelectedItems();
            if (!selectedItems.isEmpty()) {
                List<Producto> productosSeleccionados = new ArrayList<>();
                for (String selectedItem : selectedItems) {
                    productosSeleccionados.add(productos.get(listView.getItems().indexOf(selectedItem)));
                }
                mostrarFormularioEntrada(productosSeleccionados, listView);
            } else {
                mostrarAlerta(AlertType.WARNING, "Advertencia", "Por favor, selecciona al menos un producto para registrar la entrada.");
            }
        });

        // Manejar el evento de volver al menú principal
        btnVolver.setOnAction(e -> {
            despachos despachosWindow = new despachos();
            despachosWindow.start(new Stage());
            primaryStage.close();
        });

        // Crear la escena y mostrarla
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void mostrarFormularioEntrada(List<Producto> productos, ListView<String> listView) {
        Stage stage = new Stage();
        stage.setTitle("Registrar Entrada de Inventario");

        // Crear el formulario
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(15);
        grid.setHgap(15);

        // Crear campos dinámicamente para cada producto
        Map<Producto, TextField[]> productoFieldsMap = new HashMap<>();
        int row = 0;
        for (Producto producto : productos) {
            Label productoLabel = new Label(producto.descripcion);
            productoLabel.setFont(new Font("Arial", 16));
            GridPane.setConstraints(productoLabel, 0, row, 4, 1); // Span 4 columns

            Label cantidadLabel = new Label("Cantidad:");
            GridPane.setConstraints(cantidadLabel, 0, row + 1);
            TextField cantidadInput = new TextField();
            cantidadInput.setPromptText("Ingrese la cantidad");
            GridPane.setConstraints(cantidadInput, 1, row + 1);

            Label costoLabel = new Label("Costo:");
            GridPane.setConstraints(costoLabel, 0, row + 2);
            TextField costoInput = new TextField();
            costoInput.setPromptText("Ingrese el costo");
            GridPane.setConstraints(costoInput, 1, row + 2);

            Label observacionesLabel = new Label("Observaciones:");
            GridPane.setConstraints(observacionesLabel, 0, row + 3);
            TextField observacionesInput = new TextField();
            observacionesInput.setPromptText("Ingrese observaciones");
            GridPane.setConstraints(observacionesInput, 1, row + 3);

            grid.getChildren().addAll(productoLabel, cantidadLabel, cantidadInput, costoLabel, costoInput, observacionesLabel, observacionesInput);

            productoFieldsMap.put(producto, new TextField[]{cantidadInput, costoInput, observacionesInput});
            row += 4; // Move to the next set of rows for the next product
        }

        Button btnGuardar = new Button("Guardar");
        GridPane.setConstraints(btnGuardar, 1, row);

        // Estilizar el botón de guardar
        String buttonStyle = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;";
        btnGuardar.setStyle(buttonStyle);
        grid.getChildren().add(btnGuardar);

        btnGuardar.setOnAction(e -> {
            try {
                for (Map.Entry<Producto, TextField[]> entry : productoFieldsMap.entrySet()) {
                    Producto producto = entry.getKey();
                    TextField[] fields = entry.getValue();

                    int cantidad = Integer.parseInt(fields[0].getText());
                    float costo = Float.parseFloat(fields[1].getText());
                    String observaciones = fields[2].getText();

                    registrarEntrada(producto, cantidad, costo, observaciones);
                }

                actualizarListView(listView);
                stage.close();
                mostrarAlerta(AlertType.INFORMATION, "Entrada Exitosa", "Se registraron las entradas correctamente");
            } catch (NumberFormatException ex) {
                mostrarAlerta(AlertType.ERROR, "Error", "Por favor, ingrese valores válidos para cantidad y costo.");
            }
        });

        Scene scene = new Scene(grid, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void registrarEntrada(Producto producto, int cantidad, float costo, String observaciones) {
        String sqlEntrada = "INSERT INTO EntradasInventario (codigo_producto, cantidad, observaciones) VALUES (?, ?, ?)";
        String sqlActualizarCosto = "UPDATE Productos SET costo = ? WHERE codigo = ?";

        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmtEntrada = conn.prepareStatement(sqlEntrada);
             PreparedStatement pstmtActualizarCosto = conn.prepareStatement(sqlActualizarCosto)) {

            // Insertar entrada en la tabla EntradasInventario
            pstmtEntrada.setInt(1, producto.codigo);
            pstmtEntrada.setInt(2, cantidad);
            pstmtEntrada.setString(3, observaciones);
            pstmtEntrada.executeUpdate();

            // Actualizar el costo en la tabla Productos
            pstmtActualizarCosto.setFloat(1, costo);
            pstmtActualizarCosto.setInt(2, producto.codigo);
            pstmtActualizarCosto.executeUpdate();

            producto.cantidad += cantidad;
            producto.costo = costo;
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Error", "No se pudo registrar la entrada de inventario");
        }
    }

    private void actualizarListView(ListView<String> listView) {
        listView.getItems().clear();
        List<Producto> productosList = listarProductos();
        for (Producto producto : productosList) {
            listView.getItems().add(producto.toString());
        }
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}