package parser;

import java.util.HashMap;
import java.util.List;



public sealed interface Expr {

    record Line(String line) implements Expr {}

    record Number(String number) implements Expr {}

    record Array(List<Expr> list) implements Expr {}

    record Dictionary(HashMap<Expr, Expr> dictionary) implements Expr {}
}
