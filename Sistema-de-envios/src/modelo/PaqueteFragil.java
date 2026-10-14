package modelo;

import modelo.enums.TipoDestino;

// Paquete con valor declarado, embalaje y seguro.
public class PaqueteFragil extends Paquete {

    private double valorDeclarado;
    private static final double CARGO_EMBALAJE = 15.0;
    private static final double PORCENTAJE_SEGURO = 0.03;

    // Construye el paquete frágil y rechaza un valor declarado no positivo.
    public PaqueteFragil(String codigo, String remitente, String destinatario, double pesoReal, TipoDestino tipoDestino, double largo, double ancho, double alto, double valorDeclarado) {
        super(codigo, remitente, destinatario, pesoReal, tipoDestino, largo, ancho, alto);
        if (valorDeclarado <= 0) {
            throw new IllegalArgumentException("El valor declarado debe ser mayor que cero.");
        }
        this.valorDeclarado = valorDeclarado;
    }

    @Override
    // Sobrescribe para sumar al costo del paquete el embalaje y el seguro.
    public double calcularCosto() {
        return super.calcularCosto() + CARGO_EMBALAJE + valorDeclarado * PORCENTAJE_SEGURO;
    }

    @Override
    // Sobrescribe para sumar un día al tiempo del paquete.
    public int getTiempoEstimado() {
        return super.getTiempoEstimado() + 1;
    }

    @Override
    // Sobrescribe para bajar los intentos a 2.
    public int getIntentosPermitidos() {
        return 2;
    }

    @Override
    // Sobrescribe para agregar el valor declarado a la descripción del paquete.
    public String getDescripcion() {
        return super.getDescripcion() + " | Valor declarado: Q" + String.format("%.2f", valorDeclarado);
    }

    @Override
    // Sobrescribe para retornar la categoría paquete frágil.
    public String getCategoria() {
        return CATEGORIA_PAQUETE_FRAGIL;
    }
}
