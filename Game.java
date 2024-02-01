public class Game {
  private Player player;
  private MovementController movementController;
  private Level level;

  private int tickRate;
  private double sensitivity;
  private int renderDistance;

  public Game() {
    this.player = new Player(
        new Camera(new Vector3(0, 80 + 0.95 * 1.8, 0),
            (double) GameFrame.getInstance().WIDTH / (double) GameFrame.getInstance().HEIGHT),
        0.8,
        1.8, 1, 1,
        5);

    this.tickRate = 20;
    this.sensitivity = 0.01;
    this.renderDistance = 0;

    this.movementController = new MovementController(this);
    this.level = new Level(this);
  }

  public Game(String filePath) {
    this.player = loadPlayer(filePath);

    this.tickRate = 20;
    this.sensitivity = 0.01;
    this.renderDistance = 4;

    this.movementController = new MovementController(this);
    this.level = new Level(this, filePath);
  }

  public Player loadPlayer(String filePath) {
    return null;
  }

  public Player getPlayer() {
    return player;
  }

  public MovementController getMovementController() {
    return movementController;
  }

  public Level getLevel() {
    return level;
  }

  public double getSensitivity() {
    return sensitivity;
  }

  public int getRenderDistance() {
    return renderDistance;
  }
}
