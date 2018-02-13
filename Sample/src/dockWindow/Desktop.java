package dockWindow;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import javax.swing.border.*;
import java.awt.image.*;

public class Desktop extends JPanel implements MouseListener, MouseMotionListener {
    static int counter=0;

    public static final int TARGET_TOP=0;
    public static final int TARGET_BOTTOM=1;
    public static final int TARGET_LEFT=2;
    public static final int TARGET_RIGHT=3;

    protected int startDragX;
    protected int startDragY;
    protected int endDragX;
    protected int endDragY;

    protected GridBagLayout contaiterLayout;

    protected Component dragSourceComponent;
    protected Component dragTargetComponent;

    protected boolean drag=false;
    protected JWindow draggedWindow=new JWindow((JFrame)null);
    protected int splitDragX=-1;
    protected int splitDragY=-1;

    public Desktop() {
        super();
        this.setLayout(new GridBagLayout());
        this.contaiterLayout=(GridBagLayout)this.getLayout();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void addDesktopComponent(Component component,int gridX,int gridY,int gridWidth,int gridHeight) {
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.gridx=gridX;
        gbc.gridy=gridY;
        gbc.gridwidth=gridWidth;
        gbc.gridheight=gridHeight;
        gbc.weightx=gridWidth;
        gbc.weighty=gridHeight;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.anchor=GridBagConstraints.NORTHWEST;
        this.add(component,gbc);
    }

    public void paint (Graphics g) {
        super.paint(g);
        if (splitDragX>0) {
            g.drawLine(splitDragX,0,splitDragX,getHeight());
        }
        if (splitDragY>0) {
            g.drawLine(0,splitDragY,getWidth(),splitDragY);
        }
    }

    public void drop() {
        if (dragSourceComponent==null)
            dragSourceComponent=this.getComponentAt(startDragX,startDragY);
        dragTargetComponent=this.getComponentAt(endDragX,endDragY);
        if ((dragSourceComponent==null)
            || (dragTargetComponent==null)
            || (dragSourceComponent==dragTargetComponent))
            return;
        drop(dragSourceComponent,dragTargetComponent);
        dragSourceComponent=null;
    }

    public void drop(Component dragSource,Component target) {
        if (dragSource==target) {
            return;
        }
        if ((startDragX!=-1) && (startDragY!=-1))
            removeComponent(dragSource);

        Rectangle targetBounds=target.getBounds();
        int targetPlace=getTargetPlace(targetBounds,endDragX,endDragY);

        GridBagConstraints gbc=contaiterLayout.getConstraints(target);

        int gridX=0;
        int gridY=0;
/*        JLabel l=new JLabel(Integer.toString(counter));
        l.setVerticalAlignment(JLabel.CENTER);
        l.setHorizontalAlignment(JLabel.CENTER);
        l.setBorder(new LineBorder(Color.black));
        l.setOpaque(true);
        l.setBackground(Color.lightGray);*/
        switch (targetPlace) {
            case TARGET_TOP:
                gridX=gbc.gridx;
                gridY=Math.max(0,gbc.gridy-1);
                insertAbove(target,dragSource);
                counter++;
                break;
            case TARGET_BOTTOM:
                gridX=gbc.gridx;
                gridY=gbc.gridy+1;
                insertBelow(target,dragSource);
                counter++;
                break;
            case TARGET_LEFT:
                gridX=Math.max(0,gbc.gridx-1);
                gridY=gbc.gridy;
                insertBefore(target,dragSource);
                counter++;
                break;
            case TARGET_RIGHT:
                gridX=gbc.gridx+1;
                gridY=gbc.gridy;
                insertAfter(target,dragSource);
                counter++;
                break;
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                refresh();
            }
        });
    }

    public void insertBefore(Component target,Component component) {
        GridBagConstraints targetGBC=contaiterLayout.getConstraints(target);

        GridBagConstraints componentGBC=new GridBagConstraints();
        int gridX=targetGBC.gridx;
        int gridY=targetGBC.gridy;
        componentGBC.gridx=gridX;
        componentGBC.gridy=gridY;
        componentGBC.gridheight=targetGBC.gridheight;
        componentGBC.fill=GridBagConstraints.BOTH;
        componentGBC.weightx=1;
        componentGBC.weighty=targetGBC.gridheight;
        if (targetGBC.gridwidth>1) {
            targetGBC.gridwidth--;
            targetGBC.gridx++;
            this.add(target,targetGBC);
            this.add(component,componentGBC);
            return;
        }

        Component[] components=this.getComponents();
        GridBagConstraints[] componentConstraints=new GridBagConstraints[components.length];
        for (int i=0; i<components.length; i++) {
            GridBagConstraints currentGBC=contaiterLayout.getConstraints(components[i]);
            if (target==components[i]) {
                currentGBC.gridx++;
            }
            else {
                if (currentGBC.gridx>gridX) {
                    currentGBC.gridx++;
                }
                else if (currentGBC.gridx+currentGBC.gridwidth>gridX) {
                    currentGBC.gridwidth++;
                }
            }
            componentConstraints[i]=currentGBC;
        }

        this.removeAll();
        for  (int i=0; i<components.length; i++) {
             this.add(components[i],componentConstraints[i]);
        }
        this.add(component,componentGBC);
    }

    public void insertAfter(Component target,Component component) {
        GridBagConstraints targetGBC=contaiterLayout.getConstraints(target);

        GridBagConstraints componentGBC=new GridBagConstraints();
        int gridX=targetGBC.gridx+targetGBC.gridwidth;
        int gridY=targetGBC.gridy;
        componentGBC.gridx=gridX;
        componentGBC.gridy=gridY;
        componentGBC.gridheight=targetGBC.gridheight;
        componentGBC.fill=GridBagConstraints.BOTH;
        componentGBC.weightx=1;
        componentGBC.weighty=targetGBC.gridheight;
        if (targetGBC.gridwidth>1) {
            targetGBC.gridwidth--;
            componentGBC.gridx--;
            this.add(target,targetGBC);
            this.add(component,componentGBC);
            return;
        }

        Component[] components=this.getComponents();
        GridBagConstraints[] componentConstraints=new GridBagConstraints[components.length];
        for (int i=0; i<components.length; i++) {
            GridBagConstraints currentGBC=contaiterLayout.getConstraints(components[i]);
            if (components[i]!=target) {
                if (currentGBC.gridx>=gridX) {
                    currentGBC.gridx++;
                }
                else if (currentGBC.gridx+currentGBC.gridwidth>=gridX) {
                    currentGBC.gridwidth++;
                }
            }
            componentConstraints[i]=currentGBC;
        }

        this.removeAll();
        for  (int i=0; i<components.length; i++) {
             this.add(components[i],componentConstraints[i]);
        }
        this.add(component,componentGBC);
    }

    public void insertAbove(Component target,Component component) {
        GridBagConstraints targetGBC=contaiterLayout.getConstraints(target);

        GridBagConstraints componentGBC=new GridBagConstraints();
        int gridX=targetGBC.gridx;
        int gridY=targetGBC.gridy;
        componentGBC.gridx=gridX;
        componentGBC.gridy=gridY;
        componentGBC.gridwidth=targetGBC.gridwidth;
        componentGBC.fill=GridBagConstraints.BOTH;
        componentGBC.weightx=targetGBC.gridwidth;
        componentGBC.weighty=1;
        if (targetGBC.gridheight>1) {
            targetGBC.gridheight--;
            targetGBC.gridy++;
            this.add(target,targetGBC);
            this.add(component,componentGBC);
            return;
        }

        Component[] components=this.getComponents();
        GridBagConstraints[] componentConstraints=new GridBagConstraints[components.length];
        for (int i=0; i<components.length; i++) {
            GridBagConstraints currentGBC=contaiterLayout.getConstraints(components[i]);
            if (target==components[i]) {
                currentGBC.gridy++;
            } else {
                if (currentGBC.gridy>gridY) {
                    currentGBC.gridy++;
                }
                else if (currentGBC.gridy+currentGBC.gridheight>gridY) {
                    currentGBC.gridheight++;
                }
            }
            componentConstraints[i]=currentGBC;
        }

        this.removeAll();
        for  (int i=0; i<components.length; i++) {
             this.add(components[i],componentConstraints[i]);
        }
        this.add(component,componentGBC);
    }

    public void insertBelow(Component target,Component component) {
        GridBagConstraints targetGBC=contaiterLayout.getConstraints(target);

        GridBagConstraints componentGBC=new GridBagConstraints();
        int gridX=targetGBC.gridx;
        int gridY=targetGBC.gridy+targetGBC.gridheight;
        componentGBC.gridx=gridX;
        componentGBC.gridy=gridY;
        componentGBC.gridwidth=targetGBC.gridwidth;
        componentGBC.fill=GridBagConstraints.BOTH;
        componentGBC.weighty=1;
        componentGBC.weightx=targetGBC.gridwidth;
        if (targetGBC.gridheight>1) {
            targetGBC.gridheight--;
            componentGBC.gridy--;
            this.add(target,targetGBC);
            this.add(component,componentGBC);
            return;
        }

        Component[] components=this.getComponents();
        GridBagConstraints[] componentConstraints=new GridBagConstraints[components.length];
        for (int i=0; i<components.length; i++) {
            GridBagConstraints currentGBC=contaiterLayout.getConstraints(components[i]);
            if (components[i]!=target) {
                if (currentGBC.gridy>=gridY) {
                    currentGBC.gridy++;
                }
                else if (currentGBC.gridy+currentGBC.gridheight>=gridY) {
                    currentGBC.gridheight++;
                }
            }
            componentConstraints[i]=currentGBC;
        }

        this.removeAll();
        for  (int i=0; i<components.length; i++) {
             this.add(components[i],componentConstraints[i]);
        }
        this.add(component,componentGBC);
    }

    protected Component[] getLeftNeighbours(int gridX, int gridY, int gridHeight) {
        Component[] components=this.getComponents();
        Vector result=new Vector();
        for  (int i=0; i<components.length; i++) {
            GridBagConstraints currentGBC=contaiterLayout.getConstraints(components[i]);
            if (   (gridX==currentGBC.gridx+currentGBC.gridwidth)
                && (currentGBC.gridy>=gridY)
                && (currentGBC.gridy<=gridY+gridHeight)) {
                result.add(components[i]);
            }
        }

        components=new Component[result.size()];
        for (int i=0; i<components.length; i++) {
            components[i]=(Component)result.get(i);
        }
        return components;
    }

    protected Component[] getRightNeighbours(int gridX, int gridY, int gridHeight) {
        Component[] components=this.getComponents();
        Vector result=new Vector();
        for  (int i=0; i<components.length; i++) {
            GridBagConstraints currentGBC=contaiterLayout.getConstraints(components[i]);
            if (   (gridX==currentGBC.gridx)
                && (currentGBC.gridy>=gridY)
                && (currentGBC.gridy<=gridY+gridHeight)) {
                result.add(components[i]);
            }
        }

        components=new Component[result.size()];
        for (int i=0; i<components.length; i++) {
            components[i]=(Component)result.get(i);
        }
        return components;
    }

    protected Component[] getTopNeighbours(int gridX, int gridY, int gridWidth) {
        Component[] components=this.getComponents();
        Vector result=new Vector();
        for  (int i=0; i<components.length; i++) {
            GridBagConstraints currentGBC=contaiterLayout.getConstraints(components[i]);
            if (   (gridY==currentGBC.gridy+currentGBC.gridheight)
                && (currentGBC.gridx>=gridX)
                && (currentGBC.gridx<=gridX+gridWidth)) {
                result.add(components[i]);
            }
        }

        components=new Component[result.size()];
        for (int i=0; i<components.length; i++) {
            components[i]=(Component)result.get(i);
        }
        return components;
    }

    protected Component[] getBottomNeighbours(int gridX, int gridY, int gridWidth) {
        Component[] components=this.getComponents();
        Vector result=new Vector();
        for  (int i=0; i<components.length; i++) {
            GridBagConstraints currentGBC=contaiterLayout.getConstraints(components[i]);
            if (   (gridY==currentGBC.gridy)
                && (currentGBC.gridx>=gridX)
                && (currentGBC.gridx<=gridX+gridWidth)) {
                result.add(components[i]);
            }
        }

        components=new Component[result.size()];
        for (int i=0; i<components.length; i++) {
            components[i]=(Component)result.get(i);
        }
        return components;
    }

    public void removeComponent(Component component) {
        GridBagConstraints componentGBC=contaiterLayout.getConstraints(component);
        Component[] components=this.getComponents();
        GridBagConstraints[] componentConstraints=new GridBagConstraints[components.length];
        boolean removed=false;
        int gridWidth=1;
        int gridHeight=1;

        Component[] leftNeighbours=getLeftNeighbours(componentGBC.gridx,componentGBC.gridy,componentGBC.gridheight);
        if (leftNeighbours.length>0) {
            GridBagConstraints first=contaiterLayout.getConstraints(leftNeighbours[0]);
            GridBagConstraints last=contaiterLayout.getConstraints(leftNeighbours[leftNeighbours.length-1]);

            if ((first.gridy==componentGBC.gridy) && (last.gridy+last.gridheight==componentGBC.gridy+componentGBC.gridheight)) {
                for (int i=0; i<leftNeighbours.length; i++) {
                    GridBagConstraints currentGBC=contaiterLayout.getConstraints(leftNeighbours[i]);
                    currentGBC.gridwidth+=componentGBC.gridwidth;
                    this.remove(leftNeighbours[i]);
                    this.add(leftNeighbours[i],currentGBC);
                    this.remove(component);
                }
                removed=true;
            }
        }
        Component[] rightNeighbours=getRightNeighbours(componentGBC.gridx+componentGBC.gridwidth,componentGBC.gridy,componentGBC.gridheight);
        if ((rightNeighbours.length>0) && (!removed)){
            GridBagConstraints first=contaiterLayout.getConstraints(rightNeighbours[0]);
            GridBagConstraints last=contaiterLayout.getConstraints(rightNeighbours[rightNeighbours.length-1]);

            if ((first.gridy==componentGBC.gridy) && (last.gridy+last.gridheight==componentGBC.gridy+componentGBC.gridheight)) {
                for (int i=0; i<rightNeighbours.length; i++) {
                    GridBagConstraints currentGBC=contaiterLayout.getConstraints(rightNeighbours[i]);
                    currentGBC.gridx-=componentGBC.gridwidth;
                    currentGBC.gridwidth+=componentGBC.gridwidth;
                    this.remove(rightNeighbours[i]);
                    this.add(rightNeighbours[i],currentGBC);
                    this.remove(component);
                }
                removed=true;
            }
        }
        Component[] topNeighbours=getTopNeighbours(componentGBC.gridx,componentGBC.gridy,componentGBC.gridwidth);
        if ((topNeighbours.length>0) && (!removed)) {
            GridBagConstraints first=contaiterLayout.getConstraints(topNeighbours[0]);
            GridBagConstraints last=contaiterLayout.getConstraints(topNeighbours[topNeighbours.length-1]);

            if ((first.gridx==componentGBC.gridx) && (last.gridx+last.gridwidth==componentGBC.gridx+componentGBC.gridwidth)) {
                for (int i=0; i<topNeighbours.length; i++) {
                    GridBagConstraints currentGBC=contaiterLayout.getConstraints(topNeighbours[i]);
                    currentGBC.gridheight+=componentGBC.gridheight;
                    this.remove(topNeighbours[i]);
                    this.add(topNeighbours[i],currentGBC);
                    this.remove(component);
                }
                removed=true;
            }
        }
        Component[] bottomNeighbours=getBottomNeighbours(componentGBC.gridx,componentGBC.gridy+componentGBC.gridheight,componentGBC.gridwidth);
        if ((bottomNeighbours.length>0) && (!removed)) {
            GridBagConstraints first=contaiterLayout.getConstraints(bottomNeighbours[0]);
            GridBagConstraints last=contaiterLayout.getConstraints(bottomNeighbours[bottomNeighbours.length-1]);

            if ((first.gridx==componentGBC.gridx) && (last.gridx+last.gridwidth==componentGBC.gridx+componentGBC.gridwidth)) {
                for (int i=0; i<bottomNeighbours.length; i++) {
                    GridBagConstraints currentGBC=contaiterLayout.getConstraints(bottomNeighbours[i]);
                    currentGBC.gridy-=componentGBC.gridheight;
                    currentGBC.gridheight+=componentGBC.gridheight;
                    this.remove(bottomNeighbours[i]);
                    this.add(bottomNeighbours[i],currentGBC);
                    this.remove(component);
                }
                removed=true;
            }
        }

        for  (int i=0; i<components.length; i++) {
            GridBagConstraints currentGBC=contaiterLayout.getConstraints(components[i]);
            componentConstraints[i]=currentGBC;
            if (currentGBC.gridx+currentGBC.gridwidth>gridWidth) {
                gridWidth=currentGBC.gridx+currentGBC.gridwidth;
            }
            if (currentGBC.gridy+currentGBC.gridheight>gridHeight) {
                gridHeight=currentGBC.gridy+currentGBC.gridheight;
            }
        }

        if (!removed) {
            this.remove(component);
            components=this.getComponents();
            for  (int i=0; i<components.length; i++) {
                componentConstraints[i]=contaiterLayout.getConstraints(components[i]);
            }
            performLayout(components,componentConstraints,gridWidth);
            this.removeAll();
            for  (int i=0; i<components.length; i++) {
                 this.add(components[i],componentConstraints[i]);
            }
        }
    }

    protected void performLayout(Component[] components,GridBagConstraints[] componentConstraints, int gridWidth) {
        //sort components their according to gridx and gridy (ascending)
        int count=components.length;
        for (int i=count-1; i>=0;i--) {
            for (int j=0; j<i; j++) {
                int ord=componentConstraints[j].gridy*gridWidth+componentConstraints[j].gridx;
                int nextOrd=componentConstraints[j+1].gridy*gridWidth+componentConstraints[j+1].gridx;
                if (ord>nextOrd) {
                    GridBagConstraints tempGBC=componentConstraints[j+1];
                    componentConstraints[j+1]=componentConstraints[j];
                    componentConstraints[j]=tempGBC;

                    Component tempC=components[j+1];
                    components[j+1]=components[j];
                    components[j]=tempC;
                }
            }
        }


/*        for (int i=0; i<count; i++) {
            System.err.println("gridy="+componentConstraints[i].gridy+" gridx="+componentConstraints[i].gridx);
        }*/
        //layout components
        for (int i=0; i<count-1; i++) {
            GridBagConstraints currentGBC=componentConstraints[i];
            GridBagConstraints nextGBC=componentConstraints[i+1];

            currentGBC.gridheight=1;
            if (currentGBC.gridy==nextGBC.gridy) {
                currentGBC.gridwidth=nextGBC.gridx-currentGBC.gridx;
            }
            else {
                currentGBC.gridwidth=gridWidth-currentGBC.gridx;
                //removing excessive rows
                if (currentGBC.gridy<nextGBC.gridy-1) {
                    int rowCount=nextGBC.gridy-currentGBC.gridy-1;
                    for (int j=i+1; j<count; j++) {
                        componentConstraints[j].gridy-=rowCount;
                    }
                }
                nextGBC.gridx=0;
            }
        }
        if (count>0)
            componentConstraints[count-1].gridwidth=gridWidth-componentConstraints[count-1].gridx;
    }

    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     */
    public void mouseClicked(MouseEvent e) {
/*        endDragX=e.getX();
        endDragY=e.getY();
        drop();*/
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
        startDragX=e.getX();
        startDragY=e.getY();
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
        if (drag) {
            endDragX=e.getX();
            endDragY=e.getY();
//            System.err.println("startDragX="+startDragX+" startDragY="+startDragY+" endDragX="+endDragX+" endDragY="+endDragY);
            drop();
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        draggedWindow.setVisible(false);
        drag=false;
    }

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button is pressed on a component and then
     * dragged.  <code>MOUSE_DRAGGED</code> events will continue to be
     * delivered to the component where the drag originated until the
     * mouse button is released (regardless of whether the mouse position
     * is within the bounds of the component).
     * <p>
     * Due to platform-dependent Drag&Drop implementations,
     * <code>MOUSE_DRAGGED</code> events may not be delivered during a native
     * Drag&Drop operation.
     */
    public void mouseDragged(MouseEvent e) {
//        System.err.println("x="+e.getX()+" y="+e.getY());
        drag=true;
        Component target=this.getComponentAt(e.getX(),e.getY());
        Rectangle targetBounds;
        if (this.getCursor().getType()!=Cursor.MOVE_CURSOR) {
            this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        }
        if (target==null) {
            targetBounds=new Rectangle(0,0,0,0);
        }
        else {
            targetBounds=target.getBounds();
        }
        int targetPlace=getTargetPlace(targetBounds,e.getX(),e.getY());
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component
     * but no buttons have been pushed.
     */
    public void mouseMoved(MouseEvent e) {

    }

    public int getTargetPlace(Rectangle targetBounds, int x, int y) {
        int topSpace=y-targetBounds.y;
        int bottomSpace=targetBounds.y+targetBounds.height-y;

        int leftSpace=x-targetBounds.x;
        int rightSpace=targetBounds.x+targetBounds.width-x;

        int targetPlace=TARGET_TOP;
        int minSpace=topSpace;

        if (minSpace>bottomSpace) {
            targetPlace=TARGET_BOTTOM;
            minSpace=bottomSpace;
        }
        if (minSpace>leftSpace) {
            targetPlace=TARGET_LEFT;
            minSpace=leftSpace;
        }
        if (minSpace>rightSpace) {
            targetPlace=TARGET_RIGHT;
            minSpace=rightSpace;
        }
        return targetPlace;
    }

    protected Object[] getLeftSideComponents(int gridX,int gridY) {
        Vector result=new Vector();
        Component[] components=this.getComponents();
        for  (int i=0; i<components.length; i++) {
            GridBagConstraints currentGBC=contaiterLayout.getConstraints(components[i]);
            if (currentGBC.gridx+currentGBC.gridwidth==gridX) {
                result.add(components[i]);
            }
        }
        return result.toArray();
    }

    protected Object[] getRightSideComponents(int gridX,int gridY) {
        Vector result=new Vector();
        Component[] components=this.getComponents();
        for  (int i=0; i<components.length; i++) {
            GridBagConstraints currentGBC=contaiterLayout.getConstraints(components[i]);
            if (currentGBC.gridx==gridX) {
                result.add(components[i]);
            }
        }
        return result.toArray();
    }

    public void moveSplitterHorizontal(int gridX,int gridY,int x) {
        Object[] leftComponents=getLeftSideComponents(gridX,gridY);
        for (int i=0; i<leftComponents.length; i++) {
            int width=((JComponent)leftComponents[i]).getPreferredSize().width+x;
            int height=((JComponent)leftComponents[i]).getPreferredSize().height;
            ((JComponent)leftComponents[i]).setPreferredSize(new Dimension(width,height));
        }
        Object[] rightComponents=getRightSideComponents(gridX,gridY);
        for (int i=0; i<rightComponents.length; i++) {
            int width=((JComponent)rightComponents[i]).getPreferredSize().width-x;
            int height=((JComponent)rightComponents[i]).getPreferredSize().height;
            ((JComponent)rightComponents[i]).setPreferredSize(new Dimension(width,height));
        }
        refresh();
    }

    protected Object[] getTopSideComponents(int gridX,int gridY) {
        Vector result=new Vector();
        Component[] components=this.getComponents();
        for  (int i=0; i<components.length; i++) {
            GridBagConstraints currentGBC=contaiterLayout.getConstraints(components[i]);
            if (currentGBC.gridy+currentGBC.gridheight==gridY) {
                result.add(components[i]);
            }
        }
        return result.toArray();
    }

    protected Object[] getBottomSideComponents(int gridX,int gridY) {
        Vector result=new Vector();
        Component[] components=this.getComponents();
        for  (int i=0; i<components.length; i++) {
            GridBagConstraints currentGBC=contaiterLayout.getConstraints(components[i]);
            if (currentGBC.gridy==gridY) {
                result.add(components[i]);
            }
        }
        return result.toArray();
    }

    public void moveSplitterVertical(int gridX,int gridY,int y) {
        Object[] topComponents=getTopSideComponents(gridX,gridY);
        for (int i=0; i<topComponents.length; i++) {
            int height=((JComponent)topComponents[i]).getPreferredSize().height+y;
            int width=((JComponent)topComponents[i]).getPreferredSize().width;
            ((JComponent)topComponents[i]).setPreferredSize(new Dimension(width,height));
        }
        Object[] bottomComponents=getBottomSideComponents(gridX,gridY);
        for (int i=0; i<bottomComponents.length; i++) {
            int height=((JComponent)bottomComponents[i]).getPreferredSize().height-y;
            int width=((JComponent)bottomComponents[i]).getPreferredSize().width;
            ((JComponent)bottomComponents[i]).setPreferredSize(new Dimension(width,height));
        }
        refresh();
    }

    public void refresh() {
        this.invalidate();
        this.validate();
        this.repaint();

        Container c=this.getParent();
        while (c!=null) {
            if (c instanceof Window) {
                ((Window)c).repaint();
                break;
            }
            c=c.getParent();
        }
   }

   public void doLayout() {
       super.doLayout();
       Component[] components=this.getComponents();
       for  (int i=0; i<components.length; i++) {
           if (components[i] instanceof JComponent) {
               ((JComponent)components[i]).setPreferredSize(((JComponent)components[i]).getSize());
           }
       }
   }
}