package ejercicio2;

import ejercicio1.HoldingDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utilidades.BasesDatos;

import static org.junit.jupiter.api.Assertions.*;

class ClubDAOTest {


    ClubDAO dao;
    @BeforeEach
    void setUp() {
        String ruta_scrip = "src/main/java/ejercicio2/script_datos.sql";
        BasesDatos.borrarDatos("club");
        BasesDatos.volcarDatos(ruta_scrip,"club");

        dao = new ClubDAO("localhost", "club", "root", "");

    }

    @Test
    void crearEvento() {
        dao.crearEvento("Fiesta de la espuma", "2021-12-20");
    }

    @Test
    void añadirSocio() {
        dao.añadirSocio("Juan");
    }

    @Test
    void apuntarseEvento() {
        dao.apuntarseEvento("Jose", "Fiesta de la espuma");
    }

    @Test
    void eventosSocio() {
        String valor_esperado = "Eventos: Fiesta de la espuma\n" +
                "Eventos: Cata de vinos\n";

        String resultado = dao.eventosSocio("Juan");

        assertEquals(valor_esperado, resultado);
    }

    @Test
    void sociosEvento() {

    }

    @Test
    void resumentEventos() {
        String valor_esperado = "Evento: Fiesta de la espuma, Fecha: 2021-12-20, Asistente: Juan\n" +
                "Evento: Cata de vinos, Fecha: 2020-11-30, Asistente: Juan\n" +
                "Evento: Fiesta de la espuma, Fecha: 2021-12-20, Asistente: Antonio\n" +
                "Evento: Partido de futbol sala, Fecha: 2023-06-30, Asistente: Antonio\n" +
                "Evento: Cata de vinos, Fecha: 2020-11-30, Asistente: Jose\n";

        String resultado = dao.resumentEventos();

        assertEquals(valor_esperado, resultado);
    }

    @Test
    void valoracionesEvento() {
        String valor_esperado = "Evento: Fiesta de la espuma\n" +
                "Puntuación: 4\n" +
                "Comentario: Gran evento, mucha diversión\n" +
                "Fecha de creación: 2020-12-21\n" +
                "Socio: Juan\n" +
                "Evento: Fiesta de la espuma\n" +
                "Puntuación: 3\n" +
                "Comentario: Divertido pero podría haber más actividades\n" +
                "Fecha de creación: 2020-12-22\n" +
                "Socio: Antonio\n";

        String resultado = dao.valoracionesEvento("Fiesta de la espuma");

        assertEquals(valor_esperado, resultado);
    }

    @Test
    void eventoMultitudinario() {
        String valor_esperado = "Evento: Cata de vinos\n" +
                "Inscripciones: 2\n";

        String resultado = dao.eventoMultitudinario();

        assertEquals(valor_esperado, resultado);

        assertEquals(valor_esperado, dao.eventoMultitudinario());

    }

    @Test
    void sinSocios() {
        String valor_esperado = "Evento: Maraton de cine\n" +
                "Inscripciones: 0\n";

        String resultado = dao.sinSocios();

        assertEquals(valor_esperado, resultado);
    }

    @Test
    void mejorValorado() {
        String valor_esperado = "Evento: Partido de futbol sala\n" +
                "Puntuación media: 5\n";

        String resultado = dao.mejorValorado();

        assertEquals(valor_esperado, resultado);
    }

    @Test
    void borrarEventos() {
        dao.borrarEventos(2022);
    }
}