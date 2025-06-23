package com.project_aed.graph;

// imports necesarios

import java.util.LinkedList;
import java.util.Objects;

/**
 * Clase que representa un vértice genérico con su lista de adyacencia.
 *
 * @param <E> Tipo de dato almacenado en el vértice.
 */
public class Vertex<E> {
    private final E data;
    private final LinkedList<Edge<E>> adjList = new LinkedList<>();
    boolean visited = false;

    public Vertex(E data) {
        this.data = data;
    }

    /** Retorna el dato almacenado en el vértice. */
    public E getData() {
        return data;
    }

    /** Retorna la lista de aristas (adyacencia) del vértice. */
    public LinkedList<Edge<E>> getAdjList() {
        return adjList;
    }

    /** Indica si el vértice fue visitado (para recorridos). */
    public boolean isVisited() {
        return visited;
    }

    /** Marca el vértice como visitado o no. */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    /** Dos vértices son iguales si su dato es igual. */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Vertex<?> v))
            return false;
        return Objects.equals(data, v.data);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data);
    }

    /** Representación como texto del vértice y su lista de adyacencia. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(data).append(" -> ");
        for (Edge<E> e : adjList)
            sb.append(e.getDest().getData()).append(", ");
        if (!adjList.isEmpty())
            sb.setLength(sb.length() - 2); // Quita la última coma y espacio
        return sb.toString();
    }
}
