import parser.Expr;

import java.util.*;
import java.util.stream.Collectors;

public class Interpreter {

    StringBuilder text = new StringBuilder();
    List<Expr> expressions;
    int depth;

    private Interpreter(List<Expr> expressions) {
        this.expressions = expressions;
    }

    public static String interpret(List<Expr> expressions) {
        Interpreter interpreter = new Interpreter(expressions);
        return interpreter.interpret();
    }

    private String interpret() {
        int pos = 0;
        while (pos < expressions.size() - 1) {
            checkExpression(expressions.get(pos));
            text.append('\n');
            pos++;
        }
        checkExpression(expressions.get(pos));

        return text.toString();
    }

    private void checkExpression(Expr expr) {
        switch (expr) {
            case Expr.Line n -> getLine(n);
            case Expr.Number n -> getNumber(n);
            case Expr.Array n -> getArray(n);
            case Expr.Dictionary n -> getDictionary(n);
        };
    }

    private void getLine(Expr.Line line) {
        text.append('"').append(line.value()).append('"');
    }

    private void getNumber(Expr.Number line) {
        text.append(line.value().toString());
    }

    private void getArray(Expr.Array line) {
        int pos = 0;
        text.append('[');
        while (pos < line.value().size() - 1) {
            checkExpression(line.value().get(pos));
            text.append(", ");
            pos++;
        }
        checkExpression(line.value().get(pos));
        text.append("]");
    }

    private void getDictionary(Expr.Dictionary dictionary) {
        if (depth != 0) text.append('\n');

        String offset = " ".repeat(depth);
        text.append(offset).append("{");
        depth++;


        if (dictionary.value().entrySet().size() == 0) {
            depth--;
            text.append('\n').append(offset).append("}");
            return;
        }
        Iterator<Map.Entry<String, Expr>> iter = dictionary.value().entrySet().iterator();
        Map.Entry<String, Expr> entry = iter.next();


//        StringJoiner stringJoiner = new StringJoiner();
//        Collectors.joining()

        text.append("\n");
        text.append(offset).append(entry.getKey());
        text.append(": ");
        checkExpression(entry.getValue());

        while (iter.hasNext()) {
            text.append(",");
            entry = iter.next();
            text.append("\n");
            text.append(offset).append(entry.getKey());
            text.append(": ");
            checkExpression(entry.getValue());
        }
        
        /*for (Map.Entry<String, Expr> entry : dictionary.value().entrySet()) {
            text.append("\n");
            text.append(" ".repeat(numberDictionaries)).append(entry.getKey());
            text.append(": ");
            checkExpression(entry.getValue());
            text.append(",");
        }
        text.deleteCharAt(text.lastIndexOf(","));*/
        depth--;
        text.append('\n').append(offset).append("}");
    }
}
