package com.project_aed.btreeplus;

// imports necesarios

import java.util.*;

public class BPlusTree<K extends Comparable<K>, V> {
    public static class Node<K, V> {
        public boolean isLeaf;
        public List<K> keys;
        public List<Node<K, V>> children; // for internal nodes
        public List<V> values; // for leaf nodes
        public Node<K, V> next; // for leaf nodes

        Node(boolean isLeaf) {
            this.isLeaf = isLeaf;
            keys = new ArrayList<>();
            if (isLeaf) {
                values = new ArrayList<>();
            } else {
                children = new ArrayList<>();
            }
        }
    }

    private Node<K, V> root;
    private int order; // Max number of children per node

    public BPlusTree(int order) {
        if (order < 3)
            throw new IllegalArgumentException("Order must be at least 3");
        this.order = order;
        root = new Node<>(true);
    }

    public V search(K key) {
        Node<K, V> node = root;
        while (!node.isLeaf) {
            int idx = Collections.binarySearch(node.keys, key);
            int childIdx = idx >= 0 ? idx + 1 : -idx - 1;
            node = node.children.get(childIdx);
        }
        int idx = Collections.binarySearch(node.keys, key);
        if (idx >= 0)
            return node.values.get(idx);
        return null;
    }

    public void insert(K key, V value) {
        Node<K, V> r = root;
        if (r.keys.size() == order - 1) {
            Node<K, V> s = new Node<>(false);
            s.children.add(r);
            root = s;
            splitChild(s, 0);
            insertNonFull(s, key, value);
        } else {
            insertNonFull(r, key, value);
        }
    }

    private void insertNonFull(Node<K, V> node, K key, V value) {
        if (node.isLeaf) {
            int idx = Collections.binarySearch(node.keys, key);
            if (idx >= 0) {
                node.values.set(idx, value);
            } else {
                int insertIdx = -idx - 1;
                node.keys.add(insertIdx, key);
                node.values.add(insertIdx, value);
            }
        } else {
            int idx = Collections.binarySearch(node.keys, key);
            int childIdx = idx >= 0 ? idx + 1 : -idx - 1;
            Node<K, V> child = node.children.get(childIdx);
            if (child.keys.size() == order - 1) {
                splitChild(node, childIdx);
                if (key.compareTo(node.keys.get(childIdx)) > 0) {
                    childIdx++;
                }
            }
            insertNonFull(node.children.get(childIdx), key, value);
        }
    }

    private void splitChild(Node<K, V> parent, int idx) {
        Node<K, V> node = parent.children.get(idx);
        int mid = (order - 1) / 2;
        Node<K, V> sibling = new Node<>(node.isLeaf);

        if (node.isLeaf) {
            sibling.keys.addAll(node.keys.subList(mid, node.keys.size()));
            sibling.values.addAll(node.values.subList(mid, node.values.size()));
            node.keys.subList(mid, node.keys.size()).clear();
            node.values.subList(mid, node.values.size()).clear();

            sibling.next = node.next;
            node.next = sibling;

            parent.keys.add(idx, sibling.keys.get(0));
            parent.children.add(idx + 1, sibling);
        } else {
            sibling.keys.addAll(node.keys.subList(mid + 1, node.keys.size()));
            sibling.children.addAll(node.children.subList(mid + 1, node.children.size()));
            K upKey = node.keys.get(mid);

            node.keys.subList(mid, node.keys.size()).clear();
            node.children.subList(mid + 1, node.children.size()).clear();

            parent.keys.add(idx, upKey);
            parent.children.add(idx + 1, sibling);
        }
    }

    public void delete(K key) {
        delete(root, key);
        if (!root.isLeaf && root.keys.size() == 0) {
            root = root.children.get(0);
        }
    }

    private boolean delete(Node<K, V> node, K key) {
        if (node.isLeaf) {
            int idx = Collections.binarySearch(node.keys, key);
            if (idx >= 0) {
                node.keys.remove(idx);
                node.values.remove(idx);
                return true;
            }
            return false;
        } else {
            int idx = Collections.binarySearch(node.keys, key);
            int childIdx = idx >= 0 ? idx + 1 : -idx - 1;
            Node<K, V> child = node.children.get(childIdx);
            boolean deleted = delete(child, key);

            if (child.keys.size() < (order - 1) / 2) {
                rebalance(node, childIdx);
            }
            return deleted;
        }
    }

