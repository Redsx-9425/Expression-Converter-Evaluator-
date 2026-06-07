package asem.uniproject3;

public class Converter {

    public String infixToPostfix(String exp, OperatorRules rule) {
        StringBuilder res = new StringBuilder();
        CStack<String> stack = new CStack<>();

        String isValid = validate(exp, rule, "infix");
        if (isValid != null) return null;

        int i = 0;
        int len = exp.length();

        while (i < len) {
            char c = exp.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (c == '(') {
                stack.push(String.valueOf(c));
                i++;
            } else if (c == ')') {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    res.append(stack.pop()).append(' ');
                }
                if (!stack.isEmpty()) {
                    stack.pop();
                }
                i++;
            } else if (rule.isOperator(c)) {
                while (!stack.isEmpty() && !stack.peek().equals("(")
                        && rule.getPriority(stack.peek().charAt(0)) >= rule.getPriority(c)) {
                    res.append(stack.pop()).append(' ');
                }
                stack.push(String.valueOf(c));
                i++;
            } else {
                StringBuilder operandBuffer = new StringBuilder();
                while (i < len && !Character.isWhitespace(exp.charAt(i))
                        && exp.charAt(i) != '(' && exp.charAt(i) != ')'
                        && !rule.isOperator(exp.charAt(i))) {
                    operandBuffer.append(exp.charAt(i));
                    i++;
                }
                res.append(operandBuffer.toString()).append(' ');
            }
        }

        while (!stack.isEmpty()) {
            res.append(stack.pop()).append(' ');
        }

