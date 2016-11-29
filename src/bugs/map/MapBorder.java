package bugs.map;

public class MapBorder extends MapNormal
{
   private int border;

   public MapBorder(int width, int height)
   {
      super(width, height);

      border = 0;
   }

   @Override
   public void growFood(long foodToGrow)
   {
      for(long f = 0; f < foodToGrow; f++)
      {
         int x = rand.nextInt(width - (border * 2)) + border;
         int y = rand.nextInt(height - (border * 2)) + border;
         food[y][x] = true;
      }
   }

}
