public class RenderUtils {
  public static int brightness(int rgb, double brightness) {
    int r = (rgb >> 16) & 0xFF;
    int g = (rgb >> 8) & 0xFF;
    int b = (rgb >> 0) & 0xFF;
    int a = (rgb >> 24) & 0xFF;
    return (int) (a << 24) | (int) (r * brightness) << 16 | (int) (g * brightness) << 8 | (int) (b * brightness) << 0;
  }

  public static int alphaBlend(int overlay, int background) {
    int alpha = (overlay >> 24) & 0xff;
    int invAlpha = 256 - alpha;
    int red = ((overlay & 0xff0000) * alpha + (background & 0xff0000) * invAlpha) >> 8;
    int green = ((overlay & 0xff00) * alpha + (background & 0xff00) * invAlpha) >> 8;
    int blue = ((overlay & 0xff) * alpha + (background & 0xff) * invAlpha) >> 8;
    return 0xff000000 | (red & 0xFF0000) | (green & 0x00FF00) | (blue & 0x0000FF);
  }

  public static boolean isInside(Vector2 pt, Vector2[] points) {
    return isInsideTriangle(pt, points[0], points[1], points[2])
        || isInsideTriangle(pt, points[1], points[2], points[3]);
  }

  private static boolean isInsideTriangle(Vector2 pt, Vector2 pt1, Vector2 pt2, Vector2 pt3) {
    double area = Math.abs((pt1.getX() * (pt2.getY() - pt3.getY()) + pt2.getX() * (pt3.getY() - pt1.getY())
        + pt3.getX() * (pt1.getY() - pt2.getY())) / 2.0);

    double area1 = Math.abs((pt1.getX() * (pt2.getY() - pt.getY()) + pt2.getX() * (pt.getY() - pt1.getY())
        + pt.getX() * (pt1.getY() - pt2.getY())) / 2.0);
    double area2 = Math.abs((pt1.getX() * (pt.getY() - pt3.getY()) + pt.getX() * (pt3.getY() - pt1.getY())
        + pt3.getX() * (pt1.getY() - pt.getY())) / 2.0);
    double area3 = Math.abs((pt.getX() * (pt2.getY() - pt3.getY()) + pt2.getX() * (pt3.getY() - pt.getY())
        + pt3.getX() * (pt.getY() - pt2.getY())) / 2.0);

    // Check if the sum of the areas of the three triangles is equal to the area of
    // the triangle
    return Math.round(area) == Math.round(area1 + area2 + area3);
  }
}
