public class Vector2 {

  private double x;
  private double y;

  public Vector2(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /*
   * this + v
   */
  public Vector2 add(Vector2 v) {
    return new Vector2(x + v.x, y + v.y);
  }

  /*
   * this - v
   */
  public Vector2 subtract(Vector2 v) {
    return new Vector2(x - v.x, y - v.y);
  }

  public Vector2 multiply(double s) {
    return new Vector2(x * s, y * s);
  }

  public double dot(Vector2 v) {
    return x * v.x + y * v.y;
  }

  public double magnitude() {
    return Math.sqrt(x * x + y * y);
  }

  public Vector2 normalise() {
    if (magnitude() == 0) {
      return this;
    }
    return multiply(1 / magnitude());
  }

  /*
   * this projected onto v
   */
  public Vector2 projection(Vector2 v) {
    return v.multiply(dot(v) / v.dot(v));
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}
