public class Plane {
  private Vector3 normalVector;
  private double d;

  public Plane(double a, double b, double c, double d) {
    this(new Vector3(a, b, c), d);
  }

  public Plane(Vector3 normalVector, Vector3 point) {
    this(normalVector, -normalVector.dot(point));
  }

  public Plane(Vector3 firstVector, Vector3 secondVector, Vector3 point) {
    this(firstVector.cross(secondVector), point);
  }

  public Plane(Vector3 normalVector, double d) {
    this.normalVector = normalVector;
    this.d = d;
  }

  public double compare(Vector3 point) {
    return normalVector.dot(point) + d;
  }

  public Vector3 intersection(Vector3 directionVector, Vector3 point) {
    double t = -(normalVector.dot(point) + d) / normalVector.dot(directionVector);
    return point.add(directionVector.multiply(t));
  }

  public String toString() {
    return "Plane: " + normalVector + " * x + " + d + " = 0";
  }
}
