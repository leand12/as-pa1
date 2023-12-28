package UC2.PSOURCE;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 *  PSource GUI class
 */
public class Gui extends JFrame{

    private final EventQueue queue = new EventQueue();
    private final Font mainFont = new Font("Arial", Font.PLAIN,18);
    private JPanel mainPanel;
    private JLabel recordsTotal;
    private JLabel numberRecordsTotal;
    private JLabel recordsBySendorID;

    public Gui() {
        queue.invokeLater(() -> initComponents());
    }

    private void initComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0,2,0,0));

        recordsTotal = new JLabel("Number of Total Records: ");
        recordsTotal.setFont(mainFont);
        recordsTotal.setBorder(new CompoundBorder(recordsTotal.getBorder(), new EmptyBorder(0, 10 ,0,0)));

        numberRecordsTotal = new JLabel("0");
        numberRecordsTotal.setFont(mainFont);

        recordsBySendorID = new JLabel("Number of Records by Sensor ID: ");
        recordsBySendorID.setFont(mainFont);
        recordsBySendorID.setBorder(new CompoundBorder(recordsBySendorID.getBorder(), new EmptyBorder(0, 10 ,0,0)));

        JLabel occupySpace = new JLabel("");


        mainPanel.add(recordsTotal);
        mainPanel.add(numberRecordsTotal);
        mainPanel.add(recordsBySendorID);
        mainPanel.add(occupySpace);

        add(mainPanel);
        setTitle("PSOURCE");
        setSize(800,900);
        setVisible(true);
    }

    /**
     * Update sensor record count
     *
     * @param id   the sensor id.
     */
    public void updateCount(String id){
        try {
            queue.invokeAndWait(() -> {
                updateSensorCount(id);
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateSensorCount(String id){
        for(Component c : mainPanel.getComponents())
        {
            if (c.getName() != null && c.getName().equals(id))
            {
                JLabel l = (JLabel) c;
                l.setText(Integer.toString(Integer.parseInt(l.getText()) + 1 ));
                numberRecordsTotal.setText(Integer.toString(Integer.parseInt(numberRecordsTotal.getText()) + 1 ));
                return;
            }
        }
        numberRecordsTotal.setText(Integer.toString(Integer.parseInt(numberRecordsTotal.getText()) + 1 ));
        JLabel jl = new JLabel(id);
        jl.setName(id+"sensor");
        jl.setFont(mainFont);
        jl.setBorder(new CompoundBorder(jl.getBorder(), new EmptyBorder(0, 100 ,0,0)));
        JLabel l = new JLabel("1");
        l.setName(id);
        l.setFont(mainFont);
        l.setBorder(new CompoundBorder(l.getBorder(), new EmptyBorder(0, 100 ,0,0)));
        mainPanel.add(jl);
        mainPanel.add(l);    
    }
      

    
}
