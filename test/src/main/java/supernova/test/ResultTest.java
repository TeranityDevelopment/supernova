package supernova.test;

import supernova.util.Result;

public class ResultTest {

    public static void main(String[] args) {
        String name = "Izhar";
        Result<String, String> result;
        if (name.equals("Izhar")) {
            result = Result.violation("Name is Izhar which is banned.");
        } else {
            result = Result.success(name);
        }

        if (result.isSucceed()) {
            System.out.println(result.getReference());
        } else {
            System.out.println(result.getViolation());
        }
    }
}
