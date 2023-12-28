package UC4;

/**
 * Class to represent a sensor record
 */
public class SensorData {

    private String id;
    private double temperature;
    private int timestamp;

    public SensorData(String id, double temperature, int timestamp){
        this.id = id;
        this.temperature = temperature;
        this.timestamp = timestamp;
    }

    /**
     * Return the sensor ID
     */
    public String getID(){
        return id;
    }

    /**
     * Return the record temperature
     */
    public double getTemperature(){
        return temperature;
    }

    /**
     * Return the record timestamp
     */
    public int getTimestamp(){
        return timestamp;
    }

    public String toString(){
        return "ID: " + id + "\nTemperature: " + temperature + "\nTimestamp: " + timestamp + "\n";
    }
}
