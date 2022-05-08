import error.ConsoleReporter;
import lexer.Lexer;
import lexer.Token;
import parser.Expr;
import parser.Parser;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;

public record Interpreter(BufferedReader br, ConsoleReporter reporter) {

    public static String interpret(BufferedReader br, ConsoleReporter reporter) {
        Interpreter interpreter = new Interpreter(br, reporter);
        return interpreter.interpret();
    }

    private String interpret() {
        List<Token> tokens = Lexer.scan(br, reporter);
        if (tokens == null) return null;
        List<Expr> expressions = Parser.parse(tokens, reporter);
        if (expressions == null) return null;
        return fillText(expressions);
    }

    private String fillText(List<Expr> expressions) {
        StringBuilder text = new StringBuilder();
        for (Expr value : expressions) {
            text.append(getString(value)).append('\n');
        }
        return text.toString();
    }

    private StringBuilder getString(Expr expr) {
        StringBuilder text = new StringBuilder();
        switch (expr) {
            case Expr.Line n -> {
                return text.append('"').append(n.line()).append('"');
            }
            case Expr.Number n -> {
                return text.append(n.number());
            }
            case Expr.Array n -> {
                text.append("[");
                for (Expr value : n.list()) {
                    text.append(getString(value)).append(", ");
                }
                text.deleteCharAt(text.lastIndexOf(",")).deleteCharAt(text.lastIndexOf(" "));
                return text.append("]");
            }
            case Expr.Dictionary n -> {
                text.append("{");
                for (Map.Entry<Expr, Expr> entry : n.dictionary().entrySet()) {
                    text.append(getString(entry.getKey()));
                    text.append(": ");
                    text.append(getString(entry.getValue()));
                    text.append(", ");
                }
                text.deleteCharAt(text.lastIndexOf(",")).deleteCharAt(text.lastIndexOf(" "));
                return text.append("}");
            }
        }
        return text;
    }
}
