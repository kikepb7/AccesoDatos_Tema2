package ejercicio2;

import java.sql.*;
import java.time.LocalDate;

public class ClubDAO {

    private String host;
    private String base_datos;
    private String usuario;
    private String password;


    public ClubDAO(String host, String base_datos, String usuario, String password) {
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
    MÉTODOS AUXILIARES
     */
    private Integer id(String nombre, String tableName, String parameter) {
        Connection connection = null;
        PreparedStatement sentencia = null;
        ResultSet result = null;
        Integer id = null;

        try {
            String sql_id = "SELECT id FROM " + tableName +
                    "WHERE " + parameter + " = ?";

            sentencia = connection.prepareStatement(sql_id);
            sentencia.setString(1, nombre);
            result = sentencia.executeQuery();

            if(result.next()) {
                id = result.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            cerrarConexion(connection, sentencia, result);
        }

        return id;
    }



    public void crearEvento(String nombre,String fecha){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexion = establecerConexion();

            // Consultar si existe el evento previamente
            String sql_comprobarEvento = "SELECT id FROM eventos " +
                    "WHERE nombre = ? AND fecha = ?";

            sentencia = conexion.prepareStatement(sql_comprobarEvento);
            sentencia.setString(1, nombre);
            sentencia.setString(2, fecha);
            resultado = sentencia.executeQuery();

            if(resultado.next()) {
                System.out.println("El evento ya existe");
            } else {
                // Añadimos el evento a la BD
                String sql_añadirEvento = "INSERT INTO eventos (nombre, fecha) " +
                        "VALUES (?, ?)";

                sentencia = conexion.prepareStatement(sql_añadirEvento);
                sentencia.setString(1, nombre);
                sentencia.setString(2, fecha);

                int filasInsertadas = sentencia.executeUpdate();
                if (filasInsertadas > 0) {
                    System.out.println("Filas insertadas: " + filasInsertadas);
                } else {
                    System.out.println("Error al insertar el evento");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }
    }

    public void añadirSocio(String nombre){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexion = establecerConexion();

            // Comprobar si existe el socio
            String sql_comprobarSocio = "SELECT id FROM socios " +
                    "WHERE nombre = ?";

            sentencia = conexion.prepareStatement(sql_comprobarSocio);
            sentencia.setString(1, nombre);
            resultado = sentencia.executeQuery();

            if(resultado.next()) {
                System.out.println("El socio ya existe");
            } else {
                LocalDate fecha = LocalDate.now();
                // Convertimos LocalDate a java.sql.Date
                Date fechaSql = Date.valueOf(fecha);

                // Insertar socio en la BD
                String sql_insertarSocio = "INSERT INTO socios (nombre, alta) " +
                        "VALUES (?, ?)";

                sentencia = conexion.prepareStatement(sql_insertarSocio);
                sentencia.setString(1, nombre);
                sentencia.setDate(2, fechaSql);

                int filasInsertadas = sentencia.executeUpdate();
                if (filasInsertadas > 0) {
                    System.out.println("Filas insertadas: " + filasInsertadas);
                } else {
                    System.out.println("Error al insertar el socio");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }
    }


    public void apuntarseEvento(String socio,String evento){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexion = establecerConexion();

            // Comprobar si existe el socio
            String sql_comprobarSocio = "SELECT id FROM socios " +
                    "WHERE nombre = ?";

            sentencia = conexion.prepareStatement(sql_comprobarSocio);
            sentencia.setString(1, socio);
            resultado = sentencia.executeQuery();

            if(!resultado.next()) {
               throw new RuntimeException("No existe el socio");
            } else {
                // Comprobar si existe el evento
                int socioId = resultado.getInt("id");

                String sql_compobarEvento = "SELECT id FROM eventos " +
                        "WHERE nombre = ?";

                sentencia = conexion.prepareStatement(sql_compobarEvento);
                sentencia.setString(1, evento);
                resultado = sentencia.executeQuery();

                if(!resultado.next()) {
                    throw new RuntimeException("No existe el evento");
                } else {
                    // Inscribir el socio al evento
                    int eventoId = resultado.getInt("id");

                    String sql_inscribir = "INSERT INTO inscripciones (socio, evento) " +
                            "VALUES (NULL, ?, ?)";

                    sentencia = conexion.prepareStatement(sql_inscribir);
                    sentencia.setInt(1, socioId);
                    sentencia.setInt(2, eventoId);

                    int filasInsertadas = sentencia.executeUpdate();

                    if (filasInsertadas > 0) {
                        System.out.println("Filas insertadas: " +filasInsertadas);
                    } else {
                        System.out.println("Error al inscribir al socio " + socio + " en el evento " + evento);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }
    }

    public String eventosSocio(String socio){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        StringBuilder res = new StringBuilder();
        try {
            conexion = establecerConexion();

            // Comprobar si existe el socio
            String sql_comprobarSocio = "SELECT id FROM socios " +
                    "WHERE nombre = ?";

            sentencia = conexion.prepareStatement(sql_comprobarSocio);
            sentencia.setString(1, socio);
            resultado = sentencia.executeQuery();

            if(!resultado.next()) {
                System.out.println("No existe el socio");
            } else {
                int socioId = resultado.getInt("id");

                // Mostrar los nombres de los eventos a los que está apuntado
                String sql_mostrarEventos = "SELECT e.nombre AS nombre_evento FROM socios s " +
                        "JOIN inscripciones i ON s.id = i.socio " +
                        "JOIN eventos e ON i.evento = e.id " +
                        "WHERE s.id = ?";

                sentencia = conexion.prepareStatement(sql_mostrarEventos);
                sentencia.setInt(1, socioId);
                resultado = sentencia.executeQuery();

                while(resultado.next()) {
                    String evento = resultado.getString("nombre_evento");

                    res.append("Eventos: ").append(evento).append("\n");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }
        return res.toString();
    }

    public String resumentEventos(){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        StringBuilder res = new StringBuilder();

        try {
            conexion = establecerConexion();

            // Mostrar la información de un evento junto con los nombres de los asistentes
            String sql_infoEventos = "SELECT e.nombre AS nombre_evento, e.fecha AS fecha_evento, s.nombre AS nombre_asistente " +
                    "FROM eventos e " +
                    "JOIN inscripciones i ON e.id = i.evento " +
                    "JOIN socios s ON i.socio = s.id";

            sentencia = conexion.prepareStatement(sql_infoEventos);
            resultado = sentencia.executeQuery();

            while(resultado.next()) {
                String nombreEvento = resultado.getString("nombre_evento");
                String fechaEvento = resultado.getString("fecha_evento");
                String nombreAsistente = resultado.getString("nombre_asistente");

                res.append("Evento: ").append(nombreEvento).append(", Fecha: ").append(fechaEvento).append(", Asistente: ").append(nombreAsistente).append("\n");
            }

        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }

        return res.toString();
    }

    public String valoracionesEvento(String evento){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        StringBuilder res = new StringBuilder();

        try {
            conexion = establecerConexion();

            // Comprobar si existe el evento
            String sql_compobarEvento = "SELECT id FROM eventos " +
                    "WHERE nombre = ?";

            sentencia = conexion.prepareStatement(sql_compobarEvento);
            sentencia.setString(1, evento);
            resultado = sentencia.executeQuery();

            // Mostramos las valoraciones de cada evento
            if(!resultado.next()) {
                System.out.println("No existe el evento");
            } else {
                int eventoId = resultado.getInt("id");

                // Mostrar las valoraciones de cada evento
                String sql_mostrarValoraciones = "SELECT e.nombre AS evento_nombre, r.puntuacion, r.comentario, r.fecha_creacion, s.nombre AS socio_nombre " +
                        "FROM resenas_eventos r " +
                        "INNER JOIN eventos e ON r.evento_id = e.id " +
                        "INNER JOIN socios s ON r.socio_id = s.id " +
                        "WHERE r.evento_id = ?";

                sentencia = conexion.prepareStatement(sql_mostrarValoraciones);
                sentencia.setInt(1, eventoId);
                resultado = sentencia.executeQuery();

                while(resultado.next()) {
                    String eventoNombre = resultado.getString("evento_nombre");
                    int puntuacion = resultado.getInt("puntuacion");
                    String comentario = resultado.getString("comentario");
                    String fechaCreacion = resultado.getString("fecha_creacion");
                    String socioNombre = resultado.getString("socio_nombre");

                    res.append("Evento: ").append(eventoNombre).append("\n");
                    res.append("Puntuación: ").append(puntuacion).append("\n");
                    res.append("Comentario: ").append(comentario).append("\n");
                    res.append("Fecha de creación: ").append(fechaCreacion).append("\n");
                    res.append("Socio: ").append(socioNombre).append("\n");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }

        return res.toString();
    }

    public String eventoMultitudinario(){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        StringBuilder res = new StringBuilder();

        try {
            conexion = establecerConexion();

            // Mostrar el evento con más asistentes
            String sql_eventoMultitudinario = "SELECT e.nombre AS evento_nombre, COUNT(i.id) AS cantidad_inscripciones " +
                    "FROM eventos e " +
                    "LEFT JOIN inscripciones i ON e.id = i.evento " +
                    "GROUP BY e.id " +
                    "ORDER BY cantidad_inscripciones DESC " +
                    "LIMIT 1";

//            String sql = "SELECT e.nombre FROM eventos e " +
//                    "JOIN inscripciones i ON e.id = i.evento " +
//                    "GROUP BY i.evento " +
//                    "ORDER BY count(*) DESC " +
//                    "LIMIT 1";

            sentencia = conexion.prepareStatement(sql_eventoMultitudinario);
            resultado = sentencia.executeQuery();

            while(resultado.next()) {
                String eventoNombre = resultado.getString("evento_nombre");
                int cantidadInscripciones = resultado.getInt("cantidad_inscripciones");

                res.append("Evento: ").append(eventoNombre).append("\n");
                res.append("Inscripciones: ").append(cantidadInscripciones).append("\n");
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }

        return res.toString();
    }

    public String sinSocios(){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        StringBuilder res = new StringBuilder();

        try {
            conexion = establecerConexion();
            // Mostrar el evento donde no se ha inscrito ningún socio
            String sql_EventoSinSocios = "SELECT e.nombre AS evento_nombre, COUNT(i.id) AS cantidad_inscripciones " +
                    "FROM eventos e " +
                    "LEFT JOIN inscripciones i ON e.id = i.evento " +
                    "GROUP BY e.id " +
                    "HAVING COUNT(i.id) = 0";

            sentencia = conexion.prepareStatement(sql_EventoSinSocios);
            resultado = sentencia.executeQuery();

            while(resultado.next()) {
                String eventoNombre = resultado.getString("evento_nombre");
                int cantidadInscripciones = resultado.getInt("cantidad_inscripciones");

                res.append("Evento: ").append(eventoNombre).append("\n");
                res.append("Inscripciones: ").append(cantidadInscripciones).append("\n");
            }
        } catch (SQLException e) {
        System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }

        return res.toString();
    }

    public String mejorValorado(){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        StringBuilder res = new StringBuilder();

        try {
            conexion = establecerConexion();

            // Mostrar los eventos mejor valorados
            String sql_eventoMejorValorado = "SELECT e.nombre AS evento_nombre, AVG(r.puntuacion) AS media_puntuacion " +
                    "FROM eventos e " +
                    "JOIN resenas_eventos r ON e.id = r.evento_id " +
                    "GROUP BY e.id " +
                    "ORDER BY media_puntuacion DESC " +
                    "LIMIT 1";

            sentencia = conexion.prepareStatement(sql_eventoMejorValorado);
            resultado = sentencia.executeQuery();

            if(resultado.next()) {
                String eventoNombre = resultado.getString("evento_nombre");
                int media_puntuacion = resultado.getInt("media_puntuacion");

                res.append("Evento: ").append(eventoNombre).append("\n");
                res.append("Puntuación media: ").append(media_puntuacion).append("\n");
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }
        return res.toString();
    }

    public void borrarEventos(Integer año){
        Connection conexion = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexion = establecerConexion();

            // Borrar los eventos anteriores a un año concreto
            String sql_borrarEventos = "DELETE FROM eventos WHERE YEAR(fecha) <= ?";

            sentencia = conexion.prepareStatement(sql_borrarEventos);
            sentencia.setInt(1, año);

            int filasEliminadas = sentencia.executeUpdate();

            if(filasEliminadas > 0) {
                System.out.println("Eventos eliminados: " + filasEliminadas);
            } else {
                System.out.println("Error al eliminar los eventos anteriores a " + año);
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL\n" + e.getMessage());
        } finally {
            cerrarConexion(conexion, sentencia, resultado);
        }
    }
}
