package bugs.main;

import bugs.bugs.Bugs;
import bugs.bugs.BugsNormal;
import bugs.map.Map;
import bugs.map.MapBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JPanel;

public class Game
         extends JPanel
{
//   public static final int MapWidth = 1024;
//   public static final int MapWidth = 1480;
   public static final int MapWidth = 1150;
   public static final int MapHeight = 1150;

   public static final int BugTurnMutation = 1;
   public static final double InitialFood = 0.15d;
   public static final double FoodPerTurn = 0.00006d;

   public static final int InitialBugs = 1;
   public static final int InitialBugEnergy = 1000;
   public static final int BugEnergyPerTurn = 10;
   public static final int EnergyInFood = 100;
   public static final int BugBreedPoint = 2000;

//   public static final int BugTurnMutation = 1;
//   public static final double InitialFood = 0.09536d;
//   public static final double FoodPerTurn = 0.00005722d;

   private final ConcurrentLinkedQueue<ScrollEvent> inputQueue;
   private final Map map;
   private final Bugs bugs;
   private final GameView gameView;
   
   private Point mouseLocation;

   public Game()
   {
      super.setDoubleBuffered(true);
      
      inputQueue = new ConcurrentLinkedQueue<>();
      map = new MapBorder(MapWidth, MapHeight);
      map.growFood(Math.round(InitialFood * MapWidth * MapHeight));
      bugs = new BugsNormal();
      bugs.setInitialBugs(map, InitialBugs);
      gameView = new GameView(map, bugs);
      mouseLocation = null;

      GameMouseListener gameMouseListener = new GameMouseListener();
      addMouseListener(gameMouseListener);
      addMouseMotionListener(gameMouseListener);
      addMouseWheelListener(gameMouseListener);
   }

   public void loop()
   {
      processInput();
      growFood();
      moveBugs();
      repaint();
   }

   private void processInput()
   {
      ScrollEvent event;
      while((event = inputQueue.poll()) != null)
      {
         if(event instanceof ScrollMovementEvent)
         {
            ScrollMovementEvent sme = (ScrollMovementEvent)event;
            gameView.scrollView(this, sme.old, sme.current);
         }
         else if(event instanceof ScrollZoomEvent)
            gameView.zoomView(((ScrollZoomEvent) event).getZoomAdjustment());
         else if(event instanceof DebugEvent)
            gameView.setDebug(((DebugEvent) event).value);
      }
   }

   private void growFood()
   {
      map.growFood(Math.round(FoodPerTurn * MapWidth * MapHeight));
   }

   private void moveBugs()
   {
      bugs.move(map);
   }

   @Override
   public void paint(Graphics g)
   {
      super.paint(g);

      Graphics2D g2d = (Graphics2D)g;
      Color oldColor = g2d.getColor();
      Font oldFont = g2d.getFont();
      RenderingHints oldRenderingHints = g2d.getRenderingHints();

      gameView.draw(new DrawInfo(g2d, this, mouseLocation));
      
      g2d.setColor(oldColor);
      g2d.setFont(oldFont);
      g2d.setRenderingHints(oldRenderingHints);
   }

   private class GameMouseListener extends MouseAdapter
   {
      private boolean dragButtonDown = false;

      @Override
      public void mouseExited(MouseEvent e)
      {
         dragButtonDown = false;
         mouseLocation = null;
      }

      @Override
      public void mouseEntered(MouseEvent e)
      {
         dragButtonDown = false;
         mouseLocation = e.getPoint();
      }

      @Override
      public void mousePressed(MouseEvent e)
      {
         if(e.getButton() == MouseEvent.BUTTON1)
         {
            dragButtonDown = true;
            mouseLocation = e.getPoint();
            inputQueue.add(new DebugEvent(true));
         }
      }

      @Override
      public void mouseReleased(MouseEvent e)
      {
         if(e.getButton() == MouseEvent.BUTTON1)
         {
            dragButtonDown = false;
            mouseLocation = e.getPoint();
            inputQueue.add(new DebugEvent(false));
         }
      }
      
      @Override
      public void mouseMoved(MouseEvent e)
      {
         mouseLocation = e.getPoint();
      }

      @Override
      public void mouseDragged(MouseEvent e)
      {
         Point newLocation = e.getPoint();
         if(mouseLocation != null)
            inputQueue.add(new ScrollMovementEvent(mouseLocation, newLocation));
         mouseLocation = newLocation;
      }

      @Override
      public void mouseWheelMoved(MouseWheelEvent e)
      {
         mouseLocation = e.getPoint();
         if(mouseLocation != null)
         {
            Point compLocation = Game.this.getLocation();
            Dimension compSize = Game.this.getSize();
            Point center = new Point((compSize.width / 2) + compLocation.x,
                                     (compSize.height / 2) + compLocation.y);

            int deltaX = mouseLocation.x - center.x;
            int deltaY = mouseLocation.y - center.y;
            deltaX /= 32;
            deltaY /= 32;
            Point newLocation = new Point(center.x + deltaX, center.y + deltaY);
            inputQueue.add(new ScrollMovementEvent(newLocation, center));
         }
         inputQueue.add(new ScrollZoomEvent(e.getPreciseWheelRotation()));
      }
      
      private Dimension calculateDelta(Point oldPoint, Point newPoint)
      {
         return new Dimension(newPoint.x - oldPoint.x, newPoint.y - oldPoint.y);
      }
   }
   
   private static class ScrollEvent
   {
   }
   
   private static class DebugEvent extends ScrollEvent
   {
      private final boolean value;
      
      public DebugEvent(boolean value)
      {
         this.value = value;
      }
      
      public boolean getValue()
      {
         return value;
      }
   }
   
   private static class ScrollMovementEvent extends ScrollEvent
   {
      public final Point old;
      public final Point current;
      
      public ScrollMovementEvent(Point old, Point current)
      {
         this.old = old;
         this.current = current;
      }
   }
   
   private static class ScrollZoomEvent extends ScrollEvent
   {
      private final double zoom;
      
      public ScrollZoomEvent(double zoom)
      {
         this.zoom = zoom;
      }
      
      public double getZoomAdjustment()
      {
         return zoom;
      }
   }
}
