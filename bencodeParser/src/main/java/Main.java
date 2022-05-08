import error.*;
import lexer.Lexer;
import lexer.Token;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String Text = "d i123e 3:sdf i13e" +
                "i45ee 4:5493s";

        BufferedReader br = new BufferedReader(new StringReader(Text));
        ConsoleReporter reporter = new ConsoleReporter();

        String result = Interpreter.interpret(br, reporter);
        if (result == null) {
            System.out.println(reporter);
            return;
        }
        System.out.println(result);
    }
}
