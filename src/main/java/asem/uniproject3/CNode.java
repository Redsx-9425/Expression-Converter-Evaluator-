package asem.uniproject3;

public class CNode <A extends Comparable<A>>{
    private  A data;
    private int next;
    public CNode(A data ,int next){
        this.data = data;
        this.next = next;
    }

    @Override
    public String toString() {
        return "" + data;
    }

    public void setData(A data) {
        this.data = data;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public A getData() {
        return data;
    }

    public int getNext() {
        return next;
    }
}
