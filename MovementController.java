public class MovementController {
  private static final double MOVEMENT_SPEED = 15; // blocks per second

  private Game game;
  private Vector3 velocity;

  public MovementController(Game game) {
    this.game = game;
    this.velocity = new Vector3(0, 0, 0);
  }

  public void moveForward() {
    if (velocity.getX() == 1) {
      return;
    }
    velocity = velocity
        .add(new Vector3(1, 0, 0));
  }

  public void moveBackward() {
    if (velocity.getX() == -1) {
      return;
    }
    velocity = velocity
        .add(new Vector3(-1, 0, 0));
  }

  public void moveLeft() {
    if (velocity.getZ() == 1) {
      return;
    }
    velocity = velocity
        .add(new Vector3(0, 0, 1));
  }

  public void moveRight() {
    if (velocity.getZ() == -1) {
      return;
    }
    velocity = velocity
        .add(new Vector3(0, 0, -1));
  }

  public void moveUp() {
    if (velocity.getY() == 1) {
      return;
    }
    velocity = velocity
        .add(new Vector3(0, 1, 0));
  }

  public void moveDown() {
    if (velocity.getY() == -1) {
      return;
    }
    velocity = velocity
        .add(new Vector3(0, -1, 0));
  }

  public void update(double timeElapsed) {
    double verticalRotation = game.getPlayer().getCamera().getVerticalRotation();
    Vector3 v = velocity.normalise().multiply(MOVEMENT_SPEED * timeElapsed * game.getPlayer().getSpeed());
    v = new Vector3(v.getX() * Math.cos(verticalRotation) - v.getZ() * Math.sin(verticalRotation),
        v.getY(), v.getZ() * Math.cos(verticalRotation) + v.getX() * Math.sin(verticalRotation));
    // Vector3 collisionVector = game.getPlayer().getCollisionVector(v);
    setPlayerPosition(v);// .add(collisionVector));
  }

  private void setPlayerPosition(Vector3 offset) {
    game.getPlayer().setPosition(game.getPlayer().getPosition().add(offset));

    // Recalculate chunk coordinates
    // More efficient than checking if the player is in a new chunk every tick
    int newChunkX = (int) Math.floor(game.getPlayer().getPosition().getX() / Chunk.CHUNK_SIZE);
    int newChunkZ = (int) Math.floor(game.getPlayer().getPosition().getZ() / Chunk.CHUNK_SIZE);

    game.getLevel().updateChunksAfterCameraMovement(offset, game.getPlayer().getChunkX(),
        game.getPlayer().getChunkZ(),
        newChunkX,
        newChunkZ);
    game.getPlayer().setChunkX(newChunkX);
    game.getPlayer().setChunkZ(newChunkZ);
  }
}