    private void rebalance(Node<K, V> parent, int idx) {
        Node<K, V> node = parent.children.get(idx);
        int minKeys = (order - 1) / 2;

        // Try borrow from left sibling
        if (idx > 0) {
            Node<K, V> left = parent.children.get(idx - 1);
            if (left.keys.size() > minKeys) {
                if (node.isLeaf) {
                    node.keys.add(0, left.keys.remove(left.keys.size() - 1));
                    node.values.add(0, left.values.remove(left.values.size() - 1));
                    parent.keys.set(idx - 1, node.keys.get(0));
                } else {
                    node.keys.add(0, parent.keys.get(idx - 1));
                    parent.keys.set(idx - 1, left.keys.remove(left.keys.size() - 1));
                    node.children.add(0, left.children.remove(left.children.size() - 1));
                }
                return;
            }
        }
        // Try borrow from right sibling
        if (idx < parent.children.size() - 1) {
            Node<K, V> right = parent.children.get(idx + 1);
            if (right.keys.size() > minKeys) {
                if (node.isLeaf) {
                    node.keys.add(right.keys.remove(0));
                    node.values.add(right.values.remove(0));
                    parent.keys.set(idx, right.keys.get(0));
                } else {
                    node.keys.add(parent.keys.get(idx));
                    parent.keys.set(idx, right.keys.remove(0));
                    node.children.add(right.children.remove(0));
                }
                return;
            }
        }
        // Merge with sibling
        if (idx > 0) {
            merge(parent, idx - 1);
        } else {
            merge(parent, idx);
        }
    }

    private void merge(Node<K, V> parent, int idx) {
        Node<K, V> left = parent.children.get(idx);
        Node<K, V> right = parent.children.get(idx + 1);

        if (left.isLeaf) {
            left.keys.addAll(right.keys);
            left.values.addAll(right.values);
            left.next = right.next;
            parent.keys.remove(idx);
            parent.children.remove(idx + 1);
        } else {
            left.keys.add(parent.keys.remove(idx));
            left.keys.addAll(right.keys);
            left.children.addAll(right.children);
            parent.children.remove(idx + 1);
        }
    }

    // For debugging: print the tree
    public void printTree() {
        Queue<Node<K, V>> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int sz = queue.size();
            for (int i = 0; i < sz; i++) {
                Node<K, V> node = queue.poll();
                System.out.print(node.keys + " ");
                if (!node.isLeaf)
                    queue.addAll(node.children);
            }
            System.out.println();
        }
    }

    // For debugging: print all leaves
    public void printLeaves() {
        Node<K, V> node = root;
        while (!node.isLeaf)
            node = node.children.get(0);
        while (node != null) {
            System.out.print(node.keys + " ");
            node = node.next;
        }
        System.out.println();
    }

    // Imprime el árbol B+ de manera estructurada (jerárquica)
    public void printTreeStructure() {
        printTreeStructure(root, 0, true);
    }

    private void printTreeStructure(Node<K, V> node, int nivel, boolean esUltimo) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < nivel; i++) {
            indent.append("   ");
        }
        if (nivel > 0) {
            indent.append(esUltimo ? "└── " : "├── ");
        }
        String tipo = node.isLeaf ? "[Hoja] " : "[Int]  ";
        System.out.println(indent + tipo + node.keys.toString());

        if (!node.isLeaf) {
            for (int i = 0; i < node.children.size(); i++) {
                printTreeStructure(node.children.get(i), nivel + 1, i == node.children.size() - 1);
            }
        }
    }

    // Dentro de BPlusTree...
    public Node<K, V> getRoot() {
        return root;
    }

    // Example usage
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese el orden del arbol B+: ");
        int order = sc.nextInt();
        BPlusTree<Integer, String> tree = new BPlusTree<>(order);

        tree.insert(10, "A");
        tree.insert(20, "B");
        tree.insert(5, "C");
        tree.insert(6, "D");
        tree.insert(12, "E");
        tree.insert(30, "F");
        tree.insert(7, "G");
        tree.insert(17, "H");

        tree.printTree();
        tree.printLeaves();

        System.out.println("Buscar 6: " + tree.search(6));
        tree.delete(6);
        tree.printTree();
        tree.printLeaves();
    }
}