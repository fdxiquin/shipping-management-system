# Laboratorio 3 - Herencia: Sistema de envíos

**Nombre completo:** Daniel Fernando Xiquin Tezén
**Carné:** 26896

## Descripción
Programa de consola para una empresa de envíos en Guatemala. Permite cotizar y registrar envíos, registrar vehículos, armar despachos, hacerlos salir, registrar resultados de entrega y consultar reportes de envíos e ingresos.

**Clases:**
- `Envio` (abstracta) y sus hijas `Sobre`, `Paquete`, `PaqueteFragil` (hija de `Paquete`) y `CargaRefrigerada`.
- `Vehiculo`, `Despacho` y `Empresa` (guarda las listas de envíos, vehículos, despachos y los ingresos).
- Enums: `TipoDestino`, `EstadoEnvio`, `EstadoVehiculo` y `EstadoDespacho`.
- `Vista`, `ControladorEmpresa`, `ControladorEnvio`, `ControladorVehiculo` y `ControladorDespacho`.

**Decisiones de diseño:**
- El costo, el tiempo estimado, los intentos permitidos, la descripción y el requisito de refrigeración los define cada categoría con métodos abstractos sobrescritos. Por eso el menú y los despachos no cambian si se agrega una categoría nueva.
- `PaqueteFragil` hereda de `Paquete` para reutilizar las dimensiones y la fórmula de costo.
- Las reglas de negocio y las validaciones están en el modelo, y los controladores solo muestran los errores.

## Cómo ejecutar
```bash
javac -encoding UTF-8 -d bin $(find src -name "*.java")
java -cp bin main.Main
```
