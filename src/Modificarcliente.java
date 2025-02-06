import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Modificarcliente {

    public static void mostrar(Cliente clienteSeleccionado) {
        if (clienteSeleccionado == null) {
            return; // Si no se seleccionó un cliente, no hacer nada
        }

        Stage window = new Stage();
        window.setTitle("Modificar Cliente");
        window.initModality(Modality.APPLICATION_MODAL); // Bloquear interacción con la ventana principal mientras esté abierta
        window.setMinWidth(400);
        window.setMinHeight(400);

        // Crear campos de entrada con los datos actuales del cliente
        TextField nombreField = new TextField(clienteSeleccionado.getNombre());
        TextField apellidoField = new TextField(clienteSeleccionado.getApellido());
        TextField telefonoField = new TextField(clienteSeleccionado.getTelefono());
        TextField emailField = new TextField(clienteSeleccionado.getEmail());
        TextField direccionField = new TextField(clienteSeleccionado.getDireccion());

        // Crear etiquetas para los campos
        Label nombreLabel = new Label("Nombre:");
        Label apellidoLabel = new Label("Apellido:");
        Label telefonoLabel = new Label("Teléfono:");
        Label emailLabel = new Label("Email:");
        Label direccionLabel = new Label("Dirección:");

        // Botón para guardar los cambios
        Button btnGuardar = new Button("Guardar Cambios");
        btnGuardar.setStyle("-fx-font-size: 14px; -fx-background-color: #0078D7; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");

        btnGuardar.setOnAction(e -> {
            // Obtener los valores actualizados
            String nuevoNombre = nombreField.getText();
            String nuevoApellido = apellidoField.getText();
            String nuevoTelefono = telefonoField.getText();
            String nuevoEmail = emailField.getText();
            String nuevaDireccion = direccionField.getText();

            // Validar que los campos no estén vacíos
            if (nuevoNombre.isEmpty() || nuevoApellido.isEmpty() || nuevoTelefono.isEmpty() || nuevoEmail.isEmpty() || nuevaDireccion.isEmpty()) {
                System.out.println("Todos los campos son obligatorios.");
                return;
            }

            // Actualizar el cliente en la base de datos
            clientDAO.updateClient(clienteSeleccionado.getId(), nuevoNombre, nuevoApellido, nuevoTelefono, nuevoEmail, nuevaDireccion);

            // Cerrar la ventana después de guardar los cambios
            window.close();
        });

        // Crear el diseño
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(nombreLabel, 0, 0);
        grid.add(nombreField, 1, 0);
        grid.add(apellidoLabel, 0, 1);
        grid.add(apellidoField, 1, 1);
        grid.add(telefonoLabel, 0, 2);
        grid.add(telefonoField, 1, 2);
        grid.add(emailLabel, 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(direccionLabel, 0, 4);
        grid.add(direccionField, 1, 4);
        grid.add(btnGuardar, 1, 5);

        // Crear la escena y mostrar la ventana
        Scene scene = new Scene(grid);
        window.setScene(scene);
        window.showAndWait();
    }
}
