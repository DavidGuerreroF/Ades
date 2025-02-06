import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Menudia extends Application {

    private Stage mainStage; // Referencia al menú principal (despachos)
    private Label statusLabel; // Label para mostrar mensajes de estado

    // Constructor que recibe el Stage del menú principal
    public Menudia(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Platos del Día");

        // Etiquetas y controles
        Label diaLabel = createStyledLabel("Día de la Semana:");
        ComboBox<String> diaComboBox = new ComboBox<>();
        diaComboBox.getItems().addAll("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");

        Label sopaLabel = createStyledLabel("Sopa:");
        TextField sopaField = new TextField();
        sopaField.setPromptText("Ingrese la sopa del día");

        Label platoLabel = createStyledLabel("Plato:");
        TextField platoField = new TextField();
        platoField.setPromptText("Ingrese el plato del día");

        Label jugoLabel = createStyledLabel("Jugo:");
        TextField jugoField = new TextField();
        jugoField.setPromptText("Ingrese el jugo del día");

        Label postreLabel = createStyledLabel("Postre:");
        TextField postreField = new TextField();
        postreField.setPromptText("Ingrese el postre del día");

        // Crear el label de estado para mostrar mensajes
        this.statusLabel = new Label("");
        this.statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Botón de Crear
        Button btnCrear = createStyledButton("Crear");
        btnCrear.setOnAction(e -> {
            // Lógica para crear el menú
            String diaSeleccionado = diaComboBox.getValue();
            String sopa = sopaField.getText();
            String plato = platoField.getText();
            String jugo = jugoField.getText();
            String postre = postreField.getText();

            // Verificar si los campos están vacíos
            if (diaSeleccionado == null || sopa.isEmpty() || plato.isEmpty() || jugo.isEmpty() || postre.isEmpty()) {
                updateStatus("Debe completar todos los campos.", "red");
            } else {
                // Concatenamos los platos en un solo campo
                String platos = "Sopa: " + sopa + ", Plato: " + plato + ", Jugo: " + jugo + ", Postre: " + postre;

                // Crear el menú en la base de datos
                if (crearMenu(diaSeleccionado, platos)) {
                    updateStatus("Menú creado con éxito.", "green");
                } else {
                    updateStatus("Error al crear el menú.", "red");
                }
            }
        });

        // Botón de Cancelar
        Button btnCancelar = createStyledButton("Cancelar");
        btnCancelar.setOnAction(e -> {
            primaryStage.close(); // Cierra la ventana actual
            mainStage.show(); // Muestra el menú principal
        });

        // Crear un layout en cuadrícula
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);  // Aumentar espacio horizontal
        grid.setVgap(20);  // Aumentar espacio vertical
        grid.setPadding(new Insets(40));

        // Agregar elementos al layout
        grid.add(diaLabel, 0, 0);
        grid.add(diaComboBox, 1, 0);
        grid.add(sopaLabel, 0, 1);
        grid.add(sopaField, 1, 1);
        grid.add(platoLabel, 0, 2);
        grid.add(platoField, 1, 2);
        grid.add(jugoLabel, 0, 3);
        grid.add(jugoField, 1, 3);
        grid.add(postreLabel, 0, 4);
        grid.add(postreField, 1, 4);
        grid.add(statusLabel, 0, 5, 2, 1);  // Agregar el statusLabel
        grid.add(btnCrear, 0, 6);
        grid.add(btnCancelar, 1, 6);

        // Crear la escena y mostrarla
        Scene scene = new Scene(grid, 600, 400);  // Aumentar tamaño de la ventana
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateStatus(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
    }

    private boolean crearMenu(String dia, String platos) {
        try (Connection connection = conexionDB.getConnection()) {
            // Insertar un nuevo menú para el día seleccionado, no reemplazar el anterior
            String sql = "INSERT INTO menu_dia (dia_semana, plato) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, dia);
                stmt.setString(2, platos); // Insertamos los platos concatenados
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");  // Aumentar tamaño de la fuente
        return label;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setMinSize(200, 60);  // Aumentar tamaño de los botones
        button.setStyle("-fx-font-size: 18px; -fx-background-color: #0078D7; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-font-size: 18px; -fx-background-color: #005EA6; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-font-size: 18px; -fx-background-color: #0078D7; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;"));
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
