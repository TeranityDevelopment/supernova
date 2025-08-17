package supernova.util;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class ReflectionsUtil {

    public final static Reflections reflections = new Reflections(
            new ConfigurationBuilder()
                    .setScanners(Scanners.TypesAnnotated, Scanners.MethodsAnnotated, Scanners.SubTypes)
                    .setUrls(ClasspathHelper.forJavaClassPath())
    );
}
