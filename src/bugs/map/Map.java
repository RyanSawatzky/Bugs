package bugs.map;

import bugs.bugs.Bug;
import bugs.main.DrawInfo;

public interface Map
{
   public int getWidth();
   public int getHeight();
   public int boundX(int x);
   public int boundY(int y);
   public int eatFoodAt(int x, int y);
   public void growFood(long foodToGrow);
   public void bugDied(Bug bug);
   public void drawFood(DrawInfo d);
}
