import error.*;
import interpreter.Interpreter;
import lexer.*;
import parser.*;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static String interpret(BufferedReader br, int limitErrorMessages) {
        Reporter reporter = new Reporter(limitErrorMessages);
        List<Token> tokens = Lexer.scan(br, reporter);
        if (tokens == null) {
            System.err.println("Errors found: " + reporter.getNumberErrors() + "\nLimit error messages: " + limitErrorMessages);
            return null;
        }
        List<Expr> expressions = Parser.parse(tokens, reporter);

        if (expressions != null) return Interpreter.interpret(expressions);

        System.err.println("Errors found: " + reporter.getNumberErrors() + "\nLimit error messages: " + limitErrorMessages);
        return null;
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0 || args.length > 2) {
            System.err.println("""
                Bad input
                First argument: path to torrent file
                Second argument(optional): path to json file
                If second argument missing, create default out.json
                """);
            return;
        }

        try (BufferedReader in = new BufferedReader(new FileReader(args[0]));
             FileWriter out = args.length > 1 ? new FileWriter(args[1]) : null)
        {
            String str = interpret(in, 10);

            if (str == null) return;

            if (args.length == 1) System.out.println(str);
            else out.write(str);
        }
    }
}
