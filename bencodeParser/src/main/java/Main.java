import error.*;
import lexer.Lexer;
import lexer.Token;
import parser.Expr;
import parser.Parser;

import java.io.*;
import java.util.List;

public class Main {

    public static String interpret(BufferedReader br, Reporter reporter) {
        List<Token> tokens = Lexer.scan(br, reporter);
        if (tokens == null) {
            for (String v: reporter.getReporters()) {
                System.out.println(v);
            }
            return null;
        }
        List<Expr> expressions = Parser.parse(tokens, reporter);
        if (expressions != null) {
            return Interpreter.interpret(expressions);
        }
        for (String v: reporter.getReporters()) {
            System.out.println(v);
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader("src/main/resources/bencode.torrent"));
        Reporter reporter = new Reporter(5);
        String str = interpret(input, reporter);
        if (str != null)
            System.out.println(str);
    }
}
