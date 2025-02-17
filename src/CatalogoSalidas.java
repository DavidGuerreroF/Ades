import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogoSalidas extends Application {

    public static class Salida {
        private int id;
        private int codigoProducto;
        private int cantidad;
        private String fecha;
        private String observaciones;

        public Salida(int id, int codigoProducto, int cantidad, String fecha, String observaciones) {
            this.id = id;
            this.codigoProducto = codigoProducto;
            this.cantidad = cantidad;
            this.fecha = fecha;
            this.observaciones = observaciones;
        }

        @Override
        public String toString() {
            return "ID: " + id + ", Código Producto: " + codigoProducto + ", Cantidad: " + cantidad + ", Fecha: " + fecha + ", Observaciones: " + observaciones;
        }
    }

    public List<Salida> listarSalidas() {
        List<Salida> salidas = new ArrayList<>();
        String sql = "SELECT * FROM SalidasInventario";

        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("Id");
                int codigoProducto = rs.getInt("codigo_producto");
                int cantidad = rs.getInt("cantidad");
                String fecha = rs.getString("fecha");
                String observaciones = rs.getString("observaciones");

                Salida salida = new Salida(id, codigoProducto, cantidad, fecha, observaciones);
                salidas.add(salida);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salidas;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Catálogo de Salidas de Inventario");

        // Crear el ListView
        ListView<String> listView = new ListView<>();
        List<Salida> salidas = listarSalidas();
        for (Salida salida : salidas) {
            listView.getItems().add(salida.toString());
        }

        // Crear los botones
        Button btnAnularSalida = new Button("Anular Salida");
        Button btnVolver = new Button("Volver al Menú Principal");

        // Estilizar los botones
        String buttonStyle = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;";
        btnAnularSalida.setStyle(buttonStyle);
        btnVolver.setStyle(buttonStyle);

        // Crear el layout para los botones
        HBox buttonLayout = new HBox(10);
        buttonLayout.getChildren().addAll(btnAnularSalida, btnVolver);
        buttonLayout.setAlignment(Pos.CENTER);

        // Crear el layout principal
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(new Label("Salidas de Inventario:"), listView, buttonLayout);

        // Manejar el evento de anular salida
        btnAnularSalida.setOnAction(e -> {
            String selectedItem = listView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Salida salidaSeleccionada = salidas.get(listView.getSelectionModel().getSelectedIndex());
                anularSalida(salidaSeleccionada, listView);
            } else {
                mostrarAlerta(AlertType.WARNING, "Advertencia", "Por favor, selecciona una salida para anular.");
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

    private void anularSalida(Salida salida, ListView<String> listView) {
        String sqlDelete = "DELETE FROM SalidasInventario WHERE Id = ?";
        String sqlUpdate = "UPDATE Productos SET cantidad = cantidad + ? WHERE codigo = ?";

        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmtDelete = conn.prepareStatement(sqlDelete);
             PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {

            // Eliminar la salida de inventario
            pstmtDelete.setInt(1, salida.id);
            pstmtDelete.executeUpdate();

            // Actualizar la cantidad del producto
            pstmtUpdate.setInt(1, salida.cantidad);
            pstmtUpdate.setInt(2, salida.codigoProducto);
            pstmtUpdate.executeUpdate();

            // Actualizar el ListView
            listView.getItems().remove(listView.getSelectionModel().getSelectedIndex());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Error", "No se pudo anular la salida de inventario");
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