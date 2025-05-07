import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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

public class AuxiliarInventario extends Application {

    public static class Inventario {
        private String codigoProducto;
        private int saldoAnterior;
        private int entradasMes;
        private int salidasMes;
        private int saldoActual;
        private float valorSaldoAnterior;
        private float valorEntradasMes;
        private float valorSalidasMes;
        private float valorSaldoActual;

        public Inventario(String codigoProducto, int saldoAnterior, int entradasMes, int salidasMes, int saldoActual,
                          float valorSaldoAnterior, float valorEntradasMes, float valorSalidasMes, float valorSaldoActual) {
            this.codigoProducto = codigoProducto;
            this.saldoAnterior = saldoAnterior;
            this.entradasMes = entradasMes;
            this.salidasMes = salidasMes;
            this.saldoActual = saldoActual;
            this.valorSaldoAnterior = valorSaldoAnterior;
            this.valorEntradasMes = valorEntradasMes;
            this.valorSalidasMes = valorSalidasMes;
            this.valorSaldoActual = valorSaldoActual;
        }

        public String getCodigoProducto() {
            return codigoProducto;
        }

        public int getSaldoAnterior() {
            return saldoAnterior;
        }

        public int getEntradasMes() {
            return entradasMes;
        }

        public int getSalidasMes() {
            return salidasMes;
        }

        public int getSaldoActual() {
            return saldoActual;
        }

        public float getValorSaldoAnterior() {
            return valorSaldoAnterior;
        }

        public float getValorEntradasMes() {
            return valorEntradasMes;
        }

        public float getValorSalidasMes() {
            return valorSalidasMes;
        }

