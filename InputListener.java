import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingWorker;

public class InputListener implements KeyListener, MouseMotionListener, MouseListener {
  private GameCanvas canvas;

  public InputListener(GameCanvas canvas) {
    this.canvas = canvas;
  }

  @Override
  public void keyPressed(KeyEvent e) {
    InputListener listener = this;
    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

      @Override
      protected Void doInBackground() throws Exception {
        Game game = GameFrame.getInstance().getGame();
        if (game == null) {
          return null;
        }
        if (e.getKeyChar() == 'w') {
          game.getMovementController().moveForward();
        } else if (e.getKeyChar() == 's') {
          game.getMovementController().moveBackward();
        } else if (e.getKeyChar() == 'a') {
          game.getMovementController().moveLeft();
        } else if (e.getKeyChar() == 'd') {
          game.getMovementController().moveRight();
        } else if (e.getKeyChar() == ' ') {
          game.getMovementController().moveUp();
        } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
          game.getMovementController().moveDown();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          canvas.removeMouseMotionListener(listener);
          canvas.addMouseListener(listener);
        }

        return null;
      }
    };
    worker.execute();
  }

  @Override
  public void keyReleased(KeyEvent e) {
    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

      @Override
      protected Void doInBackground() throws Exception {
        Game game = GameFrame.getInstance().getGame();
        if (game == null) {
          return null;
        }
        if (e.getKeyChar() == 'w') {
          game.getMovementController().moveBackward();
        } else if (e.getKeyChar() == 's') {
          game.getMovementController().moveForward();
        } else if (e.getKeyChar() == 'a') {
          game.getMovementController().moveRight();
        } else if (e.getKeyChar() == 'd') {
          game.getMovementController().moveLeft();
        } else if (e.getKeyChar() == ' ') {
          game.getMovementController().moveDown();
        } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
          game.getMovementController().moveUp();
        }

        return null;
      }
    };
    worker.execute();
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

      @Override
      protected Void doInBackground() throws Exception {
        Game game = GameFrame.getInstance().getGame();
        if (game == null) {
          return null;
        }
        Camera camera = game.getPlayer().getCamera();
        double verticalRotation = camera.getVerticalRotation()
            - (e.getX() - GameFrame.getInstance().WIDTH / 2) * game.getSensitivity();
        double horizontalRotation = camera.getHorizontalRotation()
            - (e.getY() - GameFrame.getInstance().HEIGHT / 2) * game.getSensitivity();

        camera.setRotation(verticalRotation, horizontalRotation);

        try {
          Robot robot = new Robot();
          robot.mouseMove(canvas.getLocationOnScreen().x + GameFrame.getInstance().WIDTH / 2,
              canvas.getLocationOnScreen().y + GameFrame.getInstance().HEIGHT / 2);
        } catch (AWTException exp) {
          exp.printStackTrace();
        }
        return null;
      }
    };
    worker.execute();
  };

  @Override
  public void mouseClicked(MouseEvent e) {
    InputListener listener = this;
    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

      @Override
      protected Void doInBackground() throws Exception {
        GameFrame gameFrameInstance = GameFrame.getInstance();
        if (gameFrameInstance.getGame() == null) {
          return null;
        }
        canvas.addMouseMotionListener(listener);
        canvas.removeMouseListener(listener);
        try {
          Robot robot = new Robot();
          robot.mouseMove(canvas.getLocationOnScreen().x + gameFrameInstance.WIDTH / 2,
              canvas.getLocationOnScreen().y + gameFrameInstance.HEIGHT / 2);
        } catch (AWTException exp) {
          exp.printStackTrace();
        }
        return null;
      }
    };
    worker.execute();
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void mouseDragged(MouseEvent e) {
  }
}
