public class Vector3 {
  private double x;
  private double y;
  private double z;

  public Vector3(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /*
   * this + v
   */
  public Vector3 add(Vector3 v) {
    return new Vector3(x + v.x, y + v.y, z + v.z);
  }

  /*
   * this - v
   */
  public Vector3 subtract(Vector3 v) {
    return new Vector3(x - v.x, y - v.y, z - v.z);
  }

  public Vector3 multiply(double s) {
    return new Vector3(x * s, y * s, z * s);
  }

  public double dot(Vector3 v) {
    return x * v.x + y * v.y + z * v.z;
  }

  /*
   * this cross v
   */
  public Vector3 cross(Vector3 v) {
    return new Vector3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
  }

  public double magnitude() {
    return Math.sqrt(x * x + y * y + z * z);
  }

  public Vector3 normalise() {
    if (magnitude() == 0) {
      return this;
    }
    return multiply(1 / magnitude());
  }

  /*
   * this projected onto v
   */
  public Vector3 projection(Vector3 v) {
    return v.multiply(dot(v) / v.dot(v));
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  public String toString() {
    return "(" + x + ", " + y + ", " + z + ")";
  }
}
