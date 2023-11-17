package ejercicio3;

import org.w3c.dom.ls.LSOutput;

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

    /*
    CONEXIÓN
     */
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
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        StringBuilder res = new StringBuilder();

        try {
            conexion = establecerConexion();

            Integer clientId = returnId(conexion, cliente, "clientes", "nombre");

            if (clientId == null) {
                throw new RuntimeException("No existe el cliente " + cliente);
            } else {

                // Mostramos los componentes comprados y sus cantidades
                String sql_comprasCliente = "SELECT clientes.nombre AS cliente_nombre, productos.nombre AS producto_nombre, ventas.unidades " +
                        "FROM ventas " +
                        "JOIN clientes ON ventas.cliente = clientes.id " +
                        "JOIN productos ON ventas.producto = productos.id " +
                        "WHERE clientes.id = ?";

                sentencia = conexion.prepareStatement(sql_comprasCliente);
                sentencia.setInt(1, clientId);
                resultado = sentencia.executeQuery();

                while (resultado.next()) {
                    String clientName = resultado.getString("cliente_nombre");
                    String productName = resultado.getString("producto_nombre");
                    int amount = resultado.getInt("unidades");

                    res.append("Cliente: ").append(clientName).append(", Producto: ").append(productName).append(", Cantidad: ").append(amount).append("\n");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }

        return res.toString();
    }

    public Double recaudacionTotal(){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        Double res = 0.0;

        try {
            conexion = establecerConexion();

            // Mostrar la recaudación de la tienda por ventas
            String sql_recaudacionTotal = "SELECT SUM(productos.precio * ventas.unidades) AS recaudacion_total " +
                    "FROM ventas " +
                    "JOIN productos ON ventas.producto = productos.id";

            sentencia = conexion.prepareStatement(sql_recaudacionTotal);
            resultado = sentencia.executeQuery();

            if(resultado.next()) {
                res = resultado.getDouble("recaudacion_total");
            }

        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }

        return res;
    }


    public String porCategorias(){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        StringBuilder res = new StringBuilder();

        try {
            conexion = establecerConexion();

            // Mostrar la cantidad de ventas organizadas por categorias de productos
            String sql_porCategorias = "SELECT categorias.nombre AS categoria, COALESCE(SUM(ventas.unidades), 0) AS cantidad_ventas " +
                    "FROM ventas " +
                    "RIGHT JOIN productos ON ventas.producto = productos.id " +
                    "RIGHT JOIN categorias ON productos.categoria_id = categorias.id " +
                    "GROUP BY categorias.nombre";

            sentencia = conexion.prepareStatement(sql_porCategorias);
            resultado = sentencia.executeQuery();

            while(resultado.next()) {
                String categoria = resultado.getString("categoria");
                int ventas = resultado.getInt("cantidad_ventas");

                res.append("Categoría: ").append(categoria).append(", Ventas: ").append(ventas).append(" unidades.\n");
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }

        return res.toString();
    }

    public String ultimaVenta(){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        StringBuilder res = new StringBuilder();

        try {
            conexion = establecerConexion();

            // Mostrar el nombre del cliente y del producto de la última venta realizada en la tienda
            String sql_ultimaVenta = "SELECT clientes.nombre AS nombre_cliente, productos.nombre AS nombre_producto, ventas.unidades AS cantidad_producto " +
                    "FROM ventas " +
                    "JOIN clientes ON ventas.cliente = clientes.id " +
                    "JOIN productos ON ventas.producto = productos.id " +
                    "ORDER BY ventas.fecha DESC LIMIT 1";

            sentencia = conexion.prepareStatement(sql_ultimaVenta);
            resultado = sentencia.executeQuery();

            while(resultado.next()) {
                String nombreCliente = resultado.getString("nombre_cliente");
                String nombreProducto = resultado.getString("nombre_producto");
                int cantidadProducto = resultado.getInt("cantidad_producto");

                res.append("Cliente: ").append(nombreCliente).append(", Producto: ").append(nombreProducto).append(", Unidades: ").append(cantidadProducto).append(" unidades.");
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }

        return res.toString();
    }

    public String masVendido(){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        StringBuilder res = new StringBuilder();

        try {
            conexion = establecerConexion();

            // Devolvemos el nombre del producto con mayor cantidad de ventas, incluyendo el número de ventas junto al nombre
            String sql_masVendido = "SELECT productos.nombre AS nombre_producto, SUM(ventas.unidades) AS cantidad_ventas " +
                    "FROM ventas " +
                    "JOIN productos ON ventas.producto = productos.id " +
                    "GROUP BY productos.nombre " +
                    "ORDER BY cantidad_ventas DESC " +
                    "LIMIT 1";

            sentencia = conexion.prepareStatement(sql_masVendido);
            resultado = sentencia.executeQuery();

            while(resultado.next()) {
                String nombreProducto = resultado.getString("nombre_producto");
                int cantidadVentas = resultado.getInt("cantidad_ventas");

                res.append("Número de ventas: ").append(cantidadVentas).append(", Producto: ").append(nombreProducto);
            }

        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }

        return res.toString();
    }

    public String sinVentas(){
        return null;
    }


    public void borrarProveedor(){

    }
}
