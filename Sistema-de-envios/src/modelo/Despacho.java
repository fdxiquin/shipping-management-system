package modelo;

import java.util.ArrayList;
import modelo.enums.EstadoDespacho;
import modelo.enums.EstadoEnvio;
import modelo.enums.EstadoVehiculo;

// Agrupa los envíos de un vehículo y controla su salida y su cierre.
public class Despacho {

    private int id;
    private Vehiculo vehiculo;
    private ArrayList<Envio> envios;
    private EstadoDespacho estado;

    // Construye el despacho en preparación y valida que el vehículo esté disponible. Luego lo marca como asignado.
    public Despacho(int id, Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo del despacho no puede ser nulo.");
        }
        if (vehiculo.getEstado() != EstadoVehiculo.DISPONIBLE) {
            throw new IllegalStateException("El vehículo " + vehiculo.getPlaca() + " no está disponible.");
        }
        this.id = id;
        this.vehiculo = vehiculo;
        this.envios = new ArrayList<>();
        this.estado = EstadoDespacho.EN_PREPARACION;
        vehiculo.setEstado(EstadoVehiculo.ASIGNADO);
    }

    // Retorna el identificador para buscar el despacho.
    public int getId() {
        return id;
    }

    // Retorna el vehículo del despacho.
    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    // Retorna los envíos para recorrerlos en orden.
    public ArrayList<Envio> getEnvios() {
        return envios;
    }

    // Retorna el estado para filtrar y reportar.
    public EstadoDespacho getEstado() {
        return estado;
    }

    // Suma los pesos reales de los envíos sin guardarlo aparte.
    public double calcularPesoCargado() {
        double total = 0;
        for (Envio envio : envios) {
            total += envio.getPesoReal();
        }
        return total;
    }

    // Resta el peso cargado a la capacidad del vehículo.
    public double calcularCapacidadDisponible() {
        return vehiculo.getCapacidadMaxima() - calcularPesoCargado();
    }

    // Busca un envío dentro del despacho y retorna null si no está.
    public Envio buscarEnvio(String codigo) {
        for (Envio envio : envios) {
            if (envio.getCodigo().equals(codigo)) {
                return envio;
            }
        }
        return null;
    }

    // Valida estado, compatibilidad y capacidad. Luego agrega el envío y lo pasa a en despacho.
    public void asignarEnvio(Envio envio) {
        if (estado != EstadoDespacho.EN_PREPARACION) {
            throw new IllegalStateException("Solo se pueden asignar envíos a un despacho en preparación.");
        }
        if (envio == null) {
            throw new IllegalArgumentException("El envío indicado no existe.");
        }
        if (envio.getEstado() != EstadoEnvio.EN_BODEGA) {
            throw new IllegalStateException("El envío " + envio.getCodigo() + " no está en bodega.");
        }
        if (!envio.esCompatibleCon(vehiculo)) {
            throw new IllegalStateException("El envío " + envio.getCodigo() + " requiere un vehículo refrigerado.");
        }
        if (calcularPesoCargado() + envio.getPesoReal() > vehiculo.getCapacidadMaxima()) {
            throw new IllegalStateException("El envío " + envio.getCodigo() + " supera la capacidad disponible del vehículo.");
        }
        envios.add(envio);
        envio.setEstado(EstadoEnvio.EN_DESPACHO);
    }

    // Valida que esté en preparación y que el envío exista. Luego lo regresa a bodega.
    public void retirarEnvio(String codigo) {
        if (estado != EstadoDespacho.EN_PREPARACION) {
            throw new IllegalStateException("Solo se pueden retirar envíos de un despacho en preparación.");
        }
        Envio envio = buscarEnvio(codigo);
        if (envio == null) {
            throw new IllegalArgumentException("El envío " + codigo + " no está en este despacho.");
        }
        envios.remove(envio);
        envio.setEstado(EstadoEnvio.EN_BODEGA);
    }

    // Valida que no esté vacío y lo pasa a guardado.
    public void guardar() {
        if (estado != EstadoDespacho.EN_PREPARACION) {
            throw new IllegalStateException("Solo se puede guardar un despacho en preparación.");
        }
        if (envios.isEmpty()) {
            throw new IllegalStateException("Un despacho vacío no puede guardarse.");
        }
        estado = EstadoDespacho.GUARDADO;
    }

    // Regresa los envíos a bodega, vacía la lista y libera el vehículo.
    public void cancelar() {
        if (estado != EstadoDespacho.EN_PREPARACION && estado != EstadoDespacho.GUARDADO) {
            throw new IllegalStateException("Solo se puede cancelar un despacho en preparación o guardado.");
        }
        for (Envio envio : envios) {
            envio.setEstado(EstadoEnvio.EN_BODEGA);
        }
        envios.clear();
        estado = EstadoDespacho.CANCELADO;
        vehiculo.setEstado(EstadoVehiculo.DISPONIBLE);
    }

    // Valida el estado y pasa despacho, vehículo y envíos a en ruta. Además suma un intento a cada envío.
    public void salir() {
        if (estado != EstadoDespacho.EN_PREPARACION && estado != EstadoDespacho.GUARDADO) {
            throw new IllegalStateException("Solo puede salir un despacho guardado o en preparación.");
        }
        if (envios.isEmpty()) {
            throw new IllegalStateException("Un despacho vacío no puede salir.");
        }
        estado = EstadoDespacho.EN_RUTA;
        vehiculo.setEstado(EstadoVehiculo.EN_RUTA);
        for (Envio envio : envios) {
            envio.setEstado(EstadoEnvio.EN_RUTA);
            envio.sumarIntento();
        }
    }

    // Aplica de una vez los estados de los envíos. Luego cierra el despacho y libera el vehículo.
    public void aplicarResultados(boolean[] entregados) {
        if (estado != EstadoDespacho.EN_RUTA) {
            throw new IllegalStateException("Solo se registran resultados de un despacho en ruta.");
        }
        if (entregados == null || entregados.length != envios.size()) {
            throw new IllegalArgumentException("Debe haber un resultado por cada envío del despacho.");
        }
        for (int i = 0; i < envios.size(); i++) {
            Envio envio = envios.get(i);
            if (entregados[i]) {
                envio.setEstado(EstadoEnvio.ENTREGADO);
            } else if (envio.tieneIntentosDisponibles()) {
                envio.setEstado(EstadoEnvio.EN_BODEGA);
            } else {
                envio.setEstado(EstadoEnvio.DEVUELTO_AL_REMITENTE);
            }
        }
        estado = EstadoDespacho.CERRADO;
        vehiculo.setEstado(EstadoVehiculo.DISPONIBLE);
    }

    // Retorna el identificador, la placa, el estado, el peso cargado y la capacidad disponible.
    public String getDescripcion() {
        return "Despacho #" + id
                + " | Placa: " + vehiculo.getPlaca()
                + " | Estado: " + estado
                + " | Peso cargado: " + String.format("%.2f", calcularPesoCargado()) + " kg"
                + " | Capacidad disponible: " + String.format("%.2f", calcularCapacidadDisponible()) + " kg";
    }
}
