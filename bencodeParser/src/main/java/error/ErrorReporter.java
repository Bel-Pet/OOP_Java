package error;

public interface ErrorReporter {
    ErrorReporter EMPTY = message -> true;
    
    boolean report(String message);
}