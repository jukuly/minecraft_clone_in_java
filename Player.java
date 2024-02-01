public class Player {
  private Camera camera;
  private double width;
  private double height;
  private double speed; // scalar by which the player's velocity is multiplied
  private double jumpHeight;
  private double reach;
  private int chunkX;
  private int chunkZ;

  public Player(Camera camera, double width, double height, double speed, double jumpHeight, double reach) {
    this.camera = camera;
    this.width = width;
    this.height = height;
    this.speed = speed;
    this.jumpHeight = jumpHeight;
    this.reach = reach;
    this.chunkX = (int) Math.floor(camera.getPosition().getX() / Chunk.CHUNK_SIZE);
    this.chunkZ = (int) Math.floor(camera.getPosition().getZ() / Chunk.CHUNK_SIZE);
  }

  public Vector3 getPosition() {
    return camera.getPosition().subtract(new Vector3(0, height * 0.95, 0));
  }

  public Camera getCamera() {
    return camera;
  }

  public Vector3 getCollisionVector(Vector3 offset) {
    Vector3 wouldBePosition = getPosition().add(offset);
    Level level = GameFrame.getInstance().getGame().getLevel();

    Vector3[] hitboxCorners = new Vector3[] {
        new Vector3(wouldBePosition.getX() + width / 2, wouldBePosition.getY(), wouldBePosition.getZ() + width / 2),
        new Vector3(wouldBePosition.getX() + width / 2, wouldBePosition.getY(), wouldBePosition.getZ() - width / 2),
        new Vector3(wouldBePosition.getX() - width / 2, wouldBePosition.getY(), wouldBePosition.getZ() + width / 2),
        new Vector3(wouldBePosition.getX() - width / 2, wouldBePosition.getY(), wouldBePosition.getZ() - width / 2),
        new Vector3(wouldBePosition.getX() + width / 2, wouldBePosition.getY() + height,
            wouldBePosition.getZ() + width / 2),
        new Vector3(wouldBePosition.getX() + width / 2, wouldBePosition.getY() + height,
            wouldBePosition.getZ() - width / 2),
        new Vector3(wouldBePosition.getX() - width / 2, wouldBePosition.getY() + height,
            wouldBePosition.getZ() + width / 2),
        new Vector3(wouldBePosition.getX() - width / 2, wouldBePosition.getY() + height,
            wouldBePosition.getZ() - width / 2)
    };

    double x = 0, y = 0, z = 0;
    if (offset.getY() < 0 && (level.getBlock(hitboxCorners[0]) != null ||
        level.getBlock(hitboxCorners[1]) != null ||
        level.getBlock(hitboxCorners[2]) != null ||
        level.getBlock(hitboxCorners[3]) != null)) {
      System.out.println("Collision detected below");
      y = Math.ceil(wouldBePosition.getY()) - wouldBePosition.getY();
    }
    if (offset.getY() > 0 && (level.getBlock(hitboxCorners[4]) != null ||
        level.getBlock(hitboxCorners[5]) != null ||
        level.getBlock(hitboxCorners[6]) != null ||
        level.getBlock(hitboxCorners[7]) != null)) {
      System.out.println("Collision detected above");
      y = Math.floor(wouldBePosition.getY()) - wouldBePosition.getY();
    }
    if (offset.getX() < 0 && (level.getBlock(hitboxCorners[0]) != null ||
        level.getBlock(hitboxCorners[1]) != null ||
        level.getBlock(hitboxCorners[4]) != null ||
        level.getBlock(hitboxCorners[5]) != null)) {
      System.out.println("Collision detected south");
      x = Math.floor(wouldBePosition.getX()) - wouldBePosition.getX();
    }
    if (offset.getX() > 0 && (level.getBlock(hitboxCorners[2]) != null ||
        level.getBlock(hitboxCorners[3]) != null ||
        level.getBlock(hitboxCorners[6]) != null ||
        level.getBlock(hitboxCorners[7]) != null)) {
      System.out.println("Collision detected north");
      x = Math.ceil(wouldBePosition.getX()) - wouldBePosition.getX();
    }
    /*
     * if (offset.getZ() < 0 && (level.getBlock(hitboxCorners[0]) != null ||
     * level.getBlock(hitboxCorners[2]) != null ||
     * level.getBlock(hitboxCorners[4]) != null ||
     * level.getBlock(hitboxCorners[6]) != null)) {
     * System.out.println("Collision detected west");
     * z = Math.ceil(wouldBePosition.getZ()) - wouldBePosition.getZ();
     * }
     * if (offset.getZ() > 0 && (level.getBlock(hitboxCorners[1]) != null ||
     * level.getBlock(hitboxCorners[3]) != null ||
     * level.getBlock(hitboxCorners[5]) != null ||
     * level.getBlock(hitboxCorners[7]) != null)) {
     * System.out.println("Collision detected east");
     * z = Math.floor(wouldBePosition.getZ()) - wouldBePosition.getZ();
     * }
     */

    return new Vector3(x, y, z);
  }

  public int getChunkX() {
    return chunkX;
  }

  public int getChunkZ() {
    return chunkZ;
  }

  public void setChunkX(int chunkX) {
    this.chunkX = chunkX;
  }

  public void setChunkZ(int chunkZ) {
    this.chunkZ = chunkZ;
  }

  public void setPosition(Vector3 position) {
    camera.setPosition(position.add(new Vector3(0, height * 0.95, 0)));
  }

  public double getSpeed() {
    return speed;
  }
}