        public float getValorSaldoActual() {
            return valorSaldoActual;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Auxiliar de Inventario");

        TableView<Inventario> table = new TableView<>();
        table.setEditable(false);

        // Columnas
        TableColumn<Inventario, String> codigoProductoCol = new TableColumn<>("Código Producto");
        codigoProductoCol.setCellValueFactory(new PropertyValueFactory<>("codigoProducto"));

        TableColumn<Inventario, Integer> saldoAnteriorCol = new TableColumn<>("Saldo Anterior");
        saldoAnteriorCol.setCellValueFactory(new PropertyValueFactory<>("saldoAnterior"));

        TableColumn<Inventario, Integer> entradasMesCol = new TableColumn<>("Entradas Mes");
        entradasMesCol.setCellValueFactory(new PropertyValueFactory<>("entradasMes"));

        TableColumn<Inventario, Integer> salidasMesCol = new TableColumn<>("Salidas Mes");
        salidasMesCol.setCellValueFactory(new PropertyValueFactory<>("salidasMes"));

        TableColumn<Inventario, Integer> saldoActualCol = new TableColumn<>("Saldo Actual");
        saldoActualCol.setCellValueFactory(new PropertyValueFactory<>("saldoActual"));

        TableColumn<Inventario, Float> valorSaldoAnteriorCol = new TableColumn<>("Valor Saldo Anterior");
        valorSaldoAnteriorCol.setCellValueFactory(new PropertyValueFactory<>("valorSaldoAnterior"));

        TableColumn<Inventario, Float> valorEntradasMesCol = new TableColumn<>("Valor Entradas Mes");
        valorEntradasMesCol.setCellValueFactory(new PropertyValueFactory<>("valorEntradasMes"));

        TableColumn<Inventario, Float> valorSalidasMesCol = new TableColumn<>("Valor Salidas Mes");
        valorSalidasMesCol.setCellValueFactory(new PropertyValueFactory<>("valorSalidasMes"));

        TableColumn<Inventario, Float> valorSaldoActualCol = new TableColumn<>("Valor Saldo Actual");
        valorSaldoActualCol.setCellValueFactory(new PropertyValueFactory<>("valorSaldoActual"));

        // Añadir columnas a la tabla
        table.getColumns().addAll(codigoProductoCol, saldoAnteriorCol, entradasMesCol, salidasMesCol, saldoActualCol,
                valorSaldoAnteriorCol, valorEntradasMesCol, valorSalidasMesCol, valorSaldoActualCol);

        // Obtener datos de la base de datos
        List<Inventario> inventarios = obtenerDatosInventario();
        table.getItems().addAll(inventarios);

        // Botón para exportar datos
        Button btnExportar = new Button("Exportar a Notepad");
        btnExportar.setStyle("-fx-background-color: #0294b5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;");
        btnExportar.setOnAction(e -> exportarAFile(inventarios));

        // Botón Volver
        Button btnVolver = new Button("Volver");
        btnVolver.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;");
        btnVolver.setOnAction(e -> {
            try {
                // Crear una nueva instancia de la clase Despachos
                despachos despachos = new despachos();
                Stage stage = new Stage();
                despachos.start(stage);

                // Cerrar la ventana actual
                primaryStage.close();
            } catch (Exception ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo volver a la pantalla de despachos.");
                ex.printStackTrace();
            }
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(btnExportar, btnVolver);

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(table, buttonBox);

        Scene scene = new Scene(layout, 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private List<Inventario> obtenerDatosInventario() {
        List<Inventario> inventarios = new ArrayList<>();
        String sqlProductos = "SELECT DISTINCT codigo_producto FROM ( " +
                "SELECT codigo_producto FROM entradasinventario " +
                "UNION " +
                "SELECT codigo_producto FROM salidasinventario) productos";

        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmtProductos = conn.prepareStatement(sqlProductos);
             ResultSet rsProductos = pstmtProductos.executeQuery()) {

            while (rsProductos.next()) {
                int codigoProducto = rsProductos.getInt("codigo_producto"); // Cambiado a int

                // Obtener saldo anterior
                int saldoAnterior = 0;
                float valorSaldoAnterior = 0.0f;

                // Calcular entradas y valor de entradas
                String sqlEntradas = "SELECT SUM(e.cantidad) AS totalEntradas, " +
                        "SUM(e.cantidad * p.Costo) AS valorEntradas " +
                        "FROM entradasinventario e " +
                        "JOIN productos p ON e.codigo_producto = p.codigo " +
                        "WHERE e.codigo_producto = ?";
                int entradasMes = 0;
                float valorEntradasMes = 0.0f;

                try (PreparedStatement pstmtEntradas = conn.prepareStatement(sqlEntradas)) {
                    pstmtEntradas.setInt(1, codigoProducto); // Cambiado a setInt
                    try (ResultSet rsEntradas = pstmtEntradas.executeQuery()) {
                        if (rsEntradas.next()) {
                            entradasMes = rsEntradas.getInt("totalEntradas");
                            valorEntradasMes = rsEntradas.getFloat("valorEntradas");
                        }
                    }
                }

                // Calcular salidas y valor de salidas
                String sqlSalidas = "SELECT SUM(s.cantidad) AS totalSalidas, " +
                        "SUM(s.cantidad * p.Costo) AS valorSalidas " +
                        "FROM salidasinventario s " +
                        "JOIN productos p ON s.codigo_producto = p.codigo " +
                        "WHERE s.codigo_producto = ?";
                int salidasMes = 0;
                float valorSalidasMes = 0.0f;

                try (PreparedStatement pstmtSalidas = conn.prepareStatement(sqlSalidas)) {
                    pstmtSalidas.setInt(1, codigoProducto); // Cambiado a setInt
                    try (ResultSet rsSalidas = pstmtSalidas.executeQuery()) {
                        if (rsSalidas.next()) {
                            salidasMes = rsSalidas.getInt("totalSalidas");
                            valorSalidasMes = rsSalidas.getFloat("valorSalidas");
                        }
                    }
                }

                // Calcular saldo actual y valor de saldo actual
                int saldoActual = saldoAnterior + entradasMes - salidasMes;
                float valorSaldoActual = valorSaldoAnterior + valorEntradasMes - valorSalidasMes;

                // Crear objeto Inventario
                inventarios.add(new Inventario(String.valueOf(codigoProducto), saldoAnterior, entradasMes, salidasMes, saldoActual,
                        valorSaldoAnterior, valorEntradasMes, valorSalidasMes, valorSaldoActual));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventarios;
    }

    private void exportarAFile(List<Inventario> inventarios) {
        String filePath = "auxiliar_inventario.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Auxiliar de Inventario\n");
            writer.write("-------------------------------------------------------------\n");
            for (Inventario inventario : inventarios) {
                writer.write(String.format("Código Producto: %s\n", inventario.getCodigoProducto()));
                writer.write(String.format("Saldo Anterior: %d, Valor: %.2f\n",
                        inventario.getSaldoAnterior(), inventario.getValorSaldoAnterior()));
                writer.write(String.format("Entradas Mes: %d, Valor: %.2f\n",
                        inventario.getEntradasMes(), inventario.getValorEntradasMes()));
                writer.write(String.format("Salidas Mes: %d, Valor: %.2f\n",
                        inventario.getSalidasMes(), inventario.getValorSalidasMes()));
                writer.write(String.format("Saldo Actual: %d, Valor: %.2f\n",
                        inventario.getSaldoActual(), inventario.getValorSaldoActual()));
                writer.write("-------------------------------------------------------------\n");
            }
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Datos exportados correctamente a " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo exportar los datos.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
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