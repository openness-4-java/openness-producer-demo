package it.unimore.dipi.openness.producer.services;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import it.unimore.dipi.iot.openness.config.AuthorizedApplicationConfiguration;
import it.unimore.dipi.iot.openness.connector.EdgeApplicationAuthenticator;
import it.unimore.dipi.iot.openness.connector.EdgeApplicationConnector;
import it.unimore.dipi.iot.openness.dto.service.*;
import it.unimore.dipi.openness.producer.resources.EventResource;
import it.unimore.dipi.openness.producer.utils.DummyDataGenerator;
import it.unimore.dipi.openness.producer.utils.EventGenerationTask;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.*;

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

        String OPENNESS_CONTROLLER_BASE_AUTH_URL = "http://eaa.openness:7080/";
        String OPENNESS_CONTROLLER_BASE_APP_URL = "https://eaa.openness:7443/";
        String OPENNESS_CONTROLLER_BASE_APP_WS_URL = "wss://eaa.openness:7443/";
        EdgeApplicationAuthenticator eaa = new EdgeApplicationAuthenticator(OPENNESS_CONTROLLER_BASE_AUTH_URL);
        String applicationId = "opennessProducerDemo";
        String nameSpace = "producerdemo";
        String organizationName =  "DIPIUniMore";

        AuthorizedApplicationConfiguration authorizedApplicationConfiguration;
        EdgeApplicationAuthenticator edgeApplicationAuthenticator = new EdgeApplicationAuthenticator(OPENNESS_CONTROLLER_BASE_AUTH_URL);
        Optional<AuthorizedApplicationConfiguration> storedConfiguration = edgeApplicationAuthenticator.loadExistingAuthorizedApplicationConfiguration(applicationId, organizationName);
        if(storedConfiguration.isPresent()) {
            logger.info("AuthorizedApplicationConfiguration Loaded Correctly !");
            authorizedApplicationConfiguration = storedConfiguration.get();
        } else {
            logger.info("AuthorizedApplicationConfiguration Not Available ! Authenticating the app ...");
            authorizedApplicationConfiguration = edgeApplicationAuthenticator.authenticateApplication(nameSpace, applicationId, organizationName);
        }

        EdgeApplicationConnector edgeApplicationConnector = new EdgeApplicationConnector(OPENNESS_CONTROLLER_BASE_APP_URL, authorizedApplicationConfiguration, OPENNESS_CONTROLLER_BASE_APP_WS_URL);
        final List<EdgeApplicationServiceNotificationDescriptor> notifications = new ArrayList<>();
        final EdgeApplicationServiceNotificationDescriptor notificationDescriptor1 = new EdgeApplicationServiceNotificationDescriptor(
                "producer demo notification 1",
                "0.0.1",
                "producer demo description 1"
        );
        notifications.add(notificationDescriptor1);
        final EdgeApplicationServiceDescriptor service = new EdgeApplicationServiceDescriptor(
                new EdgeApplicationServiceUrn(applicationId, nameSpace),  // MUST BE AS DURING AUTHENTICATION
                "producer demo service",
                String.format("%s/%s", nameSpace, applicationId),  // MUST BE AS DURING AUTHENTICATION
                "test",
                notifications,
                new ServiceInfo("producer demo")
        );
        logger.info("Posting service: {}", service);
        edgeApplicationConnector.postService(service);

        logger.info("Getting services [#1]...");
        EdgeApplicationServiceList availableServiceList = edgeApplicationConnector.getAvailableServices();
        for(EdgeApplicationServiceDescriptor serviceDescriptor : availableServiceList.getServiceList()){
            logger.info("Service Info: {}", serviceDescriptor);
        }

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