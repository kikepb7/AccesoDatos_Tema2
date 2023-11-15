package ejercicio3;

import ejercicio2.ClubDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utilidades.BasesDatos;

import static org.junit.jupiter.api.Assertions.*;

class TiendaDAOTest {

    TiendaDAO dao;

    @BeforeEach
    void setUp() {

        String ruta_scrip = "src/main/java/ejercicio3/script_datos.sql";
        BasesDatos.borrarDatos("tienda");
        BasesDatos.volcarDatos(ruta_scrip,"tienda");

        dao = new TiendaDAO("localhost", "tienda", "root", "");
    }

    @Test
    void añadirVenta() {
        dao.añadirVenta("Paco Menendez", "Barra de pan", 2);
    }

    @Test
    void comprasCliente() {
    }

    @Test
    void recaudacionTotal() {
    }

    @Test
    void porCategorias() {

    }

    @Test
    void ultimaVenta() {

    }

    @Test
    void masVendido() {

    }

    @Test
    void sinVentas() {
    }

    @Test
    void borrarProveedor() {
    }
}