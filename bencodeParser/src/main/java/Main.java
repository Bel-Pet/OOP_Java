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

    public static void printer(String[] args) throws IOException {
        if (args.length == 0 || args.length > 2) {
            System.err.println("""
                Bad input
                First argument: path to torrent file
                Second argument(optional): path to json file
                If second argument missing, create default out.json
                """);
            return;
        }

        String path = "src/main/resources/";

        String str = interpret(new BufferedReader(new FileReader(path + args[0])), 10);
        if (str == null) return;

        Writer out = args.length > 1 ? new FileWriter(path + args[1]) : new FileWriter("src/main/resources/out.json");

        out.write(str);
        out.close();
    }

    public static void main(String[] args) throws IOException {
        String[] str = new String[2];
        str[0]= "bencode.torrent";
        str[1]= "f.json";
        System.out.println(str.length);
        Main.printer(str);
    }
}
