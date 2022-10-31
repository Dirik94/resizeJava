import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ResizeRectangle extends JPanel {
    Rectangle2D rectangle;

    Ellipse2D ellipse;
    Line2D line;
    double TopPointX,TopPointY,BottomPointX,BottomPointY;
    private Rectangle2D[] points;
    ShapeResizeHandler ada = new ShapeResizeHandler();

    public ResizeRectangle(Line2D.Double line) {
        this.line = line;
        this.TopPointX = line.getX1();
        this.TopPointY = line.getY1();
        this.BottomPointX = line.getX2();
        this.BottomPointY = line.getY2();
        this.points = new Rectangle2D[]{new Rectangle2D.Double(TopPointX, TopPointY, SIZE, SIZE),
                new Rectangle2D.Double(BottomPointX, BottomPointY, SIZE, SIZE),
                new Rectangle2D.Double(BottomPointX - TopPointX, BottomPointY - TopPointX, SIZE, SIZE)};
        addMouseListener(ada);
        addMouseMotionListener(ada);
    }
    public ResizeRectangle( Rectangle2D.Double rectangle){
        this.rectangle = rectangle;
        this.TopPointX = rectangle.getX();
        this.TopPointY = rectangle.getY();
        this.BottomPointX = rectangle.getHeight();
        this.BottomPointY = rectangle.getWidth();
        this.points = new Rectangle2D[]{new Rectangle2D.Double(TopPointX, TopPointY, SIZE, SIZE),
                new Rectangle2D.Double(BottomPointX, BottomPointY, SIZE, SIZE),
                new Rectangle2D.Double(BottomPointX - TopPointX, BottomPointY - TopPointX, SIZE, SIZE)};
        addMouseListener(ada);
        addMouseMotionListener(ada);
    }
    public ResizeRectangle(Ellipse2D.Double ellipse){
        this.ellipse = ellipse;
        this.TopPointX = ellipse.getX();
        this.TopPointY = ellipse.getY();
        this.BottomPointX = ellipse.getHeight();
        this.BottomPointY = ellipse.getWidth();
        this.points = new Rectangle2D[]{new Rectangle2D.Double(TopPointX, TopPointY, SIZE, SIZE),
                new Rectangle2D.Double(BottomPointX, BottomPointY, SIZE, SIZE),
                new Rectangle2D.Double(BottomPointX - TopPointX, BottomPointY - TopPointX, SIZE, SIZE)};
        addMouseListener(ada);
        addMouseMotionListener(ada);
    }

    private int SIZE = 8;
    //Below are 3 points, points[0] and [1] and top-left and bottom-right of the shape.
    // points[2] is the center of the shape
//    private Rectangle2D[] points = { new Rectangle2D.Double(TopPointX, TopPointY,SIZE, SIZE),
//            new Rectangle2D.Double(150, 150,SIZE, SIZE),
//            new Rectangle2D.Double(100, 100,SIZE, SIZE)};

//    points = { new Rectangle2D.Double(TopPointX, TopPointY,SIZE, SIZE),
//            new Rectangle2D.Double(BottomPointX, BottomPointY,SIZE, SIZE),
//            new Rectangle2D.Double(BottomPointX/2, BottomPointY/2,SIZE, SIZE)};

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        for (int i = 0; i < points.length; i++) {
            g2.fill(points[i]);
        }

        if(line != null) {
            line.setLine(points[0].getX(), points[0].getY(),points[1].getX(), points[1].getY());
            g2.draw(line);
        }

        if(rectangle != null) {
            rectangle.setFrame(points[0].getCenterX(), points[0].getCenterY(),
                    Math.abs(points[1].getCenterX()-points[0].getCenterX()),
                    Math.abs(points[1].getCenterY()- points[0].getCenterY()));
            g2.draw(rectangle);
        }

        if(ellipse != null) {
            ellipse.setFrame(points[0].getCenterX(), points[0].getCenterY(),
                    Math.abs(points[1].getCenterX()-points[0].getCenterX()),
                    Math.abs(points[1].getCenterY()- points[0].getCenterY()));
            g2.draw(ellipse);
        }
    }

    class ShapeResizeHandler extends MouseAdapter {

        private Point2D[] lastPoints = new Point2D[3];
        private int pos = -1;
        public void mousePressed(MouseEvent event) {
            Point p = event.getPoint();

            for (int i = 0; i < points.length; i++) {
                if (points[i].contains(p)) {
                    pos = i;
                    // initialize preDrag points
                    for(int j = 0; j < 3; j++){
                        lastPoints[j] = new Point2D.Double(points[j].getX(), points[j].getY());
                    }
                    return;
                }
            }
        }

        public void mouseReleased(MouseEvent event) {
            pos = -1;
        }

        public void mouseDragged(MouseEvent event) {
            if (pos == -1)
                return;
            if(pos != 2){ //if 2, it's a shape drag
                points[pos].setRect(event.getPoint().x,event.getPoint().y,points[pos].getWidth(),
                        points[pos].getHeight());
                int otherEnd = (pos==1)?0:1; //Get the end other than what is being dragged (top-left or bottom-right)
                //Get the x,y of the centre of the line joining the 2 new diagonal vertices, which will be new points[2]
                double newPoint2X = points[otherEnd].getX() + (points[pos].getX() - points[otherEnd].getX())/2;
                double newPoint2Y = points[otherEnd].getY() + (points[pos].getY() - points[otherEnd].getY())/2;
                points[2].setRect(newPoint2X, newPoint2Y, points[2].getWidth(), points[2].getHeight());
            }
            else{ //Shape drag, 1,2,3 points/marker rects need to move equal amounts
                Double deltaX = event.getPoint().x - lastPoints[2].getX();
                Double deltaY = event.getPoint().y - lastPoints[2].getY();
                for(int j = 0; j < 3; j++)
                    points[j].setRect((lastPoints[j].getX() + deltaX),(lastPoints[j].getY() + deltaY),points[j].getWidth(),
                            points[j].getHeight());

            }
            repaint();
        }
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Resize Shape2D");
//        Line2D.Double line = new Line2D.Double(50,50,150,150);
        Ellipse2D.Double rect = new Ellipse2D.Double(50,50,150,150);
        frame.add(new ResizeRectangle(rect));
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}