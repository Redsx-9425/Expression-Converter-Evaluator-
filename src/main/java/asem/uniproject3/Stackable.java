package asem.uniproject3;

public interface Stackable<T extends Comparable<T>> {
    void push(T data);
    T pop();
    T peek();
    boolean isEmpty();
    void clear();
}
