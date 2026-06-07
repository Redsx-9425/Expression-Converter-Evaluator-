package asem.uniproject3;

import java.util.EmptyStackException;

public class CStack <T extends Comparable<T>> implements Stackable<T>
{
    private CursorArray<T> ca ;
    private  int l;

    public CStack(){
        ca = new CursorArray<>(100);
        l = ca.createList();
    }
    public CStack(int capacity){
        if(capacity > 0) {
            ca = new CursorArray<>(capacity + 2);// +2 for the dummy heads
            l = ca.createList();
        }

    }


    @Override
    public void push(T data) {
        // always insert at list head
        ca.insertFirst(l, data);
    }

    @Override
    public T pop() {
        if (!isEmpty())
            return ca.deleteFirst(l);
        else
            throw new EmptyStackException();
    }

    @Override
    public T peek() {
        if (!isEmpty())
            return ca.getFirst(l);
        else
            throw  new EmptyStackException();
    }

    @Override
    public boolean isEmpty() {
        return ca.isEmpty(l);
    }

    @Override
    public void clear() {
        ca.clear(l);
    }
}
