package printer;

import parser.Expr;

import java.util.*;
import java.util.stream.Collectors;

public class JsonPrinter {
    List<Expr> expressions;
    int numberDictionaries;

    private JsonPrinter(List<Expr> expressions) {
        this.expressions = expressions;
    }

    public static String print(List<Expr> expressions) {
        JsonPrinter jsonPrinter = new JsonPrinter(expressions);
        return jsonPrinter.print();
    }

    private String print() {
        return expressions.stream().map(this::toJson).collect(Collectors.joining("\n"));
    }

    private String toJson(Expr expr) {
        return switch (expr) {
            case Expr.Line n -> getLine(n);
            case Expr.Number n -> getNumber(n);
            case Expr.Array n -> getArray(n);
            case Expr.Dictionary n -> getDictionary(n);
        };
    }

    private String getLine(Expr.Line line) {
        return '"' + line.value() + '"';
    }

    private String getNumber(Expr.Number line) {
        return line.value().toString();
    }

    private String getArray(Expr.Array line) {
        return  "["
                + line.value().stream().map(this::toJson).collect(Collectors.joining(", "))
                + "]";
    }

    private String getDictionary(Expr.Dictionary dictionary) {
        StringJoiner str = new StringJoiner("\n");

        if (numberDictionaries > 0) str.add("");
        str.add(" ".repeat(numberDictionaries) + "{");
        numberDictionaries++;

        str.add(dictionary.value().keySet().stream()
                .map(key -> addMapEntry(dictionary.value(), key)).collect(Collectors.joining(",\n")));
        numberDictionaries--;
        str.add(" ".repeat(numberDictionaries) + "}");

        return str.toString();
    }

    private String addMapEntry(Map<String, Expr> map, String key) {
        return " ".repeat(numberDictionaries)
                + "\"" + key + "\""
                + ": " + toJson(map.get(key));
    }
}
