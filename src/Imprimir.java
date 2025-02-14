import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Imprimir extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Consultar Pedido");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        Label idLabel = new Label("ID del Pedido:");
        grid.add(idLabel, 0, 0);

        TextField idField = new TextField();
        grid.add(idField, 1, 0);

        Button consultarButton = new Button("Consultar");
        grid.add(consultarButton, 1, 1);

        consultarButton.setOnAction(e -> {
            String idText = idField.getText();
            if (!idText.isEmpty()) {
                try {
                    int id = Integer.parseInt(idText);
                    Pedido pedido = obtenerPedidoPorId(id);
                    if (pedido != null) {
                        String filePath = pedido.escribirEnArchivo();
                        abrirArchivoConNotepad(filePath);
                        mostrarAlerta(AlertType.INFORMATION, "Éxito", "Pedido escrito en archivo con éxito.");
                    } else {
                        mostrarAlerta(AlertType.ERROR, "Error", "No se encontró el pedido con el id proporcionado.");
                    }
                } catch (NumberFormatException ex) {
                    mostrarAlerta(AlertType.ERROR, "Error", "ID inválido. Debe ser un número entero.");
                }
            } else {
                mostrarAlerta(AlertType.ERROR, "Error", "El campo ID no puede estar vacío.");
            }
        });

        Scene scene = new Scene(grid, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static Pedido obtenerPedidoPorId(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Pedido pedido = null;

        try {
            conn = conexionDB.getConnection();
            String sql = "SELECT * FROM pedidos WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                pedido = new Pedido(
                        rs.getInt("id"),
                        rs.getInt("id_cliente"),
                        rs.getInt("id_domiciliario"),
                        rs.getInt("id_menu_dia"),
                        rs.getString("fecha_pedido"),
                        rs.getString("estado"),
                        rs.getString("direccion_entrega"),
                        rs.getString("telefono_cliente"),
                        rs.getDouble("valor"),
                        rs.getString("plato_seleccionado")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return pedido;
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void abrirArchivoConNotepad(String filePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("notepad.exe", filePath);
            pb.start();
        } catch (IOException e) {
            mostrarAlerta(AlertType.ERROR, "Error", "No se pudo abrir el archivo con Notepad.");
            e.printStackTrace();
        }
    }
}

class Pedido {
    private int id;
    private int idCliente;
    private int idDomiciliario;
    private int idMenuDia;
    private String fechaPedido;
    private String estado;
    private String direccionEntrega;
    private String telefonoCliente;
    private double valor;
    private String platoSeleccionado;

    // Constructor
    public Pedido(int id, int idCliente, int idDomiciliario, int idMenuDia, String fechaPedido, String estado, String direccionEntrega, String telefonoCliente, double valor, String platoSeleccionado) {
        this.id = id;
        this.idCliente = idCliente;
        this.idDomiciliario = idDomiciliario;
        this.idMenuDia = idMenuDia;
        this.fechaPedido = fechaPedido;
        this.estado = estado;
        this.direccionEntrega = direccionEntrega;
        this.telefonoCliente = telefonoCliente;
        this.valor = valor;
        this.platoSeleccionado = platoSeleccionado;
    }

    // Método para escribir la información del pedido en un archivo de texto
    public String escribirEnArchivo() {
        String filePath = "pedido_" + id + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("ID: " + id);
            writer.newLine();
            writer.write("ID Cliente: " + idCliente);
            writer.newLine();
            writer.write("ID Domiciliario: " + idDomiciliario);
            writer.newLine();
            writer.write("ID Menú del Día: " + idMenuDia);
            writer.newLine();
            writer.write("Fecha del Pedido: " + fechaPedido);
            writer.newLine();
            writer.write("Estado: " + estado);
            writer.newLine();
            writer.write("Dirección de Entrega: " + direccionEntrega);
            writer.newLine();
            writer.write("Teléfono del Cliente: " + telefonoCliente);
            writer.newLine();
            writer.write("Valor: " + valor);
            writer.newLine();
            writer.write("Plato Seleccionado: " + platoSeleccionado);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }
}