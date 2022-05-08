package parser;

import java.util.List;
import java.util.Map;


public sealed interface Expr {

    record Line(String line) implements Expr {}

    // CR: int number
    record Number(String number) implements Expr {}

    record Array(List<Expr> list) implements Expr {}

    // CR: key should be Line, not Expr
    record Dictionary(Map<Expr, Expr> dictionary) implements Expr {}
}
