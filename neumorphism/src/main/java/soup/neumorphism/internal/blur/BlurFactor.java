package soup.neumorphism.internal.blur;

import android.graphics.Color;

public class BlurFactor {

    public static final int DEFAULT_RADIUS = 25;
    public static final int DEFAULT_SAMPLING = 1;

    public int width;
    public int height;
    public int radius = DEFAULT_RADIUS;
    public int sampling = DEFAULT_SAMPLING;
    public int color = Color.TRANSPARENT;
}
