package com.project_aed.visual;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.project_aed.btreeplus.BPlusTree;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class BPlusTreeVisualizer {

    // NUEVO: visualización con nombre de la zona
    public static <K extends Comparable<K>, V> void visualize(BPlusTree<K, V> tree, String zona) {
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        Map<Object, Object> nodeMap = new HashMap<>();
        // NUEVO: Mapeo de celda mxCell a nodo real del árbol
        Map<Object, BPlusTree.Node<K, V>> cellToNodeMap = new HashMap<>();
        int[] y = { 40 };

        graph.getModel().beginUpdate();
        try {
            drawNode(graph, parent, tree, tree.getRoot(), 400, y, 0, nodeMap, cellToNodeMap);
        } finally {
            graph.getModel().endUpdate();
        }

        JFrame frame = new JFrame("Productos en " + zona + " (Árbol B+)");
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        frame.getContentPane().add(graphComponent);
        frame.setSize(1000, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        // === Evento click en celda/nodo del árbol ===
        graphComponent.getGraphControl().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Object cell = graphComponent.getCellAt(e.getX(), e.getY());
                if (cell != null && cellToNodeMap.containsKey(cell)) {
                    @SuppressWarnings("unchecked")
                    BPlusTree.Node<K, V> nodo = cellToNodeMap.get(cell);
                    if (nodo.isLeaf) {
                        StringBuilder sb = new StringBuilder("Productos en esta hoja:\n\n");
                        for (V producto : nodo.values)
                            sb.append(producto).append("\n");
                        JOptionPane.showMessageDialog(frame,
                                sb.length() > 23 ? sb.toString() : "No hay productos en esta hoja.");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Nodo interno: " + nodo.keys);
                    }
                }
            }
        });
    }

    // Modificado: Recibe también cellToNodeMap para guardar la relación celda-nodo
    private static <K extends Comparable<K>, V> void drawNode(mxGraph graph, Object parent,
            BPlusTree<K, V> tree, Object nodeObj, int x, int[] y, int depth,
            Map<Object, Object> nodeMap, Map<Object, BPlusTree.Node<K, V>> cellToNodeMap) {

        if (nodeObj == null)
            return;
        @SuppressWarnings("unchecked")
        BPlusTree.Node<K, V> node = (BPlusTree.Node<K, V>) nodeObj;

        String label = node.keys.toString() + (node.isLeaf ? " (Hoja)" : "");
        Object graphNode = graph.insertVertex(parent, null, label, x, y[0], 120, 30);
        nodeMap.put(node, graphNode);
        cellToNodeMap.put(graphNode, node); // Mapea la celda (mxCell) al nodo real

        int childCount = node.isLeaf ? 0 : node.children.size();
        int width = 140 * Math.max(childCount - 1, 1);
        int childX = x - width / 2;

        if (!node.isLeaf) {
            y[0] += 80; // siguiente nivel
            for (Object childObj : node.children) {
                drawNode(graph, parent, tree, childObj, childX, y, depth + 1, nodeMap, cellToNodeMap);
                Object childGraphNode = nodeMap.get(childObj);
                graph.insertEdge(parent, null, "", graphNode, childGraphNode);
                childX += 140;
            }
            y[0] -= 80; // sube para hermanos
        }
    }
}
