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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// iText PDF
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

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

        // Botón para exportar datos a Notepad
        Button btnExportarTXT = new Button("Exportar a Notepad");
        btnExportarTXT.setStyle("-fx-background-color: #0294b5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;");
        btnExportarTXT.setOnAction(e -> exportarAFile(inventarios));

        // Botón para exportar a PDF
        Button btnExportarPDF = new Button("Exportar a PDF");
        btnExportarPDF.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;");
        btnExportarPDF.setOnAction(e -> exportarAPdf(inventarios));

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
        buttonBox.getChildren().addAll(btnExportarTXT, btnExportarPDF, btnVolver);

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
                int codigoProducto = rsProductos.getInt("codigo_producto");

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
                    pstmtEntradas.setInt(1, codigoProducto);
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
                    pstmtSalidas.setInt(1, codigoProducto);
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

    private void exportarAPdf(List<Inventario> inventarios) {
        String pdfPath = "auxiliar_inventario.pdf";
        try {
            PdfWriter writer = new PdfWriter(pdfPath);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Auxiliar de Inventario").setBold().setFontSize(16));

            // Agregar fecha de impresión
            LocalDateTime fechaHora = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fechaImpresion = "Fecha de impresión: " + fechaHora.format(formatter);
            Paragraph fecha = new Paragraph(fechaImpresion)
                    .setFontSize(12)
                    .setTextAlignment(com.itextpdf.layout.property.TextAlignment.RIGHT)
                    .setMarginBottom(10);
            document.add(fecha);

            document.add(new Paragraph(" "));

            float[] columnWidths = {50F, 40F, 40F, 40F, 40F, 60F, 60F, 60F, 60F};
            Table table = new Table(columnWidths);
            table.addHeaderCell("Código Producto");
            table.addHeaderCell("Saldo Ant.");
            table.addHeaderCell("Entradas");
            table.addHeaderCell("Salidas");
            table.addHeaderCell("Saldo Act.");
            table.addHeaderCell("Valor Saldo Ant.");
            table.addHeaderCell("Valor Entradas");
            table.addHeaderCell("Valor Salidas");
            table.addHeaderCell("Valor Saldo Act.");

            for (Inventario inventario : inventarios) {
                table.addCell(inventario.getCodigoProducto());
                table.addCell(String.valueOf(inventario.getSaldoAnterior()));
                table.addCell(String.valueOf(inventario.getEntradasMes()));
                table.addCell(String.valueOf(inventario.getSalidasMes()));
                table.addCell(String.valueOf(inventario.getSaldoActual()));
                table.addCell(String.format("%.2f", inventario.getValorSaldoAnterior()));
                table.addCell(String.format("%.2f", inventario.getValorEntradasMes()));
                table.addCell(String.format("%.2f", inventario.getValorSalidasMes()));
                table.addCell(String.format("%.2f", inventario.getValorSaldoActual()));
            }

            document.add(table);
            document.close();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Datos exportados correctamente a " + pdfPath);

            // Abrir el PDF automáticamente (en Windows)
            try {
                Runtime.getRuntime().exec("cmd /c start " + pdfPath);
            } catch (Exception ex) {
                // Ignorar error si no se puede abrir automáticamente
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo exportar los datos a PDF.");
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