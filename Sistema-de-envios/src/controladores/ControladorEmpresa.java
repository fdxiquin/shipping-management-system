package controladores;

import modelo.Despacho;
import modelo.Empresa;
import modelo.Envio;
import modelo.enums.EstadoDespacho;
import modelo.enums.EstadoEnvio;
import vista.Vista;

// Controlador principal que coordina el menú, los demás controladores y los reportes.
public class ControladorEmpresa {

    private Empresa empresa;
    private Vista vista;
    private ControladorEnvio controladorEnvio;
    private ControladorVehiculo controladorVehiculo;
    private ControladorDespacho controladorDespacho;

    // Crea la empresa, la vista y los demás controladores.
    public ControladorEmpresa() {
        empresa = new Empresa();
        vista = new Vista();
        controladorEnvio = new ControladorEnvio(empresa, vista);
        controladorVehiculo = new ControladorVehiculo(empresa, vista);
        controladorDespacho = new ControladorDespacho(empresa, vista);
    }

    // Muestra el menú principal hasta que el encargado decida salir.
    public void iniciar() {
        int opcion = 0;
        while (opcion != 9) {
            vista.mostrarMenuPrincipal();
            opcion = vista.pedirEntero("Seleccione una opción: ");
            procesarOpcion(opcion);
        }
    }

    // Dirige cada opción del menú al controlador que corresponde.
    private void procesarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1:
                    controladorEnvio.cotizarYRegistrar();
                    break;
                case 2:
                    controladorVehiculo.registrarVehiculo();
                    break;
                case 3:
                    controladorDespacho.crearDespacho();
                    break;
                case 4:
                    controladorDespacho.hacerSalirDespachos();
                    break;
                case 5:
                    controladorDespacho.registrarResultados();
                    break;
                case 6:
                    controladorEnvio.consultarEnvio();
                    break;
                case 7:
                    controladorVehiculo.consultarVehiculo();
                    break;
                case 8:
                    gestionarReportes();
                    break;
                case 9:
                    vista.mostrarMensaje("Hasta pronto.");
                    break;
                default:
                    vista.mostrarError("Opción inválida.");
                    break;
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    // Muestra el submenú de reportes y ejecuta el elegido.
    private void gestionarReportes() {
        int opcion = -1;
        while (opcion != 0) {
            vista.mostrarTitulo("Reportes");
            vista.mostrarSubmenuReportes();
            opcion = vista.pedirEntero("Seleccione un reporte: ");
            switch (opcion) {
                case 1:
                    reporteEnviosPorCategoriaYEstado();
                    break;
                case 2:
                    reporteIngresoTotal();
                    break;
                case 3:
                    reporteIngresoPorCategoria();
                    break;
                case 4:
                    reporteDespachosActivos();
                    break;
                case 0:
                    break;
                default:
                    vista.mostrarError("Opción inválida.");
                    break;
            }
        }
    }

    // Muestra cuántos envíos hay de cada categoría y estado.
    private void reporteEnviosPorCategoriaYEstado() {
        vista.mostrarTitulo("Envíos por categoría y estado");
        String[] categorias = {
            Envio.CATEGORIA_SOBRE,
            Envio.CATEGORIA_PAQUETE,
            Envio.CATEGORIA_PAQUETE_FRAGIL,
            Envio.CATEGORIA_CARGA_REFRIGERADA
        };
        for (String categoria : categorias) {
            vista.mostrarMensaje(categoria + ":");
            for (EstadoEnvio estado : EstadoEnvio.values()) {
                vista.mostrarMensaje("  " + estado + ": " + contarEnvios(categoria, estado));
            }
        }
    }

    // Muestra el ingreso total separado por registro y reintentos.
    private void reporteIngresoTotal() {
        vista.mostrarTitulo("Ingreso total");
        vista.mostrarMonto("Ingresos por registro", empresa.getIngresosRegistro());
        vista.mostrarMonto("Ingresos por reintentos", empresa.getIngresosReintentos());
        vista.mostrarMonto("Ingreso total", empresa.calcularIngresoTotal());
    }

    // Muestra el ingreso acumulado de cada categoría.
    private void reporteIngresoPorCategoria() {
        vista.mostrarTitulo("Ingreso por categoría");
        vista.mostrarMonto(Envio.CATEGORIA_SOBRE, empresa.getIngresoCategoria(Envio.CATEGORIA_SOBRE));
        vista.mostrarMonto(Envio.CATEGORIA_PAQUETE, empresa.getIngresoCategoria(Envio.CATEGORIA_PAQUETE));
        vista.mostrarMonto(Envio.CATEGORIA_PAQUETE_FRAGIL, empresa.getIngresoCategoria(Envio.CATEGORIA_PAQUETE_FRAGIL));
        vista.mostrarMonto(Envio.CATEGORIA_CARGA_REFRIGERADA, empresa.getIngresoCategoria(Envio.CATEGORIA_CARGA_REFRIGERADA));
    }

    // Muestra los despachos no cerrados ni cancelados.
    private void reporteDespachosActivos() {
        vista.mostrarTitulo("Despachos no cerrados ni cancelados");
        boolean hayActivos = false;
        for (Despacho despacho : empresa.getDespachos()) {
            if (despacho.getEstado() != EstadoDespacho.CERRADO && despacho.getEstado() != EstadoDespacho.CANCELADO) {
                vista.mostrarMensaje(despacho.getDescripcion());
                hayActivos = true;
            }
        }
        if (!hayActivos) {
            vista.mostrarMensaje("No hay despachos abiertos.");
        }
    }

    // Recorre los envíos con for each y cuenta los que coinciden.
    private int contarEnvios(String categoria, EstadoEnvio estado) {
        int total = 0;
        for (Envio envio : empresa.getEnvios()) {
            if (envio.getCategoria().equals(categoria) && envio.getEstado() == estado) {
                total++;
            }
        }
        return total;
    }
}
