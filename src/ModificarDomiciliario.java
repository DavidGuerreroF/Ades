import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ModificarDomiciliario {

    public static void mostrar(Domiciliario domiciliario) {
        // Crear la ventana emergente para modificar los datos del domiciliario
        Stage stage = new Stage();
        stage.setTitle("Modificar Domiciliario");

        // Crear los controles del formulario
        Label lblNombre = new Label("Nombre:");
        TextField txtNombre = new TextField(domiciliario.getNombre());

        Label lblApellido = new Label("Apellido:");
        TextField txtApellido = new TextField(domiciliario.getApellido());

        Label lblTelefono = new Label("Teléfono:");
        TextField txtTelefono = new TextField(domiciliario.getTelefono());

        Label lblEmail = new Label("Email:");
        TextField txtEmail = new TextField(domiciliario.getEmail());

        // Crear botón de guardar cambios
        Button btnGuardar = new Button("Guardar Cambios");

        // Acción del botón "Guardar Cambios"
        btnGuardar.setOnAction(e -> {
            String nombre = txtNombre.getText();
            String apellido = txtApellido.getText();
            String telefono = txtTelefono.getText();
            String email = txtEmail.getText();

            if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
                // Verificar que todos los campos están completos
                Alert alert = new Alert(Alert.AlertType.WARNING, "Todos los campos deben ser completados", ButtonType.OK);
                alert.showAndWait();
            } else {
                // Actualizar los datos del domiciliario
                DomiciliarioDAO.updateDomiciliario(domiciliario.getId(), nombre, apellido, telefono, email);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Domiciliario actualizado con éxito", ButtonType.OK);
                alert.showAndWait();
                stage.close(); // Cerrar la ventana de modificación
            }
        });

        // Crear el layout del formulario
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.add(lblNombre, 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(lblApellido, 0, 1);
        grid.add(txtApellido, 1, 1);
        grid.add(lblTelefono, 0, 2);
        grid.add(txtTelefono, 1, 2);
        grid.add(lblEmail, 0, 3);
        grid.add(txtEmail, 1, 3);
        grid.add(btnGuardar, 1, 4);

        // Mostrar la ventana de modificación
        Scene scene = new Scene(grid, 400, 250);
        stage.setScene(scene);
        stage.show();
    }
}
