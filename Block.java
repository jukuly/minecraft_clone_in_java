import java.util.Map;

public class Block {
  private Vector3 position;
  private boolean[] hiddenFaces;
  private BlockType type;
  private Map<String, Object> data;

  public Block(Vector3 position, BlockType type) {
    this.position = position;
    this.hiddenFaces = new boolean[] {
        false, false, false, false, false, false
    };
    this.type = type;
    this.data = null;
  }

  public Face[] getFacesToRender(Vector3 cameraPosition) {
    Face[] faces = new Face[3];
    Vector3[] blockVertices = new Vector3[] {
        new Vector3(position.getX(), position.getY(), position.getZ()),
        new Vector3(position.getX(), position.getY(), position.getZ() + 1),
        new Vector3(position.getX(),
            position.getY() + (type.getSpecialBlockRendering() == SpecialBlockRendering.LIQUID ? 0.875 : 1),
            position.getZ()),
        new Vector3(position.getX(),
            position.getY() + (type.getSpecialBlockRendering() == SpecialBlockRendering.LIQUID ? 0.875 : 1),
            position.getZ() + 1),
        new Vector3(position.getX() + 1, position.getY(), position.getZ()),
        new Vector3(position.getX() + 1, position.getY(), position.getZ() + 1),
        new Vector3(position.getX() + 1,
            position.getY() + (type.getSpecialBlockRendering() == SpecialBlockRendering.LIQUID ? 0.875 : 1),
            position.getZ()),
        new Vector3(position.getX() + 1,
            position.getY() + (type.getSpecialBlockRendering() == SpecialBlockRendering.LIQUID ? 0.875 : 1),
            position.getZ() + 1)
    };

    int i = 0;
    if (cameraPosition.getX() > position.getX() + 1 && !hiddenFaces[Orientation.NORTH.ordinal()]) {
      faces[i++] = new Face(new Vector3[] {
          blockVertices[4],
          blockVertices[5],
          blockVertices[6],
          blockVertices[7]
      }, type, Orientation.NORTH);
    }
    if (cameraPosition.getX() < position.getX() && !hiddenFaces[Orientation.SOUTH.ordinal()]) {
      faces[i++] = new Face(new Vector3[] {
          blockVertices[1],
          blockVertices[0],
          blockVertices[3],
          blockVertices[2]
      }, type, Orientation.SOUTH);
    }
    if (cameraPosition.getY() > position.getY() + 1 && !hiddenFaces[Orientation.UP.ordinal()]) {
      faces[i++] = new Face(new Vector3[] {
          blockVertices[3],
          blockVertices[2],
          blockVertices[7],
          blockVertices[6]
      }, type, Orientation.UP);
    }
    if (cameraPosition.getY() < position.getY() && !hiddenFaces[Orientation.DOWN.ordinal()]) {
      faces[i++] = new Face(new Vector3[] {
          blockVertices[1],
          blockVertices[0],
          blockVertices[5],
          blockVertices[4]
      }, type, Orientation.DOWN);
    }
    if (cameraPosition.getZ() > position.getZ() + 1 && !hiddenFaces[Orientation.WEST.ordinal()]) {
      faces[i++] = new Face(new Vector3[] {
          blockVertices[5],
          blockVertices[1],
          blockVertices[7],
          blockVertices[3]
      }, type, Orientation.WEST);
    }
    if (cameraPosition.getZ() < position.getZ() && !hiddenFaces[Orientation.EAST.ordinal()]) {
      faces[i++] = new Face(new Vector3[] {
          blockVertices[0],
          blockVertices[4],
          blockVertices[2],
          blockVertices[6]
      }, type, Orientation.EAST);
    }

    return faces;
  }

  public void hideFace(Orientation orientation) {
    hiddenFaces[orientation.ordinal()] = true;
  }

  public boolean isTransparent() {
    return type.isTransparent();
  }

  public double getSpeedModifier() {
    return type.getSpeedModifier();
  }

  public Vector3 getPosition() {
    return position;
  }

  public int getBlockTypeId() {
    return BlockType.getBlockTypeId(type);
  }
}
