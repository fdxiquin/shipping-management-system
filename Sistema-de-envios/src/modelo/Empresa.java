package modelo;

import java.util.ArrayList;
import modelo.enums.EstadoDespacho;
import modelo.enums.EstadoEnvio;

// Concentra los envíos, los vehículos, los despachos y los ingresos.
public class Empresa {

    private ArrayList<Envio> envios;
    private ArrayList<Vehiculo> vehiculos;
    private ArrayList<Despacho> despachos;
    private int siguienteIdDespacho;
    private double ingresosRegistro;
    private double ingresosReintentos;
    private double ingresoSobre;
    private double ingresoPaquete;
    private double ingresoPaqueteFragil;
    private double ingresoCargaRefrigerada;

    // Crea las tres colecciones vacías y deja los ingresos en cero.
    public Empresa() {
        envios = new ArrayList<>();
        vehiculos = new ArrayList<>();
        despachos = new ArrayList<>();
        siguienteIdDespacho = 1;
        ingresosRegistro = 0;
        ingresosReintentos = 0;
        ingresoSobre = 0;
        ingresoPaquete = 0;
        ingresoPaqueteFragil = 0;
        ingresoCargaRefrigerada = 0;
    }

    // Retorna los envíos para recorrerlos en los reportes.
    public ArrayList<Envio> getEnvios() {
        return envios;
    }

    // Retorna los despachos para recorrerlos en los reportes.
    public ArrayList<Despacho> getDespachos() {
        return despachos;
    }

    // Busca un envío por código y retorna null si no existe.
    public Envio buscarEnvio(String codigo) {
        for (Envio envio : envios) {
            if (envio.getCodigo().equals(codigo)) {
                return envio;
            }
        }
        return null;
    }

    // Busca un vehículo por placa y retorna null si no existe.
    public Vehiculo buscarVehiculo(String placa) {
        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo.getPlaca().equals(placa)) {
                return vehiculo;
            }
        }
        return null;
    }

    // Busca un despacho por identificador y retorna null si no existe.
    public Despacho buscarDespacho(int id) {
        for (Despacho despacho : despachos) {
            if (despacho.getId() == id) {
                return despacho;
            }
        }
        return null;
    }

    // Busca el despacho abierto que contiene el envío.
    public Despacho buscarDespachoDeEnvio(String codigo) {
        for (Despacho despacho : despachos) {
            boolean abierto = despacho.getEstado() != EstadoDespacho.CERRADO
                    && despacho.getEstado() != EstadoDespacho.CANCELADO;
            if (abierto && despacho.buscarEnvio(codigo) != null) {
                return despacho;
            }
        }
        return null;
    }

    // Busca el despacho en ruta que lleva el vehículo.
    public Despacho buscarDespachoEnRutaDeVehiculo(String placa) {
        for (Despacho despacho : despachos) {
            if (despacho.getEstado() == EstadoDespacho.EN_RUTA
                    && despacho.getVehiculo().getPlaca().equals(placa)) {
                return despacho;
            }
        }
        return null;
    }

    // Filtra los envíos que están en el estado indicado.
    public ArrayList<Envio> obtenerEnviosPorEstado(EstadoEnvio estado) {
        ArrayList<Envio> resultado = new ArrayList<>();
        for (Envio envio : envios) {
            if (envio.getEstado() == estado) {
                resultado.add(envio);
            }
        }
        return resultado;
    }

    // Filtra los despachos que están en el estado indicado.
    public ArrayList<Despacho> obtenerDespachosPorEstado(EstadoDespacho estado) {
        ArrayList<Despacho> resultado = new ArrayList<>();
        for (Despacho despacho : despachos) {
            if (despacho.getEstado() == estado) {
                resultado.add(despacho);
            }
        }
        return resultado;
    }

    // Valida que el código sea único y agrega el envío. Luego cobra su costo como ingreso por registro.
    public void registrarEnvio(Envio envio) {
        if (envio == null) {
            throw new IllegalArgumentException("El envío no puede ser nulo.");
        }
        if (buscarEnvio(envio.getCodigo()) != null) {
            throw new IllegalArgumentException("Ya existe un envío con el código " + envio.getCodigo() + ".");
        }
        envios.add(envio);
        double monto = envio.calcularCosto();
        ingresosRegistro += monto;
        sumarIngresoCategoria(envio.getCategoria(), monto);
    }

    // Valida que la placa sea única y agrega el vehículo.
    public void registrarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo no puede ser nulo.");
        }
        if (buscarVehiculo(vehiculo.getPlaca()) != null) {
            throw new IllegalArgumentException("Ya existe un vehículo con la placa " + vehiculo.getPlaca() + ".");
        }
        vehiculos.add(vehiculo);
    }

    // Crea el despacho con el siguiente identificador y lo agrega a la colección.
    public Despacho crearDespacho(Vehiculo vehiculo) {
        Despacho despacho = new Despacho(siguienteIdDespacho, vehiculo);
        siguienteIdDespacho++;
        despachos.add(despacho);
        return despacho;
    }

    // Suma el cargo de reintento al ingreso por reintentos y a su categoría.
    public void registrarReintento(Envio envio) {
        ingresosReintentos += Envio.CARGO_REINTENTO;
        sumarIngresoCategoria(envio.getCategoria(), Envio.CARGO_REINTENTO);
    }

    // Retorna el total cobrado por registro.
    public double getIngresosRegistro() {
        return ingresosRegistro;
    }

    // Retorna el total cobrado por reintentos.
    public double getIngresosReintentos() {
        return ingresosReintentos;
    }

    // Suma los ingresos por registro y por reintentos.
    public double calcularIngresoTotal() {
        return ingresosRegistro + ingresosReintentos;
    }

    // Retorna el ingreso acumulado de la categoría indicada.
    public double getIngresoCategoria(String categoria) {
        switch (categoria) {
            case Envio.CATEGORIA_SOBRE:
                return ingresoSobre;
            case Envio.CATEGORIA_PAQUETE:
                return ingresoPaquete;
            case Envio.CATEGORIA_PAQUETE_FRAGIL:
                return ingresoPaqueteFragil;
            case Envio.CATEGORIA_CARGA_REFRIGERADA:
                return ingresoCargaRefrigerada;
            default:
                return 0;
        }
    }

    // Suma el monto al acumulado de la categoría correspondiente.
    private void sumarIngresoCategoria(String categoria, double monto) {
        switch (categoria) {
            case Envio.CATEGORIA_SOBRE:
                ingresoSobre += monto;
                break;
            case Envio.CATEGORIA_PAQUETE:
                ingresoPaquete += monto;
                break;
            case Envio.CATEGORIA_PAQUETE_FRAGIL:
                ingresoPaqueteFragil += monto;
                break;
            case Envio.CATEGORIA_CARGA_REFRIGERADA:
                ingresoCargaRefrigerada += monto;
                break;
            default:
                throw new IllegalArgumentException("Categoría desconocida: " + categoria);
        }
    }
}
