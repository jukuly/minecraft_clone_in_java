import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public class GameCanvas extends Canvas implements Runnable {
  private static final int MAX_FPS = 240;

  private volatile boolean running;
  private double actualFPS;
  private BufferedImage frame;

  public GameCanvas() {
    super();
    this.actualFPS = 0;
  }

  @Override
  public void update(Graphics g) {
    paint(g);
  }

  @Override
  public void paint(Graphics g) {
    if (frame == null || frame.getWidth(this) != getWidth() || frame.getHeight(this) != getHeight()) {
      frame = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    Game game = GameFrame.getInstance().getGame();
    if (game == null) {
      return;
    }

    Graphics frameGraphics = frame.getGraphics();
    frameGraphics.setColor(new Color(135, 206, 235));
    frameGraphics.fillRect(0, 0, getWidth(), getHeight());

    Level level = game.getLevel();
    Camera camera = game.getPlayer().getCamera();

    for (Chunk c : level.getSortedChunks()) {
      if (c == null) {
        break;
      }
      Face[] mesh = c.getMesh();
      synchronized (mesh) {
        for (Face f : mesh) {
          if (f == null) {
            break;
          }

          boolean isInFOV = true;
          Vector2[] projectedVertices = new Vector2[f.getVertices().length];
          for (int i = 0; i < f.getVertices().length; i++) {
            if (!camera.isInFOV(f.getVertices()[i])) {
              isInFOV = false;
              break;
            }
            Vector2 projectedPoint = camera.getPointProjectionOnScreen(f.getVertices()[i], getWidth(), getHeight());
            if (projectedPoint == null) {
              continue;
            }
            projectedVertices[i] = projectedPoint;
          }
          if (!isInFOV) {
            continue;
          }

          // Assuming that the face is a convex quadrilateral
          double minX = Math.min(Math.min(projectedVertices[0].getX(), projectedVertices[1].getX()),
              Math.min(projectedVertices[2].getX(), projectedVertices[3].getX()));
          double maxX = Math.max(Math.max(projectedVertices[0].getX(), projectedVertices[1].getX()),
              Math.max(projectedVertices[2].getX(), projectedVertices[3].getX()));
          double minY = Math.min(Math.min(projectedVertices[0].getY(), projectedVertices[1].getY()),
              Math.min(projectedVertices[2].getY(), projectedVertices[3].getY()));
          double maxY = Math.max(Math.max(projectedVertices[0].getY(), projectedVertices[1].getY()),
              Math.max(projectedVertices[2].getY(), projectedVertices[3].getY()));

          Plane facePlane = f.getPlane();
          Vector3 coordinates3d;
          int pixelRGB;

          for (int i = (int) Math.max(minX, 0); i < Math.min(maxX + 1, getWidth()); i++) {
            for (int j = (int) Math.max(minY, 0); j < Math.min(maxY + 1, getHeight()); j++) {
              Vector2 point = new Vector2(i, j);
              if (!RenderUtils.isInside(point, projectedVertices)) {
                continue;
              }

              coordinates3d = camera.getPoint3D(point, facePlane, getWidth(), getHeight());
              pixelRGB = f.getRGBAt(coordinates3d);
              if (f.isTransparent()) {
                pixelRGB = RenderUtils.alphaBlend(pixelRGB, frame.getRGB(i, j));
              }
              frame.setRGB(i, j, pixelRGB);
            }
          }
        }
      }
    }

    frameGraphics.setColor(new Color(0, 0, 0));
    frameGraphics.drawString("X: " + String.format("%.2f", game.getPlayer().getPosition().getX()) + "  Y:"
        + String.format("%.2f", game.getPlayer().getPosition().getY()) + "  Z: "
        + String.format("%.2f", game.getPlayer().getPosition().getZ()), 10, 20);
    frameGraphics.drawString(
        "V Rotation: " + String.format("%.2f", camera.getVerticalRotation() * 180 / Math.PI) + "  H Rotation: "
            + String.format("%.2f", camera.getHorizontalRotation() * 180 / Math.PI),
        10, 40);
    frameGraphics.drawString("FPS: " + String.format("%.2f", actualFPS), 10, 60);

    g.drawImage(frame, 0, 0, this);
  }

  @Override
  public void run() {
    running = true;
    while (running) {
      long startTime = System.nanoTime();

      try {
        SwingUtilities.invokeAndWait(
            new Runnable() {
              @Override
              public void run() {
                paint(getGraphics());
              }
            });
      } catch (InvocationTargetException | InterruptedException e) {
        e.printStackTrace();
      }

      long sleepTime = (1000 / MAX_FPS) - System.nanoTime() - startTime / 1000000;
      if (sleepTime > 0) {
        try {
          Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      long timeElapsed = System.nanoTime() - startTime;
      GameFrame.getInstance().getGame().getMovementController().update(timeElapsed / 1000000000.0);
      actualFPS = 1000000000.0 / timeElapsed;
    }
  }

  public void stop() {
    running = false;
  }
}