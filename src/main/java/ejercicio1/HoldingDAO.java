package ejercicio1;

import java.sql.*;

public class HoldingDAO {

    private String host;
    private String base_datos;
    private String usuario;
    private String password;


    public HoldingDAO(String host, String base_datos, String usuario, String password) {
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
    ------------------
    MÉTODOS AUXILIARES
    ------------------
     */
    private int comprobarEmpleado(String email) {
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        int res = 0;

        try {
            conexion = establecerConexion();

            // SQL
            String sql_buscarEmpleado = "SELECT id FROM empleados WHERE email = ?";

            sentencia = conexion.prepareStatement(sql_buscarEmpleado);
            sentencia.setString(1, email);
            resultado = sentencia.executeQuery();

            if(resultado.next()) {
                res = resultado.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }
        return res;
    }






    public void agregarEmpleado(String nombre, String apellidos, String fecha_nacimiento, String categoria, String email, String contratacion, Double salario, String empresa) {
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexion = establecerConexion();

//            int empleadoId = comprobarEmpleado(email);
//            if(empleadoId > 0) {
//                ...
//            }

            // Consultar si el empleado ya existe
            String sql_comprobarEmpleado = "SELECT id FROM empleados WHERE email = ? " +
                    "AND empresa_id = (SELECT id FROM empresas WHERE cif = ?)";

            sentencia = conexion.prepareStatement(sql_comprobarEmpleado);
            sentencia.setString(1, email);
            sentencia.setString(2, empresa);

            resultado = sentencia.executeQuery();

            if (!resultado.next()) {
                // El empleado no existe, entonces lo añadimos
                String sql_insertEmpleado = "INSERT INTO empleados (fecha_nacimiento, category, email, nombre, apellidos, fecha_contratacion, salario, empresa_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, (SELECT id FROM empresas WHERE cif = ?))";

                sentencia = conexion.prepareStatement(sql_insertEmpleado);
                sentencia.setString(1, fecha_nacimiento);
                sentencia.setString(2, categoria);
                sentencia.setString(3, email);
                sentencia.setString(4, nombre);
                sentencia.setString(5, apellidos);
                sentencia.setString(6, contratacion);
                sentencia.setDouble(7, salario);
                sentencia.setString(8, empresa);

                int filasInsertadas = sentencia.executeUpdate();
                if (filasInsertadas > 0) {
                    System.out.println("Empleado " + nombre + " " + apellidos + " agregado correctamente a la empresa con CIF:" + empresa);
                } else {
                    // El empleado ya existe
                    System.out.println("Error al agregar el empleado");
                }
            } else {
                System.out.println("El empleado ya existe");
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }
    }


    public void subirSueldo(String empresa, Double subida) {
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexion = establecerConexion();

            // Comprobar que la empresa exista
            String sql_comprobarEmpresa = "SELECT id FROM empresas WHERE cif = ?";

            sentencia = conexion.prepareStatement(sql_comprobarEmpresa);
            sentencia.setString(1, empresa);

            resultado = sentencia.executeQuery();

            if(!resultado.next()) {
                System.out.println("No existe la empresa");
            } else {
                // Aumentamos el sueldo
                String sql_aumentarSueldo = "UPDATE empleados " +
                        "SET salario = salario + ? " +
                        "WHERE empresa_id = (SELECT id FROM empresas WHERE cif = ?)";

                sentencia = conexion.prepareStatement(sql_aumentarSueldo);
                sentencia.setDouble(1, subida);
                sentencia.setString(2, empresa);

                int filasModificadas = sentencia.executeUpdate();
                if (filasModificadas > 0) {
                    System.out.println("Se ha incrementado el sueldo a todos los empleados en " + subida + " euros");
                } else {
                    System.out.println("Error al aumentar el sueldo a los empleados");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }
    }


    public void trasladarEmpleado(String emplead,String empresa){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexion = establecerConexion();

            // Comprobamos que existe la empresa de destino
            String sql_buscarEmpresaNueva = "SELECT id FROM empresas " +
                    "WHERE cif = ?";

            sentencia = conexion.prepareStatement(sql_buscarEmpresaNueva);
            sentencia.setString(1, empresa);
            resultado = sentencia.executeQuery();

            if(!resultado.next()) {
                System.out.println("No existe la empresa de destino");
            } else {
                int empresaId = resultado.getInt("id");

                // Comprobamos que exista el empleado
                String sql_buscarEmpleado = "SELECT id FROM empleados " +
                        "WHERE email = ?";

                sentencia = conexion.prepareStatement(sql_buscarEmpleado);
                sentencia.setString(1, emplead);
                resultado = sentencia.executeQuery();

                if(!resultado.next()) {
                    System.out.println("No existe el empleado");
                } else {
                    int empleadoId = resultado.getInt("id");

                    // Actualizamos la empresa del empleado
                    String sql_actualizarEmpleado = "UPDATE empleados SET empresa_id = ? WHERE id = ?";

                    sentencia = conexion.prepareStatement(sql_actualizarEmpleado);
                    sentencia.setInt(1, empresaId);
                    sentencia.setInt(2, empleadoId);

                    int filasModificadas = sentencia.executeUpdate();
                    if (filasModificadas > 0) {
                        System.out.println("Se ha cambiado correctamente de empresa al empleado con email: " + emplead);
                    } else {
                        System.out.println("Error al cambiar la empresa del empleado");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }
    }


    public String empleadosEmpresa(String empresa) {
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        String res = "";

        try {
            conexion = establecerConexion();

            // Comprobamos si existe la empresa
            String sql_buscarEmpresa = "SELECT id FROM empresas " +
                    "WHERE cif = ?";

            sentencia = conexion.prepareStatement(sql_buscarEmpresa);
            sentencia.setString(1, empresa);
            resultado = sentencia.executeQuery();

            if(!resultado.next()) {
                System.out.println("No existe la empresa");
            }
            else {
                int empresaId = resultado.getInt("id");

                // Mostramos los nombres de todos los empleados de dicha empresa
                String sql_nombreEmpleados = "SELECT nombre, apellidos, email FROM empleados " +
                        "WHERE empresa_id = ?";

                sentencia = conexion.prepareStatement(sql_nombreEmpleados);
                sentencia.setInt(1, empresaId);
                resultado = sentencia.executeQuery();

                while(resultado.next()) {
                    res += resultado.getString(1) + " " + resultado.getString(2) + ": " + resultado.getString(3) + "\n";
                }
            }

        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }
        return res;
    }


    public void crearCoche(String modelo,String fabricante,Double cc,Integer año,String empleado){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexion = establecerConexion();

            // Comprobar si existe el empleado
            String sql_buscarEmpleado = "SELECT id FROM empleados " +
                    "WHERE email = ?";

            sentencia = conexion.prepareStatement(sql_buscarEmpleado);
            sentencia.setString(1, empleado);
            resultado = sentencia.executeQuery();

            if(!resultado.next()) {
                System.out.println("No existe el empleado");
            } else {
                int empleadoId = resultado.getInt("id");

                // Añadir coche a la BD y modificar
                String sql_insertarCoche = "INSERT INTO coches (cc, fabricante, modelo, año_lanzamiento, empleado_id) " +
                        "VALUES (?, ?, ?, ?, ?)";

                sentencia = conexion.prepareStatement(sql_insertarCoche);
                sentencia.setDouble(1, cc);
                sentencia.setString(2, fabricante);
                sentencia.setString(3, modelo);
                sentencia.setInt(4, año);
                sentencia.setInt(5, empleadoId);

                int filasInsertadas = sentencia.executeUpdate();
                if(filasInsertadas > 0) {
                    System.out.println("Coche agregado correctamente a la BD!");
                } else {
                    System.out.println("Error al agregar el coche en la BD");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }
    }


    public Double costeProyecto(String proyecto){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        double res = 0.0;

        try {
            conexion = establecerConexion();

            // Comprobar si existe el proyecto
            String sql_comprobarProyecto = "SELECT id FROM proyectos " +
                    "WHERE titulo = ?";
            sentencia = conexion.prepareStatement(sql_comprobarProyecto);
            sentencia.setString(1, proyecto);
            resultado = sentencia.executeQuery();

            if(!resultado.next()) {
                System.out.println("No existe el proyecto");
            } else {
                int proyectoId = resultado.getInt("id");

                // Suma de los sueldos
                String sql_suma = "SELECT SUM(e.salario) AS coste FROM empleados e " +
                        "INNER JOIN empleados_proyectos ep ON e.id = ep.empleado_id " +
                        "INNER JOIN proyectos p ON ep.proyecto_id = p.id " +
                        "WHERE p.titulo = ?";

                sentencia = conexion.prepareStatement(sql_suma);
                sentencia.setString(1, proyecto);
                resultado = sentencia.executeQuery();

                if(resultado.next()) {
                    res = resultado.getDouble("coste");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }
        return res;
    }


    public String resumenProyectos(){
        /*
        String sql = "SELECT * FROM proyectos";
        sentencia = conexion.preparedStatement(sql);
        resultado = sentencia.executeQuery();

        while(resultado.next()) {
            String titulo = resultado.getString("titulo");
            salida += titulo + "\n" +
                "Fecha: " + resultado.getString("comienzo") + "\n" +
                empleadosProyecto(titulo) +
                "Coste: " + costeProyecto(titulo) +
                "=========================\n"
        }
         */

        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        StringBuilder res = new StringBuilder();

        try {
            conexion = establecerConexion();

            // Mostrar la información de todos los proyectos
            String sql_infoProyectos = "SELECT p.titulo AS proyecto, e.nombre AS nombre_empleado " +
                    "FROM proyectos p " +
                    "LEFT JOIN empleados_proyectos ep ON p.id = ep.proyecto_id " +
                    "LEFT JOIN empleados e ON ep.empleado_id = e.id";

            sentencia = conexion.prepareStatement(sql_infoProyectos);
            resultado = sentencia.executeQuery();

            while(resultado.next()) {
                String proyecto = resultado.getString("proyecto");
                String nombreEmpleado = resultado.getString("nombre_empleado");

                res.append("Proyecto: ").append(proyecto).append(", Nombre del Empleado: ").append(nombreEmpleado).append(", Salario: ").append(costeProyecto(proyecto)).append("\n");
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }

        return res.toString();
    }


    public Integer empleadosSinCoche() {
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        Integer res = 0;

        try {
            conexion =establecerConexion();

            // Comprobar qué empleados no tienen coche
            String sql_empleadosSinCoche = "SELECT COUNT(*) AS cantidad FROM empleados e " +
                    "LEFT JOIN coches c ON e.id = c.empleado_id " +
                    "WHERE c.empleado_id IS NULL";

            sentencia = conexion.prepareStatement(sql_empleadosSinCoche);
            resultado = sentencia.executeQuery();

            if(resultado.next()) {
                res = resultado.getInt("cantidad");
            }

        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }

        return res;
    }


    public void borrarProyectosSinEmp(){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexion =establecerConexion();

            // Comprobar que proyectos no tiene empleados
            String sql_comprobarProyectos = "SELECT p.id AS proyecto_id " +
                    "FROM proyectos p " +
                    "LEFT JOIN empleados_proyectos ep ON p.id = ep.proyecto_id " +
                    "WHERE ep.proyecto_id IS NULL";

            sentencia = conexion.prepareStatement(sql_comprobarProyectos);
            resultado = sentencia.executeQuery();

            // Borramos los proyectos sin empleados
            while(resultado.next()) {
                int proyectoId = resultado.getInt("proyecto_id");
                String sql_borrarProyecto = "DELETE FROM proyectos WHERE id = ?";

                sentencia = conexion.prepareStatement(sql_borrarProyecto);
                sentencia.setInt(1, proyectoId);

                int filasEliminadas = sentencia.executeUpdate();

                if(filasEliminadas > 0) {
                    System.out.println("Filas eliminadas: " + filasEliminadas);
                } else {
                    System.out.println("Error al eliminar los proyectos sin empleados");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }
    }


    public void borrarAño(Integer año){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexion = establecerConexion();

            // Eliminar empleados de los proyectos
            String sql_eliminarEmpleados = "DELETE FROM empleados_proyectos " +
                    "WHERE proyecto_id IN (SELECT id FROM proyectos WHERE YEAR(comienzo) = ?)";

            sentencia = conexion.prepareStatement(sql_eliminarEmpleados);
            sentencia.setInt(1, año);
            sentencia.executeUpdate();

            // Una vez eliminados los empleados, pasamos a eliminar el proyecto por el año especificado
            String sql_eliminarProyecto = "DELETE FROM proyectos WHERE YEAR(comienzo) = ?";

            sentencia = conexion.prepareStatement(sql_eliminarProyecto);
            sentencia.setInt(1, año);
            int filasEliminadas = sentencia.executeUpdate();

            if(filasEliminadas > 0) {
                System.out.println("Proyectos eliminadas: " + filasEliminadas);
            } else {
                System.out.println("Error al eliminar los proyectos que comenzaron en " + año);
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }
    }
}