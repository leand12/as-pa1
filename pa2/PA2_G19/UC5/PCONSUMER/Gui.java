package UC5.PCONSUMER;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import UC1.SensorData;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 *  PConsumer GUI class
 */
public class Gui extends JFrame{

    private final EventQueue queue = new EventQueue();
    private final Font mainFont = new Font("Arial", Font.PLAIN,12);
    private JPanel mainPanel;
    private JPanel[] consumerToTalDataPanel;
    private JPanel[] consumerSensorDataPanel;
    private JScrollPane[] consumerScroll;
    private JLabel[] recordsTotal;
    private JLabel[] numberRecordsTotal;
    private JLabel[] recordsBySendorID;
    private JLabel minTemperature;
    private JLabel maxTemperature;

    public Gui() {
        queue.invokeLater(() -> initComponents());
    }

    private void initComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4,3,0,0));

        minTemperature = new JLabel("Min Temperature: ");
        minTemperature.setFont(mainFont);
        minTemperature.setBorder(new CompoundBorder(minTemperature.getBorder(), new EmptyBorder(0, 300 ,0,0)));
        mainPanel.add(minTemperature);

        JLabel emptyLabel = new JLabel("");
        mainPanel.add(emptyLabel);

        maxTemperature = new JLabel("Max Temperature: ");
        maxTemperature.setFont(mainFont);
        maxTemperature.setBorder(new CompoundBorder(maxTemperature.getBorder(), new EmptyBorder(0, 0 ,0,300)));
        mainPanel.add(maxTemperature);

        consumerSensorDataPanel = new JPanel[9];
        consumerToTalDataPanel = new JPanel[9];
        consumerScroll = new JScrollPane[9];
        recordsTotal = new JLabel[9];
        numberRecordsTotal = new JLabel[9];
        recordsBySendorID = new JLabel[9];

        for(int i = 0; i<9; i++){
            JPanel jp = new JPanel();
            jp.setLayout(new GridLayout(1,2,0,0));
            jp.setBorder(BorderFactory.createTitledBorder("Consumer " + Integer.toString(i)));

            consumerSensorDataPanel[i] = new JPanel();
            consumerSensorDataPanel[i].setLayout(new BoxLayout(consumerSensorDataPanel[i], BoxLayout.PAGE_AXIS));
            consumerSensorDataPanel[i].setBorder(BorderFactory.createTitledBorder("Sensor Data Received"));

            consumerToTalDataPanel[i] = new JPanel();
            consumerToTalDataPanel[i].setLayout(new GridLayout(0, 2));
            consumerToTalDataPanel[i].setBorder(BorderFactory.createTitledBorder("General data"));

            recordsTotal[i] = new JLabel("Total Records: ");
            recordsTotal[i].setFont(mainFont);
            recordsTotal[i].setBorder(new CompoundBorder(recordsTotal[i].getBorder(), new EmptyBorder(0, 10 ,0,0)));

            numberRecordsTotal[i] = new JLabel("0");
            numberRecordsTotal[i].setFont(mainFont);

            recordsBySendorID[i] = new JLabel("Records by Sensor:");
            recordsBySendorID[i].setFont(mainFont);
            recordsBySendorID[i].setBorder(new CompoundBorder(recordsBySendorID[i].getBorder(), new EmptyBorder(0, 10 ,0,0)));

            consumerScroll[i] = new JScrollPane(consumerSensorDataPanel[i]);

            consumerToTalDataPanel[i].add(recordsTotal[i]);
            consumerToTalDataPanel[i].add(numberRecordsTotal[i]);
            consumerToTalDataPanel[i].add(recordsBySendorID[i]);
            consumerToTalDataPanel[i].add(new JLabel(""));

            jp.add(consumerScroll[i]);
            jp.add(consumerToTalDataPanel[i]);
            mainPanel.add(jp);

        }

        add(mainPanel);
        setTitle("PCONSUME");
        setVisible(true);
        setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Update sensor count and add new sensor record
     *
     * @param consumerID   the id of the consumer thread
     * 
     * @param data   the sensor record.
     */
    public void updateCount(int consumerID, SensorData data){
        try {
            queue.invokeAndWait(() -> {
                updateSensorData(consumerID, data);
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update minimum recorder temperature
     *
     * @param temperature the minimum temperature
     */
    public void updateMinTemperature(double temperature){
        try {
            queue.invokeAndWait(() -> {
                minTemperature.setText("Min Temperature: " + temperature);
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        
    }

    /**
     * Update maximum recorder temperature
     *
     * @param temperature the maximum temperature
     */
    public void updateMaxTemperature(double temperature){
        try {
            queue.invokeAndWait(() -> {
                maxTemperature.setText("Max Temperature: " + temperature);
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateSensorData(int consumerID, SensorData data){

        // Update general data
        for(Component c : consumerToTalDataPanel[consumerID].getComponents())
        {
            if (c.getName() != null && c.getName().equals(data.getID()))
            {
                JLabel l = (JLabel) c;
                l.setText(Integer.toString(Integer.parseInt(l.getText()) + 1 ));
                numberRecordsTotal[consumerID].setText(Integer.toString(Integer.parseInt(numberRecordsTotal[consumerID].getText()) + 1 ));
                
                // Add sensor record
                JLabel tempjl = new JLabel("<html>ID: " + data.getID() + "<br>Temperature: " + Double.toString(data.getTemperature()) + "<br>Timestamp: " + Integer.toString(data.getTimestamp()) + "<br>");
                consumerSensorDataPanel[consumerID].add(tempjl);
                return;
            }
        }
        numberRecordsTotal[consumerID].setText(Integer.toString(Integer.parseInt(numberRecordsTotal[consumerID].getText()) + 1 ));
        JLabel jl = new JLabel(data.getID());
        jl.setName(data.getID()+"sensor");
        jl.setFont(mainFont);
        jl.setBorder(new CompoundBorder(jl.getBorder(), new EmptyBorder(0, 50 ,0,0)));
        JLabel l = new JLabel("1");
        l.setName(data.getID());
        l.setFont(mainFont);
        l.setBorder(new CompoundBorder(l.getBorder(), new EmptyBorder(0, 50 ,0,0)));
        consumerToTalDataPanel[consumerID].add(jl);
        consumerToTalDataPanel[consumerID].add(l);    


        // Add sensor record
        JLabel tempjl = new JLabel("<html>ID: " + data.getID() + "<br>Temperature: " + Double.toString(data.getTemperature()) + "<br>Timestamp: " + Integer.toString(data.getTimestamp()) + "<br>");
        consumerSensorDataPanel[consumerID].add(tempjl);
    }
      
    
}
