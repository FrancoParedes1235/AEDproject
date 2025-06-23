package com.project_aed.graph;

import com.project_aed.graph.analysis.GraphAnalyzer;
import com.project_aed.graph.analysis.DigraphAnalyzer;

import java.util.List;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.project_aed.graph.GraphLink;
import com.project_aed.graph.Vertex;
import com.project_aed.graph.Edge;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class GraphStressTest {
    public static void main(String[] args) {
        // Grafo NO dirigido: prueba propiedades y operaciones masivas
        GraphLink<Integer> g = new GraphLink<>(false);

        // Inserta 20 nodos
        for (int i = 1; i <= 20; i++)
            g.addVertex(i);

        // Conecta nodos en cadena (camino largo)
        for (int i = 1; i < 20; i++)
            g.addEdge(i, i + 1);

        // Hazlo ciclo
        g.addEdge(20, 1);

        // Agrega aristas extras para crear múltiples ciclos y ramas
        g.addEdge(1, 5);
        g.addEdge(5, 10);
        g.addEdge(2, 8);
        g.addEdge(8, 15);
        g.addEdge(10, 15);

        // Inserta aristas con peso
        g.addEdge(3, 17, 42);
        g.addEdge(17, 11, 5);

        System.out.println("==== LISTA DE ADYACENCIA (NO dirigido) ====");
        System.out.println(GraphAnalyzer.listaAdyacencia(g));

        System.out.println("==== MATRIZ DE ADYACENCIA ====");
        System.out.println(GraphAnalyzer.matrizAdyacencia(g));

        System.out.println("==== REPRESENTACIÓN FORMAL ====");
        System.out.println(GraphAnalyzer.representacionFormal(g));

        // Prueba de grados
        System.out.println("Grado de 10: " + GraphAnalyzer.grado(g, 10));
        System.out.println("Grado de 1: " + GraphAnalyzer.grado(g, 1));
        System.out.println("Grado de 20: " + GraphAnalyzer.grado(g, 20));

        // Propiedades estructurales
        System.out.println("¿Es ciclo? " + GraphAnalyzer.esCiclo(g));
        System.out.println("¿Es camino? " + GraphAnalyzer.esCamino(g));
        System.out.println("¿Es rueda? " + GraphAnalyzer.esRueda(g));
        System.out.println("¿Es completo? " + GraphAnalyzer.esCompleto(g));
        System.out.println("¿Es conexo? " + g.isConnected());

        // Recorridos
        System.out.print("DFS desde 1: ");
        g.dfs(1);
        System.out.print("BFS desde 1: ");
        g.bfs(1);

        // Prueba de camino más corto (de 1 a 15)
        List<Integer> path = g.shortestPath(1, 15);
        System.out.println("Camino más corto de 1 a 15: " + path);

        // Elimina nodos y aristas
        System.out.println("\n== Elimina nodo 10 y arista 1-5 ==");
        g.removeVertex(10);
        g.removeEdge(1, 5);

        System.out.println(GraphAnalyzer.listaAdyacencia(g));
        System.out.println("¿Es conexo? " + g.isConnected());

        // Grafo dirigido para probar DigraphAnalyzer
        GraphLink<String> dg = new GraphLink<>(true);
        for (char c = 'A'; c <= 'H'; c++)
            dg.addVertex(String.valueOf(c));
        dg.addEdge("A", "B");
        dg.addEdge("A", "C");
        dg.addEdge("B", "D");
        dg.addEdge("C", "D");
        dg.addEdge("D", "E");
        dg.addEdge("E", "F");
        dg.addEdge("F", "G");
        dg.addEdge("G", "H");
        dg.addEdge("H", "A"); // ciclo dirigido

        System.out.println("==== DIGRAPH: Lista de adyacencia ====");
        System.out.println(DigraphAnalyzer.listaAdyacencia(dg));
        System.out.println("==== DIGRAPH: Matriz de adyacencia ====");
        System.out.println(DigraphAnalyzer.matrizAdyacencia(dg));
        System.out.println("==== DIGRAPH: Representación formal ====");
        System.out.println(DigraphAnalyzer.representacionFormal(dg));
        System.out.print("DIGRAPH DFS desde A: ");
        dg.dfs("A");
        System.out.print("DIGRAPH BFS desde A: ");
        dg.bfs("A");
        List<String> dpath = dg.shortestPath("A", "F");
        System.out.println("DIGRAPH Camino más corto de A a F: " + dpath);

        // Elimina vértice y muestra cambios
        dg.removeVertex("D");
        System.out.println("DIGRAPH Lista de adyacencia tras eliminar D:\n" + DigraphAnalyzer.listaAdyacencia(dg));

        // Visualiza el grafo NO dirigido
        visualizeGraph(g, false);

        // Visualiza el grafo dirigido
        visualizeGraph(dg, true);
    }

    public static <E> void visualizeGraph(GraphLink<E> graph, boolean directed) {
        mxGraph mxgraph = new mxGraph();
        Object parent = mxgraph.getDefaultParent();

        // Para posicionar los nodos sin que se encimen
        int x = 20, y = 20, dx = 60, dy = 60, col = 0, row = 0;
        int maxCols = (int) Math.ceil(Math.sqrt(graph.getVertices().size()));

        mxgraph.getModel().beginUpdate();
        try {
            // Mapear Vertex -> mxCell
            Map<E, Object> vertexMap = new HashMap<>();
            for (Vertex<E> v : graph.getVertices()) {
                // Calcula posición tipo "grilla"
                if (col >= maxCols) {
                    col = 0;
                    row++;
                }
                Object cell = mxgraph.insertVertex(parent, null, v.getData().toString(), x + col * dx, y + row * dy, 40,
                        40);
                vertexMap.put(v.getData(), cell);
                col++;
            }
            // Añadir las aristas
            for (Vertex<E> v : graph.getVertices()) {
                for (Edge<E> e : v.getAdjList()) {
                    Object src = vertexMap.get(v.getData());
                    Object dst = vertexMap.get(e.getDest().getData());
                    // Para no dirigir aristas duplicadas en no dirigidos
                    if (!directed && v.getData().toString().compareTo(e.getDest().getData().toString()) > 0)
                        continue;
                    String label = e.getWeight() != null ? e.getWeight().toString() : "";
                    mxgraph.insertEdge(parent, null, label, src, dst);
                }
            }
        } finally {
            mxgraph.getModel().endUpdate();
        }

        // Muestra en una ventana
        JFrame frame = new JFrame(directed ? "Grafo Dirigido" : "Grafo NO Dirigido");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mxGraphComponent graphComponent = new mxGraphComponent(mxgraph);
        frame.getContentPane().add(graphComponent);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
