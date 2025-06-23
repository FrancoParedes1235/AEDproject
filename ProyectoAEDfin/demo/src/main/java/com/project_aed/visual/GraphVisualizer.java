package com.project_aed.visual;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.project_aed.graph.GraphLink;
import com.project_aed.graph.Vertex;
import com.project_aed.graph.Edge;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class GraphVisualizer {
    public static <E> void visualizeGraph(GraphLink<E> graph, boolean directed) {
        mxGraph mxgraph = new mxGraph();
        Object parent = mxgraph.getDefaultParent();

        // Para organizar los nodos en cuadrícula
        int x = 40, y = 40, dx = 120, dy = 120, col = 0, row = 0;
        int maxCols = (int) Math.ceil(Math.sqrt(graph.getVertices().size()));

        mxgraph.getModel().beginUpdate();
        try {
            // Mapeo Vertex -> mxCell
            Map<E, Object> vertexMap = new HashMap<>();
            for (Vertex<E> v : graph.getVertices()) {
                if (col >= maxCols) {
                    col = 0;
                    row++;
                }
                Object cell = mxgraph.insertVertex(parent, null, v.getData().toString(),
                        x + col * dx, y + row * dy, 60, 40);
                vertexMap.put(v.getData(), cell);
                col++;
            }
            // Añadir las aristas
            for (Vertex<E> v : graph.getVertices()) {
                for (Edge<E> e : v.getAdjList()) {
                    Object src = vertexMap.get(v.getData());
                    Object dst = vertexMap.get(e.getDest().getData());
                    // Para no duplicar en no dirigidos
                    if (!directed && v.getData().toString().compareTo(e.getDest().getData().toString()) > 0)
                        continue;
                    String label = e.getWeight() != null ? e.getWeight().toString() : "";
                    mxgraph.insertEdge(parent, null, label, src, dst);
                }
            }
        } finally {
            mxgraph.getModel().endUpdate();
        }
        JFrame frame = new JFrame(directed ? "Grafo Dirigido" : "Grafo NO Dirigido");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mxGraphComponent graphComponent = new mxGraphComponent(mxgraph);
        frame.getContentPane().add(graphComponent);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
