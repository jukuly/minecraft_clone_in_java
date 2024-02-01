import java.awt.image.BufferedImage;

public class Face {
  private Vector3[] vertices;
  private BlockType type;
  private Orientation orientation;
  private Plane plane;

  public Face(Vector3[] vertices, BlockType type, Orientation orientation) {
    this.vertices = vertices;
    this.type = type;
    this.orientation = orientation;
    this.plane = new Plane(vertices[0].subtract(vertices[1]), vertices[0].subtract(vertices[2]), vertices[0]);
  }

  public Vector3[] getVertices() {
    return vertices;
  }

  public boolean isTransparent() {
    return type.isTransparent();
  }

  public Orientation getOrientation() {
    return orientation;
  }

  public int getRGBAt(Vector3 point3d) {
    Vector3 pointCenteredAtOrigin = point3d.subtract(vertices[0]);
    Vector3 horizontalProjection = pointCenteredAtOrigin.projection(vertices[0].subtract(vertices[1]));
    Vector3 verticalProjection = pointCenteredAtOrigin.projection(vertices[0].subtract(vertices[2]));

    int textureSize = BlockType.TEXTURE_SIZE;
    BufferedImage texture = type.getTexture(orientation);

    int x = (int) Math.floor(textureSize - horizontalProjection.magnitude() / 1 * textureSize);
    int y = (int) Math.floor(textureSize - verticalProjection.magnitude() / 1 * textureSize);

    if (x < 0 || x >= textureSize || y < 0 || y >= textureSize) {
      return 0;
    }

    int rgb = texture.getRGB(x, y);
    if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH) {
      return RenderUtils.brightness(rgb, 0.95);
    } else if (orientation == Orientation.EAST || orientation == Orientation.WEST) {
      return RenderUtils.brightness(rgb, 0.9);
    } else if (orientation == Orientation.UP || orientation == Orientation.DOWN) {
      return rgb;
    }

    return texture.getRGB(x, y);
  }

  public Plane getPlane() {
    return plane;
  }
}