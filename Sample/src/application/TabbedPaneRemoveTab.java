package application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TabbedPaneRemoveTab extends JPanel {
    public TabbedPaneRemoveTab() {
        initializeUI();
    }

    private void initializeUI() {
        final JTabbedPane pane = new JTabbedPane(JTabbedPane.LEFT);
        pane.addTab("A Tab", new JPanel());
        pane.addTab("B Tab", new JPanel());

        JPanel tabPanel = new JPanel();
        pane.addTab("C Tab", tabPanel);
        pane.addTab("D Tab", new JPanel());
        pane.addTab("E Tab", new JPanel());

        //
        // Remove the last tab from JTabbedPane
        //
        pane.remove(pane.getTabCount() - 1);

        //
        // Remove tab that contains a tabPanel component which is
        // the C Tab.
        //
        pane.remove(tabPanel);

        JButton button = new JButton("Remove All Tabs");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //
                // Remove all tabs from JTabbedPane
                //
                pane.removeAll();        
            }
        });

        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(400, 200));
        this.add(pane, BorderLayout.CENTER);
        this.add(button, BorderLayout.SOUTH);
    }

    public static void showFrame() {
        JPanel panel = new TabbedPaneRemoveTab();
        panel.setOpaque(true);

        JFrame frame = new JFrame("JTabbedPane Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TabbedPaneRemoveTab.showFrame();
            }
        });
    }
}