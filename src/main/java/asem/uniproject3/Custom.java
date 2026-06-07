package asem.uniproject3;


public class Custom implements OperatorRules {

    private CursorArray<Operator> operator ;
    private CursorArray<Character> LanguageDomain;
    private int listOp;
    private int listDo;

    public Custom(CursorArray<Operator> operator,CursorArray<Character> languageDomain){
        this.operator = operator;
        this.LanguageDomain = languageDomain;
        listOp = operator.createList();
        listDo = languageDomain.createList();
    }
    public int getPriority(char c) {
        Operator temp = operator.getData(new Operator(c), listOp);
        if (temp != null)
            return temp.getPriority();
        return -1; // not valid operator

    }
    public boolean isOperator(char c)
    {
        return operator.find(new Operator(c), listOp);
    }
    public boolean fromDomain(char c){
        return LanguageDomain.find(c, listDo);
    }

    public CursorArray<Character> getLanguageDomain() {
        return LanguageDomain;
    }

    public int getListDo() {
        return listDo;
    }

    public int getListOp() {
        return listOp;
    }

    public CursorArray<Operator> getOperator() {
        return operator;
    }

    public void setOperator(CursorArray<Operator> operator) {
        this.operator = operator;
    }

    public void setLanguageDomain(CursorArray<Character> languageDomain) {
        LanguageDomain = languageDomain;
    }
}
