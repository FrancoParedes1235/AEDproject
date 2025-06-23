package com.project_aed.graph.analysis;

// imports necesarios

import com.project_aed.graph.Edge;
import com.project_aed.graph.GraphLink;
import com.project_aed.graph.Vertex;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

/**
 * Utilidades de análisis y representación para grafos <b>no dirigidos</b>.
 * Acceso solo por la API pública de los grafos.
 */
public final class GraphAnalyzer {

    private GraphAnalyzer() {
        // Clase utilitaria, no instanciable.
    }

    /** 5.a Grado de un vértice. */
    public static <E> int grado(GraphLink<E> g, E nodo) {
        Vertex<E> v = g.find(nodo);
        return (v == null) ? -1 : v.getAdjList().size();
    }

    /** 5.b ¿Es camino simple lineal? */
    public static <E> boolean esCamino(GraphLink<E> g) {
        int extremos = 0;
        for (Vertex<E> v : g.getVertices()) {
            int d = v.getAdjList().size();
            if (d == 1)
                extremos++;
            else if (d != 2)
                return false;
        }
        return extremos == 2;
    }

    /** 5.c ¿Es ciclo simple? */
    public static <E> boolean esCiclo(GraphLink<E> g) {
        for (Vertex<E> v : g.getVertices()) {
            if (v.getAdjList().size() != 2)
                return false;
        }
        return true;
    }

    /** 5.d ¿Es rueda? */
    public static <E> boolean esRueda(GraphLink<E> g) {
        int n = g.getVertices().size();
        int centros = 0;
        for (Vertex<E> v : g.getVertices()) {
            int d = v.getAdjList().size();
            if (d == n - 1)
                centros++;
            else if (d != 3)
                return false;
        }
        return centros == 1;
    }

    /** 5.e ¿Es completo? */
    public static <E> boolean esCompleto(GraphLink<E> g) {
        int n = g.getVertices().size();
        for (Vertex<E> v : g.getVertices()) {
            if (v.getAdjList().size() != n - 1)
                return false;
        }
        return true;
    }

    /** 6.a Representación formal (sin repeticiones) */
    public static <E> String representacionFormal(GraphLink<E> g) {
        StringBuilder sb = new StringBuilder("Vértices: ");
        for (Vertex<E> v : g.getVertices()) {
            sb.append(v.getData()).append(' ');
        }

        sb.append("\nAristas: ");
        Set<String> aristas = new HashSet<>();
        for (Vertex<E> v : g.getVertices()) {
            for (Edge<E> e : v.getAdjList()) {
                String a1 = v.getData().toString();
                String a2 = e.getDest().getData().toString();
                // Ordenar los extremos para evitar duplicados
                aristas.add(a1.compareTo(a2) < 0 ? a1 + "-" + a2 : a2 + "-" + a1);
            }
        }
        for (String a : aristas) {
            sb.append(a).append(' ');
        }
        return sb.toString();
    }

    /** 6.b Lista de adyacencia. */
    public static <E> String listaAdyacencia(GraphLink<E> g) {
        StringBuilder sb = new StringBuilder();
        for (Vertex<E> v : g.getVertices()) {
            sb.append(v.getData()).append(" -> ");
            for (Edge<E> e : v.getAdjList()) {
                sb.append(e.getDest().getData()).append(", ");
            }
            if (!v.getAdjList().isEmpty())
                sb.setLength(sb.length() - 2);
            sb.append('\n');
        }
        return sb.toString();
    }

    /** 6.c Matriz de adyacencia 0-1. */
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

        StringBuilder sb = new StringBuilder("   ");
        for (Vertex<E> v : vertices)
            sb.append(v.getData()).append(' ');
        sb.append('\n');
        for (int i = 0; i < n; i++) {
            sb.append(vertices.get(i).getData()).append(": ");
            for (int j = 0; j < n; j++) {
                sb.append(m[i][j]).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
