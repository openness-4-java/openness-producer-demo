package it.unimore.dipi.openness.producer.model;

import java.util.Map;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project openness-producer-demo
 * @created 14/10/2020 - 20:25
 */
public class EventDescriptor {

    public static final String TRAFFIC_JAM_EVENT_TYPE = "event.traffic.jam";

    public static final String ACCIDENT_EVENT_TYPE = "event.traffic.accident";

    public static final String ROAD_WORK_EVENT_TYPE = "event.traffic.roadwork";

    private String id;

    private long timestamp;

    private double latitude;

    private double longitude;

    private String type;

    private Map<String, Object> metadata;

    public EventDescriptor() {
    }

    public EventDescriptor(String id, long timestamp, double latitude, double longitude, String type, Map<String, Object> metadata) {
        this.id = id;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EventDescriptor{");
        sb.append("id='").append(id).append('\'');
        sb.append(", timestamp=").append(timestamp);
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", type='").append(type).append('\'');
        sb.append(", metadata=").append(metadata);
        sb.append('}');
        return sb.toString();
    }
}
