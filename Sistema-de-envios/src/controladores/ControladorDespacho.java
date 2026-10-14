package controladores;

import java.util.ArrayList;
import modelo.Despacho;
import modelo.Empresa;
import modelo.Envio;
import modelo.Vehiculo;
import modelo.enums.EstadoDespacho;
import modelo.enums.EstadoEnvio;
import vista.Vista;

// Controla la creación, la salida y los resultados de los despachos.
public class ControladorDespacho {

    private Empresa empresa;
    private Vista vista;

    // Recibe la empresa y la vista para trabajar con ellas.
    public ControladorDespacho(Empresa empresa, Vista vista) {
        this.empresa = empresa;
        this.vista = vista;
    }

    // Pide la placa de un vehículo disponible y crea el despacho. Luego abre su submenú.
    public void crearDespacho() {
        vista.mostrarTitulo("Crear despachos");
        String placa = vista.pedirTexto("Placa del vehículo disponible: ");
        Vehiculo vehiculo = empresa.buscarVehiculo(placa);
        if (vehiculo == null) {
            vista.mostrarError("La placa " + placa + " no existe.");
            return;
        }
        try {
            Despacho despacho = empresa.crearDespacho(vehiculo);
            vista.mostrarExito("Despacho #" + despacho.getId() + " creado en preparación.");
            gestionarDespacho(despacho);
        } catch (IllegalArgumentException | IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    // Repite el submenú hasta que el despacho se guarde, salga o se cancele.
    private void gestionarDespacho(Despacho despacho) {
        while (despacho.getEstado() == EstadoDespacho.EN_PREPARACION) {
            vista.mostrarSubmenuDespacho();
            int opcion = vista.pedirEntero("Seleccione una opción: ");
            switch (opcion) {
                case 1:
                    asignarEnvio(despacho);
                    break;
                case 2:
                    retirarEnvio(despacho);
                    break;
                case 3:
                    mostrarContenido(despacho);
                    break;
                case 4:
                    guardarDespacho(despacho);
                    break;
                case 5:
                    enviarARuta(despacho);
                    break;
                case 6:
                    cancelarDespacho(despacho);
                    break;
                default:
                    vista.mostrarError("Opción inválida.");
                    break;
            }
        }
    }

    // Muestra los envíos en bodega y asigna el que elija el encargado.
    private void asignarEnvio(Despacho despacho) {
        if (despacho.calcularCapacidadDisponible() <= 0) {
            vista.mostrarError("El despacho ya está lleno. Guárdelo o envíelo a ruta.");
            return;
        }
        ArrayList<Envio> enBodega = empresa.obtenerEnviosPorEstado(EstadoEnvio.EN_BODEGA);
        if (enBodega.isEmpty()) {
            vista.mostrarMensaje("No hay envíos en bodega.");
            return;
        }
        mostrarEnvios(enBodega);
        String codigo = vista.pedirTexto("Código del envío a asignar: ");
        Envio envio = empresa.buscarEnvio(codigo);
        if (envio == null) {
            vista.mostrarError("El envío " + codigo + " no existe.");
            return;
        }
        try {
            despacho.asignarEnvio(envio);
            vista.mostrarExito("Envío " + codigo + " asignado al despacho.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    // Muestra los envíos del despacho y retira el elegido.
    private void retirarEnvio(Despacho despacho) {
        if (despacho.getEnvios().isEmpty()) {
            vista.mostrarMensaje("El despacho no tiene envíos.");
            return;
        }
        mostrarEnvios(despacho.getEnvios());
        String codigo = vista.pedirTexto("Código del envío a retirar: ");
        try {
            despacho.retirarEnvio(codigo);
            vista.mostrarExito("Envío " + codigo + " regresó a la bodega.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    // Muestra los envíos con el peso cargado y la capacidad disponible.
    private void mostrarContenido(Despacho despacho) {
        vista.mostrarMensaje(despacho.getDescripcion());
        if (despacho.getEnvios().isEmpty()) {
            vista.mostrarMensaje("El despacho no tiene envíos.");
            return;
        }
        mostrarEnvios(despacho.getEnvios());
    }

    // Guarda el despacho y muestra el error si no se puede.
    private void guardarDespacho(Despacho despacho) {
        try {
            despacho.guardar();
            vista.mostrarExito("Despacho #" + despacho.getId() + " guardado.");
        } catch (IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    // Cancela el despacho tanto desde la creación como desde la salida.
    private void cancelarDespacho(Despacho despacho) {
        try {
            despacho.cancelar();
            vista.mostrarExito("Despacho #" + despacho.getId() + " cancelado. El vehículo quedó disponible.");
        } catch (IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    // Hace salir el despacho y cobra el cargo a cada envío en reintento.
    private void enviarARuta(Despacho despacho) {
        try {
            despacho.salir();
            for (Envio envio : despacho.getEnvios()) {
                if (envio.esReintento()) {
                    empresa.registrarReintento(envio);
                    vista.mostrarMonto("Cargo por reintento del envío " + envio.getCodigo(), Envio.CARGO_REINTENTO);
                }
            }
            vista.mostrarExito("Despacho #" + despacho.getId() + " en ruta.");
        } catch (IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    // Muestra los despachos guardados y permite hacerlo salir o cancelarlo.
    public void hacerSalirDespachos() {
        vista.mostrarTitulo("Hacer salir despachos");
        ArrayList<Despacho> guardados = empresa.obtenerDespachosPorEstado(EstadoDespacho.GUARDADO);
        if (guardados.isEmpty()) {
            vista.mostrarMensaje("No hay despachos guardados.");
            return;
        }
        mostrarDespachos(guardados);
        int id = vista.pedirEntero("Identificador del despacho: ");
        Despacho despacho = empresa.buscarDespacho(id);
        if (despacho == null) {
            vista.mostrarError("El despacho " + id + " no existe.");
            return;
        }
        if (despacho.getEstado() != EstadoDespacho.GUARDADO) {
            vista.mostrarError("El despacho " + id + " no está guardado.");
            return;
        }
        vista.mostrarSubmenuSalidaDespacho();
        int opcion = vista.pedirEntero("Seleccione una opción: ");
        switch (opcion) {
            case 1:
                enviarARuta(despacho);
                break;
            case 2:
                cancelarDespacho(despacho);
                break;
            case 0:
                break;
            default:
                vista.mostrarError("Opción inválida.");
                break;
        }
    }

    // Muestra los despachos en ruta, pide el resultado de cada envío y aplica todo al final.
    public void registrarResultados() {
        vista.mostrarTitulo("Registrar resultados de entrega");
        ArrayList<Despacho> enRuta = empresa.obtenerDespachosPorEstado(EstadoDespacho.EN_RUTA);
        if (enRuta.isEmpty()) {
            vista.mostrarMensaje("No hay despachos en ruta.");
            return;
        }
        mostrarDespachos(enRuta);
        int id = vista.pedirEntero("Identificador del despacho: ");
        Despacho despacho = empresa.buscarDespacho(id);
        if (despacho == null) {
            vista.mostrarError("El despacho " + id + " no existe.");
            return;
        }
        if (despacho.getEstado() != EstadoDespacho.EN_RUTA) {
            vista.mostrarError("El despacho " + id + " no está en ruta.");
            return;
        }
        boolean[] entregados = new boolean[despacho.getEnvios().size()];
        int posicion = 0;
        for (Envio envio : despacho.getEnvios()) {
            int resultado = pedirResultado(envio);
            if (resultado < 0) {
                vista.mostrarMensaje("Registro cancelado. El despacho sigue en ruta.");
                return;
            }
            entregados[posicion] = resultado == 1;
            posicion++;
        }
        try {
            despacho.aplicarResultados(entregados);
            vista.mostrarExito("Resultados aplicados. Despacho #" + id + " cerrado.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    // Pide 1, 0 o un número negativo y repite si el valor es otro.
    private int pedirResultado(Envio envio) {
        vista.mostrarMensaje("\n" + envio.getDescripcion());
        while (true) {
            int resultado = vista.pedirEntero("Resultado (1 = entregado, 0 = no entregado, negativo = cancelar): ");
            if (resultado == 1 || resultado == 0 || resultado < 0) {
                return resultado;
            }
            vista.mostrarError("Solo se acepta 1, 0 o un número negativo.");
        }
    }

    // Muestra la descripción de cada envío.
    private void mostrarEnvios(ArrayList<Envio> envios) {
        for (Envio envio : envios) {
            vista.mostrarMensaje(envio.getDescripcion());
        }
    }

    // Muestra la descripción de cada despacho.
    private void mostrarDespachos(ArrayList<Despacho> despachos) {
        for (Despacho despacho : despachos) {
            vista.mostrarMensaje(despacho.getDescripcion());
        }
    }
}
