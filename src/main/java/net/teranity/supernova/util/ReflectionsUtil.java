package net.teranity.supernova.util;

import net.teranity.supernova.dependecyinjection.annotations.DependencyInjection;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

@DependencyInjection
public class ReflectionsUtil {

    public Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forJavaClassPath())
            .setScanners(Scanners.TypesAnnotated)
    );
}
