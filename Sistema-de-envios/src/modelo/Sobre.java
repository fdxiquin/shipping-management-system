package modelo;

import modelo.enums.TipoDestino;

// Envío liviano con costo fijo según el destino.
public class Sobre extends Envio {

    private static final double PESO_MAXIMO = 0.5;
    private static final double COSTO_URBANO = 20.0;
    private static final double COSTO_DEPARTAMENTAL = 35.0;

    // Construye el sobre y rechaza un peso mayor a 0.5 kg.
    public Sobre(String codigo, String remitente, String destinatario, double pesoReal, TipoDestino tipoDestino) {
        super(codigo, remitente, destinatario, pesoReal, tipoDestino);
        if (pesoReal > PESO_MAXIMO) {
            throw new IllegalArgumentException("Un sobre no puede pesar más de 0.5 kg.");
        }
    }

    @Override
    // Sobrescribe para cobrar un costo fijo según el destino.
    public double calcularCosto() {
        if (tipoDestino == TipoDestino.URBANO) {
            return COSTO_URBANO;
        }
        return COSTO_DEPARTAMENTAL;
    }

    @Override
    // Sobrescribe para estimar 1 día urbano y 2 departamental.
    public int getTiempoEstimado() {
        if (tipoDestino == TipoDestino.URBANO) {
            return 1;
        }
        return 2;
    }

    @Override
    // Sobrescribe para permitir 3 intentos.
    public int getIntentosPermitidos() {
        return 3;
    }

    @Override
    // Sobrescribe para mostrar los datos comunes con su categoría.
    public String getDescripcion() {
        return "[" + getCategoria() + "] " + getDatosComunes();
    }

    @Override
    // Sobrescribe para indicar que no exige refrigeración.
    public boolean requiereRefrigeracion() {
        return false;
    }

    @Override
    // Sobrescribe para retornar la categoría sobre.
    public String getCategoria() {
        return CATEGORIA_SOBRE;
    }
}
