import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

// iText imports
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

public class Cuadre extends Application {
    private Label totalSalesLabel, totalProfitLabel, errorMessage;
    private DatePicker startDatePicker, endDatePicker;
    private Button generateReportButton, backButton, showGraphButton;
    private ComboBox<String> reportTypeComboBox;

    @Override
    public void start(Stage primaryStage) {
        Label titleLabel = new Label("Reporte de Ventas");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #0294b5;");

        Label startDateLabel = new Label("Fecha de Inicio:");
        startDatePicker = new DatePicker(LocalDate.now());

        Label endDateLabel = new Label("Fecha de Fin:");
        endDatePicker = new DatePicker(LocalDate.now());

        Label reportTypeLabel = new Label("Tipo de Reporte:");
        reportTypeComboBox = new ComboBox<>();
        reportTypeComboBox.getItems().addAll("Diario", "Mensual", "Anual");
        reportTypeComboBox.setValue("Diario");

        totalSalesLabel = new Label("Total Ventas: $0.00");
        totalSalesLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: green;");

        totalProfitLabel = new Label("Ganancias Netas: $0.00");
        totalProfitLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: blue;");

        generateReportButton = new Button("Generar Reporte");
        generateReportButton.setStyle("-fx-font-size: 18px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        generateReportButton.setOnAction(e -> generateCuadreReport());

        showGraphButton = new Button("Mostrar Gráfica");
        showGraphButton.setStyle("-fx-font-size: 18px; -fx-background-color: #FF9800; -fx-text-fill: white;");
        showGraphButton.setOnAction(e -> showSalesGraph());

        backButton = new Button("Volver");
        backButton.setStyle("-fx-font-size: 18px; -fx-background-color: #0294b5; -fx-text-fill: white;");
        backButton.setOnAction(e -> goBackToDespachos(primaryStage));

        errorMessage = new Label();
        errorMessage.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setPadding(new Insets(40, 40, 40, 40));

        gridPane.add(titleLabel, 0, 0, 2, 1);
        gridPane.add(startDateLabel, 0, 1);
        gridPane.add(startDatePicker, 1, 1);
        gridPane.add(endDateLabel, 0, 2);
        gridPane.add(endDatePicker, 1, 2);
        gridPane.add(reportTypeLabel, 0, 3);
        gridPane.add(reportTypeComboBox, 1, 3);
        gridPane.add(generateReportButton, 0, 4, 2, 1);
        gridPane.add(showGraphButton, 0, 5, 2, 1);
        gridPane.add(totalSalesLabel, 0, 6, 2, 1);
        gridPane.add(totalProfitLabel, 0, 7, 2, 1);
        gridPane.add(backButton, 0, 8, 2, 1);
        gridPane.add(errorMessage, 0, 9, 2, 1);

        GridPane.setHalignment(generateReportButton, HPos.CENTER);
        GridPane.setHalignment(showGraphButton, HPos.CENTER);
        GridPane.setHalignment(backButton, HPos.CENTER);
        GridPane.setHalignment(errorMessage, HPos.CENTER);

        Scene scene = new Scene(gridPane, 500, 600);
        scene.setFill(Color.web("#f4f4f4"));
        primaryStage.setTitle("Reporte de Ventas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void generateCuadreReport() {
        try {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            double totalSales = calculateSalesByDateRange(startDate, endDate);
            double totalProfit = calculateProfit(totalSales);

            totalSalesLabel.setText("Total Ventas: $" + totalSales);
            totalProfitLabel.setText("Ganancias Netas: $" + totalProfit);

            // Generar el contenido del reporte
            String reportContent = generateReportContent(startDate, endDate, totalSales, totalProfit);

            // Ruta del archivo PDF
            String pdfFilePath = "reporte_ventas.pdf";

            // Crear el PDF usando iText
            createPdfReport(pdfFilePath, reportContent);

            // Mostrar mensaje de éxito
            errorMessage.setStyle("-fx-text-fill: green;");
            errorMessage.setText("Reporte generado correctamente.");

            // Abrir el archivo PDF automáticamente
            Runtime.getRuntime().exec("cmd /c start " + pdfFilePath);

        } catch (IOException e) {
            errorMessage.setText("Error al generar el reporte: " + e.getMessage());
        }
    }

    private void createPdfReport(String pdfFilePath, String reportContent) {
        try (PdfWriter writer = new PdfWriter(pdfFilePath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Agregar un título al documento
            Paragraph title = new Paragraph("Reporte de Ventas")
                    .setFontSize(20)
                    .setBold()
                    .setMarginBottom(20);
            document.add(title);

            // Agregar contenido del reporte
            Paragraph content = new Paragraph(reportContent)
                    .setFontSize(12)
                    .setMarginBottom(20);
            document.add(content);

            // Mensaje de éxito
            System.out.println("PDF creado en: " + pdfFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage.setText("Error al crear el archivo PDF: " + e.getMessage());
        }
    }

    private double calculateSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        double totalSales = 0.0;
        String query = "SELECT valor FROM pedidos WHERE estado = 'Cobrado' AND fecha_pedido BETWEEN ? AND ?";
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, java.sql.Date.valueOf(startDate));
            pstmt.setDate(2, java.sql.Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                totalSales += rs.getDouble("valor");
            }
        } catch (SQLException e) {
            errorMessage.setText("Error al cargar las ventas: " + e.getMessage());
        }
        return totalSales;
    }

    private double calculateProfit(double totalSales) {
        double fixedCosts = 0; // Ejemplo de costos fijos
        double variableCosts = totalSales * 0.0; // Ejemplo: 30% de las ventas son costos variables
        return totalSales - (fixedCosts + variableCosts);
    }

    private String generateReportContent(LocalDate startDate, LocalDate endDate, double totalSales, double totalProfit) {
        return "Reporte de ventas\n" +
                "Fecha de Inicio: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                "Fecha de Fin: " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                "----------------------------------------\n" +
                "Total Ventas : $" + totalSales + "\n" +
                "Ganancias: $" + totalProfit + "\n" +
                "----------------------------------------\n" +
                "ADES Software";
    }

    private void showSalesGraph() {
        Stage graphStage = new Stage();
        graphStage.setTitle("Gráfica de Ventas");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Día");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Ventas ($)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Ventas por Día");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ventas");

        Map<String, Double> salesData = getSalesData();
        for (Map.Entry<String, Double> entry : salesData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);

        Scene scene = new Scene(barChart, 800, 600);
        graphStage.setScene(scene);
        graphStage.show();
    }

    private Map<String, Double> getSalesData() {
        Map<String, Double> salesData = new HashMap<>();
        String query = "SELECT fecha_pedido, SUM(valor) AS total FROM pedidos WHERE estado = 'Cobrado' GROUP BY fecha_pedido";
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String date = rs.getDate("fecha_pedido").toString();
                double total = rs.getDouble("total");
                salesData.put(date, total);
            }
        } catch (SQLException e) {
            errorMessage.setText("Error al generar datos de la gráfica: " + e.getMessage());
        }
        return salesData;
    }

    private void goBackToDespachos(Stage primaryStage) {
        despachos despachosWindow = new despachos();
        primaryStage.close();
        despachosWindow.start(new Stage());
    }

    public static void main(String[] args) {
        launch(args);
    }
}