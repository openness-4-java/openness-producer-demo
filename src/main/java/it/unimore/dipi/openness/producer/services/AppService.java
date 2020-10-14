package it.unimore.dipi.openness.producer.services;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import it.unimore.dipi.openness.producer.resources.EventResource;
import it.unimore.dipi.openness.producer.utils.DummyDataGenerator;
import it.unimore.dipi.openness.producer.utils.EventGenerationTask;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
import java.util.Timer;

public class AppService extends Application<AppConfig> {

    final protected Logger logger = LoggerFactory.getLogger(AppService.class);

    private Timer eventTimer;

    public static void main(String[] args) throws Exception{

        new AppService().run(new String[]{"server", args.length > 0 ? args[0] : "configuration.yml"});
    }

    public void run(AppConfig appConfig, Environment environment) throws Exception {

        //Create Demo Locations, Device and Users
        DummyDataGenerator.generateMultipleDummyEventData(appConfig.getTisDataManager(),10);
        startEventGeneratorPeriodicTask(appConfig);

        //Add our defined resources
        environment.jersey().register(new EventResource(appConfig));

        // Enable CORS headers
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

    }

    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<AppConfig>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(AppConfig configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });
    }

    private void startEventGeneratorPeriodicTask(AppConfig appConfig){
        try{
            this.eventTimer = new Timer();
            this.eventTimer.schedule(new EventGenerationTask(appConfig), 10000, 10000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}