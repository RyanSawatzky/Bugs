package bugs.util;

public class HSL
{
   private final int hue;
   private final int saturation;
   private final int luminence;
   
   public HSL(int hue, int saturation, int luminence)
   {
      this.hue = hue;
      this.saturation = saturation;
      this.luminence = luminence;
   }
   
   public int getHue()
   {  return hue; }
   
   public int getSaturation()
   {  return saturation;   }
   
   public int getLuminence()
   {  return luminence; }
}
