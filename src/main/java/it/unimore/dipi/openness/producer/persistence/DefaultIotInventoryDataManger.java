package it.unimore.dipi.openness.producer.persistence;

import it.unimore.dipi.openness.producer.exception.DataManagerException;
import it.unimore.dipi.openness.producer.model.EventDescriptor;
import java.util.*;

/**
 *
 * Demo Traffic Information System (TIS) Data Manager handling all data in a local cache implemented through Maps and Lists
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project openness-producer-demo
 * @created 05/10/2020 - 11:48
 */
public class DefaultIotInventoryDataManger implements TisDataManager {

    private HashMap<String, EventDescriptor> eventMap;

    public DefaultIotInventoryDataManger(){
        this.eventMap = new HashMap<>();
    }

    //EVENT MANAGEMENT

    @Override
    public List<EventDescriptor> getEventList() throws DataManagerException {
        return new ArrayList<>(this.eventMap.values());
    }

    @Override
    public Optional<EventDescriptor> getEventById(String eventId) throws DataManagerException {
        return Optional.ofNullable(this.eventMap.get(eventId));
    }

    @Override
    public EventDescriptor createNewEvent(EventDescriptor eventDescriptor) throws DataManagerException {

        //TODO Improve data validation
        if(eventDescriptor != null){

            //Define UUID
            if(eventDescriptor.getId() == null)
                eventDescriptor.setId(UUID.randomUUID().toString());

            //Add the new user to the UserMap
            this.eventMap.put(eventDescriptor.getId(), eventDescriptor);

            return eventDescriptor;
        }
        else
            throw new DataManagerException("Wrong parameters");
    }

    @Override
    public EventDescriptor updateEvent(EventDescriptor eventDescriptor) throws DataManagerException {
        //TODO Add incoming object validation
        this.eventMap.put(eventDescriptor.getId(), eventDescriptor);
        return eventDescriptor;
    }

    @Override
    public EventDescriptor deleteEvent(String eventId) throws DataManagerException {
        return this.eventMap.remove(eventId);
    }
}
