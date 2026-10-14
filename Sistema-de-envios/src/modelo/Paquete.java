package modelo;

import modelo.enums.TipoDestino;

// Envío con dimensiones y costo según su peso facturable.
public class Paquete extends Envio {

    private double largo;
    private double ancho;
    private double alto;
    private static final double TARIFA_POR_KILO = 8.0;
    private static final double DIVISOR_VOLUMETRICO = 5000.0;

    // Construye el paquete y rechaza dimensiones no positivas.
    public Paquete(String codigo, String remitente, String destinatario, double pesoReal, TipoDestino tipoDestino, double largo, double ancho, double alto) {
        super(codigo, remitente, destinatario, pesoReal, tipoDestino);
        if (largo <= 0 || ancho <= 0 || alto <= 0) {
            throw new IllegalArgumentException("El largo, el ancho y el alto deben ser mayores que cero.");
        }
        this.largo = largo;
        this.ancho = ancho;
        this.alto = alto;
    }

    // Calcula largo por ancho por alto dividido entre 5000.
    public double calcularPesoVolumetrico() {
        return (largo * ancho * alto) / DIVISOR_VOLUMETRICO;
    }

    // Toma el mayor entre el peso real y el volumétrico, redondeado hacia arriba.
    public int calcularPesoFacturable() {
        double mayor = Math.max(pesoReal, calcularPesoVolumetrico());
        return (int) Math.ceil(mayor);
    }

    @Override
    // Sobrescribe para sumar la tarifa base y el cobro por kilogramo facturable.
    public double calcularCosto() {
        return getTarifaBase() + TARIFA_POR_KILO * calcularPesoFacturable();
    }

    @Override
    // Sobrescribe para estimar 2 días urbano y 4 departamental.
    public int getTiempoEstimado() {
        if (tipoDestino == TipoDestino.URBANO) {
            return 2;
        }
        return 4;
    }

    @Override
    // Sobrescribe para permitir 3 intentos.
    public int getIntentosPermitidos() {
        return 3;
    }

    @Override
    // Sobrescribe para mostrar los datos comunes y las dimensiones.
    public String getDescripcion() {
        return "[" + getCategoria() + "] " + getDatosComunes()
                + " | Dimensiones: " + String.format("%.2f x %.2f x %.2f", largo, ancho, alto) + " cm";
    }

    @Override
    // Sobrescribe para indicar que no exige refrigeración.
    public boolean requiereRefrigeracion() {
        return false;
    }

    @Override
    // Sobrescribe para retornar la categoría paquete.
    public String getCategoria() {
        return CATEGORIA_PAQUETE;
    }
}
