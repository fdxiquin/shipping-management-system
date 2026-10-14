package controladores;

import modelo.CargaRefrigerada;
import modelo.Despacho;
import modelo.Empresa;
import modelo.Envio;
import modelo.Paquete;
import modelo.PaqueteFragil;
import modelo.Sobre;
import modelo.enums.TipoDestino;
import vista.Vista;

// Controla la cotización, el registro y la consulta de envíos.
public class ControladorEnvio {

    private Empresa empresa;
    private Vista vista;

    // Recibe la empresa y la vista para trabajar con ellas.
    public ControladorEnvio(Empresa empresa, Vista vista) {
        this.empresa = empresa;
        this.vista = vista;
    }

    // Pide los datos comunes y dirige la creación según la categoría. Además muestra los errores de validación.
    public void cotizarYRegistrar() {
        vista.mostrarTitulo("Cotizar y registrar envíos");
        vista.mostrarSubmenuCategorias();
        int categoria = vista.pedirEntero("Seleccione la categoría: ");
        if (categoria == 0) {
            return;
        }
        if (categoria < 1 || categoria > 4) {
            vista.mostrarError("Categoría inválida.");
            return;
        }
        try {
            String codigo = pedirCodigoNuevo();
            String remitente = vista.pedirTexto("Remitente: ");
            String destinatario = vista.pedirTexto("Destinatario: ");
            double pesoReal = vista.pedirDecimal("Peso real en kg: ");
            TipoDestino tipoDestino = pedirTipoDestino();
            Envio envio;
            switch (categoria) {
                case 1:
                    envio = crearSobre(codigo, remitente, destinatario, pesoReal, tipoDestino);
                    break;
                case 2:
                    envio = crearPaquete(codigo, remitente, destinatario, pesoReal, tipoDestino);
                    break;
                case 3:
                    envio = crearPaqueteFragil(codigo, remitente, destinatario, pesoReal, tipoDestino);
                    break;
                default:
                    envio = crearCargaRefrigerada(codigo, remitente, destinatario, pesoReal, tipoDestino);
                    break;
            }
            procesarCotizacion(envio);
        } catch (IllegalArgumentException | IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    // Construye un sobre, que no pide datos propios.
    private Envio crearSobre(String codigo, String remitente, String destinatario, double pesoReal, TipoDestino tipoDestino) {
        return new Sobre(codigo, remitente, destinatario, pesoReal, tipoDestino);
    }

    // Pide largo, ancho y alto y construye el paquete.
    private Envio crearPaquete(String codigo, String remitente, String destinatario, double pesoReal, TipoDestino tipoDestino) {
        double largo = vista.pedirDecimal("Largo en cm: ");
        double ancho = vista.pedirDecimal("Ancho en cm: ");
        double alto = vista.pedirDecimal("Alto en cm: ");
        return new Paquete(codigo, remitente, destinatario, pesoReal, tipoDestino, largo, ancho, alto);
    }

    // Pide las dimensiones y el valor declarado y construye el paquete frágil.
    private Envio crearPaqueteFragil(String codigo, String remitente, String destinatario, double pesoReal, TipoDestino tipoDestino) {
        double largo = vista.pedirDecimal("Largo en cm: ");
        double ancho = vista.pedirDecimal("Ancho en cm: ");
        double alto = vista.pedirDecimal("Alto en cm: ");
        double valorDeclarado = vista.pedirDecimal("Valor declarado en Q: ");
        return new PaqueteFragil(codigo, remitente, destinatario, pesoReal, tipoDestino, largo, ancho, alto, valorDeclarado);
    }

    // Pide la temperatura y construye la carga refrigerada.
    private Envio crearCargaRefrigerada(String codigo, String remitente, String destinatario, double pesoReal, TipoDestino tipoDestino) {
        double temperatura = vista.pedirDecimal("Temperatura requerida en °C: ");
        return new CargaRefrigerada(codigo, remitente, destinatario, pesoReal, tipoDestino, temperatura);
    }

    // Pide el código y verifica que no esté registrado.
    private String pedirCodigoNuevo() {
        String codigo = vista.pedirTexto("Código del envío: ");
        if (empresa.buscarEnvio(codigo) != null) {
            throw new IllegalArgumentException("Ya existe un envío con el código " + codigo + ".");
        }
        return codigo;
    }

    // Muestra las opciones de destino y retorna la elegida.
    private TipoDestino pedirTipoDestino() {
        vista.mostrarMenuTipoDestino();
        int opcion = vista.pedirEntero("Seleccione el tipo de destino: ");
        if (opcion == 1) {
            return TipoDestino.URBANO;
        }
        if (opcion == 2) {
            return TipoDestino.DEPARTAMENTAL;
        }
        throw new IllegalArgumentException("El tipo de destino debe ser urbano o departamental.");
    }

    // Muestra el costo y el tiempo y pregunta si el cliente acepta. Solo registra si acepta.
    private void procesarCotizacion(Envio envio) {
        vista.mostrarMonto("Costo", envio.calcularCosto());
        vista.mostrarMensaje("Tiempo estimado: " + envio.getTiempoEstimado() + " día(s) hábil(es)");
        if (vista.pedirConfirmacion("¿El cliente acepta la cotización?")) {
            empresa.registrarEnvio(envio);
            vista.mostrarExito("Envío " + envio.getCodigo() + " registrado y en bodega.");
        } else {
            vista.mostrarMensaje("Cotización rechazada. No se registró nada.");
        }
    }

    // Pide el código y muestra todos los datos del envío, incluido su despacho.
    public void consultarEnvio() {
        vista.mostrarTitulo("Consultar envíos");
        String codigo = vista.pedirTexto("Código del envío: ");
        Envio envio = empresa.buscarEnvio(codigo);
        if (envio == null) {
            vista.mostrarError("El envío " + codigo + " no existe.");
            return;
        }
        vista.mostrarMensaje(envio.getDescripcion());
        vista.mostrarMonto("Costo", envio.calcularCosto());
        vista.mostrarMensaje("Tiempo estimado: " + envio.getTiempoEstimado() + " día(s) hábil(es)");
        vista.mostrarMensaje("Estado: " + envio.getEstado());
        vista.mostrarMensaje("Intentos realizados: " + envio.getIntentosRealizados()
                + " de " + envio.getIntentosPermitidos());
        Despacho despacho = empresa.buscarDespachoDeEnvio(codigo);
        if (despacho != null) {
            vista.mostrarMensaje("Despacho asignado: #" + despacho.getId());
        }
    }
}
