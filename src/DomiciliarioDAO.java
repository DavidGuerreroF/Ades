import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Clase DAO para interactuar con la tabla 'domiciliarios'
public class DomiciliarioDAO {

    // Consultas SQL
    private static final String GET_ALL_DOMICILIARIOS = "SELECT * FROM domiciliarios";
    private static final String DELETE_DOMICILIARIO = "DELETE FROM domiciliarios WHERE id = ?";
    private static final String INSERT_DOMICILIARIO = "INSERT INTO domiciliarios (nombre, apellido, telefono, email) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_DOMICILIARIO = "UPDATE domiciliarios SET nombre = ?, apellido = ?, telefono = ?, email = ? WHERE id = ?";

    // Obtener todos los domiciliarios
    public static List<Domiciliario> getAllDomiciliarios() {
        List<Domiciliario> domiciliarios = new ArrayList<>();

        try (Connection conn = conexionDB.getConnection(); // Obtener conexión desde la clase externa
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL_DOMICILIARIOS)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String telefono = rs.getString("telefono");
                String email = rs.getString("email");

                Domiciliario domiciliario = new Domiciliario(id, nombre, apellido, telefono, email);
                domiciliarios.add(domiciliario);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener los domiciliarios: " + e.getMessage());
        }

        return domiciliarios;
    }

    // Borrar un domiciliario por ID
    public static void deleteDomiciliario(int id) {
        try (Connection conn = conexionDB.getConnection(); // Obtener conexión desde la clase externa
             PreparedStatement pstmt = conn.prepareStatement(DELETE_DOMICILIARIO)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al borrar el domiciliario: " + e.getMessage());
        }
    }

    // Insertar un nuevo domiciliario
    public static void insertDomiciliario(String nombre, String apellido, String telefono, String email) {
        try (Connection conn = conexionDB.getConnection(); // Obtener conexión desde la clase externa
             PreparedStatement pstmt = conn.prepareStatement(INSERT_DOMICILIARIO)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, apellido);
            pstmt.setString(3, telefono);
            pstmt.setString(4, email);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al insertar el domiciliario: " + e.getMessage());
        }
    }

    // Actualizar los datos de un domiciliario
    public static void updateDomiciliario(int id, String nombre, String apellido, String telefono, String email) {
        try (Connection conn = conexionDB.getConnection(); // Obtener conexión desde la clase externa
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_DOMICILIARIO)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, apellido);
            pstmt.setString(3, telefono);
            pstmt.setString(4, email);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar el domiciliario: " + e.getMessage());
        }
    }
}
