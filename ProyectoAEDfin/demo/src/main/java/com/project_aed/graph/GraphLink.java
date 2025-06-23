package com.project_aed.graph;

// imports necesarios

import java.util.*;

/**
 * Grafo genérico con listas de adyacencia.
 * Si es dirigido, las aristas solo van de origen a destino; si no, se agregan
 * en ambos sentidos.
 *
 * @param <E> Tipo de dato almacenado en cada vértice.
 */
public class GraphLink<E> {

    // Indica si el grafo es dirigido.
    private final boolean directed;
    // Lista de vértices (usamos LinkedList por eficiencia en inserciones/borrados).
    private final LinkedList<Vertex<E>> vertices = new LinkedList<>();

    /** Constructor: crea grafo dirigido o no dirigido. */
    public GraphLink(boolean directed) {
        this.directed = directed;
    }

    /** Añade un vértice al grafo si no existe ya. */
    public void addVertex(E data) {
        Vertex<E> v = new Vertex<>(data);
        if (!vertices.contains(v)) // usa equals de Vertex
            vertices.addLast(v);
    }

    /** Busca un vértice por su dato. Devuelve null si no existe. */
    public Vertex<E> find(E data) {
        for (Vertex<E> v : vertices)
            if (Objects.equals(v.getData(), data)) // object tiene su excepcion para null
                return v;
        return null;
    }

    /**
     * Añade una arista (con peso opcional) entre dos datos.
     * Si es dirigido, solo añade de 'from' a 'to'.
     * Si es no dirigido, añade también de 'to' a 'from'.
     * No hace nada si algún vértice no existe.
     */
    public void addEdge(E from, E to, Integer w) {
        Vertex<E> v1 = find(from), v2 = find(to);
        if (v1 == null || v2 == null)
            return;
        v1.getAdjList().addLast(new Edge<>(v2, w));
        if (!directed)
            v2.getAdjList().addLast(new Edge<>(v1, w));
    }

    /** Añade una arista sin peso (peso nulo) entre dos datos. */
    public void addEdge(E from, E to) {
        addEdge(from, to, null);
    }

    /** Busca si existe un vértice con el dato dado. */
    public boolean searchVertex(E data) {
        return find(data) != null;
    }

    /** Busca si existe una arista entre dos datos (de 'from' a 'to'). */
    public boolean searchEdge(E from, E to) {
        Vertex<E> v1 = find(from), v2 = find(to);
        if (v1 == null || v2 == null)
            return false;
        for (Edge<E> e : v1.getAdjList())
            if (e.getDest().equals(v2))
                return true;
        return false;
    }

    /**
     * Elimina la arista entre 'from' y 'to'.
     * Si es no dirigido, elimina en ambos sentidos.
     */
    public void removeEdge(E from, E to) {
        Vertex<E> v1 = find(from), v2 = find(to);
        if (v1 == null || v2 == null)
            return;
        v1.getAdjList().remove(new Edge<>(v2, null));
        if (!directed)
            v2.getAdjList().remove(new Edge<>(v1, null));
    }

    /** Elimina un vértice y todas las aristas que lo conectan. */
    public void removeVertex(E data) {
        Vertex<E> v = find(data);
        if (v == null)
            return;
        vertices.remove(v);
        // Elimina aristas que apuntan a este vértice en todos los demás vértices
        for (Vertex<E> u : vertices)
            u.getAdjList().removeIf(e -> e.getDest().equals(v));
    }

    /* ===================== Recorridos ===================== */

    /**
     * Recorrido en profundidad (DFS) desde un dato inicial. Imprime los nodos
     * visitados.
     */
    public void dfs(E start) {
        Vertex<E> s = find(start);
        if (s == null)
            return;

        Deque<Vertex<E>> stack = new ArrayDeque<>(); // pila para DFS
        stack.push(s);

        while (!stack.isEmpty()) {
            Vertex<E> v = stack.pop();
            if (!v.visited) {
                v.visited = true;
                System.out.print(v.getData() + " ");
                for (Edge<E> e : v.getAdjList())
                    if (!e.getDest().visited)
                        stack.push(e.getDest());
            }
        }
        resetVisited();
        System.out.println();
    }

    /**
     * Recorrido en anchura (BFS) desde un dato inicial. Imprime los nodos
     * visitados.
     */
    public void bfs(E start) {
        Vertex<E> s = find(start);
        if (s == null)
            return;

        Deque<Vertex<E>> queue = new ArrayDeque<>();
        queue.addLast(s);
        s.visited = true;

        while (!queue.isEmpty()) {
            Vertex<E> v = queue.pollFirst();
            System.out.print(v.getData() + " ");
            for (Edge<E> e : v.getAdjList())
                if (!e.getDest().visited) {
                    e.getDest().visited = true;
                    queue.addLast(e.getDest());
                }
        }
        resetVisited();
        System.out.println();
    }

    /* ===================== Algoritmo de Dijkstra ===================== */

    /**
     * Devuelve el camino más corto (como lista de datos) entre 'from' y 'to'
     * usando el algoritmo de Dijkstra (asume aristas de peso 1 si no tienen peso).
     * Si no existe camino, devuelve lista vacía.
     */
    public List<E> shortestPath(E from, E to) {
        Vertex<E> s = find(from), t = find(to);
        if (s == null || t == null)
            return Collections.emptyList(); // no existe camino

        Map<Vertex<E>, Integer> dist = new HashMap<>();
        Map<Vertex<E>, Vertex<E>> prev = new HashMap<>();
        PriorityQueue<Vertex<E>> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));

        // Inicialización de distancias
        for (Vertex<E> v : vertices)
            dist.put(v, Integer.MAX_VALUE);
        dist.put(s, 0);
        pq.add(s);

        while (!pq.isEmpty()) {
            Vertex<E> v = pq.poll();
            for (Edge<E> e : v.getAdjList()) {
                int w = (e.getWeight() == null ? 1 : e.getWeight());
                int nd = dist.get(v) + w;
                if (nd < dist.get(e.getDest())) {
                    dist.put(e.getDest(), nd);
                    prev.put(e.getDest(), v);
                    pq.remove(e.getDest()); // Actualiza prioridad
                    pq.add(e.getDest());
                }
            }
        }

        // Reconstrucción del camino
        LinkedList<E> path = new LinkedList<>();
        for (Vertex<E> x = t; x != null; x = prev.get(x))
            path.addFirst(x.getData());
        // Si el inicio no es alcanzable, el camino no inicia con 'from'
        if (path.isEmpty() || !path.getFirst().equals(from))
            return Collections.emptyList();
        return path;
    }

    /* ===================== Conectividad ===================== */

    /**
     * Verifica si el grafo es conexo (solo válido para no dirigidos).
     * Realiza un BFS y revisa si todos los vértices fueron visitados.
     */
    public boolean isConnected() {
        if (vertices.isEmpty())
            return true;
        // Realiza BFS desde el primer vértice
        Vertex<E> first = vertices.getFirst();
        bfs(first.getData());
        for (Vertex<E> v : vertices)
            if (!v.visited) {
                resetVisited();
                return false;
            }
        resetVisited();
        return true;
    }

    /* ===================== Utilidades ===================== */

    /** Devuelve la lista de vértices del grafo. */
    public List<Vertex<E>> getVertices() {
        return vertices;
    }

    /** Reinicia la marca de 'visitado' de todos los vértices. */
    private void resetVisited() {
        for (Vertex<E> v : vertices)
            v.visited = false;
    }

    @Override
    public String toString() {
        return vertices.toString();
    }
}
