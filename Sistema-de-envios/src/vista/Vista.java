package vista;

import java.util.Scanner;

// Única vista que muestra menús y mensajes y lee los datos del encargado.
public class Vista {

    private Scanner scanner;

    // Crea el lector de teclado.
    public Vista() {
        scanner = new Scanner(System.in);
    }

    // Muestra las nueve opciones del menú principal.
    public void mostrarMenuPrincipal() {
        System.out.println("\n1. Cotizar y registrar envíos");
        System.out.println("2. Registrar vehículos");
        System.out.println("3. Crear despachos");
        System.out.println("4. Hacer salir despachos");
        System.out.println("5. Registrar resultados de entrega");
        System.out.println("6. Consultar envíos");
        System.out.println("7. Consultar vehículos");
        System.out.println("8. Reportes");
        System.out.println("9. Salir del programa");
    }

    // Muestra las cuatro categorías de envío y la opción de volver.
    public void mostrarSubmenuCategorias() {
        System.out.println("1. Sobre");
        System.out.println("2. Paquete");
        System.out.println("3. Paquete frágil");
        System.out.println("4. Carga refrigerada");
        System.out.println("0. Volver");
    }

    // Muestra las opciones urbano y departamental.
    public void mostrarMenuTipoDestino() {
        System.out.println("1. Urbano");
        System.out.println("2. Departamental");
    }

    // Muestra las opciones para armar, guardar, enviar o cancelar un despacho.
    public void mostrarSubmenuDespacho() {
        System.out.println("\n1. Asignar un envío");
        System.out.println("2. Retirar un envío");
        System.out.println("3. Ver contenido del despacho");
        System.out.println("4. Guardar el despacho");
        System.out.println("5. Enviar el despacho a ruta");
        System.out.println("6. Cancelar el despacho");
    }

    // Muestra las opciones de hacer salir o cancelar un despacho guardado.
    public void mostrarSubmenuSalidaDespacho() {
        System.out.println("1. Hacer salir el despacho");
        System.out.println("2. Cancelar el despacho");
        System.out.println("0. Volver");
    }

    // Muestra los cuatro reportes y la opción de volver.
    public void mostrarSubmenuReportes() {
        System.out.println("\n1. Envíos por categoría y estado");
        System.out.println("2. Ingreso total");
        System.out.println("3. Ingreso por categoría");
        System.out.println("4. Despachos no cerrados ni cancelados");
        System.out.println("0. Volver");
    }

    // Muestra el título de cada sección.
    public void mostrarTitulo(String titulo) {
        System.out.println("\n=== " + titulo + " ===");
    }

    // Muestra un mensaje general.
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    // Muestra un mensaje de error o de operación rechazada.
    public void mostrarError(String mensaje) {
        System.out.println("Error: " + mensaje);
    }

    // Muestra un mensaje de operación exitosa.
    public void mostrarExito(String mensaje) {
        System.out.println("Listo: " + mensaje);
    }

    // Muestra un monto en quetzales con dos decimales.
    public void mostrarMonto(String etiqueta, double monto) {
        System.out.println(etiqueta + ": Q" + String.format("%.2f", monto));
    }

    // Pide un texto y repite mientras esté vacío.
    public String pedirTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            mostrarError("El campo no puede estar vacío.");
        }
    }

    // Pide un entero y repite si el formato es incorrecto.
    public int pedirEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                mostrarError("Ingrese un número entero válido.");
            }
        }
    }

    // Pide un decimal y repite si el formato es incorrecto.
    public double pedirDecimal(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim().replace(',', '.');
            try {
                double valor = Double.parseDouble(texto);
                if (Double.isNaN(valor) || Double.isInfinite(valor)) {
                    throw new NumberFormatException();
                }
                return valor;
            } catch (NumberFormatException e) {
                mostrarError("Ingrese un número decimal válido.");
            }
        }
    }

    // Pide una respuesta de sí o no y repite si es otra.
    public boolean pedirConfirmacion(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (s/n): ");
            String texto = scanner.nextLine().trim().toLowerCase();
            if (texto.equals("s") || texto.equals("si") || texto.equals("sí")) {
                return true;
            }
            if (texto.equals("n") || texto.equals("no")) {
                return false;
            }
            mostrarError("Responda s o n.");
        }
    }
}
