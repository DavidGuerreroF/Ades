import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

public class GestionClave extends Application {

    private Set<String> clavesExistentes; // Para almacenar las claves existentes

    @Override
    public void start(Stage primaryStage) {
        // Inicializar el conjunto de claves existentes (en una aplicación real, esto vendría de una base de datos)
        clavesExistentes = new HashSet<>();

        // Título de la ventana
        primaryStage.setTitle("Gestión de Claves");

        // Crear controles
        Label nuevaClaveLabel = new Label("Nueva Clave:");
        TextField nuevaClaveField = new TextField();
        nuevaClaveField.setPromptText("Ingrese nueva clave");

        Label eliminarClaveLabel = new Label("Eliminar Clave:");
        TextField eliminarClaveField = new TextField();
        eliminarClaveField.setPromptText("Ingrese clave a eliminar");

        // Botón para crear una nueva clave
        Button btnCrearClave = new Button("Crear Clave");
        btnCrearClave.setOnAction(e -> {
            String nuevaClave = nuevaClaveField.getText().trim();
            if (nuevaClave.isEmpty()) {
                showAlert("Error", "Debe ingresar una nueva clave.");
            } else {
                clavesExistentes.add(nuevaClave);
                showAlert("Éxito", "Clave creada correctamente.");
                nuevaClaveField.clear(); // Limpiar el campo después de crear la clave
            }
        });

        // Botón para eliminar una clave existente
        Button btnEliminarClave = new Button("Eliminar Clave");
        btnEliminarClave.setOnAction(e -> {
            String claveAEliminar = eliminarClaveField.getText().trim();
            if (claveAEliminar.isEmpty()) {
                showAlert("Error", "Debe ingresar la clave a eliminar.");
            } else if (clavesExistentes.contains(claveAEliminar)) {
                clavesExistentes.remove(claveAEliminar);
                showAlert("Éxito", "Clave eliminada correctamente.");
                eliminarClaveField.clear(); // Limpiar el campo después de eliminar la clave
            } else {
                showAlert("Error", "Clave no encontrada.");
            }
        });

        // Crear el layout en cuadrícula
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        // Agregar los controles al layout
        grid.add(nuevaClaveLabel, 0, 0);
        grid.add(nuevaClaveField, 1, 0);
        grid.add(btnCrearClave, 0, 1, 2, 1);
        grid.add(eliminarClaveLabel, 0, 2);
        grid.add(eliminarClaveField, 1, 2);
        grid.add(btnEliminarClave, 0, 3, 2, 1);

        // Crear la escena y mostrarla
        Scene scene = new Scene(grid, 300, 200); // Ventana pequeña
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Método para mostrar alertas
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
