package com.project_aed;

// Importa JGraphX
import com.mxgraph.view.mxGraph;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.model.mxCell; // este import es necesario para manejar eventos de clic

// Importa tus propias clases
import com.project_aed.graph.GraphLink;
import com.project_aed.graph.Edge;
import com.project_aed.graph.Vertex;
import com.project_aed.visual.GraphVisualizer;
import com.project_aed.visual.BPlusTreeVisualizer;
import com.project_aed.btreeplus.BPlusTree;
import com.project_aed.btreeplus.Producto;

// Resto de imports estándar
import javax.swing.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        // 1. Grafo dirigido y ponderado de zonas del almacén
        GraphLink<String> almacen = new GraphLink<>(true);
        String[] zonas = {
                "Recepción", "EstanteA", "EstanteB", "EstanteC",
                "ZonaFría", "Despacho", "Carga", "Pasillo1", "Pasillo2", "Control"
        };
        for (String zona : zonas)
            almacen.addVertex(zona);

        // Aristas
        almacen.addEdge("Recepción", "Pasillo1", 4);
        almacen.addEdge("Pasillo1", "EstanteA", 2);
        almacen.addEdge("Pasillo1", "EstanteB", 3);
        almacen.addEdge("EstanteA", "Pasillo2", 2);
        almacen.addEdge("EstanteB", "Pasillo2", 2);
        almacen.addEdge("Pasillo2", "EstanteC", 3);
        almacen.addEdge("EstanteC", "ZonaFría", 2);
        almacen.addEdge("ZonaFría", "Despacho", 5);
        almacen.addEdge("Despacho", "Carga", 3);
        almacen.addEdge("Carga", "Control", 4);
        almacen.addEdge("EstanteB", "Despacho", 5);
        almacen.addEdge("EstanteA", "ZonaFría", 6);

        // 2. Map zona -> BPlusTree de productos
        Map<String, BPlusTree<Integer, Producto>> productosPorZona = new HashMap<>();
        for (String zona : zonas) {
            productosPorZona.put(zona, new BPlusTree<>(4));
        }

        // 3. Insertar productos en las zonas
        productosPorZona.get("EstanteA").insert(101, new Producto("Arroz 1kg", "L123", "Granos", "2025-09-10"));
        productosPorZona.get("EstanteA").insert(102, new Producto("Azúcar 1kg", "L200", "Granos", "2025-10-15"));
        productosPorZona.get("EstanteA").insert(103, new Producto("Aceite 1L", "L321", "Aceites", "2026-01-12"));

        productosPorZona.get("EstanteB").insert(201, new Producto("Cereal", "B312", "Desayuno", "2025-10-05"));
        productosPorZona.get("EstanteB").insert(202, new Producto("Galletas", "B410", "Snacks", "2026-03-11"));

        productosPorZona.get("ZonaFría").insert(301, new Producto("Leche 1L", "Z100", "Lácteos", "2024-08-01"));
        productosPorZona.get("ZonaFría").insert(302, new Producto("Yogurt", "Z101", "Lácteos", "2024-07-02"));
        productosPorZona.get("ZonaFría").insert(303, new Producto("Queso", "Z102", "Lácteos", "2024-09-10"));

        productosPorZona.get("Despacho").insert(401, new Producto("Caja Embalada", "D123", "Envíos", "2026-12-01"));

        // ... (puedes agregar más productos/zona si gustas)

        // 4. Visualizar el grafo, con click para abrir B+ de la zona
        visualizarGrafoConEventos(almacen, productosPorZona);

        // 5. Ejemplo: Buscar producto en TODO el almacén
        int claveBuscada = 102;
        for (Map.Entry<String, BPlusTree<Integer, Producto>> entry : productosPorZona.entrySet()) {
            Producto p = entry.getValue().search(claveBuscada);
            if (p != null) {
                System.out.println("Producto encontrado en zona: " + entry.getKey() + " => " + p);
            }
        }

        // 6. Menú interactivo para interactuar con el almacén
        menuInteractivo(almacen, productosPorZona);

    }

    // menu interactivo para interactuar con el almacén
    public static void menuInteractivo(
            GraphLink<String> almacen,
            Map<String, BPlusTree<Integer, Producto>> productosPorZona) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== MENÚ ALMACÉN ===");
            System.out.println("1. Mostrar grafo visual interactivo");
            System.out.println("2. Añadir zona");
            System.out.println("3. Eliminar zona");
            System.out.println("4. Añadir ruta");
            System.out.println("5. Eliminar ruta");
            System.out.println("6. BFS");
            System.out.println("7. DFS");
            System.out.println("8. Dijkstra");
            System.out.println("9. Ver productos de una zona (visual B+)");
            System.out.println("10. Añadir producto a zona");
            System.out.println("11. Eliminar producto de zona");
            System.out.println("12. Buscar producto por ID en todo el almacén");
            System.out.println("13. Mostrar árbol B+ completo de una zona (consola)");
            System.out.println("14. Salir");
            System.out.print("Elige una opción: ");
            String entrada = sc.nextLine().trim();
            int opc;
            try {
                opc = Integer.parseInt(entrada);
            } catch (NumberFormatException ex) {
                System.out.println("Opción inválida.");
                continue;
            }

            if (opc == 14)
                break;

            switch (opc) {
                case 1:
                    visualizarGrafoConEventos(almacen, productosPorZona);
                    break;
                case 2:
                    System.out.print("Nombre de la nueva zona: ");
                    String zona = sc.nextLine().trim();
                    if (zona.isEmpty() || productosPorZona.containsKey(zona)) {
                        System.out.println("Zona inválida o ya existe.");
                    } else {
                        almacen.addVertex(zona);
                        productosPorZona.put(zona, new BPlusTree<>(4));
                        System.out.println("Zona añadida.");
                    }
                    break;
                case 3:
                    System.out.print("Nombre de la zona a eliminar: ");
                    String zonaDel = sc.nextLine().trim();
                    almacen.removeVertex(zonaDel);
                    productosPorZona.remove(zonaDel);
                    System.out.println("Zona eliminada.");
                    break;
                case 4:
                    System.out.print("Origen: ");
                    String from = sc.nextLine().trim();
                    System.out.print("Destino: ");
                    String to = sc.nextLine().trim();
                    System.out.print("Peso: ");
                    int peso = Integer.parseInt(sc.nextLine().trim());
                    almacen.addEdge(from, to, peso);
                    System.out.println("Ruta añadida.");
                    break;
                case 5:
                    System.out.print("Origen: ");
                    String from2 = sc.nextLine().trim();
                    System.out.print("Destino: ");
                    String to2 = sc.nextLine().trim();
                    almacen.removeEdge(from2, to2);
                    System.out.println("Ruta eliminada.");
                    break;
                case 6:
                    System.out.print("Zona inicio: ");
                    String bfsStart = sc.nextLine().trim();
                    almacen.bfs(bfsStart);
                    break;
                case 7:
                    System.out.print("Zona inicio: ");
                    String dfsStart = sc.nextLine().trim();
                    almacen.dfs(dfsStart);
                    break;
                case 8:
                    System.out.print("Inicio: ");
                    String dStart = sc.nextLine().trim();
                    System.out.print("Fin: ");
                    String dEnd = sc.nextLine().trim();
                    System.out.println("Camino más corto: " + almacen.shortestPath(dStart, dEnd));
                    break;
                case 9:
                    System.out.print("Zona: ");
                    String zonaVer = sc.nextLine().trim();
                    if (productosPorZona.containsKey(zonaVer))
                        BPlusTreeVisualizer.visualize(productosPorZona.get(zonaVer), zonaVer);
                    else
                        System.out.println("Zona no encontrada.");
                    break;
                case 10:
                    System.out.print("Zona: ");
                    String zonaProd = sc.nextLine().trim();
                    if (!productosPorZona.containsKey(zonaProd)) {
                        System.out.println("Zona no encontrada.");
                        break;
                    }
                    System.out.print("ID producto: ");
                    int id = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Nombre: ");
                    String nom = sc.nextLine().trim();
                    System.out.print("Lote: ");
                    String lote = sc.nextLine().trim();
                    System.out.print("Categoria: ");
                    String cat = sc.nextLine().trim();
                    System.out.print("Caducidad: ");
                    String cad = sc.nextLine().trim();
                    productosPorZona.get(zonaProd).insert(id, new Producto(nom, lote, cat, cad));
                    System.out.println("Producto añadido.");
                    break;
                case 11:
                    System.out.print("Zona: ");
                    String zonaRem = sc.nextLine().trim();
                    if (!productosPorZona.containsKey(zonaRem)) {
                        System.out.println("Zona no encontrada.");
                        break;
                    }
                    System.out.print("ID producto a eliminar: ");
                    int idRem = Integer.parseInt(sc.nextLine().trim());
                    productosPorZona.get(zonaRem).delete(idRem);
                    System.out.println("Producto eliminado.");
                    break;
                case 12:
                    System.out.print("ID a buscar: ");
                    int idFind = Integer.parseInt(sc.nextLine().trim());
                    boolean found = false;
                    for (var entry : productosPorZona.entrySet()) {
                        Producto p = entry.getValue().search(idFind);
                        if (p != null) {
                            System.out.println("Encontrado en " + entry.getKey() + ": " + p);
                            found = true;
                        }
                    }
                    if (!found)
                        System.out.println("No encontrado.");
                    break;
                case 13:
                    System.out.print("Zona: ");
                    String zonaTree = sc.nextLine().trim();
                    if (productosPorZona.containsKey(zonaTree))
                        productosPorZona.get(zonaTree).printTreeStructure();
                    else
                        System.out.println("Zona no encontrada.");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
        System.out.println("Fin del programa.");
    }

    /**
     * Visualiza el grafo y muestra el árbol B+ correspondiente al dar click en una
     * zona.
     */
    public static void visualizarGrafoConEventos(
            GraphLink<String> graph,
            Map<String, BPlusTree<Integer, Producto>> productosPorZona) {
        // Usamos un código similar a tu GraphVisualizer, pero con eventos de click:
        mxGraph mxgraph = new mxGraph();
        Object parent = mxgraph.getDefaultParent();

        int x = 40, y = 40, dx = 120, dy = 120, col = 0, row = 0;
        int maxCols = (int) Math.ceil(Math.sqrt(graph.getVertices().size()));
        Map<String, Object> vertexMap = new HashMap<>();

        mxgraph.getModel().beginUpdate();
        try {
            for (Vertex<String> v : graph.getVertices()) {
                if (col >= maxCols) {
                    col = 0;
                    row++;
                }
                Object cell = mxgraph.insertVertex(parent, null, v.getData(),
                        x + col * dx, y + row * dy, 80, 50);
                vertexMap.put(v.getData(), cell);
                col++;
            }
            for (Vertex<String> v : graph.getVertices()) {
                for (Edge<String> e : v.getAdjList()) {
                    Object src = vertexMap.get(v.getData());
                    Object dst = vertexMap.get(e.getDest().getData());
                    String label = e.getWeight() != null ? e.getWeight().toString() : "";
                    mxgraph.insertEdge(parent, null, label, src, dst);
                }
            }
        } finally {
            mxgraph.getModel().endUpdate();
        }

        JFrame frame = new JFrame("Mapa del Almacén (Click en zona para ver productos)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mxGraphComponent graphComponent = new mxGraphComponent(mxgraph);
        frame.getContentPane().add(graphComponent);
        frame.setSize(900, 700);
        frame.setVisible(true);

        // --- Evento al hacer click en un nodo ---
        graphComponent.getGraphControl().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Object cell = graphComponent.getCellAt(e.getX(), e.getY());
                if (cell instanceof mxCell && ((mxCell) cell).isVertex()) {
                    String zona = (String) ((mxCell) cell).getValue();
                    BPlusTree<Integer, Producto> tree = productosPorZona.get(zona);
                    if (tree != null) {
                        // Visualiza el B+ tree de la zona
                        BPlusTreeVisualizer.visualize(tree, zona);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Esta zona no tiene productos.");
                    }
                }
            }
        });
    }
}
