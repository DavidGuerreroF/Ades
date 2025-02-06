import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Cuadre extends Application {
    private Label totalSalesLabel, errorMessage;
    private DatePicker startDatePicker, endDatePicker;
    private Button generateReportButton, backButton;

    @Override
    public void start(Stage primaryStage) {
        // Crear el título
        Label titleLabel = new Label("Cuadre de Caja");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #0294b5;");

        // Fechas de inicio y fin
        Label startDateLabel = new Label("Fecha de Inicio:");
        startDatePicker = new DatePicker(LocalDate.now());

        Label endDateLabel = new Label("Fecha de Fin:");
        endDatePicker = new DatePicker(LocalDate.now());

        // Etiqueta para mostrar el total de ventas
        totalSalesLabel = new Label("Total Ventas: $0.00");
        totalSalesLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: green;");

        // Botón para generar el reporte
        generateReportButton = new Button("Generar Reporte");
        generateReportButton.setStyle("-fx-font-size: 18px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        generateReportButton.setOnAction(e -> generateCuadreReport());

        // Botón Volver
        backButton = new Button("Volver");
        backButton.setStyle("-fx-font-size: 18px; -fx-background-color: #0294b5; -fx-text-fill: white;");
        backButton.setOnAction(e -> goBackToDespachos(primaryStage));

        // Etiqueta de error
        errorMessage = new Label();
        errorMessage.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");

        // Layout del formulario
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
        gridPane.add(generateReportButton, 0, 3, 2, 1);
        gridPane.add(totalSalesLabel, 0, 4, 2, 1);
        gridPane.add(backButton, 0, 5, 2, 1);
        gridPane.add(errorMessage, 0, 6, 2, 1);

        GridPane.setHalignment(generateReportButton, HPos.CENTER);
        GridPane.setHalignment(backButton, HPos.CENTER);
        GridPane.setHalignment(errorMessage, HPos.CENTER);

        Scene scene = new Scene(gridPane, 600, 600);
        scene.setFill(Color.web("#f4f4f4"));
        primaryStage.setTitle("Cuadre de Caja");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void generateCuadreReport() {
        try {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            double totalSales = calculateSalesByDateRange(startDate, endDate);

            // Mostrar el total de ventas en la interfaz
            totalSalesLabel.setText("Total Ventas: $" + totalSales);

            // Guardar el reporte en un archivo
            String report = generateReportContent(startDate, endDate, totalSales);
            try (FileWriter fileWriter = new FileWriter("cuadre.txt")) {
                fileWriter.write(report);
            }

            // Mostrar mensaje de éxito
            errorMessage.setStyle("-fx-text-fill: green;");
            errorMessage.setText("Reporte generado correctamente.");
            Runtime.getRuntime().exec("notepad cuadre.txt");

        } catch (IOException e) {
            errorMessage.setText("Error al generar el reporte: " + e.getMessage());
        }
    }

    private double calculateSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        double totalSales = 0.0;
        String query = "SELECT valor_factura FROM facturas WHERE fecha BETWEEN ? AND ?";

        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, java.sql.Date.valueOf(startDate));  // Convertir LocalDate a java.sql.Date
            pstmt.setDate(2, java.sql.Date.valueOf(endDate));    // Convertir LocalDate a java.sql.Date

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                totalSales += rs.getDouble("valor_factura");
            }
        } catch (SQLException e) {
            errorMessage.setText("Error al cargar las ventas: " + e.getMessage());
        }
        return totalSales;
    }

    private String generateReportContent(LocalDate startDate, LocalDate endDate, double totalSales) {
        return "Reporte de Cuadre de Caja\n" +
                "Fecha de Inicio: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                "Fecha de Fin: " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                "----------------------------------------\n" +
                "Total Ventas: $" + totalSales + "\n" +
                "----------------------------------------\n" +
                "Este reporte ha sido generado por el sistema de facturación.\n" +
                "Software: ADES Software";
    }

    private void goBackToDespachos(Stage primaryStage) {
        // Crear una nueva instancia de la clase Despachos
        despachos despachosWindow = new despachos();

        // Cerrar la ventana actual (CuadreCaja)
        primaryStage.close();

        // Llamar al método start() de Despachos (esto mostrará la ventana Despachos)
        despachosWindow.start(new Stage());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
