package bugs.bugs;

import bugs.main.DrawInfo;
import bugs.main.Game;
import bugs.map.Map;
import bugs.util.BugsColor;
import bugs.util.HSL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class BugsNormal implements Bugs
{
   private static final Random rand = new Random();
   static
   {
      rand.setSeed(System.currentTimeMillis());
   }


   private final List<Bug> bugs;

   public BugsNormal()
   {
      bugs = new CopyOnWriteArrayList<>();
   }

   @Override
   public void setInitialBugs(Map map, int numberBugs)
   {
      for(int i = 0; i < numberBugs; i++)
      {
         bugs.add(createNewBug(map, null));
      }
   }

   private static final int turnBug(Bug bug)
   {
      int newDirection = bug.direction;
      int roll = rand.nextInt(100);
      int turn;
      for(turn = 0; turn < 8; turn++)
      {
         if(roll < bug.turns[turn])
            break;
         else
            roll -= bug.turns[turn];
      }
      switch(turn)
      {
         case Bug.TurnNo:
            break;
         case Bug.TurnSlightLeft:
            newDirection -= 1;
            break;
         case Bug.TurnSlightRight:
            newDirection += 1;
            break;
         case Bug.TurnLeft:
            newDirection -= 2;
            break;
         case Bug.TurnRight:
            newDirection += 2;
            break;
         case Bug.TurnHardLeft:
            newDirection -= 3;
            break;
         case Bug.TurnHardRight:
            newDirection += 3;
            break;
         case Bug.TurnAround:
            newDirection += 4;
            break;
      }
      while(newDirection < 0)
         newDirection += 8;
      while(newDirection >= 8)
         newDirection -= 8;
      return newDirection;
   }

   private static final void moveBug(Map map, Bug bug)
   {
      int x = bug.x;
      int y = bug.y;
      switch(bug.direction)
      {
         case Bug.NorthWest:
            x--;
            y--;
            break;
         case Bug.North:
            y--;
            break;
         case Bug.NorthEast:
            x++;
            y--;
            break;
         case Bug.East:
            x++;
            break;
         case Bug.SouthEast:
            x++;
            y++;
            break;
         case Bug.South:
            y++;
            break;
         case Bug.SouthWest:
            x--;
            y++;
            break;
         case Bug.West:
            x--;
            break;
      }
      if(y < 0)
         y += map.getHeight();
      if(y >= map.getHeight())
         y -= map.getHeight();
      bug.x = map.boundX(x);
      bug.y = map.boundY(y);
   }

   private static final void bugEat(Map map, Bug bug)
   {
      for(int h = 0; h < 3; h++)
      {
         for(int w = 0; w < 3; w++)
         {
            bug.energy += map.eatFoodAt(bug.x + w, bug.y + h);
         }
      }
   }

   @Override
   public void move(Map map)
   {
      List<Bug> newBugs = new LinkedList<Bug>();
      List<Bug> deadBugs = new LinkedList<Bug>();

      for(Bug bug : bugs)
      {
         bug.direction = turnBug(bug);
         moveBug(map, bug);
         bugEat(map, bug);

         bug.energy -= Game.BugEnergyPerTurn;
         if(bug.energy <= 0)
            deadBugs.add(bug);
         else if(bug.energy >= Game.BugBreedPoint)
            newBugs.add(createNewBug(map, bug));
      }

      bugs.removeAll(deadBugs);
      bugs.addAll(newBugs);
   }

   @Override
   public void drawBugs(DrawInfo d)
   {
      for(Bug bug : bugs)
      {
         d.g.setColor(bug.color.toRgbColor());
         d.g.fillRect(bug.x, bug.y, 3, 3);
      }
   }
   
   private static int newBugHue()
   {
      int hue = rand.nextInt(200);
      if(hue > 54)
         hue += 110 + (hue - 54);
      return hue;
   }

   private static int newBugHue(int oldHue, boolean freak)
   {
      int change = 0;
      if(freak == true)
         change = 100;
      else
         change = rand.nextInt(3) - 1;

      int newHue = oldHue + change;
      if(newHue < 0)
         newHue = 256 + newHue;
      if(newHue > 255)
         newHue = newHue - 256;
      if(newHue > 54 && newHue < 111)
      {
         if(change > 0)
            newHue = 110 + (newHue - 54);
         if(change < 0)
            newHue = 55 - (111 - newHue);
      }
      return newHue;
   }

   private Bug createNewBug(Map map, Bug parent)
   {
      Bug newBug = new Bug();
      if(parent == null)
      {
         newBug.energy = Game.InitialBugEnergy;
         newBug.x = rand.nextInt(map.getWidth());
         newBug.y = rand.nextInt(map.getHeight());
         newBug.direction = rand.nextInt(8);
         newBug.color = BugsColor.fromHsl(new HSL(newBugHue(), 255, 150));

         for(int i = 0; i < 7; i++)
            newBug.turns[i] = 12;
         newBug.turns[7] = 16;
      }
      else
      {
         newBug.energy = parent.energy / 2;
         parent.energy = parent.energy - newBug.energy;
         newBug.x = parent.x;
         newBug.y = parent.y;
         newBug.direction = rand.nextInt(8);

         boolean freak = false;
         newBug.color = BugsColor.fromHsl(new HSL(newBugHue(parent.color.toHslColor().getHue(), freak), 255, 150));

         for(int i = 0; i < 8; i++)
            newBug.turns[i] = parent.turns[i];

         int turnFrom = rand.nextInt(8);
         int turnTo = rand.nextInt(8);
         
         int amount = freak ? 100 : Game.BugTurnMutation;
         if(newBug.turns[turnFrom] - amount < 0)
            amount = 0 - newBug.turns[turnFrom];
         if(newBug.turns[turnTo] + amount > 100)
            amount = 100 - newBug.turns[turnTo];

         newBug.turns[turnFrom] -= amount;
         newBug.turns[turnTo] += amount;
      }
      
      return newBug;
   }
}
