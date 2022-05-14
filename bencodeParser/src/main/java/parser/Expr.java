package parser;

import java.util.List;
import java.util.Map;

public sealed interface Expr {

    record Line(String value) implements Expr {}

    record Number(Integer value) implements Expr {}

    record Array(List<Expr> value) implements Expr {}

    record Dictionary(Map<String, Expr> value) implements Expr {}
}
