package ejercicio3;

import java.sql.*;
import java.time.LocalDate;

public class TiendaDAO {
    private String host;
    private String base_datos;
    private String usuario;
    private String password;


    public TiendaDAO(String host, String base_datos, String usuario, String password) {
        this.host = host;
        this.base_datos = base_datos;
        this.usuario = usuario;
        this.password = password;
    }

    public Connection establecerConexion() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://" + this.host + "/" + this.base_datos, this.usuario, this.password);
    }

    public void cerrarConexion(Connection conexion, PreparedStatement sentencia, ResultSet resultado) {
        try {
            if (resultado != null) resultado.close();

            if (sentencia != null) sentencia.close();

            if (conexion != null) conexion.close();

        } catch (SQLException exception) {
            System.out.println("Error al cerrar la conexión\n" + exception.getMessage());
            exception.printStackTrace();
        }
    }

    /*
    MÉTODO AUXILIARES
     */
    private Integer returnId(Connection connection, String parameterValue, String tableName, String parameter) {
        PreparedStatement sentencia = null;
        ResultSet result = null;
        Integer id = null;

        try {
            String sql_id = "SELECT id FROM " + tableName +
                    " WHERE " + parameter + " = ?";

            sentencia = connection.prepareStatement(sql_id);
            sentencia.setString(1, parameterValue);
            result = sentencia.executeQuery();

            if(result.next()) {
                id = result.getInt("id");
            }

        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            // Do not close the connection here
            cerrarConexion(null, sentencia, result);
        }
        return id;
    }


    public void añadirVenta(String cliente,String producto, Integer ventas){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexion = establecerConexion();

            Integer productId = returnId(conexion, producto, "productos", "nombre");
            Integer clientId = returnId(conexion, cliente, "clientes", "nombre");

            if (productId == null) {
                throw new RuntimeException("No existe el producto " + producto);
            }
            if (clientId == null) {
                throw new RuntimeException("No existe el cliente " + cliente);
            }

            LocalDate fecha = LocalDate.now();
            Date fechaSql = Date.valueOf(fecha);

            // Añadimos una venta
            String sql_añadirVenta = "INSERT INTO ventas (producto, cliente, fecha, unidades) " +
                    "VALUES (?, ?, ?, ?)";

            sentencia = conexion.prepareStatement(sql_añadirVenta);
            sentencia.setInt(1, productId);
            sentencia.setInt(2, clientId);
            sentencia.setDate(3, fechaSql);
            sentencia.setInt(4, ventas);

            int filasInsertadas = sentencia.executeUpdate();

            System.out.println(filasInsertadas > 0
                    ? cliente + " ha realizado una compra de " + ventas + " unidades"
                    : "Error al realizar la venta");

        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }
    }

    public String comprasCliente(String cliente){
        return null;
    }

    public Double recaudacionTotal(){
        return null;
    }


    public String porCategorias(){
        return null;
    }

    public String ultimaVenta(){
        return null;
    }

    public String masVendido(){
        return null;
    }

    public String sinVentas(){
        return null;
    }


    public void borrarProveedor(){

    }
}
