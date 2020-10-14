package it.unimore.dipi.openness.producer.utils;

import it.unimore.dipi.openness.producer.model.EventDescriptor;
import it.unimore.dipi.openness.producer.services.AppConfig;
import it.unimore.dipi.openness.producer.services.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class EventGenerationTask extends TimerTask {

    final protected Logger logger = LoggerFactory.getLogger(AppService.class);

    private AppConfig appConfig;

    public EventGenerationTask(AppConfig appConfig) {
            this.appConfig = appConfig;
        }

    @Override
    public void run() {
        try {

            logger.info("Event Timer Task ! ....");

            EventDescriptor eventDescriptor = DummyDataGenerator.generateDummyEventData(this.appConfig.getTisDataManager());

            logger.info("New Event Generated -> {}", eventDescriptor);

        } catch (Exception e) {
            e.printStackTrace();
            cancel();
        }
    }
}