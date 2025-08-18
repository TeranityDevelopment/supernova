package supernova.test;

import supernova.util.Results;

public class ResultsTest {

    public static void main(String[] args) {
        String a = "d";
        Results<String, RuntimeException> results = Results.fromValidator(a, RuntimeException.class)
                .notNull(new RuntimeException("String cannot be null."))
                .greaterThan("c", new RuntimeException(""))
                .toResults();

        if (results.hasViolations()) {
            results.throwEachViolations();
        } else {
            System.out.println(results.getReference());
        }
    }
}
