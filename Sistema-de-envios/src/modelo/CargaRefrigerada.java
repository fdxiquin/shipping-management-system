package modelo;

import modelo.enums.TipoDestino;

// Envío urbano que exige cadena de frío y vehículo refrigerado.
public class CargaRefrigerada extends Envio {

    private double temperaturaRequerida;
    private static final double TEMPERATURA_MINIMA = -25.0;
    private static final double TEMPERATURA_MAXIMA = 8.0;
    private static final double TARIFA_POR_KILO = 12.0;
    private static final double CARGO_CADENA_FRIO = 60.0;

    // Construye la carga y rechaza temperaturas fuera de rango o destino departamental.
    public CargaRefrigerada(String codigo, String remitente, String destinatario, double pesoReal, TipoDestino tipoDestino, double temperaturaRequerida) {
        super(codigo, remitente, destinatario, pesoReal, tipoDestino);
        if (tipoDestino != TipoDestino.URBANO) {
            throw new IllegalArgumentException("Una carga refrigerada solo puede tener destino urbano.");
        }
        if (temperaturaRequerida < TEMPERATURA_MINIMA || temperaturaRequerida > TEMPERATURA_MAXIMA) {
            throw new IllegalArgumentException("La temperatura debe estar entre -25 °C y 8 °C.");
        }
        this.temperaturaRequerida = temperaturaRequerida;
    }

    @Override
    // Sobrescribe para sumar la tarifa base, el cobro por kilogramo real y la cadena de frío.
    public double calcularCosto() {
        return getTarifaBase() + TARIFA_POR_KILO * pesoReal + CARGO_CADENA_FRIO;
    }

    @Override
    // Sobrescribe para estimar siempre 1 día.
    public int getTiempoEstimado() {
        return 1;
    }

    @Override
    // Sobrescribe para permitir solo 1 intento.
    public int getIntentosPermitidos() {
        return 1;
    }

    @Override
    // Sobrescribe para mostrar los datos comunes y la temperatura.
    public String getDescripcion() {
        return "[" + getCategoria() + "] " + getDatosComunes()
                + " | Temperatura requerida: " + String.format("%.2f", temperaturaRequerida) + " °C";
    }

    @Override
    // Sobrescribe para exigir un vehículo refrigerado.
    public boolean requiereRefrigeracion() {
        return true;
    }

    @Override
    // Sobrescribe para retornar la categoría carga refrigerada.
    public String getCategoria() {
        return CATEGORIA_CARGA_REFRIGERADA;
    }
}
