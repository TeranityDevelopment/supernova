package net.teranity.supernova.test;

import net.teranity.supernova.dependecyinjection.DependencyInjectionContainer;
import net.teranity.supernova.dependecyinjection.annotations.AutoInject;
import net.teranity.supernova.dependecyinjection.annotations.DependencyInjection;

public class DependencyInjectionTest {

    public static class Example {
        public String name = "John Doe";

        public Example() {
            DependencyInjectionContainer.initialize(Example.class, this);
        }
    }

    @DependencyInjection
    public static class ExampleAutoInject {

        @AutoInject
        private Example example;
    }

    public static void main(String[] args) {
        ExampleAutoInject exampleAutoInject = DependencyInjectionContainer.getBean(ExampleAutoInject.class);

        UserExample userExample;

        System.out.println(exampleAutoInject.example.name);
    }
}
