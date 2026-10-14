package modelo;

import modelo.enums.EstadoEnvio;
import modelo.enums.TipoDestino;

// Clase abstracta que agrupa los datos y las reglas comunes de todo envío.
public abstract class Envio {

    protected String codigo;
    protected String remitente;
    protected String destinatario;
    protected double pesoReal;
    protected TipoDestino tipoDestino;
    protected EstadoEnvio estado;
    protected int intentosRealizados;
    public static final double CARGO_REINTENTO = 15.0;
    private static final double TARIFA_BASE_URBANA = 25.0;
    private static final double TARIFA_BASE_DEPARTAMENTAL = 40.0;
    public static final String CATEGORIA_SOBRE = "Sobre";
    public static final String CATEGORIA_PAQUETE = "Paquete";
    public static final String CATEGORIA_PAQUETE_FRAGIL = "Paquete frágil";
    public static final String CATEGORIA_CARGA_REFRIGERADA = "Carga refrigerada";

    // Asigna los datos comunes y valida textos y peso. Inicia en bodega con cero intentos.
    protected Envio(String codigo, String remitente, String destinatario, double pesoReal, TipoDestino tipoDestino) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código del envío no puede estar vacío.");
        }
        if (remitente == null || remitente.trim().isEmpty()) {
            throw new IllegalArgumentException("El remitente no puede estar vacío.");
        }
        if (destinatario == null || destinatario.trim().isEmpty()) {
            throw new IllegalArgumentException("El destinatario no puede estar vacío.");
        }
        if (pesoReal <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor que cero.");
        }
        if (tipoDestino == null) {
            throw new IllegalArgumentException("El tipo de destino debe ser urbano o departamental.");
        }
        this.codigo = codigo.trim();
        this.remitente = remitente.trim();
        this.destinatario = destinatario.trim();
        this.pesoReal = pesoReal;
        this.tipoDestino = tipoDestino;
        this.estado = EstadoEnvio.EN_BODEGA;
        this.intentosRealizados = 0;
    }

    // Retorna el código para buscar y mostrar el envío.
    public String getCodigo() {
        return codigo;
    }

    // Retorna el peso real para sumar la carga del despacho.
    public double getPesoReal() {
        return pesoReal;
    }

    // Retorna el estado para filtrar y reportar.
    public EstadoEnvio getEstado() {
        return estado;
    }

    // Cambia el estado según la operación realizada.
    public void setEstado(EstadoEnvio estado) {
        this.estado = estado;
    }

    // Retorna los intentos hechos para la consulta.
    public int getIntentosRealizados() {
        return intentosRealizados;
    }

    // Suma un intento de entrega cuando el despacho sale.
    public void sumarIntento() {
        intentosRealizados++;
    }

    // Indica si el intento acumulado es el segundo o posterior.
    public boolean esReintento() {
        return intentosRealizados > 1;
    }

    // Compara los intentos realizados con los permitidos por la categoría.
    public boolean tieneIntentosDisponibles() {
        return intentosRealizados < getIntentosPermitidos();
    }

    // Decide si el vehículo sirve según el requisito de refrigeración.
    public boolean esCompatibleCon(Vehiculo vehiculo) {
        return !requiereRefrigeracion() || vehiculo.isRefrigerado();
    }

    // Arma el texto de los datos comunes para reutilizarlo en las descripciones.
    protected String getDatosComunes() {
        return "Código: " + codigo
                + " | Remitente: " + remitente
                + " | Destinatario: " + destinatario
                + " | Peso real: " + String.format("%.2f", pesoReal) + " kg"
                + " | Destino: " + tipoDestino;
    }

    // Retorna la tarifa base según el destino para paquetes y cargas refrigeradas.
    protected double getTarifaBase() {
        if (tipoDestino == TipoDestino.URBANO) {
            return TARIFA_BASE_URBANA;
        }
        return TARIFA_BASE_DEPARTAMENTAL;
    }

    // Obliga a cada categoría a definir su fórmula de costo.
    public abstract double calcularCosto();

    // Obliga a cada categoría a definir sus días hábiles de entrega.
    public abstract int getTiempoEstimado();

    // Obliga a cada categoría a definir sus intentos permitidos.
    public abstract int getIntentosPermitidos();

    // Obliga a cada categoría a mostrar sus propios datos.
    public abstract String getDescripcion();

    // Obliga a cada categoría a indicar si exige vehículo refrigerado.
    public abstract boolean requiereRefrigeracion();

    // Obliga a cada categoría a identificarse para agrupar sin preguntar su clase.
    public abstract String getCategoria();
}
