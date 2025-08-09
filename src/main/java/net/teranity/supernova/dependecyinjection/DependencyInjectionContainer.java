package net.teranity.supernova.dependecyinjection;

import net.teranity.supernova.dependecyinjection.annotations.AutoInject;
import net.teranity.supernova.dependecyinjection.annotations.DependencyInjection;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DependencyInjectionContainer {

    private static final Reflections reflections;
    private static final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

    static {
        reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath())
                .setScanners(Scanners.TypesAnnotated)
        );

        new DependencyInjectionContainer();
    }

    private DependencyInjectionContainer() {
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(DependencyInjection.class);
        for (Class<?> clazz : classes) {
            try {
                Object object = createInstance(clazz);
                instances.put(clazz, object);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        for (Class<?> clazz : classes) {
            Object instance = instances.get(clazz);
            if (instance == null) continue;

            try {
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(AutoInject.class)) {
                        Class<?> fieldType = field.getType();
                        Object dependency = getBean(fieldType);

                        if (dependency != null) {
                            field.setAccessible(true);
                            field.set(instance, dependency);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> T getBean(Class<T> clazz) {
        return clazz.cast(instances.get(clazz));
    }

    public static void initialize(Class<?> clazz, Object instance) {
        instances.put(clazz, instance);
        injectDependencies(instance);

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(DependencyInjection.class);
        for (Class<?> clazz1 : classes) {
            Object instance1 = instances.get(clazz1);
            if (instance1 == null) continue;

            try {
                for (Field field : clazz1.getDeclaredFields()) {
                    if (field.isAnnotationPresent(AutoInject.class)) {
                        Class<?> fieldType = field.getType();
                        Object dependency = getBean(fieldType);

                        if (dependency != null) {
                            field.setAccessible(true);
                            field.set(instance1, dependency);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Object createInstance(Class<?> clazz)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = clazz.getConstructors()[0];
        Class<?>[] paramTypes = constructor.getParameterTypes();

        Object[] params = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            params[i] = instances.get(paramTypes[i]);
        }

        return constructor.newInstance(params);
    }

    public static void injectDependencies(Object instance) {
        Class<?> clazz = instance.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(AutoInject.class)) {
                Class<?> dependencyType = field.getType();
                Object dependency = getBean(dependencyType);
                if (dependency != null) {
                    try {
                        field.setAccessible(true);
                        field.set(instance, dependency);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
