import java.util.Arrays;

public class Chunk {
  public static final int CHUNK_SIZE = 16;
  private static final int CHUNK_HEIGHT = 256;

  private int x;
  private int z;
  private Block[][][] blocks;

  private Face[] mesh;
  private volatile boolean meshDirty;

  public Chunk(int x, int z) {
    this.x = x;
    this.z = z;
    this.blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
    this.mesh = new Face[0];
    this.meshDirty = true;
  }

  public Chunk(int x, int z, Block[][][] blocks) {
    this.x = x;
    this.z = z;
    this.blocks = blocks;
    this.mesh = new Face[0];
    this.meshDirty = true;
  }

  public Block getBlock(int x, int y, int z) {
    if (x < 0 || x >= CHUNK_SIZE || z < 0 || z >= CHUNK_SIZE || y < 0 || y >= blocks[0].length) {
      return null;
    }
    return blocks[x][y][z];
  }

  private boolean isBlockVisible(int x, int y, int z) {
    if (blocks[x][y][z] == null) {
      return false;
    }

    boolean visible = false;
    Block[] surroundingBlocks = new Block[6];
    Level level = GameFrame.getInstance().getGame().getLevel();

    if (x > 0) {
      surroundingBlocks[1] = blocks[x - 1][y][z];
    } else {
      Chunk c = level.getChunk(this.x - 1, this.z);
      if (c == null) {
        visible = true;
      } else {
        surroundingBlocks[1] = c.getBlock(CHUNK_SIZE - 1, y, z);
      }
    }

    if (x < CHUNK_SIZE - 1) {
      surroundingBlocks[0] = blocks[x + 1][y][z];
    } else {
      Chunk c = level.getChunk(this.x + 1, this.z);
      if (c == null) {
        visible = true;
      } else {
        surroundingBlocks[0] = c.getBlock(0, y, z);
      }
    }

    if (y > 0)
      surroundingBlocks[3] = blocks[x][y - 1][z];
    else
      visible = true;

    if (y < blocks[0].length - 1)
      surroundingBlocks[2] = blocks[x][y + 1][z];
    else
      visible = true;

    if (z > 0) {
      surroundingBlocks[5] = blocks[x][y][z - 1];
    } else {
      Chunk c = level.getChunk(this.x, this.z - 1);
      if (c == null) {
        visible = true;
      } else {
        surroundingBlocks[5] = c.getBlock(x, y, CHUNK_SIZE - 1);
      }
    }

    if (z < CHUNK_SIZE - 1) {
      surroundingBlocks[4] = blocks[x][y][z + 1];
    } else {
      Chunk c = level.getChunk(this.x, this.z + 1);
      if (c == null) {
        visible = true;
      } else {
        surroundingBlocks[4] = c.getBlock(x, y, 0);
      }
    }

    for (int i = 0; i < surroundingBlocks.length; i++) {
      Block block = surroundingBlocks[i];
      if (block == null || (block.isTransparent() && !blocks[x][y][z].isTransparent())) {
        visible = true;
      } else {
        blocks[x][y][z].hideFace(Orientation.values()[i]);
      }
    }

    return visible;
  }

  public void setBlock(Block block) {
    int relativeX = (int) Math.floor(block.getPosition().getX()) - x * CHUNK_SIZE;
    int integerY = (int) Math.floor(block.getPosition().getY());
    int relativeZ = (int) Math.floor(block.getPosition().getZ()) - z * CHUNK_SIZE;
    if (relativeX < 0 || relativeX >= CHUNK_SIZE) {
      throw new IndexOutOfBoundsException("x is out of bounds");
    }
    if (relativeZ < 0 || relativeZ >= CHUNK_SIZE) {
      throw new IndexOutOfBoundsException("z is out of bounds");
    }
    if (integerY < 0 || integerY >= CHUNK_HEIGHT) {
      throw new IndexOutOfBoundsException("y is out of bounds");
    }
    if (integerY >= blocks[0].length) {
      int newHeight = Math.min(Math.max(blocks[0].length + CHUNK_SIZE, integerY + 1), CHUNK_HEIGHT);
      for (int i = 0; i < blocks.length; i++) {
        blocks[i] = Arrays.copyOf(blocks[i], newHeight);
        for (int j = 0; j < blocks[i].length; j++) {
          if (blocks[i][j] == null) {
            blocks[i][j] = new Block[CHUNK_SIZE];
          }
        }
      }
    }
    blocks[relativeX][integerY][relativeZ] = block;
  }

