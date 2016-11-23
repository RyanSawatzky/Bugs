package bugs.bugs;

import bugs.util.BugsColor;

public class Bug
{
   public static final int North = 0;
   public static final int NorthEast = 1;
   public static final int East = 2;
   public static final int SouthEast = 3;
   public static final int South = 4;
   public static final int SouthWest = 5;
   public static final int West = 6;
   public static final int NorthWest = 7;

   public static final int TurnNo = 0;
   public static final int TurnSlightLeft = 1;
   public static final int TurnSlightRight = 2;
   public static final int TurnLeft = 3;
   public static final int TurnRight = 4;
   public static final int TurnHardLeft = 5;
   public static final int TurnHardRight = 6;
   public static final int TurnAround = 7;
   

   public int energy;
   public int x;
   public int y;
   public int direction;
   public BugsColor color;
   public int turns[];

   public Bug()
   {
      turns = new int[8];
   }
}
