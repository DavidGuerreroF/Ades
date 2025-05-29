import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

// iText imports
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.image.ImageData;

public class Cuadre extends Application {
    private Label totalSalesLabel, totalProfitLabel, errorMessage;
    private DatePicker startDatePicker, endDatePicker;
    private Button generateReportButton, backButton;
    private TextField gastosVentasField;

    @Override
    public void start(Stage primaryStage) {
        Label titleLabel = new Label("Reporte de Ventas");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #0294b5;");

        Label startDateLabel = new Label("Fecha de Inicio:");
        startDateLabel.setStyle("-fx-font-size: 18px;");
        startDatePicker = new DatePicker(LocalDate.now());
        startDatePicker.setStyle("-fx-font-size: 16px;");

        Label endDateLabel = new Label("Fecha de Fin:");
        endDateLabel.setStyle("-fx-font-size: 18px;");
        endDatePicker = new DatePicker(LocalDate.now());
        endDatePicker.setStyle("-fx-font-size: 16px;");

        Label gastosLabel = new Label("Gastos de Ventas:");
        gastosLabel.setStyle("-fx-font-size: 18px;");
        gastosVentasField = new TextField();
        gastosVentasField.setPromptText("Ingrese los gastos de ventas");
        gastosVentasField.setStyle("-fx-font-size: 16px;");
        gastosVentasField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*(\\.\\d{0,2})?")) {
                return change;
            }
            return null;
        }));

        totalSalesLabel = new Label("Total Ventas: $0.00");
        totalSalesLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: green; -fx-font-weight: bold;");

        totalProfitLabel = new Label("Ganancias Netas: $0.00");
        totalProfitLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #0A5A9C; -fx-font-weight: bold;");

        generateReportButton = new Button("Generar Reporte");
        generateReportButton.setStyle("-fx-font-size: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-pref-width: 250px; -fx-font-weight: bold;");
        generateReportButton.setOnAction(e -> generateCuadreReport());

        backButton = new Button("Volver");
        backButton.setStyle("-fx-font-size: 20px; -fx-background-color: #0294b5; -fx-text-fill: white; -fx-pref-width: 250px; -fx-font-weight: bold;");
        backButton.setOnAction(e -> goBackToDespachos(primaryStage));

        errorMessage = new Label();
        errorMessage.setStyle("-fx-text-fill: red; -fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(25);
        gridPane.setVgap(25);
        gridPane.setPadding(new Insets(60, 60, 60, 60));
        gridPane.setStyle("-fx-background-color: #f4f4f4;");

        gridPane.add(titleLabel, 0, 0, 2, 1);
        gridPane.add(startDateLabel, 0, 1);
        gridPane.add(startDatePicker, 1, 1);
        gridPane.add(endDateLabel, 0, 2);
        gridPane.add(endDatePicker, 1, 2);
        gridPane.add(gastosLabel, 0, 3);
        gridPane.add(gastosVentasField, 1, 3);
        gridPane.add(generateReportButton, 0, 4, 2, 1);
        gridPane.add(totalSalesLabel, 0, 5, 2, 1);
        gridPane.add(totalProfitLabel, 0, 6, 2, 1);
        gridPane.add(backButton, 0, 7, 2, 1);
        gridPane.add(errorMessage, 0, 8, 2, 1);

        GridPane.setHalignment(titleLabel, HPos.CENTER);
        GridPane.setHalignment(generateReportButton, HPos.CENTER);
        GridPane.setHalignment(backButton, HPos.CENTER);
        GridPane.setHalignment(totalSalesLabel, HPos.CENTER);
        GridPane.setHalignment(totalProfitLabel, HPos.CENTER);
        GridPane.setHalignment(errorMessage, HPos.CENTER);

        Scene scene = new Scene(gridPane, 700, 750);
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
            double gastosVentas = obtenerGastosVentas();
            double totalProfit = calculateProfit(totalSales, gastosVentas);

            totalSalesLabel.setText("Total Ventas: $" + String.format("%.2f", totalSales));
            totalProfitLabel.setText("Ganancias Netas: $" + String.format("%.2f", totalProfit));

            String pdfFilePath = "reporte_ventas.pdf";
            createPdfReport(pdfFilePath, startDate, endDate, totalSales, gastosVentas, totalProfit);

            errorMessage.setStyle("-fx-text-fill: green; -fx-font-size: 18px; -fx-font-weight: bold;");
            errorMessage.setText("Reporte generado correctamente.");

            Runtime.getRuntime().exec("cmd /c start " + pdfFilePath);

        } catch (IOException e) {
            errorMessage.setStyle("-fx-text-fill: red; -fx-font-size: 18px; -fx-font-weight: bold;");
            errorMessage.setText("Error al generar el reporte: " + e.getMessage());
        }
    }

    private double obtenerGastosVentas() {
        String text = gastosVentasField.getText();
        if (text == null || text.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            errorMessage.setText("El campo gastos de ventas debe ser un número.");
            return 0.0;
        }
    }

    private void createPdfReport(String pdfFilePath, LocalDate startDate, LocalDate endDate, double totalSales, double gastosVentas, double totalProfit) {
        try (PdfWriter writer = new PdfWriter(pdfFilePath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // LOGO centrado y pequeño arriba
            try {
                String logoPath = "C:/PROYECTO/images/logo.png";
                ImageData imageData = ImageDataFactory.create(logoPath);
                Image logo = new Image(imageData);
                logo.scaleToFit(90, 90); // Un poco más grande que antes, pero aún pequeño
                logo.setMarginBottom(10);
                logo.setHorizontalAlignment(com.itextpdf.layout.property.HorizontalAlignment.CENTER);
                document.add(logo);
            } catch (Exception ex) {
                System.out.println("No se pudo cargar el logo: " + ex.getMessage());
            }

            // Título centrado y grande
            Paragraph title = new Paragraph("REPORTE DE VENTAS\n\n")
                    .setFontSize(28)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);

            // Fechas centradas
            Paragraph fechas = new Paragraph(
                    "Fecha de Inicio: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                            "Fecha de Fin: " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"
            )
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(fechas);

            // Línea separadora
            Paragraph linea = new Paragraph("————————————————————————————————————")
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(linea);

            // Datos principales, centrados y grandes (sin color)
            Paragraph ventas = new Paragraph("Total Ventas: $" + String.format("%.2f", totalSales))
                    .setFontSize(22)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold();
            document.add(ventas);

            Paragraph gastos = new Paragraph("Gastos de Ventas: $" + String.format("%.2f", gastosVentas))
                    .setFontSize(22)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold();
            document.add(gastos);

            Paragraph ganancias = new Paragraph("Ganancias Netas: $" + String.format("%.2f", totalProfit))
                    .setFontSize(24)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold();
            document.add(ganancias);

            // Línea separadora
            document.add(linea);

            // Pie de página
            Paragraph footer = new Paragraph("\nADES Software")
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(footer);

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

    private double calculateProfit(double totalSales, double gastosVentas) {
        double fixedCosts = 0; // Ejemplo de costos fijos
        double variableCosts = totalSales * 0.0; // Ejemplo: 0% de las ventas son costos variables
        return totalSales - (fixedCosts + variableCosts + gastosVentas);
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