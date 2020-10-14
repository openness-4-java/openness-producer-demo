package it.unimore.dipi.openness.producer.utils;

import it.unimore.dipi.openness.producer.model.EventDescriptor;
import it.unimore.dipi.openness.producer.persistence.TisDataManager;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project http-iot-api-demo
 * @created 05/10/2020 - 12:04
 */
public class DummyDataGenerator {

    public static void generateMultipleDummyEventData(TisDataManager tisDataManager, int eventNumber){
        for (int i=0; i<eventNumber; i++)
            generateDummyEventData(tisDataManager);
    }

    public static void generateDummyEventData(TisDataManager tisDataManager){

        try{

            Random random = new Random();

            EventDescriptor eventDescriptor = new EventDescriptor();
            eventDescriptor.setId(UUID.randomUUID().toString());
            eventDescriptor.setLatitude(random.nextDouble());
            eventDescriptor.setLongitude(random.nextDouble());
            eventDescriptor.setType(getRandomEventType(random.nextInt(3)));
            eventDescriptor.setTimestamp(System.currentTimeMillis());
            eventDescriptor.setMetadata(new HashMap<String, Object>() {
                {
                    put("source", "ROAD_SIDE_UNIT");
                    put("accuracy", Double.toString(random.nextDouble()));
                }
            });

            tisDataManager.createNewEvent(eventDescriptor);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String getRandomEventType(int randomValue){

        String result;
        switch (randomValue){
            case 0: result = EventDescriptor.ACCIDENT_EVENT_TYPE;
            break;
            case 1: result = EventDescriptor.ROAD_WORK_EVENT_TYPE;
                break;
            default: result = EventDescriptor.TRAFFIC_JAM_EVENT_TYPE;
        }

        return result;
    }

}
