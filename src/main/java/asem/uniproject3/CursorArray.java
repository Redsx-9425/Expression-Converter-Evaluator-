package asem.uniproject3;

public class CursorArray<T extends Comparable<T>> {
    CNode<T>[] ca;

    public CursorArray(int capacity) {
        ca = new CNode[capacity];
        for (int i = 0; i < capacity - 1; i++)
            ca[i] = new CNode<>(null, i + 1);
        ca[capacity - 1] = new CNode<>(null, 0);
    }

    public int malloc() {
        int p = ca[0].getNext();
        if (p == 0) return 0;
        ca[0].setNext(ca[p].getNext());
        return p;
    }

    public void free(int p) {
        ca[p].setNext(ca[0].getNext());
        ca[0].setNext(p);
    }

    public int createList() {
        int l = malloc();
        if (l != 0) {
            ca[l] = new CNode<>(null, 0);
            return l;
        }
        return 0;
    }

    public void insertFirst(int l, T data) {
        int p = malloc();
        if (p == 0) return;
        ca[p] = new CNode<>(data, ca[l].getNext());
        ca[l].setNext(p);
    }

    public void insertSorted(int l, T data) {
        int p = malloc();
        if (p == 0) return;
        int prev = l;
        int curr = ca[l].getNext();
        while (curr != 0 && ca[curr].getData().compareTo(data) < 0) {
            prev = curr;
            curr = ca[curr].getNext();
        }
        ca[p] = new CNode<>(data, curr);
        ca[prev].setNext(p);
    }

    public void insertLast(int l, T data) {
        if (l == 0 || ca[l] == null) return;
        int temp = l;
        if (ca[temp] == null) return;
        int p = malloc();
        if (p == 0) return;
        while (ca[temp].getNext() != 0) {
            temp = ca[temp].getNext();
        }
        ca[p] = new CNode<>(data, 0);
        ca[temp].setNext(p);
    }

    public void traverse(int l) {
        int temp = ca[l].getNext();

        while (temp != 0) {
            System.out.print(ca[temp] + "-->");
            temp = ca[temp].getNext();
        }
    }

    public boolean find(T data, int l) {
        if (l == 0) return false;
        int curr = ca[l].getNext();
        while (curr != 0) {
            if (ca[curr].getData().compareTo(data) == 0) return true;
            curr = ca[curr].getNext();
        }
        return false;
    }
    public T getData(T data, int l) {
        if (l == 0) return null;
        int curr = ca[l].getNext();
        while (curr != 0) {
            if (ca[curr].getData().compareTo(data) == 0) return ca[curr].getData();
            curr = ca[curr].getNext();
        }
        return null;
    }


    public int findPrev(T data, int l) {
        if (l == 0) return -1;
        int prev = l;
        int curr = ca[l].getNext();
        while (curr != 0) {
            if (ca[curr].getData().compareTo(data) == 0) return prev;
            prev = curr;
            curr = ca[curr].getNext();
        }
        return -1;
    }

    public boolean isEmpty() {
        // free list empty?
        return ca[0].getNext() == 0;
    }

    public T deleteFirst(int l){
        if (l == 0) return null;
        int first = ca[l].getNext();
        if (first != 0) {
            T data = ca[first].getData();
            ca[l].setNext(ca[first].getNext());
            free(first);
            return data;
        }
        return null;
    }

    public void clear(int l){
        if (l == 0) return;
        int head = ca[l].getNext();
        if (head == 0) return; // already empty
        int tail = head;
        while (ca[tail].getNext() != 0) {
            tail = ca[tail].getNext();
        }
        // attach this list to free list
        ca[tail].setNext(ca[0].getNext());
        ca[0].setNext(head);
        ca[l].setNext(0);
    }

    public T getFirst(int l){
        if (l == 0) return null;
        int first = ca[l].getNext();
        if (first == 0) return null;
        return ca[first].getData();
    }
    public boolean isEmpty(int l){
        return ca[l].getNext() == 0;
    }

    public int getNext(int p) {
        if (p < 0 || p >= ca.length || ca[p] == null) return 0;
        return ca[p].getNext();
    }

    public T getNodeData(int p) {
        if (p < 0 || p >= ca.length || ca[p] == null) return null;
        return ca[p].getData();
    }
}
