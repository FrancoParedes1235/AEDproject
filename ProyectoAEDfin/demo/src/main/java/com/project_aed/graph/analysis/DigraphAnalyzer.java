package com.project_aed.graph.analysis;

// imports necesarios

import com.project_aed.graph.Edge;
import com.project_aed.graph.GraphLink;
import com.project_aed.graph.Vertex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilidades de reporte y análisis para grafos <b>dirigidos</b>.
 * Acceso solo por la API pública de los grafos.
 */
public final class DigraphAnalyzer {

    private DigraphAnalyzer() {
        // Clase utilitaria, no instanciable.
    }

    /** 8.a Representación formal de un grafo dirigido. */
    public static <E> String representacionFormal(GraphLink<E> g) {
        StringBuilder sb = new StringBuilder("Vértices: ");
        for (Vertex<E> v : g.getVertices()) {
            sb.append(v.getData()).append(' ');
        }

        sb.append("\nAristas dirigidas: ");
        for (Vertex<E> v : g.getVertices()) {
            for (Edge<E> e : v.getAdjList()) {
                sb.append('(')
                        .append(v.getData()).append(" -> ")
                        .append(e.getDest().getData())
                        .append(") ");
            }
        }
        return sb.toString();
    }

    /** 8.b Lista de adyacencia de un grafo dirigido. */
    public static <E> String listaAdyacencia(GraphLink<E> g) {
        StringBuilder sb = new StringBuilder();
        for (Vertex<E> v : g.getVertices()) {
            sb.append(v.getData()).append(" -> ");
            for (Edge<E> e : v.getAdjList()) {
                sb.append(e.getDest().getData()).append(", ");
            }
            if (!v.getAdjList().isEmpty())
                sb.setLength(sb.length() - 2); // Quita la última coma
            sb.append('\n');
        }
        return sb.toString();
    }

    /** 8.c Matriz de adyacencia 0-1 de un grafo dirigido. */
    public static <E> String matrizAdyacencia(GraphLink<E> g) {
        List<Vertex<E>> vertices = g.getVertices();
        int n = vertices.size();

        // Mapeo de dato a índice
        Map<E, Integer> idx = new HashMap<>();
        for (int i = 0; i < n; i++) {
            idx.put(vertices.get(i).getData(), i);
        }

        int[][] m = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (Edge<E> e : vertices.get(i).getAdjList()) {
                Integer j = idx.get(e.getDest().getData());
                if (j != null)
                    m[i][j] = 1;
            }
        }

        StringBuilder sb = new StringBuilder("     ");
        for (Vertex<E> v : vertices)
            sb.append(v.getData()).append(' ');
        sb.append('\n');
        for (int i = 0; i < n; i++) {
            sb.append(vertices.get(i).getData()).append(" : ");
            for (int j = 0; j < n; j++) {
                sb.append(m[i][j]).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
