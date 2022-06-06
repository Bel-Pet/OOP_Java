package error;

import lexer.Token;

import java.util.List;

public class Reporter implements ErrorReporter {

    private final int maxMessages;
    private int nMessages;

    public Reporter(int maxMessages) {
        this.maxMessages = maxMessages;
    }

    @Override
    public boolean report(String message) {
        nMessages++;
        System.err.println(message);
        return nMessages < maxMessages;
    }

    @Override
    public boolean hasErrors() {
        return nMessages > 0;
    }

    public int getNumberErrors() {
        return nMessages;
    }
}
