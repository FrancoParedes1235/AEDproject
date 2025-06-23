package com.project_aed.btreeplus;

// imports necesarios

public class TestBPlusProductos {
    public static void main(String[] args) {
        int orden = 5; // Orden alto para simular ramas grandes
        BPlusTree<Integer, Producto> arbol = new BPlusTree<>(orden);

        // --- Inserción masiva
        for (int i = 1; i <= 100; i++) {
            String nombre = "Producto" + i;
            String lote = "L" + ((i % 10) + 1);
            String categoria = "Categoria" + ((i % 5) + 1);
            String fecha = "202" + (i % 10) + "-0" + ((i % 12) + 1) + "-15";
            Producto p = new Producto(nombre, lote, categoria, fecha);
            arbol.insert(i, p);
        }
        System.out.println("=== Árbol después de inserciones masivas ===");
        arbol.printTree();

        // --- Búsqueda de productos existentes
        System.out.println("\n=== Búsqueda de productos por clave (ID) ===");
        for (int i = 1; i <= 5; i++) {
            Producto p = arbol.search(i);
            System.out.println("ID " + i + ": " + (p != null ? p : "No encontrado"));
        }

        // --- Búsqueda de producto NO existente
        System.out.println("\nBuscar producto con ID 200: " + arbol.search(200));

        // --- Prueba de sobrescritura (mismo ID, diferente producto)
        Producto prodNuevo = new Producto("NuevoProducto", "L20", "CategoriaNueva", "2030-12-31");
        arbol.insert(5, prodNuevo); // Sobrescribe ID 5
        System.out.println("\nProducto con ID 5 tras sobrescribir: " + arbol.search(5));

        // --- Eliminación de productos (incluso claves límite y mitad)
        int[] aEliminar = { 1, 50, 100, 25, 80, 60 };
        for (int id : aEliminar) {
            arbol.delete(id);
            System.out.println("Eliminado ID: " + id + ". Buscar después: " + arbol.search(id));
        }

        System.out.println("\n=== Árbol después de varias eliminaciones ===");
        arbol.printTree();

        // --- Recorrido ordenado por hojas
        System.out.println("\n=== Recorrido por hojas (productos en orden) ===");
        arbol.printLeaves();

        // --- Inserta más para ver splits en hojas ya reducidas
        System.out.println("\n--- Insertando productos extra para probar splits ---");
        for (int i = 101; i <= 120; i++) {
            arbol.insert(i, new Producto("Extra" + i, "L" + (i % 7), "Cat" + (i % 3), "203" + (i % 9) + "-07-20"));
        }
        arbol.printTree();
        System.out.println("\n=== Recorrido final por hojas ===");
        arbol.printLeaves();

        System.out.println("=== Árbol B+ estructurado ===");
        arbol.printTreeStructure();

    }
}
