package com.project_aed.graph;

// imports necesarios
import com.project_aed.graph.analysis.GraphAnalyzer;
import com.project_aed.graph.analysis.DigraphAnalyzer;

import java.util.List;

public class GraphMain {
    public static void main(String[] args) {
        System.out.println("==== Grafo NO dirigido (sin pesos) ====");
        GraphLink<String> g = new GraphLink<>(false);

        // Insertar vértices
        g.addVertex("A");
        g.addVertex("B");
        g.addVertex("C");
        g.addVertex("D");
        g.addVertex("E");

        // Insertar aristas
        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "D");
        g.addEdge("C", "D");
        g.addEdge("D", "E");
        g.addEdge("E", "A");

        System.out.println("Lista de adyacencia:\n" + GraphAnalyzer.listaAdyacencia(g));
        System.out.println("Matriz de adyacencia:\n" + GraphAnalyzer.matrizAdyacencia(g));
        System.out.println("Representación formal:\n" + GraphAnalyzer.representacionFormal(g));
        System.out.println("Grado de D: " + GraphAnalyzer.grado(g, "D"));
        System.out.println("¿Es ciclo? " + GraphAnalyzer.esCiclo(g));
        System.out.println("¿Es camino? " + GraphAnalyzer.esCamino(g));
        System.out.println("¿Es rueda? " + GraphAnalyzer.esRueda(g));
        System.out.println("¿Es completo? " + GraphAnalyzer.esCompleto(g));
        System.out.println("¿Es conexo? " + g.isConnected());

        System.out.print("DFS desde A: ");
        g.dfs("A");
        System.out.print("BFS desde A: ");
        g.bfs("A");

        System.out.println("\nCaminos más cortos:");
        List<String> path = g.shortestPath("A", "E");
        System.out.println("De A a E: " + path);

        System.out.println("\n== Eliminando vértice D y arista A-E ==");
        g.removeVertex("D");
        g.removeEdge("A", "E");

        System.out.println("Lista de adyacencia tras borrar:\n" + GraphAnalyzer.listaAdyacencia(g));
        System.out.println("¿Es conexo? " + g.isConnected());

        // Probar autoloops y multiaristas
        g.addEdge("B", "B");
        g.addEdge("A", "B");
        g.addEdge("A", "B");
        System.out.println("Lista de adyacencia tras autoloops y multiaristas:\n" + GraphAnalyzer.listaAdyacencia(g));

        // Grafo vacío
        GraphLink<Integer> gEmpty = new GraphLink<>(false);
        System.out.println("Grafo vacío conectado? " + gEmpty.isConnected());

        System.out.println("\n==== Grafo DIRIGIDO (con pesos) ====");
        GraphLink<String> dg = new GraphLink<>(true);
        dg.addVertex("1");
        dg.addVertex("2");
        dg.addVertex("3");
        dg.addVertex("4");

        dg.addEdge("1", "2", 4);
        dg.addEdge("1", "3", 1);
        dg.addEdge("3", "2", 2);
        dg.addEdge("2", "4", 1);
        dg.addEdge("3", "4", 5);

        System.out.println("Lista de adyacencia:\n" + DigraphAnalyzer.listaAdyacencia(dg));
        System.out.println("Matriz de adyacencia:\n" + DigraphAnalyzer.matrizAdyacencia(dg));
        System.out.println("Representación formal:\n" + DigraphAnalyzer.representacionFormal(dg));

        System.out.print("DFS desde 1: ");
        dg.dfs("1");
        System.out.print("BFS desde 1: ");
        dg.bfs("1");

        List<String> dpath = dg.shortestPath("1", "4");
        System.out.println("Camino más corto de 1 a 4: " + dpath);

        // Prueba de arista inexistente y vértice inexistente
        System.out.println("¿Existe arista 1->5? " + dg.searchEdge("1", "5"));
        System.out.println("¿Existe vértice 'X'? " + dg.searchVertex("X"));

        // Eliminar nodo y probar de nuevo
        dg.removeVertex("2");
        System.out.println("Lista de adyacencia tras eliminar 2:\n" + DigraphAnalyzer.listaAdyacencia(dg));

        // Grafo con auto-loop dirigido
        dg.addEdge("4", "4", 7);
        System.out.println("Lista de adyacencia con autoloop:\n" + DigraphAnalyzer.listaAdyacencia(dg));
    }
}
