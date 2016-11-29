package bugs.map;

import bugs.bugs.Bug;
import bugs.main.DrawInfo;
import bugs.main.Game;
import bugs.util.BugsColor;
import java.awt.Color;
import java.util.Random;

public class MapNormal implements Map
{
   protected final Random rand;
   protected final BugsColor foodColor;
   protected final int width;
   protected final int height;
   protected final boolean food[][];

   public MapNormal(int width, int height)
   {
      this.rand = new Random();
      this.rand.setSeed(System.currentTimeMillis());
      this.foodColor = BugsColor.fromRgb(new Color(0, 192, 0));
      this.width = width;
      this.height = height;
      this.food = new boolean[height][width];
   }

   @Override
   public int getWidth()
   {  return width;  }
   
   @Override
   public int getHeight()
   {  return height; }

   @Override
   public int boundX(int x)
   {
      while(x < 0)
         x += width;
      while(x >= width)
         x -= width;
      return x;
   }
   
   @Override
   public int boundY(int y)
   {
      while(y < 0)
         y += height;
      while(y >= height)
         y -= height;
      return y;
   }

   @Override
   public int eatFoodAt(int x, int y)
   {
      x = boundX(x);
      y = boundY(y);

      if(food[y][x] == true)
      {
         food[y][x] = false;
         return Game.EnergyInFood;
      }
      else
         return 0;
   }

   @Override
   public void growFood(long foodToGrow)
   {
      for(long f = 0; f < foodToGrow; f++)
      {
         int x = rand.nextInt(width);
         int y = rand.nextInt(height);
         food[y][x] = true;
      }
   }

   @Override
   public void bugDied(Bug bug)
   {
//      for(int r = 0; r < 3; r++)
//      {
//         for(int c = 0; c < 3; c++)
//         {
//            int y = r + bug.y;
//            int x = c + bug.x;
//            
//            if(y >= 0 && y < height && x >= 0 && x < width)
//               food[y][x] = true;
//         }
//      }
   }

   @Override
   public void drawFood(DrawInfo d)
   {
      d.g.setColor(foodColor.toRgbColor());
      for(int y = 0; y < height; y++)
      {
         for(int x = 0; x < width; x++)
         {
            if(food[y][x] == true)
               d.g.drawLine(x, y, x, y);
         }
      }
   }
}
