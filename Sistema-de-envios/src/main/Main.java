package main;

import controladores.ControladorEmpresa;

public class Main {

    private static ControladorEmpresa controlador = new ControladorEmpresa();

    // Llama al método iniciar del controlador de la empresa.
    public static void main(String[] args) {
        controlador.iniciar();
    }
}
