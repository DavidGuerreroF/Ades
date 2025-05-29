import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Stage;

// iText imports para PDF
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        // Crear TableView
        TableView<Producto> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Producto, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().id)));

        TableColumn<Producto, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().codigo)));

        TableColumn<Producto, String> colDescripcion = new TableColumn<>("Descripción");
        colDescripcion.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().descripcion));

        TableColumn<Producto, String> colCosto = new TableColumn<>("Costo");
        colCosto.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().costo)));

        TableColumn<Producto, String> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().cantidad)));

        TableColumn<Producto, String> colObservaciones = new TableColumn<>("Observaciones");
        colObservaciones.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().observaciones));

        tableView.getColumns().addAll(colId, colCodigo, colDescripcion, colCosto, colCantidad, colObservaciones);

        List<Producto> productos = listarProductos();
        tableView.getItems().addAll(productos);

        // Botones
        Button btnActualizar = new Button("Actualizar Producto");
        Button btnEliminar = new Button("Eliminar ITEM");
        Button btnExportarPDF = new Button("Generar PDF");
        Button btnVolver = new Button("Volver al Menú Principal");

        String buttonStyle = "-fx-background-color: #0294b5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;";
        btnActualizar.setStyle(buttonStyle);
        btnEliminar.setStyle("-fx-background-color: #b50202; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;");
        btnExportarPDF.setStyle(buttonStyle);
        btnVolver.setStyle(buttonStyle);

        HBox buttonLayout = new HBox(10);
        buttonLayout.getChildren().addAll(btnActualizar, btnEliminar, btnExportarPDF, btnVolver);
        buttonLayout.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(new Label("Productos:"), tableView, buttonLayout);

        // Evento actualizar producto
        btnActualizar.setOnAction(e -> {
            Producto productoSeleccionado = tableView.getSelectionModel().getSelectedItem();
            if (productoSeleccionado != null) {
                mostrarFormularioProducto(productoSeleccionado, tableView);
            } else {
                mostrarAlerta(AlertType.WARNING, "Advertencia", "Por favor, selecciona un producto para actualizar.");
            }
        });

        // Evento eliminar producto
        btnEliminar.setOnAction(e -> {
            Producto productoSeleccionado = tableView.getSelectionModel().getSelectedItem();
            if (productoSeleccionado != null) {
                if (productoSeleccionado.cantidad == 0) {
                    boolean confirmado = mostrarConfirmacion("¿Estás seguro de eliminar este producto?");
                    if (confirmado) {
                        eliminarProducto(productoSeleccionado, tableView);
                    }
                } else {
                    mostrarAlerta(AlertType.WARNING, "No permitido", "Solo se puede eliminar el producto si la cantidad es 0.");
                }
            } else {
                mostrarAlerta(AlertType.WARNING, "Advertencia", "Por favor, selecciona un producto para eliminar.");
            }
        });

        // Evento exportar a PDF
        btnExportarPDF.setOnAction(e -> exportarAPdf(tableView.getItems()));

        // Evento volver
        btnVolver.setOnAction(e -> {
            despachos despachosWindow = new despachos();
            despachosWindow.start(new Stage());
            primaryStage.close();
        });

        // Mostrar
        Scene scene = new Scene(layout, 1050, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // En el formulario de actualización, costo y cantidad no pueden ser editados
    private void mostrarFormularioProducto(Producto producto, TableView<Producto> tableView) {
        Stage stage = new Stage();
        stage.setTitle("Actualizar Producto");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(15);
        grid.setHgap(15);

        Label codigoLabel = new Label("Código:");
        TextField codigoInput = new TextField(String.valueOf(producto.codigo));

        Label descripcionLabel = new Label("Descripción:");
        TextField descripcionInput = new TextField(producto.descripcion);

        Label costoLabel = new Label("Costo:");
        TextField costoInput = new TextField(String.valueOf(producto.costo));
        costoInput.setEditable(false);
        costoInput.setStyle("-fx-background-color: #e0e0e0;");

        Label cantidadLabel = new Label("Cantidad Disponible:");
        TextField cantidadInput = new TextField(String.valueOf(producto.cantidad));
        cantidadInput.setEditable(false);
        cantidadInput.setStyle("-fx-background-color: #e0e0e0;");

        Label observacionesLabel = new Label("Observaciones:");
        TextField observacionesInput = new TextField(producto.observaciones);

        Button btnGuardar = new Button("Guardar");
        String buttonStyle = "-fx-background-color: #0294b5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;";
        btnGuardar.setStyle(buttonStyle);

        grid.add(codigoLabel, 0, 0);
        grid.add(codigoInput, 1, 0);
        grid.add(descripcionLabel, 0, 1);
        grid.add(descripcionInput, 1, 1);
        grid.add(costoLabel, 0, 2);
        grid.add(costoInput, 1, 2);
        grid.add(cantidadLabel, 0, 3);
        grid.add(cantidadInput, 1, 3);
        grid.add(observacionesLabel, 0, 4);
        grid.add(observacionesInput, 1, 4);
        grid.add(btnGuardar, 0, 5, 2, 1);

        GridPane.setHalignment(btnGuardar, javafx.geometry.HPos.CENTER);

        btnGuardar.setOnAction(e -> {
            producto.codigo = Integer.parseInt(codigoInput.getText());
            producto.descripcion = descripcionInput.getText();
            // producto.costo y producto.cantidad NO se modifican aquí
            producto.observaciones = observacionesInput.getText();

            actualizarProducto(producto, tableView);
            stage.close();
        });

        Scene scene = new Scene(grid, 400, 350);
        stage.setScene(scene);
        stage.show();
    }

    private void actualizarProducto(Producto producto, TableView<Producto> tableView) {
        String sql = "UPDATE Productos SET codigo = ?, descripcion = ?, observaciones = ? WHERE id = ?";

        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, producto.codigo);
            pstmt.setString(2, producto.descripcion);
            pstmt.setString(3, producto.observaciones);
            pstmt.setInt(4, producto.id);

            pstmt.executeUpdate();
            tableView.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Error", "No se pudo actualizar el producto");
        }
    }

    private void eliminarProducto(Producto producto, TableView<Producto> tableView) {
        String sql = "DELETE FROM Productos WHERE id = ?";
        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, producto.id);
            pstmt.executeUpdate();

            tableView.getItems().remove(producto);
            mostrarAlerta(AlertType.INFORMATION, "Eliminado", "Producto eliminado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Error", "No se pudo eliminar el producto.");
        }
    }

    private boolean mostrarConfirmacion(String mensaje) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
        return result == ButtonType.OK;
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void exportarAPdf(List<Producto> productos) {
        String pdfPath = "productos.pdf";
        try (PdfWriter writer = new PdfWriter(pdfPath);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            Paragraph titulo = new Paragraph("Catálogo de Productos")
                    .setFontSize(22)
                    .setBold()
                    .setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(titulo);

            // Agregar fecha de impresión
            LocalDateTime fechaHora = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fechaImpresion = "Fecha de impresión: " + fechaHora.format(formatter);

            Paragraph fecha = new Paragraph(fechaImpresion)
                    .setFontSize(12)
                    .setTextAlignment(com.itextpdf.layout.property.TextAlignment.RIGHT)
                    .setMarginBottom(10);

            document.add(fecha);

            float[] columnWidths = {40F, 60F, 150F, 60F, 60F, 120F};
            Table table = new Table(columnWidths);

            table.addHeaderCell("ID");
            table.addHeaderCell("Código");
            table.addHeaderCell("Descripción");
            table.addHeaderCell("Costo");
            table.addHeaderCell("Cantidad");
            table.addHeaderCell("Observaciones");

            for (Producto producto : productos) {
                table.addCell(String.valueOf(producto.id));
                table.addCell(String.valueOf(producto.codigo));
                table.addCell(producto.descripcion);
                table.addCell(String.valueOf(producto.costo));
                table.addCell(String.valueOf(producto.cantidad));
                table.addCell(producto.observaciones);
            }

            document.add(table);

            mostrarAlerta(AlertType.INFORMATION, "Éxito", "Productos exportados correctamente a productos.pdf");

            try {
                Runtime.getRuntime().exec("cmd /c start " + pdfPath);
            } catch (Exception ex) {
                // Puede fallar en otros sistemas operativos, ignorar
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Error", "No se pudo exportar los productos a PDF.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}