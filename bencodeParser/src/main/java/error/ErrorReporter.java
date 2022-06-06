package error;

import lexer.Token;

import java.util.List;

public interface ErrorReporter {

    boolean report(String message);

    boolean hasErrors();
}