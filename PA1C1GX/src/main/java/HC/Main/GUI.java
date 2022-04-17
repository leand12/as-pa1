package HC.Main;

import HC.Data.ERoom;
import HC.Entities.TPatient;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static HC.Data.ERoom.*;


public class GUI extends JFrame {
    private final EventQueue queue = new EventQueue();
    private final HashMap<TPatient, ERoom> patientsRoom = new HashMap<>();
    private JPanel panel1;
    private JFormattedTextField formattedTextField1;
    private JFormattedTextField formattedTextField2;
    private JFormattedTextField formattedTextField3;
    private JPanel etr1;
    private JPanel etr2;
    private JPanel evr1;
    private JPanel evr2;
    private JPanel evr3;
    private JPanel evr4;
    private JPanel wtr1;
    private JPanel wtr2;
    private JPanel mdw;
    private JPanel mdr1;
    private JPanel mdr3;
    private JPanel mdr4;
    private JPanel mdr2;
    private JPanel cashier;
    private JPanel eth;
    private JPanel wth;
    private JPanel pyh;
    private JPanel out;

    public GUI(int NoA, int NoC, int NoS) {
        queue.invokeLater(() -> initComponents(NoA, NoC, NoS));
    }

    public static void main(String[] args) {
        new GUI(4, 4, 10);
    }

    private Seats getRoomSeats(ERoom room) {
        return switch (room) {
            case ETH -> (Seats) eth.getComponent(0);
            case ET1 -> (Seats) etr1.getComponent(0);
            case ET2 -> (Seats) etr2.getComponent(0);
            case EVR1 -> (Seats) evr1.getComponent(0);
            case EVR2 -> (Seats) evr2.getComponent(0);
            case EVR3 -> (Seats) evr3.getComponent(0);
            case EVR4 -> (Seats) evr4.getComponent(0);
            case WTH -> (Seats) wth.getComponent(0);
            case WTR1 -> (Seats) wtr1.getComponent(0);
            case WTR2 -> (Seats) wtr2.getComponent(0);
            case MDH -> (Seats) mdw.getComponent(0);
            case MDR1 -> (Seats) mdr1.getComponent(0);
            case MDR2 -> (Seats) mdr2.getComponent(0);
            case MDR3 -> (Seats) mdr3.getComponent(0);
            case MDR4 -> (Seats) mdr4.getComponent(0);
            case PYH -> (Seats) pyh.getComponent(0);
            case CSH -> (Seats) cashier.getComponent(0);
            case OUT -> (Seats) out.getComponent(0);
            default -> throw new IllegalArgumentException("Room unrecognized: " + room);
        };
    }

