package ejercicio1;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utilidades.BasesDatos;

import static org.junit.jupiter.api.Assertions.*;

class HoldingDAOTest {

    HoldingDAO dao;

    @BeforeEach
    void setUp() {
        String ruta_scrip = "src/main/java/ejercicio1/script_datos.sql";
        BasesDatos.borrarDatos("holding");
        BasesDatos.volcarDatos(ruta_scrip,"holding");

        dao = new HoldingDAO("localhost", "holding", "root", "");
    }

    @Test
    void agregarEmpleado() {
        dao.agregarEmpleado("Juan", "Pérez", "1990-03-15", "Analista", "dsgsdgfhdfh@example.com", "2021-01-15", 3500.00, "12345678A");
    }

    @Test
    void subirSueldo() {
        dao.subirSueldo("12345678A", 1000.00);
    }

    @Test
    void trasladarEmpleado() {
        dao.trasladarEmpleado("analista1@example.com", "98765432B");
    }

    @Test
    void empleadosEmpresa() {
        String valor_esperado ="Juan Pérez: analista1@example.com\n" +
                "Carlos Martínez: gerente1@example.com\n";

        String resultado = dao.empleadosEmpresa("12345678A");

        assertEquals(valor_esperado, resultado);

    }

    @Test
    void crearCoche() {
        dao.crearCoche("Supra", "Toyota", 2000.00, 1996, "analista1@example.com");
    }

    @Test
    void costeProyecto() {
        Double valor_esperado = 4500.00;

        Double resultado = dao.costeProyecto("FusionWorks");

        assertEquals(valor_esperado, resultado);
    }

    @Test
    void resumenProyectos() {
        String valor_esperado = "Proyecto: CodeFusion, Nombre del Empleado: Juan, Salario: 6300.0\n" +
                "Proyecto: CodeFusion, Nombre del Empleado: María, Salario: 6300.0\n" +
                "Proyecto: FusionWorks, Nombre del Empleado: Carlos, Salario: 4500.0\n" +
                "Proyecto: CyberPulse, Nombre del Empleado: Laura, Salario: 3800.0\n" +
                "Proyecto: QuantumQuest, Nombre del Empleado: null, Salario: 0.0\n";

        String resultado = dao.resumenProyectos();

        assertEquals(valor_esperado, resultado);
    }

    @Test
    void empleadosSinCoche() {
        Integer valor_esperado = 0;

        Integer resultado = dao.empleadosSinCoche();

        assertEquals(valor_esperado, resultado);
    }

    @Test
    void borrarProyectosSinEmp() {
        dao.borrarProyectosSinEmp();
    }

    @Test
    void borrarAño() {
        dao.borrarAño(2021);
    }
}