package asem.uniproject3;

public class Conventional implements OperatorRules {


    public int getPriority(char c) {
        if ( c == '+' || c == '-')
            return 1;
        else if (c == '*' || c == '/' || c =='%')
            return 2;
        else if (c == '^')
            return 3;
        else
            return -1;

    }

    @Override
    public boolean fromDomain(char c) {
        return Character.isDigit(c);
    }

    public boolean isOperator(char c) {
        return c == '+' || c == '%' || c == '^' || c == '-' || c == '/' || c == '*';
    }
}
