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
        String valor_esperado = "Cliente: Paco Menendez, Producto: Plato de macarrones, Cantidad: 5\n" +
                "Cliente: Paco Menendez, Producto: Botella de vino, Cantidad: 4\n";

        assertEquals(valor_esperado, dao.comprasCliente("Paco Menendez"));
    }

    @Test
    void recaudacionTotal() {
        Double valor_esperado = 224.0;

        assertEquals(valor_esperado, dao.recaudacionTotal());
    }

    @Test
    void porCategorias() {
        String valor_esperado = "Categoría: Bebida, Ventas: 24 unidades.\n" +
                "Categoría: Comida, Ventas: 5 unidades.\n" +
                "Categoría: Otros, Ventas: 0 unidades.\n";

        assertEquals(valor_esperado, dao.porCategorias());
    }

    @Test
    void ultimaVenta() {
        String valor_esperado = "Cliente: Paco Menendez, Producto: Botella de vino, Unidades: 4 unidades.";

        assertEquals(valor_esperado, dao.ultimaVenta());
    }

    @Test
    void masVendido() {
        String valor_esperado = "Número de ventas: 17, Producto: Botella de agua";

        assertEquals(valor_esperado, dao.masVendido());
    }

    @Test
    void sinVentas() {
    }

    @Test
    void borrarProveedor() {
    }
}