package supernova.util;

public class ExceptionViolationHandler extends ViolationHandler<Exception>{
    @Override
    public void handle(Violation<Exception> violation) {
        try {
            throw violation.value();
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }
}
