package it.unimore.dipi.openness.producer.exception;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project http-iot-api-demo
 * @created 05/10/2020 - 12:59
 */
public class DataManagerConflict extends Exception {

    public DataManagerConflict(String errorMessage){
        super(errorMessage);
    }

}
