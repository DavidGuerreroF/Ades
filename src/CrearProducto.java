import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CrearProducto extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Crear Producto");

        // Crear el GridPane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(15);
        grid.setHgap(15);

        // Etiquetas y campos de texto
        Label codigoLabel = new Label("Código:");
        GridPane.setConstraints(codigoLabel, 0, 0);
        TextField codigoInput = new TextField();
        GridPane.setConstraints(codigoInput, 1, 0);

        Label descripcionLabel = new Label("Descripción:");
        GridPane.setConstraints(descripcionLabel, 0, 1);
        TextField descripcionInput = new TextField();
        GridPane.setConstraints(descripcionInput, 1, 1);

        Label costoLabel = new Label("Costo:");
        GridPane.setConstraints(costoLabel, 0, 2);
        TextField costoInput = new TextField();
        GridPane.setConstraints(costoInput, 1, 2);

        Label cantidadLabel = new Label("Cantidad Disponible:");
        GridPane.setConstraints(cantidadLabel, 0, 3);
        TextField cantidadInput = new TextField();
        GridPane.setConstraints(cantidadInput, 1, 3);

        Label observacionesLabel = new Label("Observaciones:");
        GridPane.setConstraints(observacionesLabel, 0, 4);
        TextArea observacionesInput = new TextArea();
        observacionesInput.setPrefRowCount(3);
        GridPane.setConstraints(observacionesInput, 1, 4);

        // Botón de crear producto
        Button crearProductoButton = new Button("Crear Producto");
        crearProductoButton.setFont(new Font("Arial", 14));
        crearProductoButton.setTextFill(Color.WHITE);
        crearProductoButton.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(5), Insets.EMPTY)));
        crearProductoButton.setMinSize(150, 40);
        crearProductoButton.setOnAction(e -> {
            int codigo = Integer.parseInt(codigoInput.getText());
            String descripcion = descripcionInput.getText();
            double costo = Double.parseDouble(costoInput.getText());
            int cantidad = Integer.parseInt(cantidadInput.getText());
            String observaciones = observacionesInput.getText();

            if (crearProducto(codigo, descripcion, costo, cantidad, observaciones)) {
                mostrarAlerta(AlertType.INFORMATION, "Éxito", "Producto creado exitosamente.");
            } else {
                mostrarAlerta(AlertType.ERROR, "Error", "No se pudo crear el producto");
            }
        });

        // Botón de volver
        Button volverButton = new Button("Volver");
        volverButton.setFont(new Font("Arial", 14));
        volverButton.setTextFill(Color.WHITE);
        volverButton.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(5), Insets.EMPTY)));
        volverButton.setMinSize(150, 40);
        volverButton.setOnAction(e -> {
            despachos despachos = new despachos();
            despachos.start(new Stage());
            primaryStage.close();
        });

        // Contenedor para los botones
        HBox buttonBox = new HBox(20);
        buttonBox.getChildren().addAll(crearProductoButton, volverButton);
        GridPane.setConstraints(buttonBox, 1, 5);

        // Agregar todos los elementos al GridPane
        grid.getChildren().addAll(codigoLabel, codigoInput, descripcionLabel, descripcionInput, costoLabel, costoInput, cantidadLabel, cantidadInput, observacionesLabel, observacionesInput, buttonBox);

        // Crear la escena y mostrarla
        Scene scene = new Scene(grid, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private boolean crearProducto(int codigo, String descripcion, double costo, int cantidad, String observaciones) {
        String sql = "INSERT INTO Productos (codigo, descripcion, costo, cantidad, observaciones) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, codigo);
            pstmt.setString(2, descripcion);
            pstmt.setDouble(3, costo);
            pstmt.setInt(4, cantidad);
            pstmt.setString(5, observaciones);

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
