import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Asegúrate de importar la clase de conexión a la base de datos
// import PROYECTO.conexionDB;

public class clientDAO {

    // Método para guardar un cliente en la base de datos
    public static void saveClient(String nombre, String apellido, String telefono, String email, String direccion) {
        String insertSQL = "INSERT INTO clientes (nombre, apellido, telefono, email, direccion) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            // Establecer los valores de los parámetros en la consulta
            pstmt.setString(1, nombre);
            pstmt.setString(2, apellido);
            pstmt.setString(3, telefono);
            pstmt.setString(4, email);
            pstmt.setString(5, direccion);

            // Ejecutar la consulta
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cliente guardado con éxito.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para modificar un cliente existente
    public static void updateClient(int clientId, String nombre, String apellido, String telefono, String email, String direccion) {
        String updateSQL = "UPDATE clientes SET nombre = ?, apellido = ?, telefono = ?, email = ?, direccion = ? WHERE id = ?";

        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            // Establecer los valores de los parámetros en la consulta
            pstmt.setString(1, nombre);
            pstmt.setString(2, apellido);
            pstmt.setString(3, telefono);
            pstmt.setString(4, email);
            pstmt.setString(5, direccion);
            pstmt.setInt(6, clientId);  // Asumimos que el cliente tiene un ID único

            // Ejecutar la consulta
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cliente modificado con éxito.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para borrar un cliente
    public static void deleteClient(int clientId) {
        String deleteSQL = "DELETE FROM clientes WHERE id = ?";

        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            // Establecer el valor del parámetro en la consulta
            pstmt.setInt(1, clientId);  // El ID del cliente a borrar

            // Ejecutar la consulta
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cliente borrado con éxito.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para obtener el ID de un cliente por su nombre
    public static int getClientIdByName(String nombre) {
        String selectSQL = "SELECT id FROM clientes WHERE nombre = ?";
        int clientId = -1;  // Valor predeterminado si no se encuentra el cliente

        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

            // Establecer el parámetro en la consulta
            pstmt.setString(1, nombre);

            // Ejecutar la consulta
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                clientId = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientId;
    }

    // Método para obtener todos los clientes (con todos sus detalles)
    public static List<Cliente> getAllClients() {
        String selectSQL = "SELECT id, nombre, apellido, telefono, email, direccion FROM clientes";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = conexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

            // Ejecutar la consulta
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Crear un cliente con todos los detalles
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String telefono = rs.getString("telefono");
                String email = rs.getString("email");
                String direccion = rs.getString("direccion");

                Cliente cliente = new Cliente(id, nombre, apellido, telefono, email, direccion);
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }
}