  private int generateMeshHelper(int x, int y, int z, int i, Vector3 cameraPosition, Face[] newMesh) {
    if (blocks[x][y][z] == null) {
      return i;
    }
    if (isBlockVisible(x, y, z)) {
      for (Face f : blocks[x][y][z].getFacesToRender(cameraPosition)) {
        if (f == null) {
          break;
        }
        newMesh[i] = f;
        i++;
      }
    }
    return i;
  }

  private void generateMesh(Vector3 cameraPosition) {
    Face[] newMesh = new Face[CHUNK_SIZE * blocks[0].length * CHUNK_SIZE * 3];
    int i = 0;

    int relativeX = (int) Math.floor(cameraPosition.getX()) - this.x * CHUNK_SIZE;
    int integerY = (int) Math.floor(cameraPosition.getY());
    int relativeZ = (int) Math.floor(cameraPosition.getZ()) - this.z * CHUNK_SIZE;

    int x, y, z;
    for (x = 0; x < Math.min(relativeX, CHUNK_SIZE); x++) {
      for (y = 0; y < Math.min(blocks[0].length, integerY); y++) {
        for (z = 0; z < Math.min(relativeZ, CHUNK_SIZE); z++) {
          i = generateMeshHelper(x, y, z, i, cameraPosition, newMesh);
        }
        for (z = CHUNK_SIZE - 1; z >= Math.max(relativeZ, 0); z--) {
          i = generateMeshHelper(x, y, z, i, cameraPosition, newMesh);
        }
      }
      for (y = blocks[0].length - 1; y >= Math.max(integerY, 0); y--) {
        for (z = 0; z < Math.min(relativeZ, CHUNK_SIZE); z++) {
          i = generateMeshHelper(x, y, z, i, cameraPosition, newMesh);
        }
        for (z = CHUNK_SIZE - 1; z >= Math.max(relativeZ, 0); z--) {
          i = generateMeshHelper(x, y, z, i, cameraPosition, newMesh);
        }
      }
    }
    for (x = CHUNK_SIZE - 1; x >= Math.max(relativeX, 0); x--) {
      for (y = 0; y < Math.min(blocks[0].length, integerY); y++) {
        for (z = 0; z < Math.min(relativeZ, CHUNK_SIZE); z++) {
          i = generateMeshHelper(x, y, z, i, cameraPosition, newMesh);
        }
        for (z = CHUNK_SIZE - 1; z >= Math.max(relativeZ, 0); z--) {
          i = generateMeshHelper(x, y, z, i, cameraPosition, newMesh);
        }
      }
      for (y = blocks[0].length - 1; y >= Math.max(integerY, 0); y--) {
        for (z = 0; z < Math.min(relativeZ, CHUNK_SIZE); z++) {
          i = generateMeshHelper(x, y, z, i, cameraPosition, newMesh);
        }
        for (z = CHUNK_SIZE - 1; z >= Math.max(relativeZ, 0); z--) {
          i = generateMeshHelper(x, y, z, i, cameraPosition, newMesh);
        }
      }
    }

    synchronized (mesh) {
      mesh = newMesh;
      meshDirty = false;
    }
  }

  public Face[] getMesh() {
    return mesh;
  }

  public void setMeshDirty() {
    meshDirty = true;
  }

  public void updateMesh(Vector3 cameraPosition) {
    if (meshDirty) {
      generateMesh(cameraPosition);
    }
  }

  public int getX() {
    return x;
  }

  public int getZ() {
    return z;
  }

  public String toSaveString() {
    StringBuilder sb = new StringBuilder();
    sb.append(blocks[0].length + ";");
    for (int i = 0; i < blocks.length; i++) {
      for (int j = 0; j < blocks[0].length; j++) {
        for (int k = 0; k < blocks[0][0].length; k++) {
          if (blocks[i][j][k] != null) {
            sb.append(i + "-" + j + "-" + k + "-");
            sb.append(blocks[i][j][k].getBlockTypeId());
            sb.append("_");
          }
        }
      }
    }
    return sb.toString();
  }
}