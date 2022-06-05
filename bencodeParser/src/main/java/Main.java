import error.*;
import interpreter.Interpreter;
import lexer.Lexer;
import lexer.Token;
import parser.Expr;
import parser.Parser;

import java.io.*;
import java.util.List;

public class Main {

    public static String interpret(BufferedReader br, int limitErrorMessages) {
        Reporter reporter = new Reporter(limitErrorMessages);
        List<Token> tokens = Lexer.scan(br, reporter);
        List<Expr> expressions = Parser.parse(tokens, reporter);

        if (expressions != null) return Interpreter.interpret(expressions);

        System.out.println("Errors found: " + reporter.getNumberErrors() + "\nLimit error messages: " + limitErrorMessages);
        return null;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader("src/main/resources/bencode.torrent"));

        Writer out = new FileWriter("src/main/resources/out.json");

        String str = interpret(input, 10);
        if (str != null) out.write(str);//System.out.println(str);

        out.close();
    }
}
