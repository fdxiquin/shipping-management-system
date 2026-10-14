package modelo;

import modelo.enums.EstadoVehiculo;

// Representa un vehículo con su capacidad y su estado.
public class Vehiculo {

    private String placa;
    private double capacidadMaxima;
    private boolean refrigerado;
    private EstadoVehiculo estado;

    // Construye el vehículo y valida placa y capacidad. Inicia disponible.
    public Vehiculo(String placa, double capacidadMaxima, boolean refrigerado) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("La placa no puede estar vacía.");
        }
        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad máxima debe ser mayor que cero.");
        }
        this.placa = placa.trim();
        this.capacidadMaxima = capacidadMaxima;
        this.refrigerado = refrigerado;
        this.estado = EstadoVehiculo.DISPONIBLE;
    }

    // Retorna la placa para buscar el vehículo.
    public String getPlaca() {
        return placa;
    }

    // Retorna la capacidad para calcular el espacio disponible.
    public double getCapacidadMaxima() {
        return capacidadMaxima;
    }

    // Retorna si tiene refrigeración para validar la compatibilidad.
    public boolean isRefrigerado() {
        return refrigerado;
    }

    // Retorna el estado para validar si está disponible.
    public EstadoVehiculo getEstado() {
        return estado;
    }

    // Cambia el estado según el despacho.
    public void setEstado(EstadoVehiculo estado) {
        this.estado = estado;
    }

    // Retorna los datos y el estado del vehículo para mostrarlos.
    public String getDescripcion() {
        return "Placa: " + placa
                + " | Capacidad máxima: " + String.format("%.2f", capacidadMaxima) + " kg"
                + " | Refrigerado: " + (refrigerado ? "Sí" : "No")
                + " | Estado: " + estado;
    }
}
