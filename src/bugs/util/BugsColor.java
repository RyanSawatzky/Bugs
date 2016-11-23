package bugs.util;

import java.awt.Color;

public class BugsColor
{
   private final Color rgb;
   private final HSL hsl;
   
   private BugsColor(Color rgb, HSL hsl)
   {
      this.rgb = rgb;
      this.hsl = hsl;
   }

   public Color toRgbColor()
   {
      return rgb;
   }
   
   public HSL toHslColor()
   {
      return hsl;
   }
   
   public static final BugsColor fromRgb(Color rgb)
   {
      int[] ints = new int[3];
      float[] floats = RgbToHslFloats(rgb);

      for(int i = 0; i < 3; i++)
         ints[i] = floatToInt(floats[i]);

      return new BugsColor(rgb, new HSL(ints[0], ints[1], ints[2]));
   }
   
   public static final BugsColor fromHsl(HSL hsl)
   {
      float hf = intToFloat(Math.max(0, Math.min(255, hsl.getHue())));
      float sf = intToFloat(Math.max(0, Math.min(255, hsl.getSaturation())));
      float lf = intToFloat(Math.max(0, Math.min(255, hsl.getLuminence())));

      return new BugsColor(HSLtoColor(hf, sf, lf), hsl);
   }


   private static float intToFloat( int val )
   {
      val = Math.max( 0, Math.min( 255, val ) );
      return( (float)(val + 1) / 256.0f );
   }

   private static int floatToInt( float val )
   {
      val = Math.min( 1.0f, Math.max( 0.0f, val ) );

      float f_val = val * 256.0f;
      int   i_val = Math.round( f_val ) - 1;

      return Math.max( 0, Math.min( 255, i_val ) );
   }

   private static final float ONE_SIXTH  = (1.0f/6.0f);
   private static final float ONE_THIRD  = (1.0f/3.0f);
   private static final float ONE_HALF   = (1.0f/2.0f);
   private static final float TWO_THIRDS = (2.0f/3.0f);

   /**
    * Converts the specified Color object into the HSL color model. This method
    * returns a float array with three members. The first member is the hue,
    * the second member is the saturation, and the third member is the
    * luminance value. All returned float values are in the range [0.0, 1.0].
    *
    * This method is somewhat time expensive, so results from the method should
    * be cached or precomputed as much as possible.
    *
    * @param color the Color to convert to HSL
    * @return an array containing the HSL values as floats [0.0, 1.0]
    */
   private static float[] RgbToHslFloats( Color color )
   {
      int i_r   = color.getRed();
      int i_g   = color.getGreen();
      int i_b   = color.getBlue();
      int i_max = Math.max( i_r, Math.max( i_g, i_b ) );
      int i_min = Math.min( i_r, Math.min( i_g, i_b ) );

      float r   = intToFloat( i_r );
      float g   = intToFloat( i_g );
      float b   = intToFloat( i_b );
      float max = intToFloat( i_max );
      float min = intToFloat( i_min );

      float h = 0.0f;
      if( i_max == i_r )
      {
         h = ONE_SIXTH * (( g - b ) / ( max - min )) + 1.0f;
      }
      else if( i_max == i_g )
      {
         h = ONE_SIXTH * (( b - r ) / ( max - min )) + ONE_THIRD;
      }
      else if( i_max == i_b )
      {
         h = ONE_SIXTH * (( r - g ) / ( max - min )) + TWO_THIRDS;
      }

      if( h > 1.0f )
         h -= 1.0f;
      else if( h < 0.0f )
         h += 1.0f;

      float l = ONE_HALF * ( max + min );

      float s = 0.0f;
      if( i_max != i_min )
      {
         if( l <= 0.5f )
         {
            s = (max - min) / (max + min);
         }
         else
         {
            s = (max - min) / (2 - (max + min));
         }
      }

      float[] floats = new float[ 3 ];
      floats[ 0 ] = h;
      floats[ 1 ] = s;
      floats[ 2 ] = l;

      return floats;
   }

   /**
    * Converts the specified HSL values into a Color object.
    *
    * This method is somewhat time expensive, so results from the method should
    * be cached or precomputed as much as possible.
    *
    * @param h the hue value in the range [0.0, 1.0]
    * @param s the saturation value in the range [0.0, 1.0]
    * @param l the luminance value in the range [0.0, 1.0]
    * @return
    */
   public static Color HSLtoColor(float h, float s, float l)
   {
      h = Math.max( 0.0f, Math.min( 1.0f, h ) );
      s = Math.max( 0.0f, Math.min( 1.0f, s ) );
      l = Math.max( 0.0f, Math.min( 1.0f, l ) );

      float q = 0.0f;
      if( l < 0.5f )
         q = ( l * ( 1.0f + s ) );
      else
         q = ( l + s - ( l * s ) );

      float p = ( 2 * l ) - q;

      float trgb[] = new float[ 3 ];
      trgb[ 0 ] = h + ONE_THIRD;
      trgb[ 1 ] = h;
      trgb[ 2 ] = h - ONE_THIRD;

      for( int i = 0; i < 3; i++ )
      {
         if( trgb[ i ] < 0.0f )
            trgb[ i ] = trgb[ i ] + 1.0f;
         else if( trgb[ i ] > 1.0f )
            trgb[ i ] = trgb[ i ] - 1.0f;
      }

      float rgb[] = new float[ 3 ];
      for( int i = 0; i < 3; i++ )
      {
         if( trgb[i] < ONE_SIXTH )
         {
            rgb[i] = p + ((q - p) * 6.0f * trgb[i]);
         }
         else if( trgb[i] < ONE_HALF )
         {
            rgb[i] = q;
         }
         else if( trgb[i] < TWO_THIRDS )
         {
            rgb[i] = p + ((q - p) * 6.0f * (TWO_THIRDS - trgb[i] ));
         }
         else
         {
            rgb[i] = p;
         }
      }

      return new Color( floatToInt( rgb[0] ),
                        floatToInt( rgb[1] ),
                        floatToInt( rgb[2] ) );
   }
}
