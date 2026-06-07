package asem.uniproject3;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class FileHandler {

    private int lastExpReaderSkipped = 0;

    public int getLastExpReaderSkipped() {
        return lastExpReaderSkipped;
    }

    public int operatorReader(File file, Custom custom) {
        int added = 0;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty() || line.toLowerCase().startsWith("operators")) continue;

                String[] parts = line.split("\\s+");
                if (parts.length == 0) continue;

                String last = parts[parts.length - 1];

                if (last.matches("\\d+")) {
                    int priority = Integer.parseInt(last);
                    for (int i = 0; i < parts.length - 1; i++) {
                        String token = parts[i];
                        if (token.isEmpty()) continue;

                        char symbol = token.charAt(0);
                        Operator o = new Operator(symbol, priority);
                        custom.getOperator().insertSorted(custom.getListOp(), o);
                        added++;
                    }
                    continue;
                }

                if (parts.length >= 2) {
                    for (int i = 0; i < parts.length - 1; i += 2) {
                        String tok = parts[i];
                        String pr = parts[i+1];
                        if (pr.matches("\\d+")) {
                            int priority = Integer.parseInt(pr);

                            char symbol = tok.charAt(0);
                            Operator o = new Operator(symbol, priority);
                            custom.getOperator().insertSorted(custom.getListOp(), o);
                            added++;
                        }
                    }
                } else {
                    String token = parts[0];
                    if (!token.matches("\\d+")) {
                        char symbol = token.charAt(0);
                        Operator o = new Operator(symbol, 1);
                        custom.getOperator().insertSorted(custom.getListOp(), o);
                        added++;
                    }
                }
            }
        } catch (IOException e) {
        }

        return added;
    }

    public boolean operatorWriter(File file, Custom custom) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("Operators Priority");

            CursorArray<Operator> opCursor = custom.getOperator();
            int headIndex = custom.getListOp();

            if (opCursor != null && headIndex != 0) {
                int curr = opCursor.getNext(headIndex);

                while (curr != 0) {
                    Operator op = opCursor.getNodeData(curr);
                    if (op != null) {
                        writer.println(op.getSymbol() + " " + op.getPriority());
                    }
                    curr = opCursor.getNext(curr);
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean domainWriter(File file, Custom custom) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {

            CursorArray<Character> domainCursor = custom.getLanguageDomain();
            int headIndex = custom.getListDo();

            if (domainCursor != null && headIndex != 0) {
                int curr = domainCursor.getNext(headIndex);

                while (curr != 0) {
                    Character word = domainCursor.getNodeData(curr);
                    if (word != null) {
                        writer.print(word + " ");
                    }
                    curr = domainCursor.getNext(curr);
                }
            }

            writer.println();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public int domainReader(File file, Custom custom) {
        int added = 0;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNext()) {
                String word = sc.next();
                if (word.isEmpty()) continue;

                Character ch = word.charAt(0);
                custom.getLanguageDomain().insertFirst(custom.getListDo(), ch);
                added++;
            }
        } catch (IOException e) {

        }
        return added;
    }

    public CStack<String> expReader(File file){
        CStack<String> res = new CStack<>(500);
        int skipped = 0;
        try (Scanner sc = new Scanner(file)) {
            System.out.println("Reading expressions from: " + file.getAbsolutePath());
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) { skipped++; continue; }

                String lower = line.toLowerCase();
                String type = null;
                String expr = line;
                if (lower.startsWith("infix:") || lower.startsWith("postfix:") || lower.startsWith("prefix:")) {
                    int idx = line.indexOf(':');
                    type = line.substring(0, idx).trim().toUpperCase();
                    expr = line.substring(idx + 1).trim();
                } else {
                    type = detectNotationType(line);
                }

                if (expr != null && !expr.isEmpty() && type != null) {
                    res.push(type + "|" + expr);
                } else {
                    skipped++;
                }
            }

        } catch (IOException e) {

        }
        lastExpReaderSkipped = skipped;
        return res;
    }

    private String detectNotationType(String line) {
        if (line == null) return null;
        String[] tokens = line.trim().split("\\s+");
        if (tokens.length == 0) return null;

        int firstIdx = 0;
        while (firstIdx < tokens.length && tokens[firstIdx].equals("(")) {
            firstIdx++;
        }

        int lastIdx = tokens.length - 1;
        while (lastIdx >= 0 && tokens[lastIdx].equals(")")) {
            lastIdx--;
        }

        if (firstIdx < tokens.length && isOperator(tokens[firstIdx])) return "PREFIX";
        if (lastIdx >= 0 && isOperator(tokens[lastIdx])) return "POSTFIX";

        boolean maybeInfix = true;
        for (int i = 0; i < tokens.length; i++) {
            boolean op = isOperator(tokens[i]);
            if (i % 2 == 0) {
                if (op) { maybeInfix = false; break; }
            } else {
                if (!op) { maybeInfix = false; break; }
            }
        }
        if (maybeInfix) return "INFIX";

        int opCount = 0, operandCount = 0;
        for (String t : tokens) {
            if (isOperator(t)) opCount++; else operandCount++;
        }
        if (operandCount >= opCount + 1) return "INFIX";

        return "POSTFIX";
    }

    private boolean isOperator(String s) {
        if (s == null || s.isEmpty()) return false;
        if ("+-*/%^$".contains(s)) return true;
        if (s.length() == 1 && !Character.isLetterOrDigit(s.charAt(0))) return true;
        return false;
    }
}