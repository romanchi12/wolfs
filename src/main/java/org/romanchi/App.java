package org.romanchi;

import org.romanchi.services.ModelationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import javax.jws.WebParam;

@SpringBootApplication
public class App {
    public static void main( String[] args ) throws InterruptedException {
        SpringApplication application =
                new SpringApplicationBuilder(App.class).web(WebApplicationType.NONE).build(args);
        ApplicationContext context = application.run(args);
        ModelationService modelationService = context.getBean(ModelationService.class);

        modelationService.run();
    }
}
