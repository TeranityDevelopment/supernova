package supernova.util;

public class ThrowableViolationHandler extends ViolationHandler<Throwable> {

    @Override
    public void handle(Violation<Throwable> violation) {
        try {
            throw violation.value();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
