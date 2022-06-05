package error;

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

    public int getNumberErrors() {
        return nMessages;
    }
}
