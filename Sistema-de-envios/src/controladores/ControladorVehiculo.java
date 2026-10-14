package controladores;

import modelo.Despacho;
import modelo.Empresa;
import modelo.Vehiculo;
import modelo.enums.EstadoVehiculo;
import vista.Vista;

// Controla el registro y la consulta de vehículos.
public class ControladorVehiculo {

    private Empresa empresa;
    private Vista vista;

    // Recibe la empresa y la vista para trabajar con ellas.
    public ControladorVehiculo(Empresa empresa, Vista vista) {
        this.empresa = empresa;
        this.vista = vista;
    }

    // Pide placa, capacidad y refrigeración. Luego registra el vehículo si los datos son válidos.
    public void registrarVehiculo() {
        vista.mostrarTitulo("Registrar vehículos");
        try {
            String placa = vista.pedirTexto("Placa: ");
            if (empresa.buscarVehiculo(placa) != null) {
                throw new IllegalArgumentException("Ya existe un vehículo con la placa " + placa + ".");
            }
            double capacidad = vista.pedirDecimal("Capacidad máxima en kg: ");
            boolean refrigerado = vista.pedirConfirmacion("¿Cuenta con refrigeración?");
            Vehiculo vehiculo = new Vehiculo(placa, capacidad, refrigerado);
            empresa.registrarVehiculo(vehiculo);
            vista.mostrarExito("Vehículo registrado y disponible.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    // Pide la placa y muestra los datos del vehículo. Si va en ruta, muestra además su despacho.
    public void consultarVehiculo() {
        vista.mostrarTitulo("Consultar vehículos");
        String placa = vista.pedirTexto("Placa: ");
        Vehiculo vehiculo = empresa.buscarVehiculo(placa);
        if (vehiculo == null) {
            vista.mostrarError("La placa " + placa + " no existe.");
            return;
        }
        vista.mostrarMensaje(vehiculo.getDescripcion());
        if (vehiculo.getEstado() == EstadoVehiculo.EN_RUTA) {
            Despacho despacho = empresa.buscarDespachoEnRutaDeVehiculo(placa);
            if (despacho != null) {
                vista.mostrarMensaje("Despacho que lleva: " + despacho.getDescripcion());
            }
        }
    }
}