        return res.toString().trim();
    }

    public String infixToPrefix(String exp, OperatorRules rule) {
        String isValid = validate(exp, rule, "infix");
        if (isValid != null) return null;

        CStack<String> tokenStack = new CStack<>();
        int i = 0;
        int len = exp.length();

        while (i < len) {
            char c = exp.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (c == '(') {
                tokenStack.push(")");
                i++;
            } else if (c == ')') {
                tokenStack.push("(");
                i++;
            } else if (rule.isOperator(c)) {
                tokenStack.push(String.valueOf(c));
                i++;
            } else {
                StringBuilder operandBuffer = new StringBuilder();
                while (i < len && !Character.isWhitespace(exp.charAt(i))
                        && exp.charAt(i) != '(' && exp.charAt(i) != ')'
                        && !rule.isOperator(exp.charAt(i))) {
                    operandBuffer.append(exp.charAt(i));
                    i++;
                }
                tokenStack.push(operandBuffer.toString());
            }
        }

        StringBuilder swapped = new StringBuilder();
        while (!tokenStack.isEmpty()) {
            swapped.append(tokenStack.pop()).append(" ");
        }

        String postfix = infixToPostfix(swapped.toString().trim(), rule);
        if (postfix == null) return null;

        String[] postfixTokens = postfix.trim().split("\\s+");
        StringBuilder prefix = new StringBuilder();
        for (int j = postfixTokens.length - 1; j >= 0; j--) {
            prefix.append(postfixTokens[j]).append(' ');
        }

        return prefix.toString().trim();
    }

    public String postfixToInfix(String exp, OperatorRules rule) {
        CStack<String> stack = new CStack<>();
        String isValid = validate(exp, rule, "postfix");
        if (isValid != null) return null;

        int i = 0;
        int len = exp.length();

        while (i < len) {
            char c = exp.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (rule.isOperator(c)) {
                String operand2 = stack.pop();
                String operand1 = stack.pop();
                stack.push("( " + operand1 + " " + c + " " + operand2 + " )");
                i++;
            } else {
                StringBuilder operandBuffer = new StringBuilder();
                while (i < len && !Character.isWhitespace(exp.charAt(i)) && !rule.isOperator(exp.charAt(i))) {
                    operandBuffer.append(exp.charAt(i));
                    i++;
                }
                stack.push(operandBuffer.toString());
            }
        }

        return stack.isEmpty() ? "" : stack.pop();
    }

    public String prefixToInfix(String exp, OperatorRules rule) {
        String isValid = validate(exp, rule, "prefix");
        if (isValid != null) return null;

        CStack<String> reversalStack = new CStack<>();
        int i = 0;
        int len = exp.length();

        while (i < len) {
            char c = exp.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (rule.isOperator(c)) {
                reversalStack.push(String.valueOf(c));
                i++;
            } else {
                StringBuilder operandBuffer = new StringBuilder();
                while (i < len && !Character.isWhitespace(exp.charAt(i)) && !rule.isOperator(exp.charAt(i))) {
                    operandBuffer.append(exp.charAt(i));
                    i++;
                }
                reversalStack.push(operandBuffer.toString());
            }
        }

        CStack<String> expressionStack = new CStack<>();
        while (!reversalStack.isEmpty()) {
            String curr = reversalStack.pop();

            if (curr.length() == 1 && rule.isOperator(curr.charAt(0))) {
                String operand1 = expressionStack.pop();
                String operand2 = expressionStack.pop();
                expressionStack.push("( " + operand1 + " " + curr + " " + operand2 + " )");
            } else {
                expressionStack.push(curr);
            }
        }

        return expressionStack.isEmpty() ? "" : expressionStack.pop();
    }

    public String postfixToPrefix(String exp, OperatorRules rule) {
        String infix = postfixToInfix(exp, rule);
        return infixToPrefix(infix, rule);
    }

    public double evaluatePostfix(String exp) {
        CStack<Double> stack = new CStack<>();
        String[] tokens = exp.trim().split("\\s+");

        for (String token : tokens) {
            if (token.isEmpty()) continue;

            char firstChar = token.charAt(0);
            if (Character.isDigit(firstChar) || (token.length() > 1 && firstChar == '-')) {
                stack.push(Double.parseDouble(token));
            } else {
                double operand2 = stack.pop();
                double operand1 = stack.pop();

                switch (token.charAt(0)) {
                    case '+': stack.push(operand1 + operand2); break;
                    case '-': stack.push(operand1 - operand2); break;
                    case '*': stack.push(operand1 * operand2); break;
                    case '/':
                        if (operand2 == 0) throw new ArithmeticException("Division by zero!");
                        stack.push(operand1 / operand2);
                        break;
                    case '^': stack.push(Math.pow(operand1, operand2)); break;
                    default: throw new IllegalArgumentException("Unknown evaluation token operator: " + token);
                }
            }
        }
        return stack.pop();
    }

    public String validate(String exp, OperatorRules rule, String type) {
        if (exp == null || exp.trim().isEmpty()) {
            return "Error: Expression cannot be empty";
        }

        int parenthesesBalance = 0;
        int operandCount = 0;
        int operatorCount = 0;

        String firstChar = null;
        String lastChar = "";

        int i = 0;
        int len = exp.length();

        while (i < len) {
            char ch = exp.charAt(i);

            if (Character.isWhitespace(ch)) {
                i++;
                continue;
            }

            String curr = String.valueOf(ch);

            if (curr.equals("(")) {
                if (!type.equalsIgnoreCase("infix")) return "Error: Parentheses only allowed in Infix";
                if (firstChar == null) firstChar = curr;
                parenthesesBalance++;

                if (!lastChar.isEmpty() && (!lastChar.equals("(") && (lastChar.length() != 1 || !rule.isOperator(lastChar.charAt(0))))) {
                    return "Error: Missing operator before opening parenthesis";
                }
                lastChar = curr;
                i++;
                continue;
            }

            if (curr.equals(")")) {
                if (!type.equalsIgnoreCase("infix")) return "Error: Parentheses only allowed in Infix";
                if (firstChar == null) firstChar = curr;
                parenthesesBalance--;
                if (parenthesesBalance < 0) return "Error: Mismatched parentheses";

                if (lastChar.equals("(") || (lastChar.length() == 1 && rule.isOperator(lastChar.charAt(0)))) {
                    return "Error: Invalid operator placement before closing parenthesis";
                }
                lastChar = curr;
                i++;
                continue;
            }

            if (rule.isOperator(ch)) {
                if (firstChar == null) firstChar = curr;
                operatorCount++;

                if (type.equalsIgnoreCase("infix")) {
                    if (lastChar.equals("(") || (lastChar.length() == 1 && rule.isOperator(lastChar.charAt(0)))) {
                        return "Error: Two operators cannot be adjacent in Infix notation";
                    }
                }
                lastChar = curr;
                i++;
                continue;
            }

            StringBuilder operandBuffer = new StringBuilder();
            while (i < len && !Character.isWhitespace(exp.charAt(i))
                    && exp.charAt(i) != '(' && exp.charAt(i) != ')'
                    && !rule.isOperator(exp.charAt(i))) {
                operandBuffer.append(exp.charAt(i));
                i++;
            }

            String token = operandBuffer.toString();
            char firstTokenChar = token.charAt(0);

            boolean isValidOperand = isNumeric(token) || (token.length() == 1 && rule.fromDomain(firstTokenChar));

            if (isValidOperand) {
                if (firstChar == null) firstChar = token;
                operandCount++;

                if (type.equalsIgnoreCase("infix")) {
                    if (!lastChar.isEmpty() && !lastChar.equals("(") && (lastChar.length() != 1 || !rule.isOperator(lastChar.charAt(0)))) {
                        return "Error: Two operands cannot be adjacent in Infix notation";
                    }
                }
                lastChar = token;
            } else {
                return "Error: Undefined operand or character token '" + token + "'";
            }
        }

        if (parenthesesBalance != 0) return "Error: Mismatched parentheses";
        if (operandCount != (operatorCount + 1)) return "Error: Missing an operator or operand";

        boolean firstIsOp = (firstChar != null && firstChar.length() == 1 && rule.isOperator(firstChar.charAt(0)));
        boolean lastIsOp = (lastChar.length() == 1 && rule.isOperator(lastChar.charAt(0)));

        if (type.equalsIgnoreCase("infix")) {
            if (firstIsOp || lastIsOp) return "Error: Infix expressions cannot start or end with an operator";
        } else if (type.equalsIgnoreCase("postfix")) {
            if (firstIsOp) return "Error: Postfix expressions cannot start with an operator";
            if (!lastIsOp) return "Error: Postfix expressions must end with an operator";
        } else if (type.equalsIgnoreCase("prefix")) {
            if (!firstIsOp) return "Error: Prefix expressions must start with an operator";
            if (lastIsOp) return "Error: Prefix expressions cannot end with an operator";
        }

        return null;
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}