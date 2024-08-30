package com.github.coderodde.util;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.0.0 (Aug 30, 2024)
 * @since 1.0.0 (Aug 30, 2024)
 */
public class IntTreeSortV2 {

    public static void sort(int[] array) {
        sort(array, 0, array.length);
    }

    public static void sort(int[] array, int fromIndex, int toIndex) {
        if (toIndex - fromIndex < 2) {
            return;
        }

        new IntTreeSortV2(array, fromIndex, toIndex).sort();
    }

    private final int[] array;
    private final int fromIndex;
    private final int toIndex;
    private final HashTableTreeNode[] table;
    private final int mask;
    private TreeNode root;

    private IntTreeSortV2(int[] array, int fromIndex, int toIndex) {
        this.array     = array;
        this.fromIndex = fromIndex;
        this.toIndex   = toIndex;
        int capacity   = computeCapacity(toIndex - fromIndex);
        this.table     = new HashTableTreeNode[capacity];
        this.mask      = capacity - 1;
    }

    private static int computeCapacity(int length) {
        int ret = 1;

        while (ret < length) {
            ret *= 2;
        }

        return ret;
    }

    private static final class TreeNode {
        int key;
        int count;
        int height;
        TreeNode left;
        TreeNode right;
        TreeNode parent;

        TreeNode(int key) {
            this.key = key;
            this.count = 1;
        }
    }

    private static final class HashTableTreeNode {
        int key;
        int height;
        TreeNode treeNode;
        HashTableTreeNode left;
        HashTableTreeNode right;
        HashTableTreeNode parent;

        HashTableTreeNode(int key, TreeNode treeNode) {
            this.key = key;
            this.treeNode = treeNode;
        }
    }

    private static int height(TreeNode node) {
        return node == null ? -1 : node.height;
    }
    
    private static int height(HashTableTreeNode node) {
        return node == null ? -1 : node.height;
    }

    private int hashTableIndex(int element) {
        return element & mask;
    }

    private TreeNode findTreeNode(int element, int elementHash) {
        HashTableTreeNode node = table[elementHash];
        
        while (node != null) {
            if (node.key == element) {
                return node.treeNode;
            }
            
            if (element < node.key) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        
        return null;
    }

    private void sort() {
        int initialKey = array[fromIndex];
        root = new TreeNode(initialKey);
        table[hashTableIndex(initialKey)] = new HashTableTreeNode(initialKey, 
                                                                  root);

        for (int i = fromIndex + 1; i < toIndex; ++i) {
            int currentElement = array[i];
            int currentElementHash = hashTableIndex(currentElement);

            TreeNode treeNode = findTreeNode(currentElement, 
                                             currentElementHash);

            if (treeNode != null) {
                treeNode.count++;
            } else {
                TreeNode newnode = addTreeNode(currentElement);
                
                HashTableTreeNode newEntry =
                        new HashTableTreeNode(
                                currentElement, 
                                newnode);
                
                addHashTable(newEntry);
            }
        }

        TreeNode node = minimum(root);
        int index = fromIndex;

        while (node != null) {
            int key = node.key;
            int count = node.count;

            for (int i = 0; i < count; ++i) {
                array[index++] = key;
            }

            node = successor(node);
        }
    }

    private TreeNode minimum(TreeNode node) {
        while (node.left != null) {
            node = node.left;
        }

        return node;
    }

    private TreeNode successor(TreeNode node) {
        if (node.right != null) {
            return minimum(node.right);
        }

        TreeNode parent = node.parent;

        while (parent != null && parent.right == node) {
            node = parent;
            parent = parent.parent;
        }

        return parent;
    }

        private TreeNode addTreeNode(int key) {
        TreeNode parent = null;
        TreeNode node = root;

        while (node != null) {
            if (key < node.key) {
                parent = node;
                node = node.left;
            } else if (key > node.key) {
                parent = node;
                node = node.right;
            } else {
                break;
            }
        }

        TreeNode newnode = new TreeNode(key);

        if (key < parent.key) {
            parent.left = newnode;
        } else {
            parent.right = newnode;
        }

        newnode.parent = parent;
        fixAfterInsertion(newnode);
        return newnode;
    }
    
    private void addHashTable(HashTableTreeNode newNode) {
        int key = newNode.key;
        int hash = hashTableIndex(key);
        HashTableTreeNode parent = null;
        HashTableTreeNode node = table[hash];
        
        if (node == null) {
            table[hash] = newNode;
            return;
        }
        
        while (node != null) {
            if (key < node.key) {
                parent = node;
                node = node.left;
            } else if (node.key < key) {
                parent = node;
                node = node.right;
            } else {
                break;
            }
        }
        
        if (key < parent.key) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }
        
        newNode.parent = parent;
        fixAfterInsertion(newNode.parent);
    }
    
