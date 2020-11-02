package it.unimore.dipi.openness.producer.utils;

import it.unimore.dipi.iot.openness.connector.EdgeApplicationConnector;
import it.unimore.dipi.iot.openness.dto.service.NotificationFromProducer;
import it.unimore.dipi.openness.producer.model.EventDescriptor;
import it.unimore.dipi.openness.producer.services.AppConfig;
import it.unimore.dipi.openness.producer.services.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class EventGenerationTask extends TimerTask {

    final protected Logger logger = LoggerFactory.getLogger(AppService.class);

    private AppConfig appConfig;
    private final EdgeApplicationConnector eac;
    private final String notificationName;
    private final String notificationVersion;

    public EventGenerationTask(AppConfig appConfig, final EdgeApplicationConnector edgeApplicationConnector, String notificationName, String notificationVersion) {
            this.appConfig = appConfig;
            this.eac = edgeApplicationConnector;
            this.notificationName = notificationName;
            this.notificationVersion = notificationVersion;
        }

    @Override
    public void run() {
        try {

            logger.info("Event Timer Task ! ....");
            EventDescriptor eventDescriptor = DummyDataGenerator.generateDummyEventData(this.appConfig.getTisDataManager());
            logger.info("New Event Generated -> {}", eventDescriptor);

            logger.info("Posting event to notification service...");
            final NotificationFromProducer notification = new NotificationFromProducer(
                    this.notificationName,
                    this.notificationVersion,
                    eventDescriptor
                    );
            eac.postNotification(notification);
            logger.info("Posting event to notification service...\n\tnotification -> {}", notification);

        } catch (Exception e) {
            e.printStackTrace();
            cancel();
        }
    }
}