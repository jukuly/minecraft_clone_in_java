public class Camera {
  private Vector3 position;
  private Vector3 centreVector;
  private Vector3 verticalVector;
  private Vector3 horizontalVector;
  private double verticalRotation;
  private double horizontalRotation;
  private double horizontalFieldOfView;
  private Plane screen;

  public Camera(Vector3 position, double verticalRotation, double horizontalRotation, double aspectRatio,
      double horizontalFieldOfView) {
    this.position = position;
    this.verticalRotation = verticalRotation;
    this.horizontalRotation = horizontalRotation;
    this.horizontalFieldOfView = horizontalFieldOfView;
    setVectors();
  }

  public Camera(Vector3 position, double aspectRatio) {
    this(position, 0, 0, aspectRatio, Math.PI / 2);
  }

  public void setPosition(Vector3 position) {
    this.position = position;
  }

  private void setVectors() {
    this.centreVector = new Vector3(
        Math.cos(verticalRotation) * Math.cos(horizontalRotation),
        Math.sin(horizontalRotation),
        Math.sin(verticalRotation) * Math.cos(horizontalRotation));
    this.verticalVector = new Vector3(
        Math.cos(verticalRotation) * -Math.sin(horizontalRotation),
        Math.cos(horizontalRotation),
        Math.sin(verticalRotation) * -Math.sin(horizontalRotation));
    this.horizontalVector = centreVector.cross(verticalVector);
    setScreen();
  }

  private void setScreen() {
    Vector3 screenCentre = centreVector.multiply(1 / Math.tan(horizontalFieldOfView / 2));
    screen = new Plane(centreVector, screenCentre);
  }

  public void setRotation(double verticalRotation, double horizontalRotation) {
    this.verticalRotation = verticalRotation % (2 * Math.PI);
    if (this.verticalRotation < 0) {
      this.verticalRotation += 2 * Math.PI;
    }
    this.horizontalRotation = Math.max(Math.min(horizontalRotation, Math.PI / 2), -Math.PI / 2);
    setVectors();
  }

  public boolean isInFOV(Vector3 point) {
    // this is not the right way to do it, but it is good enough for now
    // TODO: do that properly
    return centreVector.dot(point.subtract(position).normalise()) > Math.cos(horizontalFieldOfView / 2);
  }

  public Vector2 getPointProjectionOnScreen(Vector3 point, double screenWidth, double screenHeight) {

    // If the point is behind the camera, return null
    if (centreVector.dot(point.subtract(position)) <= 0) {
      return null;
    }

    Vector3 positionOnScreen = screen.intersection(point.subtract(position), new Vector3(0, 0, 0));
    Vector3 horizontalProjection = positionOnScreen.projection(horizontalVector);
    Vector3 verticalProjection = positionOnScreen.projection(verticalVector);

    // Accounts for points that could be just outside the screen
    double x = horizontalProjection.magnitude() * Math.signum(horizontalProjection.dot(horizontalVector));
    double y = verticalProjection.magnitude() * Math.signum(verticalProjection.dot(verticalVector));

    return new Vector2(-x * screenWidth / 2 + screenWidth / 2,
        // screenWidth because that is by how much we have to scale the coordinates
        // (both x and y) to fit the screen
        -y * screenWidth / 2 + screenHeight / 2);
  }

  public Vector3 getPoint3D(Vector2 point, Plane plane, double screenWidth, double screenHeight) {
    Vector3 positionOnScreen = horizontalVector.multiply(-(screenWidth / 2 - point.getX()) / (screenWidth / 2))
        .add(verticalVector.multiply(-(screenHeight / 2 - point.getY()) / (screenWidth / 2)))
        .add(position.subtract(centreVector.multiply(1 / Math.tan(horizontalFieldOfView / 2))));
    Vector3 positionOnFace = plane.intersection(positionOnScreen.subtract(position), position);

    return positionOnFace;
  }

  public Vector3 getPosition() {
    return position;
  }

  public double getVerticalRotation() {
    return verticalRotation;
  }

  public double getHorizontalRotation() {
    return horizontalRotation;
  }
}