    private void fixAfterInsertion(TreeNode node) {
        TreeNode parent = node.parent;
        TreeNode grandParent;
        TreeNode subTree;

        while (parent != null) {
            if (height(parent.left) == height(parent.right) + 2) {
                grandParent = parent.parent;

                if (height(parent.left.left) >= height(parent.left.right)) {
                    subTree = rightRotate(parent);
                } else {
                    subTree = leftRightRotate(parent);
                }

                if (grandParent == null) {
                    root = subTree;
                } else if (grandParent.left == parent) {
                    grandParent.left = subTree;
                } else {
                    grandParent.right = subTree;
                }

                if (grandParent != null) {
                    grandParent.height = Math.max(
                            height(grandParent.left),
                            height(grandParent.right)) + 1;
                }

                return;
            } else if (height(parent.right) == height(parent.left) + 2) {
                grandParent = parent.parent;

                if (height(parent.right.right) >= height(parent.right.left)) {
                    subTree = leftRotate(parent);
                } else {
                    subTree = rightLeftRotate(parent);
                }

                if (grandParent == null) {
                    root = subTree;
                } else if (grandParent.left == parent) {
                    grandParent.left = subTree;
                } else {
                    grandParent.right = subTree;
                }

                if (grandParent != null) {
                    grandParent.height =
                            Math.max(height(grandParent.left),
                                     height(grandParent.right)) + 1;
                }

                return;
            }

            parent.height = Math.max(height(parent.left), 
                                     height(parent.right)) + 1;
            parent = parent.parent;
        }
        
        
    }
    
    private void fixAfterInsertion(HashTableTreeNode node) {
        HashTableTreeNode parent = node.parent;
        HashTableTreeNode grandParent;
        HashTableTreeNode subTree;

        while (parent != null) {
            if (height(parent.left) == height(parent.right) + 2) {
                grandParent = parent.parent;

                if (height(parent.left.left) >= height(parent.left.right)) {
                    subTree = rightRotate(parent);
                } else {
                    subTree = leftRightRotate(parent);
                }

                if (grandParent == null) {
                    table[hashTableIndex(node.key)] = subTree;
                } else if (grandParent.left == parent) {
                    grandParent.left = subTree;
                } else {
                    grandParent.right = subTree;
                }

                if (grandParent != null) {
                    grandParent.height = Math.max(
                            height(grandParent.left),
                            height(grandParent.right)) + 1;
                }

                return;
            } else if (height(parent.right) == height(parent.left) + 2) {
                grandParent = parent.parent;

                if (height(parent.right.right) >= height(parent.right.left)) {
                    subTree = leftRotate(parent);
                } else {
                    subTree = rightLeftRotate(parent);
                }

                if (grandParent == null) {
                    table[hashTableIndex(node.key)] = subTree;
                } else if (grandParent.left == parent) {
                    grandParent.left = subTree;
                } else {
                    grandParent.right = subTree;
                }

                if (grandParent != null) {
                    grandParent.height =
                            Math.max(height(grandParent.left),
                                     height(grandParent.right)) + 1;
                }

                return;
            }

            parent.height = Math.max(height(parent.left), 
                                     height(parent.right)) + 1;
            parent = parent.parent;
        }
    }

    private TreeNode leftRotate(TreeNode node1) {
        TreeNode node2 = node1.right;
        node2.parent = node1.parent;
        node1.parent = node2;
        node1.right = node2.left;
        node2.left = node1;

        if (node1.right != null) {
            node1.right.parent = node1;
        }

        node1.height = Math.max(height(node1.left), height(node1.right)) + 1;
        node2.height = Math.max(height(node2.left), height(node2.right)) + 1;
        return node2;
    }

    private TreeNode rightRotate(TreeNode node1) {
        TreeNode node2 = node1.left;
        node2.parent = node1.parent;
        node1.parent = node2;
        node1.left = node2.right;
        node2.right = node1;

        if (node1.left != null) {
            node1.left.parent = node1;
        }

        node1.height = Math.max(height(node1.left), height(node1.right)) + 1;
        node2.height = Math.max(height(node2.left), height(node2.right)) + 1;
        return node2;
    }

    private TreeNode rightLeftRotate(TreeNode node1) {
        TreeNode node2 = node1.right;
        node1.right = rightRotate(node2);
        return leftRotate(node1);
    }

    private TreeNode leftRightRotate(TreeNode node1) {
        TreeNode node2 = node1.left;
        node1.left = leftRotate(node2);
        return rightRotate(node1);
    }

    private HashTableTreeNode leftRotate(HashTableTreeNode node1) {
        HashTableTreeNode node2 = node1.right;
        node2.parent = node1.parent;
        node1.parent = node2;
        node1.right = node2.left;
        node2.left = node1;

        if (node1.right != null) {
            node1.right.parent = node1;
        }

        node1.height = Math.max(height(node1.left), height(node1.right)) + 1;
        node2.height = Math.max(height(node2.left), height(node2.right)) + 1;
        return node2;
    }

    private HashTableTreeNode rightRotate(HashTableTreeNode node1) {
        HashTableTreeNode node2 = node1.left;
        node2.parent = node1.parent;
        node1.parent = node2;
        node1.left = node2.right;
        node2.right = node1;

        if (node1.left != null) {
            node1.left.parent = node1;
        }

        node1.height = Math.max(height(node1.left), height(node1.right)) + 1;
        node2.height = Math.max(height(node2.left), height(node2.right)) + 1;
        return node2;
    }

    private HashTableTreeNode rightLeftRotate(HashTableTreeNode node1) {
        HashTableTreeNode node2 = node1.right;
        node1.right = rightRotate(node2);
        return leftRotate(node1);
    }

    private HashTableTreeNode leftRightRotate(HashTableTreeNode node1) {
        HashTableTreeNode node2 = node1.left;
        node1.left = leftRotate(node2);
        return rightRotate(node1);
    }
}
