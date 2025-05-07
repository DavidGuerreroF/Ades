import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogoProductos extends Application {

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
        primaryStage.setTitle("Catálogo de Productos");

        // Crear el ListView
        ListView<String> listView = new ListView<>();
        List<Producto> productos = listarProductos();
        for (Producto producto : productos) {
            listView.getItems().add(producto.toString());
        }

        // Crear los botones
        Button btnActualizar = new Button("Actualizar Producto");
        Button btnExportar = new Button("Guardar e imprimir");
        Button btnVolver = new Button("Volver al Menú Principal");

        // Estilizar los botones
        String buttonStyle = "-fx-background-color: #0294b5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;";
        btnActualizar.setStyle(buttonStyle);
        btnExportar.setStyle(buttonStyle);
        btnVolver.setStyle(buttonStyle);

        // Crear el layout para los botones
        HBox buttonLayout = new HBox(10);
        buttonLayout.getChildren().addAll(btnActualizar, btnExportar, btnVolver);
        buttonLayout.setAlignment(Pos.CENTER);

        // Crear el layout principal
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(new Label("Productos:"), listView, buttonLayout);

        // Manejar el evento de actualizar producto
        btnActualizar.setOnAction(e -> {
            String selectedItem = listView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Producto productoSeleccionado = productos.get(listView.getSelectionModel().getSelectedIndex());
                mostrarFormularioProducto(productoSeleccionado, listView);
            } else {
                mostrarAlerta(AlertType.WARNING, "Advertencia", "Por favor, selecciona un producto para actualizar.");
            }
        });

        // Manejar el evento de exportar a Bloc de Notas
        btnExportar.setOnAction(e -> exportarAFile(productos));

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

    private void mostrarFormularioProducto(Producto producto, ListView<String> listView) {
        Stage stage = new Stage();
        stage.setTitle("Actualizar Producto");

        // Crear el formulario
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(15);
        grid.setHgap(15);

        Label codigoLabel = new Label("Código:");
        GridPane.setConstraints(codigoLabel, 0, 0);
        TextField codigoInput = new TextField(String.valueOf(producto.codigo));
        GridPane.setConstraints(codigoInput, 1, 0);

        Label descripcionLabel = new Label("Descripción:");
        GridPane.setConstraints(descripcionLabel, 0, 1);
        TextField descripcionInput = new TextField(producto.descripcion);
        GridPane.setConstraints(descripcionInput, 1, 1);

        Label costoLabel = new Label("Costo:");
        GridPane.setConstraints(costoLabel, 0, 2);
        TextField costoInput = new TextField(String.valueOf(producto.costo));
        GridPane.setConstraints(costoInput, 1, 2);

        Label cantidadLabel = new Label("Cantidad Disponible:");
        GridPane.setConstraints(cantidadLabel, 0, 3);
        TextField cantidadInput = new TextField(String.valueOf(producto.cantidad));
        GridPane.setConstraints(cantidadInput, 1, 3);

        Label observacionesLabel = new Label("Observaciones:");
        GridPane.setConstraints(observacionesLabel, 0, 4);
        TextField observacionesInput = new TextField(producto.observaciones);
        GridPane.setConstraints(observacionesInput, 1, 4);

        Button btnGuardar = new Button("Guardar");
        GridPane.setConstraints(btnGuardar, 1, 5);

        // Estilizar el botón de guardar
        String buttonStyle = "-fx-background-color: #0294b5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;";
        btnGuardar.setStyle(buttonStyle);

        grid.getChildren().addAll(codigoLabel, codigoInput, descripcionLabel, descripcionInput, costoLabel, costoInput, cantidadLabel, cantidadInput, observacionesLabel, observacionesInput, btnGuardar);

        btnGuardar.setOnAction(e -> {
            producto.codigo = Integer.parseInt(codigoInput.getText());
            producto.descripcion = descripcionInput.getText();
            producto.costo = Float.parseFloat(costoInput.getText());
            producto.cantidad = Integer.parseInt(cantidadInput.getText());
            producto.observaciones = observacionesInput.getText();

            actualizarProducto(producto, listView);

            stage.close();
        });

        Scene scene = new Scene(grid, 400, 350);
        stage.setScene(scene);
        stage.show();
    }

    private void actualizarProducto(Producto producto, ListView<String> listView) {
        String sql = "UPDATE Productos SET codigo = ?, descripcion = ?, costo = ?, cantidad = ?, observaciones = ? WHERE id = ?";

        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, producto.codigo);
            pstmt.setString(2, producto.descripcion);
            pstmt.setFloat(3, producto.costo);
            pstmt.setInt(4, producto.cantidad);
            pstmt.setString(5, producto.observaciones);
            pstmt.setInt(6, producto.id);

            pstmt.executeUpdate();
            listView.getItems().set(listView.getSelectionModel().getSelectedIndex(), producto.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Error", "No se pudo actualizar el producto");
        }
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void exportarAFile(List<Producto> productos) {
        String filePath = "productos.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Producto producto : productos) {
                writer.write(producto.toString());
                writer.newLine();
            }
            mostrarAlerta(AlertType.INFORMATION, "Éxito", "Productos exportados correctamente a " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Error", "No se pudo exportar los productos.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}