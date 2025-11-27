package supernova.util;

public class PrintViolationHandler extends ViolationHandler<String> {

    private String format = "Error: %s";

    @Override
    public void handle(Violation<String> violation) {
        final String message = String.format(format, violation.value());
        System.out.println(message);
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
