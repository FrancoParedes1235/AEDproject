package com.project_aed.graph;

// imports necesarios

/**
 * Clase que representa una arista de un grafo, con destino y peso opcional.
 *
 * @param <E> Tipo de dato almacenado en los vértices.
 */
public class Edge<E> {
    private final Vertex<E> dest;
    private final Integer weight;

    public Edge(Vertex<E> dest, Integer weight) {
        this.dest = dest;
        this.weight = weight;
    }

    public Edge(Vertex<E> dest) {
        this(dest, null);
    }

    /** Retorna el vértice de destino de la arista. */
    public Vertex<E> getDest() {
        return dest;
    }

    /** Retorna el peso de la arista, o null si no tiene peso. */
    public Integer getWeight() {
        return weight;
    }

    /** Dos aristas son iguales si apuntan al mismo destino. */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Edge<?> e))
            return false;
        return dest.equals(e.dest);
    }

    @Override
    public int hashCode() {
        return dest.hashCode();
    }

    /** Representación como texto de la arista. */
    @Override
    public String toString() {
        return weight == null ? dest.getData().toString()
                : dest.getData() + " [" + weight + "]";
    }
}
