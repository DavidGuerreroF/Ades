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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

        // Crear los botones
        Button btnEntrada = new Button("Registrar Entrada");
        Button btnVolver = new Button("Volver al Menú Principal");

        // Estilizar los botones
        String buttonStyle = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;";
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
            String selectedItem = listView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Producto productoSeleccionado = productos.get(listView.getSelectionModel().getSelectedIndex());
                mostrarFormularioEntrada(productoSeleccionado, listView);
            } else {
                mostrarAlerta(AlertType.WARNING, "Advertencia", "Por favor, selecciona un producto para registrar la entrada.");
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

    private void mostrarFormularioEntrada(Producto producto, ListView<String> listView) {
        Stage stage = new Stage();
        stage.setTitle("Registrar Entrada de Inventario");

        // Crear el formulario
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(15);
        grid.setHgap(15);

        Label cantidadLabel = new Label("Cantidad:");
        GridPane.setConstraints(cantidadLabel, 0, 0);
        TextField cantidadInput = new TextField();
        GridPane.setConstraints(cantidadInput, 1, 0);

        Label observacionesLabel = new Label("Observaciones:");
        GridPane.setConstraints(observacionesLabel, 0, 1);
        TextField observacionesInput = new TextField();
        GridPane.setConstraints(observacionesInput, 1, 1);

        Button btnGuardar = new Button("Guardar");
        GridPane.setConstraints(btnGuardar, 1, 2);

        // Estilizar el botón de guardar
        String buttonStyle = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;";
        btnGuardar.setStyle(buttonStyle);

        grid.getChildren().addAll(cantidadLabel, cantidadInput, observacionesLabel, observacionesInput, btnGuardar);

        btnGuardar.setOnAction(e -> {
            int cantidad = Integer.parseInt(cantidadInput.getText());
            String observaciones = observacionesInput.getText();

            registrarEntrada(producto, cantidad, observaciones, listView);

            stage.close();
        });

        Scene scene = new Scene(grid, 400, 250);
        stage.setScene(scene);
        stage.show();
    }

    private void registrarEntrada(Producto producto, int cantidad, String observaciones, ListView<String> listView) {
        String sql = "INSERT INTO EntradasInventario (codigo_producto, cantidad, observaciones) VALUES (?, ?, ?)";

        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, producto.codigo);
            pstmt.setInt(2, cantidad);
            pstmt.setString(3, observaciones);

            pstmt.executeUpdate();
            producto.cantidad += cantidad;
            listView.getItems().set(listView.getSelectionModel().getSelectedIndex(), producto.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Error", "No se pudo registrar la entrada de inventario");
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