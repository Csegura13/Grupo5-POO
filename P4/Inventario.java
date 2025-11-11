package P4;

import java.util.ArrayList;

class Producto {
    private String nombre;
    private double precio;
    private int cantidad;

    public Producto(String nombre) {
        this(nombre, 0.0, 0);
    }

    public Producto(String nombre, double precio) {
        this(nombre, precio, 0);
    }

    public Producto(String nombre, double precio, int cantidad) {
        if (precio < 0 || cantidad < 0) {
            throw new IllegalArgumentException("Precio o cantidad no pueden ser negativos.");
        }
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "Producto: " + nombre + ", Precio: " + precio + ", Cantidad: " + cantidad;
    }
}

public class Inventario {
    private ArrayList<Producto> productos = new ArrayList<>();

    // Sobrecarga de métodos para agregar productos
    public void agregarProducto(String nombre) {
        productos.add(new Producto(nombre));
    }

    public void agregarProducto(String nombre, double precio) {
        productos.add(new Producto(nombre, precio));
    }

    public void agregarProducto(String nombre, double precio, int cantidad) {
        try {
            productos.add(new Producto(nombre, precio, cantidad));
        } catch (IllegalArgumentException e) {
            System.out.println("Error al agregar producto: " + e.getMessage());
        }
    }

    // Mostrar todos los productos
    public void mostrarProductos() {
        if (productos.isEmpty()) {
            System.out.println("No hay productos en el inventario.");
        } else {
            for (Producto p : productos) {
                System.out.println(p);
            }
        }
    }

    // Método main de prueba
    public static void main(String[] args) {
        Inventario inventario = new Inventario();

        inventario.agregarProducto("Laptop");
        inventario.agregarProducto("Mouse", 35.50);
        inventario.agregarProducto("Teclado", 120.0, 10);
        inventario.agregarProducto("Monitor", -50.0, 5); // Causa error
        inventario.agregarProducto("USB", 25.0, -3);     // Causa error

        System.out.println("\nLista de productos en inventario:");
        inventario.mostrarProductos();
    }
}
