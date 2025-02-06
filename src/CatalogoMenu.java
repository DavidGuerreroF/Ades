import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.*;

public class CatalogoMenu extends Application {

    private TableView<MenuDia> tableView; // Tabla para mostrar los menús
    private TextField diaSemanaField, platoField; // Campos para editar los datos
    private Button btnGuardarCambios, btnCancelar;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Catálogo de Menús");

        // Crear los campos para la edición
        diaSemanaField = new TextField();
        platoField = new TextField();

        // Configurar los botones
        btnGuardarCambios = new Button("Guardar Cambios");
        btnCancelar = new Button("Cancelar");

        // Acción al presionar el botón "Cancelar"
        btnCancelar.setOnAction(event -> volverAlMenuPrincipal(primaryStage));

        // Tabla para mostrar los menús
        tableView = new TableView<>();
        TableColumn<MenuDia, String> diaColumn = new TableColumn<>("Día de la Semana");
        diaColumn.setCellValueFactory(cellData -> cellData.getValue().diaSemanaProperty());
        TableColumn<MenuDia, String> platoColumn = new TableColumn<>("Plato");
        platoColumn.setCellValueFactory(cellData -> cellData.getValue().platoProperty());

        tableView.getColumns().add(diaColumn);
        tableView.getColumns().add(platoColumn);

        // Cargar los datos de la base de datos
        cargarMenuDia();

        // Acción al seleccionar un menú de la tabla
        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MenuDia>() {
            @Override
            public void changed(ObservableValue<? extends MenuDia> observable, MenuDia oldValue, MenuDia newValue) {
                if (newValue != null) {
                    diaSemanaField.setText(newValue.getDiaSemana());
                    platoField.setText(newValue.getPlato());
                }
            }
        });

        // Acción al presionar el botón "Guardar Cambios"
        btnGuardarCambios.setOnAction(e -> guardarCambios());

        // Layout de la interfaz
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(tableView, diaSemanaField, platoField, btnGuardarCambios, btnCancelar);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void cargarMenuDia() {
        // Limpiar la tabla antes de cargar los nuevos datos
        tableView.getItems().clear();

        try (Connection connection = conexionDB.getConnection()) {
            String query = "SELECT * FROM menu_dia";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String diaSemana = rs.getString("dia_semana");
                String plato = rs.getString("plato");
                int id = rs.getInt("id"); // Suponiendo que el id está en la base de datos
                MenuDia menu = new MenuDia(id, diaSemana, plato);
                tableView.getItems().add(menu);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al cargar los datos.");
            alert.show();
        }
    }

    private void guardarCambios() {
        MenuDia selectedMenu = tableView.getSelectionModel().getSelectedItem();
        if (selectedMenu != null) {
            String diaSemana = diaSemanaField.getText();
            String plato = platoField.getText();

            try (Connection connection = conexionDB.getConnection()) {
                String sql = "UPDATE menu_dia SET dia_semana = ?, plato = ? WHERE id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, diaSemana);
                stmt.setString(2, plato);
                stmt.setInt(3, selectedMenu.getId());
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    selectedMenu.setDiaSemana(diaSemana);
                    selectedMenu.setPlato(plato);
                    tableView.refresh();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Menú actualizado con éxito.");
                    alert.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error al actualizar el menú.");
                    alert.show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Seleccione un menú para modificar.");
            alert.show();
        }
    }

    private void volverAlMenuPrincipal(Stage primaryStage) {
        // Cerrar la ventana actual
        primaryStage.close();

        // Crear y mostrar la ventana principal (Despachos)
        despachos despachos = new despachos();
        despachos.start(new Stage()); // Iniciar la ventana principal en una nueva instancia de Stage
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class MenuDia {
        private int id;
        private StringProperty diaSemana;
        private StringProperty plato;

        public MenuDia(int id, String diaSemana, String plato) {
            this.id = id;
            this.diaSemana = new SimpleStringProperty(diaSemana);
            this.plato = new SimpleStringProperty(plato);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDiaSemana() {
            return diaSemana.get();
        }

        public void setDiaSemana(String diaSemana) {
            this.diaSemana.set(diaSemana);
        }

        public StringProperty diaSemanaProperty() {
            return diaSemana;
        }

        public String getPlato() {
            return plato.get();
        }

        public void setPlato(String plato) {
            this.plato.set(plato);
        }

        public StringProperty platoProperty() {
            return plato;
        }
    }
}
