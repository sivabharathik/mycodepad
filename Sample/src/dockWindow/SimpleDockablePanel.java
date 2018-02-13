package dockWindow;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class SimpleDockablePanel extends JPanel  implements MouseListener, MouseMotionListener {

    protected int moveDirection=-1;

    protected JWindow dragWindow=null;
    protected Desktop ownerDesktop;
    protected Point desktopWindowLocation=new Point(0,0);

    protected static final int DIRECTION_NONE=-1;
    protected static final int DIRECTION_TOP=0;
    protected static final int DIRECTION_LEFT=1;
    protected static final int DIRECTION_RIGHT=2;
    protected static final int DIRECTION_BOTTOM=3;

    protected int startDragX=0;
    protected int startDragY=0;

    public static int DESKTOP_REGIM=0;
    public static int WINDOW_REGIM=1;
    protected int regim=DESKTOP_REGIM;

    public SimpleDockablePanel() {
        super();
//        dragWindow.getContentPane().setLayout(new BorderLayout());
        setPreferredSize(null);
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
        Container c=this.getParent();
        if (c==null) return;

        this.startDragX=e.getX();
        this.startDragY=e.getY();
        if (regim==DESKTOP_REGIM) {
            BufferedImage img=new BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.TYPE_INT_RGB);
            this.paint(img.getGraphics());
            if (dragWindow==null) {
                Container parent=c;
                Window owner=null;
                while (parent!=null) {
                    if (parent instanceof Window) {
                        owner=(Window)parent;
                    }
                    parent=parent.getParent();
                }
                dragWindow=new JWindow(owner);
                dragWindow.getContentPane().setLayout(new BorderLayout());
            }
            dragWindow.getContentPane().removeAll();
            dragWindow.getContentPane().add(new JLabel(new ImageIcon(img)));
            dragWindow.setSize(this.getWidth(),this.getHeight());
        }
        else {

        }

        if (!(c instanceof Desktop)) return;
        Desktop parentDesktop=(Desktop)c;
        this.ownerDesktop=parentDesktop;
        parentDesktop.startDragX=startDragX+this.getX();
        parentDesktop.startDragY=startDragY+this.getY();
        if (startDragY<2) {
            parentDesktop.splitDragY=startDragY+this.getY();
            parentDesktop.repaint(0,parentDesktop.splitDragY,parentDesktop.getWidth(),1);
            moveDirection=DIRECTION_TOP;
        }
        else if (startDragY>getHeight()-2) {
            parentDesktop.splitDragY=startDragY+this.getY();
            parentDesktop.repaint(0,parentDesktop.splitDragY,parentDesktop.getWidth(),1);
            moveDirection=DIRECTION_BOTTOM;
        }
        else if (startDragX<2) {
            parentDesktop.splitDragX=startDragX+this.getX();
            parentDesktop.repaint(parentDesktop.splitDragX,0,1,parentDesktop.getHeight());
            moveDirection=DIRECTION_LEFT;
        }
        else if (startDragX>getWidth()-2) {
            parentDesktop.splitDragX=startDragX+this.getX();
            parentDesktop.repaint(parentDesktop.splitDragX,0,1,parentDesktop.getHeight());
            moveDirection=DIRECTION_RIGHT;
        }
        else {
            parentDesktop.drag=true;
            parentDesktop.dragSourceComponent=this;
        }
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
        Container c=this.getParent();
        if (c==null) return;
        if (c instanceof Desktop) {
            Desktop parentDesktop=(Desktop)c;
            parentDesktop.endDragX=e.getX()+this.getX();
            parentDesktop.endDragY=e.getY()+this.getY();
            if (parentDesktop.drag) {
                if ((parentDesktop.endDragX<0) ||
                    (parentDesktop.endDragX>parentDesktop.getWidth()) ||
                    (parentDesktop.endDragY<0) ||
                    (parentDesktop.endDragY>parentDesktop.getHeight()) ) {
                    //drop outside the desktop
                    parentDesktop.removeComponent(this);
                    parentDesktop.refresh();
                    dragWindow.getContentPane().removeAll();
                    dragWindow.getContentPane().add(this);
                    dragWindow.setVisible(true);
                    desktopWindowLocation=dragWindow.getOwner().getLocation();
                    dragWindow.getOwner().addComponentListener(new ComponentAdapter() {
                        public void componentHidden(ComponentEvent e) {
                        }

                        public void componentMoved(ComponentEvent e) {
                            Point oldLocation=dragWindow.getLocation();
                            oldLocation.x+=dragWindow.getOwner().getX()-desktopWindowLocation.x;
                            oldLocation.y+=dragWindow.getOwner().getY()-desktopWindowLocation.y;
                            dragWindow.setLocation(oldLocation);
                            desktopWindowLocation=dragWindow.getOwner().getLocation();
                        }

                        public void componentResized(ComponentEvent e) {
                        }

                        public void componentShown(ComponentEvent e) {
                        }
                    });
                    regim=WINDOW_REGIM;
                }
                else {
                    parentDesktop.drop();
                    dragWindow.dispose();
                }
            }
            parentDesktop.drag=false;

            int oldDragX=parentDesktop.splitDragX;
            parentDesktop.splitDragX=-1;
            parentDesktop.repaint(oldDragX,0,1,parentDesktop.getHeight());
            if (oldDragX!=-1) {
                GridBagConstraints thisGBC=parentDesktop.contaiterLayout.getConstraints(this);
                int gridX=0;
                int gridY=thisGBC.gridy;
                int x=parentDesktop.endDragX-parentDesktop.startDragX;
                if (moveDirection==DIRECTION_LEFT) {
                    gridX=thisGBC.gridx;
                } else if (moveDirection==DIRECTION_RIGHT) {
                    gridX=thisGBC.gridx+thisGBC.gridwidth;
                }
                parentDesktop.moveSplitterHorizontal(gridX,gridY,x);
            }

            int oldDragY=parentDesktop.splitDragY;
            parentDesktop.splitDragY=-1;
            parentDesktop.repaint(0,oldDragY,parentDesktop.getWidth(),1);
            if (oldDragY!=-1) {
                GridBagConstraints thisGBC=parentDesktop.contaiterLayout.getConstraints(this);
                int gridX=thisGBC.gridx;
                int gridY=0;
                int y=parentDesktop.endDragY-parentDesktop.startDragY;
                if (moveDirection==DIRECTION_TOP) {
                    gridY=thisGBC.gridy;
                } else if (moveDirection==DIRECTION_BOTTOM) {
                    gridY=thisGBC.gridy+thisGBC.gridheight;
                }
                parentDesktop.moveSplitterVertical(gridX,gridY,y);
            }
            moveDirection=DIRECTION_NONE;
        }
        else { //window
            Point p=e.getPoint();
            SwingUtilities.convertPointToScreen(p,this);
            if (ownerDesktop!=null) {
                SwingUtilities.convertPointFromScreen(p,ownerDesktop);
                ownerDesktop.startDragX=-1;
                ownerDesktop.startDragY=-1;
                ownerDesktop.dragSourceComponent=this;

                ownerDesktop.endDragX=p.x;
                ownerDesktop.endDragY=p.y;
                if ((ownerDesktop.endDragX>0) &&
                    (ownerDesktop.endDragX<ownerDesktop.getWidth()) &&
                    (ownerDesktop.endDragY>0) &&
                    (ownerDesktop.endDragY<ownerDesktop.getHeight()) ) {
                    dragWindow.getContentPane().removeAll();
                    dragWindow.hide();
                    ownerDesktop.drop();
                    }
/*                if (parentDesktop.drag) {
                    if ((parentDesktop.endDragX<0) ||
                        (parentDesktop.endDragX>parentDesktop.getWidth()) ||
                        (parentDesktop.endDragY<0) ||
                        (parentDesktop.endDragY>parentDesktop.getHeight()) ) {
                        //drop outside the desktop
                        parentDesktop.removeComponent(this);
                        parentDesktop.refresh();
                        dragWindow.getContentPane().removeAll();
                        dragWindow.getContentPane().add(this);
                        dragWindow.setVisible(true);
                        regim=WINDOW_REGIM;
                    }
                    else {*/
//                    }
//                }
            }
        }
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
        Container c=this.getParent();
        if (c==null) return;
        if (c instanceof Desktop) {
            Desktop parentDesktop=(Desktop)c;
            if (parentDesktop.splitDragX>0) {
                int oldSplitX=parentDesktop.splitDragX;
                parentDesktop.splitDragX=-1;
                parentDesktop.repaint(oldSplitX,0,1,parentDesktop.getHeight());

                parentDesktop.splitDragX=e.getX()+this.getX();
                parentDesktop.repaint(parentDesktop.splitDragX,0,1,parentDesktop.getHeight());
            }
            if (parentDesktop.splitDragY>0) {
                int oldSplitY=parentDesktop.splitDragY;
                parentDesktop.splitDragY=-1;
                parentDesktop.repaint(0,oldSplitY,parentDesktop.getWidth(),1);

                parentDesktop.splitDragY=e.getY()+this.getY();
                parentDesktop.repaint(0,parentDesktop.splitDragY,parentDesktop.getWidth(),1);
            }

            if (parentDesktop.drag) {
                Point p=e.getPoint();
                SwingUtilities.convertPointToScreen(p,this);
                p.x-=startDragX;
                p.y-=startDragY;
                dragWindow.setLocation(p);
                dragWindow.setSize(this.getWidth(),getHeight());
                dragWindow.show();
            }
        }
        else {
            if ((startDragX>-1) && (startDragY>-1)) {
                Point p=e.getPoint();
                SwingUtilities.convertPointToScreen(p,this);
                p.x-=startDragX;
                p.y-=startDragY;
                dragWindow.setLocation(p);
            }
        }
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component
     * but no buttons have been pushed.
     */
    public void mouseMoved(MouseEvent e) {
        int x=e.getX();
        int y=e.getY();
        if (regim!=DESKTOP_REGIM)
            return;
        if (y<2) {
            this.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
        }
        else if (y>getHeight()-2) {
            this.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
        }
        else if (x<2) {
            this.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
        }
        else if (x>getWidth()-2) {
            this.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
        }
        else {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

    }
}