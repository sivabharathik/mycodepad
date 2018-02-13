package dockWindow;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;

public class DockablePanel extends SimpleDockablePanel {

    protected String title="Title";
    protected Color titleBackgroundColor=new Color(170,170,255);
    protected Color titleForegroundColor=Color.white;

    public DockablePanel() {
        super();
        super.setBorder(new EmptyBorder(20,2,2,2));
    }

    public void setTitle(String title) {
        this.title=title;
        repaint();
    }

    public void setTitleBackground(Color color) {
        titleBackgroundColor=color;
    }

    public Color getTitleBackground() {
        return titleBackgroundColor;
    }

    public void setTitleForeground(Color color) {
        titleForegroundColor=color;
    }

    public Color getTitleForeground() {
        return titleForegroundColor;
    }

    public void setBorder(Border border) {
        Border margin = new EmptyBorder(20,2,2,2);
        super.setBorder(new CompoundBorder(margin, border));
    }

    public void paint (Graphics g) {
        super.paint(g);
        int width=getWidth();
        int height=getHeight();
        Color oldColor=g.getColor();
        g.setColor(Color.gray);
        g.drawRect(0,0,width-1,height-1);
        g.setColor(Color.darkGray);
        g.drawRect(1,1,width-2,height-2);
        g.setColor(Color.white);
        g.drawLine(1,height-2,width-2,height-2);
        g.drawLine(width-2,1,width-2,height-2);

        //draw caption
        width-=4;
        height-=4;
        g.setColor(titleBackgroundColor);
        ((Graphics2D)g).setPaint(new GradientPaint(1,2,Color.blue,width-2,height-2,Color.cyan));
        g.fillRect(2,2,width,18);
        g.setColor(titleForegroundColor);
        g.drawString(title,3,15);

        //draw close button
        g.setColor(Color.lightGray);
        g.fillRect(width-17,4,15,14);
        g.setColor(Color.white);
        g.drawLine(width-17,4,width-2,4);
        g.drawLine(width-17,4,width-17,18);
        g.setColor(Color.darkGray.darker());
        g.drawLine(width-17,18,width-2,18);
        g.drawLine(width-2,4,width-2,18);
        g.setColor(Color.darkGray);
        g.drawLine(width-16,17,width-3,17);
        g.drawLine(width-3,5,width-3,17);
        g.setColor(Color.black);
        g.drawLine(width-13,8,width-7,14);
        g.drawLine(width-13,14,width-7,8);

        g.setColor(oldColor);
    }
    public void mousePressed(MouseEvent e) {
        if (regim==DESKTOP_REGIM) {
            if ((e.getY()>this.getHeight()-2) || (e.getY()<18) || (e.getX()<2) || (e.getX()>this.getWidth()-2))
                super.mousePressed(e);
        }
        else {
            if ((e.getY()>1) && (e.getY()<18) && (e.getX()>2) && (e.getX()<this.getWidth()-2))
                super.mousePressed(e);
            else {
                startDragX=-1;
                startDragY=-1;
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (regim==DESKTOP_REGIM) {
            super.mouseDragged(e);
        }
        else {
            if ((startDragX>-1) && (startDragY>-1)) {
                super.mouseDragged(e);
            }
        }
    }
    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     */
    public void mouseClicked(MouseEvent e) {
        Container c=this.getParent();
        if (c==null) return;
        int width=this.getWidth()-4;
        if ((e.getX()>=width-17) && (e.getX()<=width-2) && (e.getY()>=4) && (e.getY()<=14)) {
            if (c instanceof Desktop) {
                Desktop parentDesktop=(Desktop)c;
                parentDesktop.removeComponent(this);
                parentDesktop.refresh();
            }
            dragWindow.hide();
        }
        else {
            super.mouseClicked(e);
        }
    }
}