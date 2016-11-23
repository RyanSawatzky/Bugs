package bugs.bugs;

import bugs.main.DrawInfo;
import bugs.map.Map;

public interface Bugs
{
   public void setInitialBugs(Map map, int numberBugs);
   public void move(Map map);
   public void drawBugs(DrawInfo d);
}
