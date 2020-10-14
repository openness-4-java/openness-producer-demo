package it.unimore.dipi.openness.producer.persistence;

import it.unimore.dipi.openness.producer.exception.DataManagerException;
import it.unimore.dipi.openness.producer.model.EventDescriptor;

import java.util.List;
import java.util.Optional;

/**
 *
 * Demo Traffic Information System (TIS) - Data Manager Interface
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project openness-producer-demo
 * @created 05/10/2020 - 11:44
 */
public interface TisDataManager {

    //Event Management
    public List<EventDescriptor> getEventList() throws DataManagerException;

    public Optional<EventDescriptor> getEventById(String eventId) throws DataManagerException;

    public EventDescriptor createNewEvent(EventDescriptor eventDescriptor) throws DataManagerException;

    public EventDescriptor updateEvent(EventDescriptor eventDescriptor) throws DataManagerException;

    public EventDescriptor deleteEvent(String eventId) throws DataManagerException;

}
