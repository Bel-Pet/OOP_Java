import error.*;
import printer.JsonPrinter;
import lexer.Lexer;
import lexer.Token;
import parser.Expr;
import parser.Parser;

import java.io.*;
import java.util.List;

public class Main {

    public static String getJson(BufferedReader br, int limitErrorMessages) {
        Reporter reporter = new Reporter(limitErrorMessages);
        List<Token> tokens = Lexer.scan(br, reporter);
        if (tokens == null) {
            System.out.println("Errors found: " + reporter.getNumberErrors() + "\nLimit error messages: " + limitErrorMessages);
            return null;
        }
        List<Expr> expressions = Parser.parse(tokens, reporter);

        if (expressions != null) return JsonPrinter.print(expressions);

        System.out.println("Errors found: " + reporter.getNumberErrors() + "\nLimit error messages: " + limitErrorMessages);
        return null;
    }

    public static void main(String[] args) throws IOException {
        // CR: first main argument - input file, second - output (optional). for incorrect usage print usage message
        BufferedReader input = new BufferedReader(new FileReader("src/main/resources/bencode.torrent"));

        Writer out = new BufferedWriter(new FileWriter("src/main/resources/out.json"));

        String json = getJson(input, 10);
        if (json != null) out.write(json);//System.out.println(str);

        out.close();
    }
}
