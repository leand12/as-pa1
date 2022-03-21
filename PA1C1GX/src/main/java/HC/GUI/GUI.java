package HC.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

public class GUI extends JFrame {

    private JPanel panel1;
    private JFormattedTextField formattedTextField1;
    private JFormattedTextField formattedTextField2;
    private JFormattedTextField formattedTextField3;
    private JPanel etr1;
    private JPanel etr2;
    private JScrollPane eth;
    private JPanel evr1;
    private JPanel evr2;
    private JPanel evr3;
    private JPanel evr4;
    private JScrollPane wth;
    private JPanel wtr1;
    private JPanel wtr2;
    private JPanel mdw;
    private JPanel mdr1;
    private JPanel mdr3;
    private JPanel mdr4;
    private JPanel mdr2;
    private JScrollPane pyh;
    private JPanel cashier;
    private JScrollPane out;

    public GUI() {
        initComponents();
    }

    public void initComponents() {
        formattedTextField1.setText("XX");
        formattedTextField2.setText("YY");
        formattedTextField3.setText("ZZ");

        etr1.setLayout(new GridLayout(1, 1));
        etr1.add(new SeatsGraphics(5, 0, 5));
        etr2.setLayout(new GridLayout(1, 1));
        etr2.add(new SeatsGraphics(5, 5, 0));

        evr1.setLayout(new GridLayout(1, 1));
        evr1.add(new SeatsGraphics(1, 0, 0));
        evr2.setLayout(new GridLayout(1, 1));
        evr2.add(new SeatsGraphics(1, 0, 0));
        evr3.setLayout(new GridLayout(1, 1));
        evr3.add(new SeatsGraphics(1, 0, 0));
        evr4.setLayout(new GridLayout(1, 1));
        evr4.add(new SeatsGraphics(1, 0, 0));

        wtr1.setLayout(new GridLayout(1, 1));
        wtr1.add(new SeatsGraphics(5, 0, 5));
        wtr2.setLayout(new GridLayout(1, 1));
        wtr2.add(new SeatsGraphics(5, 5, 0));

        mdw.setLayout(new GridLayout(1, 1));
        mdw.add(new SeatsGraphics(2, 1, 1));

        mdr1.setLayout(new GridLayout(1, 1));
        mdr1.add(new SeatsGraphics(1, 0, 1));
        mdr2.setLayout(new GridLayout(1, 1));
        mdr2.add(new SeatsGraphics(1, 0, 1));
        mdr3.setLayout(new GridLayout(1, 1));
        mdr3.add(new SeatsGraphics(1, 1, 0));
        mdr4.setLayout(new GridLayout(1, 1));
        mdr4.add(new SeatsGraphics(1, 1, 0));

        cashier.setLayout(new GridLayout(1, 1));
        cashier.add(new SeatsGraphics(1, 0, 0));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("HC GUI");
        add(panel1);
        pack();
        setLocationByPlatform(true);
        setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(GUI::new);
    }
}

class SeatsGraphics extends JComponent {
    private static final int P = 5;
    private static final int R = 30;
    private static final int D = 2 * R;

    private int numberOfSeats;
    private int numberOfAdultSeats;
    private int numberOfChildSeats;
    private Patient[] patients;

    SeatsGraphics(int numberOfSeats, int numberOfAdultSeats, int numberOfChildSeats) {
        this.numberOfSeats = numberOfSeats;
        this.numberOfAdultSeats = numberOfAdultSeats;
        this.numberOfChildSeats = numberOfChildSeats;
        this.patients = new Patient[numberOfSeats];
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.LIGHT_GRAY);

        for (int y = 0; y <= numberOfSeats/3; y++) {
            for (int x = 0; x < 3; x++) {
                int seat = 3*y + x;
                if (seat >= numberOfSeats) break;

                if (seat < numberOfChildSeats) {
                    Ellipse2D shape = new Ellipse2D.Double(x*D + P, y*D + P, D - P, D - P);
                    g2.fill(shape);
                } else if (seat < numberOfAdultSeats + numberOfChildSeats) {
                    Path2D path = new Path2D.Double();
                    path.moveTo(x*D + P, y*D + D - P);
                    path.lineTo(x*D + D - P, y*D + D - P);
                    path.lineTo(x*D + D / 2.0, y*D + P);
                    path.closePath();
                    g2.fill(path);
                } else {
                    Rectangle2D shape = new Rectangle2D.Double(x*D + P, y*D + P, D - P, D - P);
                    g2.fill(shape);
                }
            }
        }
    }
}

class Patient {
    private DoS degreeOfSeverity;
    private boolean isAdult;
}

enum DoS {
    RED,
    YELLOW,
    BLUE,
    NONE
}
