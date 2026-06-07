package asem.uniproject3;

public interface OperatorRules {
    boolean isOperator(char c);
    int getPriority(char c);
    boolean fromDomain(char c);
}
