import javax.swing.JFrame;

public class GameFrame extends JFrame {
  private static GameFrame gameFrame;

  public final int WIDTH = 1280;
  public final int HEIGHT = 720;

  private Thread renderThread;
  private boolean running;

  private GameCanvas minecraftCanvas;
  private InputListener inputListener;

  private Game game;

  private GameFrame() {
    super("Minecraft");
    this.running = false;

    this.minecraftCanvas = new GameCanvas();
    this.inputListener = new InputListener(minecraftCanvas);
    minecraftCanvas.addKeyListener(inputListener);
    minecraftCanvas.addMouseListener(inputListener);

    setSize(WIDTH, HEIGHT);
    setResizable(false);
    add(minecraftCanvas);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public Game getGame() {
    return game;
  }

  private void start() {
    running = true;
    game = new Game();
    setVisible(true);

    renderThread = new Thread(minecraftCanvas);
    renderThread.start();
  }

  public static GameFrame getInstance() {
    if (gameFrame == null) {
      gameFrame = new GameFrame();
    }
    return gameFrame;
  }

  public static void main(String[] args) {
    getInstance().start();
  }
}
