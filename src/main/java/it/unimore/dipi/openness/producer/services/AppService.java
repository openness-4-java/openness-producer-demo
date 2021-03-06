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
import it.unimore.dipi.iot.openness.exception.EdgeApplicationAuthenticatorException;
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

    private final String APPLICATION_ID = "opennessProducerDemoTraffic";
    private final String NAME_SPACE = "producerdemo";
    private final String ORG_NAME = "DIPIUniMore";
    public static final String NOTIFICATION_NAME = "producer demo notification traffic";
    public static final String NOTIFICATION_VERSION = "0.0.1";

    public static void main(String[] args) throws Exception{

        new AppService().run(new String[]{"server", args.length > 0 ? args[0] : "configuration.yml"});
    }

    public void run(AppConfig appConfig, Environment environment) throws Exception {

        //Create Demo Locations, Device and Users
        DummyDataGenerator.generateMultipleDummyEventData(appConfig.getTisDataManager(),10);

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

        final AuthorizedApplicationConfiguration authorizedApplicationConfiguration = handleAuth(appConfig);

        EdgeApplicationConnector edgeApplicationConnector = new EdgeApplicationConnector(appConfig.myApi, authorizedApplicationConfiguration, appConfig.myWs);
        final List<EdgeApplicationServiceNotificationDescriptor> notifications = new ArrayList<>();
        final EdgeApplicationServiceNotificationDescriptor notificationDescriptor1 = new EdgeApplicationServiceNotificationDescriptor(
                NOTIFICATION_NAME,
                NOTIFICATION_VERSION,
                "producer demo description traffic"
        );
        notifications.add(notificationDescriptor1);
        final EdgeApplicationServiceDescriptor service = new EdgeApplicationServiceDescriptor(
                new EdgeApplicationServiceUrn(APPLICATION_ID, NAME_SPACE),  // MUST BE AS DURING AUTHENTICATION
                "producer demo traffic service",
                String.format("%s", appConfig.endpoint),  // TODO: what about NAME_SPACE, APPLICATION_ID?
                "ready",
                notifications,
                "producer demo traffic service"
        );
        logger.info("Posting service: {}", service);
        edgeApplicationConnector.postService(service);

        startEventGeneratorPeriodicTask(appConfig, edgeApplicationConnector, NOTIFICATION_NAME, NOTIFICATION_VERSION);

        logger.info("Getting services...");
        EdgeApplicationServiceList availableServiceList = edgeApplicationConnector.getAvailableServices();
        for(EdgeApplicationServiceDescriptor serviceDescriptor : availableServiceList.getServiceList()){
            logger.info("Service Info: {}", serviceDescriptor);
        }

    }

    private AuthorizedApplicationConfiguration handleAuth(final AppConfig appConfig) throws EdgeApplicationAuthenticatorException {
        final AuthorizedApplicationConfiguration authorizedApplicationConfiguration;
        final EdgeApplicationAuthenticator edgeApplicationAuthenticator = new EdgeApplicationAuthenticator(appConfig.myAuth);
        final Optional<AuthorizedApplicationConfiguration> storedConfiguration = edgeApplicationAuthenticator.loadExistingAuthorizedApplicationConfiguration(APPLICATION_ID, ORG_NAME);
        if(storedConfiguration.isPresent()) {
            logger.info("AuthorizedApplicationConfiguration Loaded Correctly !");
            authorizedApplicationConfiguration = storedConfiguration.get();
        } else {
            logger.info("AuthorizedApplicationConfiguration Not Available ! Authenticating the app ...");
            authorizedApplicationConfiguration = edgeApplicationAuthenticator.authenticateApplication(NAME_SPACE, APPLICATION_ID, ORG_NAME);
        }
        return authorizedApplicationConfiguration;
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

    private void startEventGeneratorPeriodicTask(AppConfig appConfig, final EdgeApplicationConnector edgeApplicationConnector, final String notificationName, final String notificationVersion){
        try{
            this.eventTimer = new Timer();
            this.eventTimer.schedule(new EventGenerationTask(appConfig, edgeApplicationConnector, notificationName, notificationVersion), 10000, 10000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}