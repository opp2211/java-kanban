package historymanager;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Node prev;
        Node next;
        Task data;

        public Node(Task data, Node prev, Node next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

    }
    private final Map<Integer, Node> nodeMap = new HashMap<>();
    Node head;
    Node tail;

    private void linkLast(Task task) {
        Node node = new Node(task, tail, null);
        if (tail == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
        nodeMap.put(task.getId(), node);
    }

    private void removeNode(Node node) {
        if (node == null)
            return;
        if (node == head) {
            if (node != tail)
                node.next.prev = null;
            else
                tail = null;
            head = node.next;
        } else if (node == tail) {
            node.prev.next = null;
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
            node.prev.next = node.next;
        }
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);
        removeNode(node);
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
    }
    @Override
    public void clear() {
        nodeMap.clear();
        head = null;
        tail = null;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> list = new ArrayList<>();
        Node node = head;
        while (node != null){
            list.add(node.data);
            node = node.next;
        }
        return list;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryHistoryManager that = (InMemoryHistoryManager) o;
        return nodeMap.equals(that.nodeMap) && Objects.equals(head, that.head) && Objects.equals(tail, that.tail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeMap, head, tail);
    }
}