    public void addPatient(ERoom room, TPatient patient) {
        try {
            queue.invokeAndWait(() -> {
                ERoom prevRoom = patientsRoom.put(patient, room);
                // remove patient automatically in case not removed before
                if (prevRoom != null) getRoomSeats(prevRoom).removePatient(patient);
                getRoomSeats(room).addPatient(patient);
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void removePatient(ERoom room, TPatient patient) {
        try {
            queue.invokeAndWait(() -> {
                patientsRoom.remove(patient);
                getRoomSeats(room).removePatient(patient);
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateRoom(ERoom room) {
        queue.invokeLater(() -> getRoomSeats(room).repaint());
    }

    private void initComponents(int NoA, int NoC, int NoS) {
        formattedTextField1.setText(String.valueOf(NoA));
        formattedTextField2.setText(String.valueOf(NoC));
        formattedTextField3.setText(String.valueOf(NoS));

        for (JComponent c : new JComponent[]{etr1, etr2, evr1, evr2, evr3, evr4, wtr1, wtr2,
                mdw, mdr1, mdr2, mdr3, mdr4, cashier, eth, wth, pyh, out}) {
            c.setLayout(new GridLayout(1, 1));
        }

        eth.add(new SeatsList(ETH), 0);
        wth.add(new SeatsList(WTH), 0);
        pyh.add(new SeatsList(PYH), 0);
        out.add(new SeatsList(OUT), 0);

        int n = NoS / 2;
        etr1.add(new SeatsRoom(ET1, n, 0, n), 0);
        etr2.add(new SeatsRoom(ET2, n, n, 0), 0);

        evr1.add(new SeatsRoom(EVR1, 1, 0, 0), 0);
        evr2.add(new SeatsRoom(EVR2, 1, 0, 0), 0);
        evr3.add(new SeatsRoom(EVR3, 1, 0, 0), 0);
        evr4.add(new SeatsRoom(EVR4, 1, 0, 0), 0);

        wtr1.add(new SeatsRoom(WTR1, n, 0, n), 0);
        wtr2.add(new SeatsRoom(WTR2, n, n, 0), 0);

        mdw.add(new SeatsRoom(MDH, 2, 1, 1), 0);

        mdr1.add(new SeatsRoom(MDR1, 1, 0, 1), 0);
        mdr2.add(new SeatsRoom(MDR2, 1, 0, 1), 0);
        mdr3.add(new SeatsRoom(MDR3, 1, 1, 0), 0);
        mdr4.add(new SeatsRoom(MDR4, 1, 1, 0), 0);

        cashier.add(new SeatsRoom(CSH, 1, 0, 0), 0);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("HC GUI");

        add(panel1);
        setSize(1400, 800);
        setLocationByPlatform(true);
        setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "MDH", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mdw = new JPanel();
        mdw.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(mdw, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        mdw.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "MDW", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final Spacer spacer1 = new Spacer();
        panel4.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel4.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mdr1 = new JPanel();
        mdr1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(mdr1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mdr1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "MDR1", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        mdr2 = new JPanel();
        mdr2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(mdr2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mdr2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "MDR2", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        mdr3 = new JPanel();
        mdr3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(mdr3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mdr3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "MDR3", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        mdr4 = new JPanel();
        mdr4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(mdr4, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mdr4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "MDR4", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "PYH", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel7.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        pyh = new JPanel();
        pyh.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane1.setViewportView(pyh);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel8, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel8.add(spacer3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel8.add(spacer4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        cashier = new JPanel();
        cashier.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel8.add(cashier, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        cashier.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "Cashier", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel9, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel9.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "OUT", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel9.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        out = new JPanel();
        out.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane2.setViewportView(out);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 7, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 50), null, null, 2, false));
        final JLabel label1 = new JLabel();
        label1.setText("NoA:");
        panel10.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("NoS:");
        panel10.add(label2, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("NoC:");
        panel10.add(label3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel10.add(spacer5, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        formattedTextField1 = new JFormattedTextField();
        formattedTextField1.setEditable(false);
        formattedTextField1.setEnabled(true);
        formattedTextField1.setOpaque(true);
        panel10.add(formattedTextField1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(40, -1), new Dimension(40, -1), 0, false));
        formattedTextField2 = new JFormattedTextField();
        formattedTextField2.setEditable(false);
        formattedTextField2.setEnabled(true);
        formattedTextField2.setText("");
        panel10.add(formattedTextField2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(40, -1), new Dimension(40, -1), 0, false));
        formattedTextField3 = new JFormattedTextField();
        formattedTextField3.setEditable(false);
        formattedTextField3.setEnabled(true);
        panel10.add(formattedTextField3, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(40, -1), new Dimension(40, -1), 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel11, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel11.add(panel12, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel12.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "ETH", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel12.add(panel13, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        etr1 = new JPanel();
        etr1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel13.add(etr1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        etr1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "ETR1", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        etr2 = new JPanel();
        etr2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel13.add(etr2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        etr2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "ETR2", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel12.add(panel14, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane3 = new JScrollPane();
        panel14.add(scrollPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        eth = new JPanel();
        eth.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane3.setViewportView(eth);
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel11.add(panel15, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel15.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "EVH", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        evr1 = new JPanel();
        evr1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel15.add(evr1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        evr1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "EVR1", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        evr2 = new JPanel();
        evr2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel15.add(evr2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        evr2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "EVR2", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        evr3 = new JPanel();
        evr3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel15.add(evr3, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        evr3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "EVR4", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        evr4 = new JPanel();
        evr4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel15.add(evr4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        evr4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "EVR3", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel11.add(panel16, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel16.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "WTH", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel16.add(panel17, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane4 = new JScrollPane();
        panel17.add(scrollPane4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        wth = new JPanel();
        wth.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane4.setViewportView(wth);
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel16.add(panel18, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        wtr1 = new JPanel();
        wtr1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel18.add(wtr1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        wtr1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "WTR1", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        wtr2 = new JPanel();
        wtr2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel18.add(wtr2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        wtr2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-14448965)), "WTR2", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}


abstract class Seats extends JPanel {
    private static final int P = 3;
    private static final int R = 30;
    private static final int D = 2 * R;
    protected final ERoom room;

    public Seats(ERoom room) {
        this.room = room;
    }

    abstract void addPatient(TPatient patient);
    abstract void removePatient(TPatient patient);

    protected void paintPatient(Graphics2D g2, int x, int y, TPatient patient) {
        switch (patient.getDos()) {
            case RED -> g2.setPaint(Color.RED);
            case YELLOW -> g2.setPaint(Color.YELLOW);
            case BLUE -> g2.setPaint(Color.BLUE);
            default -> g2.setPaint(Color.GRAY);
        }

        // draw shape
        if (patient.isAdult()) {
            paintTriangle(g2, x, y, false);
        } else {
            paintCircle(g2, x, y, false);
        }

        // draw ID label centered
        var font = new Font(null, Font.PLAIN, 18);
        var text = String.valueOf(patient.getNN());
        var metrics = g2.getFontMetrics(font);
        g2.setFont(font);
        g2.setPaint(Color.BLACK);
        g2.drawString(text,
                x * D + P + (D - P - metrics.stringWidth(text)) / 2,
                y * D + P + (D - P - metrics.getHeight()) / 2 + metrics.getAscent());
    }

    protected void paintSquare(Graphics2D g2, int x, int y, boolean isSeat) {
        int P = isSeat ? this.P : this.P + 4;
        Rectangle2D shape = new Rectangle2D.Double(x * D + P, y * D + P, D - 2*P, D - 2*P);
        g2.fill(shape);
    }

    protected void paintTriangle(Graphics2D g2, int x, int y, boolean isSeat) {
        int P = isSeat ? this.P : this.P + 3;
        Path2D path = new Path2D.Double();
        path.moveTo(x * D + P, y * D + D - P);      // bottom left corner
        path.lineTo(x * D + D - P, y * D + D - P);      // bottom right corner
        path.lineTo(x * D + D / 2.0, y * D + P);    // upper corner
        path.closePath();
        g2.fill(path);
    }

    protected void paintCircle(Graphics2D g2, int x, int y, boolean isSeat) {
        int P = isSeat ? this.P : this.P + 4;
        Ellipse2D shape = new Ellipse2D.Double(x * D + P, y * D + P, D - 2*P, D - 2*P);
        g2.fill(shape);
    }
}


class SeatsList extends Seats {
    private final ArrayList<TPatient> patients;

    SeatsList(ERoom room) {
        super(room);
        this.patients = new ArrayList<>();
    }

    @Override
    public void addPatient(TPatient patient) {
        if (patients.contains(patient))
            throw new IllegalArgumentException("Patient " + patient + " already inside the room " + room);

        patients.add(patient);
        repaint();
    }

    @Override
    public void removePatient(TPatient patient) {
        if (!patients.contains(patient))
            throw new IllegalArgumentException("Patient " + patient + " not found in room " + room);

        patients.remove(patient);
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // paint patients
        for (var y = 0; y <= patients.size() / 3; y++) {
            for (var x = 0; x < 3; x++) {
                int seat = 3 * y + x;
                if (seat >= patients.size()) break;

                TPatient patient = patients.get(seat);
                paintPatient(g2, x, y, patient);
            }
        }
    }
}

class SeatsRoom extends Seats {
    private final int numberOfSeats;
    private final int numberOfAdultSeats;
    private final int numberOfChildSeats;
    private final TPatient[] patients;

    SeatsRoom(ERoom room, int numberOfSeats, int numberOfAdultSeats, int numberOfChildSeats) {
        super(room);
        this.numberOfSeats = numberOfSeats;
        this.numberOfAdultSeats = numberOfAdultSeats;
        this.numberOfChildSeats = numberOfChildSeats;
        this.patients = new TPatient[numberOfSeats];
    }

    @Override
    public void addPatient(TPatient patient) {
        if (Arrays.asList(patients).contains(patient))
            throw new IllegalArgumentException("Patient " + patient + " already inside the room " + room);

        for (var i = 0; i < numberOfSeats; i++) {
            if (patients[i] == null) {
                // found empty seat for new patient

                // check seat is for right patient (adult or child)
                var adequateSeat = false;
                if (i < numberOfChildSeats) {
                    if (!patient.isAdult()) adequateSeat = true;
                } else if (i < numberOfAdultSeats + numberOfChildSeats) {
                    if (patient.isAdult()) adequateSeat = true;
                } else {
                    adequateSeat = true;
                }

                if (adequateSeat) {
                    // add patient
                    patients[i] = patient;
                    repaint();
                    return;
                }
            }
        }
        throw new IllegalArgumentException("No seat available for Patient " + patient + " in room " + room);
    }

    @Override
    public void removePatient(TPatient patient) {
        for (var i = 0; i < numberOfSeats; i++) {
            if (patients[i] == patient) {
                // remove patient from seat
                patients[i] = null;
                repaint();
                return;
            }
        }
        throw new IllegalArgumentException("Patient " + patient + " not found in room " + room);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // paint seats
        g2.setPaint(Color.LIGHT_GRAY);
        for (var y = 0; y <= numberOfSeats / 3; y++) {
            for (var x = 0; x < 3; x++) {
                int seat = 3 * y + x;
                if (seat >= numberOfSeats) break;

                if (seat < numberOfChildSeats) {
                    paintCircle(g2, x, y, true);
                } else if (seat < numberOfAdultSeats + numberOfChildSeats) {
                    paintTriangle(g2, x, y, true);
                } else {
                    paintSquare(g2, x, y, true);
                }
            }
        }

        // paint patients
        for (var y = 0; y <= numberOfSeats / 3; y++) {
            for (var x = 0; x < 3; x++) {
                int seat = 3 * y + x;
                if (seat >= numberOfSeats) break;

                TPatient patient = patients[seat];
                if (patient == null) continue;

                paintPatient(g2, x, y, patient);
            }
        }
    }
}
