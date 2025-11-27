package supernova.util;

import java.util.HashMap;
import java.util.Map;

public class ViolationHandlers {

    private static final Map<Class<?>, ViolationHandler<?>> HANDLERS = new HashMap<>();

    static {
        HANDLERS.put(String.class, new PrintViolationHandler());
        HANDLERS.put(Exception.class, new ExceptionViolationHandler());
        HANDLERS.put(Throwable.class, new ThrowableViolationHandler());
    }

    public static void addHandler(Class<?> classType, ViolationHandler<?> violationHandler) {
        if (HANDLERS.containsKey(classType)) return;

        HANDLERS.put(classType, violationHandler);
    }

    public static void removeHandler(Class<?> classType, ViolationHandler<?> violationHandler) {
        if (!HANDLERS.containsKey(classType)) return;
        if (!HANDLERS.get(classType).equals(violationHandler)) return;

        HANDLERS.remove(classType, violationHandler);
    }

    public static ViolationHandler<?> getViolationHandler(Class<?> violationClass) {
        ViolationHandler<?> handler = HANDLERS.get(violationClass);

        if (handler != null) {
            return handler;
        }

        if (Exception.class.isAssignableFrom(violationClass)) {
            return HANDLERS.get(Exception.class);
        }

        if (Throwable.class.isAssignableFrom(violationClass)) {
            return HANDLERS.get(Throwable.class);
        }

        return null;
    }
}
