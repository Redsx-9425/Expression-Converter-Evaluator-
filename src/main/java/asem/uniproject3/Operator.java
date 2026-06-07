package asem.uniproject3;

public class Operator implements Comparable<Operator>{
    private char symbol;
    private int priority;

    public Operator(char symbol,int priority){
        this.symbol = symbol;
        this.priority = priority;
    }
    public Operator(char symbol){
        this.symbol = symbol;
    }

    public int getPriority() {
        return priority;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    @Override
    public int compareTo(Operator o) {

        if (this.symbol == o.symbol) return 0;

        int diff = this.priority - o.priority;
        if (diff != 0) return diff;
        return Character.compare(this.symbol,o.symbol);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Operator){
            Operator temp = (Operator)obj;
            return temp.symbol == this.symbol;
        }
        return false;
    }
}
