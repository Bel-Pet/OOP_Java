package error;

import java.util.ArrayList;
import java.util.List;

public class Reporter implements ErrorReporter {

    private final int maxMessages;
    private final List<String> buffer = new ArrayList<>();
    private int nMessages;

    public Reporter(int maxMessages) {
        this.maxMessages = maxMessages;
    }

    @Override
    public boolean report(String message) {
        buffer.add(message);
        nMessages++;
        return nMessages < maxMessages;
    }

    public List<String> getReporters() {
        return buffer;
    }
}