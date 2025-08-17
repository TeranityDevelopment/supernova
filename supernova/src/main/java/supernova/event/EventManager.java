package supernova.event;

import org.reflections.Reflections;
import supernova.util.ImmutableTripletObject;
import supernova.util.ReflectionsUtil;
import supernova.util.Triplet;
import supernova.util.concurrent.ConcurrentTriplet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.*;

public class EventManager {

    private static final ExecutorService executorService;
    private static final Triplet<Class<? extends EventBase>, Object, Method> listeners = new ConcurrentTriplet<>();

    static  {
        executorService = Executors.newFixedThreadPool(4);

        Reflections reflections = ReflectionsUtil.reflections;
        Set<Method> methods = reflections.getMethodsAnnotatedWith(EventListener.class);

        for (Method method : methods) {
            if (method.getParameterCount() == 1 &&
                    EventBase.class.isAssignableFrom(method.getParameterTypes()[0])) {

                @SuppressWarnings("unchecked")
                Class<? extends EventBase> eventType =
                        (Class<? extends EventBase>) method.getParameterTypes()[0];

                try {
                    Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
                    method.setAccessible(true);

                    listeners.put(eventType, instance, method);
                } catch (Exception e) {
                    throw new EventException(e.getMessage());
                }
            }
        }
    }

    public static <T extends EventBase> void fire(T event) {
        for (ImmutableTripletObject<Class<? extends EventBase>, Object, Method> listener : listeners.tripletObjectSet()) {
            Class<?> classType = listener.primaryKey();
            Object instance = listener.secondaryKey();
            Method method = listener.value();

            if (classType.equals(event.getClass())) {
                Runnable task = () -> {
                    try {
                        method.invoke(instance, event);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        throw new EventException(e.getMessage());
                    }
                };

                if (event.isAsync()) {
                    event.future = executorService.submit(task);
                } else {
                    task.run();
                }
            }
        }
    }

}
