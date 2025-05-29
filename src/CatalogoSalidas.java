import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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

        public Salida(int id, int codigoProducto, int cantidad, String fecha) {
            this.id = id;
            this.codigoProducto = codigoProducto;
            this.cantidad = cantidad;
            this.fecha = fecha;
        }

        public int getId() {
            return id;
        }

        public int getCodigoProducto() {
            return codigoProducto;
        }

        public int getCantidad() {
            return cantidad;
        }

        public String getFecha() {
            return fecha;
        }
    }

    public List<Salida> listarSalidas() {
        List<Salida> salidas = new ArrayList<>();
        String sql = "SELECT Id, codigo_producto, cantidad, fecha FROM SalidasInventario";

        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("Id");
                int codigoProducto = rs.getInt("codigo_producto");
                int cantidad = rs.getInt("cantidad");
                String fecha = rs.getString("fecha");

                Salida salida = new Salida(id, codigoProducto, cantidad, fecha);
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

        // Crear TableView
        TableView<Salida> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Salida, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Salida, Integer> colCodigoProducto = new TableColumn<>("Código Producto");
        colCodigoProducto.setCellValueFactory(new PropertyValueFactory<>("codigoProducto"));

        TableColumn<Salida, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        TableColumn<Salida, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        tableView.getColumns().addAll(colId, colCodigoProducto, colCantidad, colFecha);

        List<Salida> salidas = listarSalidas();
        tableView.getItems().addAll(salidas);

        // Botón Volver
        Button btnVolver = new Button("Volver al Menú Principal");
        String buttonStyle = "-fx-background-color: #0294b5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;";
        btnVolver.setStyle(buttonStyle);

        HBox buttonLayout = new HBox(10);
        buttonLayout.getChildren().addAll(btnVolver);
        buttonLayout.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(new Label("Salidas de Inventario:"), tableView, buttonLayout);

        btnVolver.setOnAction(e -> {
            despachos despachosWindow = new despachos();
            despachosWindow.start(new Stage());
            primaryStage.close();
        });

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}