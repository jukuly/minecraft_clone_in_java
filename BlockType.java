import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BlockType {
  private static BufferedImage TEXTURE = null;
  public static final int TEXTURE_SIZE = 16;
  public static final BlockType STONE;
  public static final BlockType WATER;
  public static final BlockType GRASS;
  public static final BlockType DIRT;
  public static final BlockType SAND;
  static {
    try {
      TEXTURE = ImageIO.read(new File("./textures.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    STONE = new BlockType(new Vector2(0, 0), false, 0, null);
    WATER = new BlockType(new Vector2(0, 1), true, 0.8, SpecialBlockRendering.LIQUID);
    GRASS = new BlockType(new Vector2(0, 2), new Vector2(1, 2), new Vector2(0, 3), false, 0, null);
    DIRT = new BlockType(new Vector2(0, 3), false, 0, null);
    SAND = new BlockType(new Vector2(0, 4), false, 0, null);
  }

  private static BlockType[] BLOCK_TYPES = new BlockType[] {
      STONE,
      WATER,
      GRASS,
      DIRT,
      SAND
  };

  private Vector2 topUV;
  private Vector2 sideUV;
  private Vector2 bottomUV;
  private boolean transparent;
  private double speedModifier;
  private SpecialBlockRendering specialBlockRendering;

  public BlockType(Vector2 topUV, Vector2 sideUV, Vector2 bottomUV, boolean transparent, double speedModifier,
      SpecialBlockRendering specialBlockRendering) {
    this.topUV = topUV;
    this.sideUV = sideUV;
    this.bottomUV = bottomUV;
    this.transparent = transparent;
    this.speedModifier = speedModifier;
    this.specialBlockRendering = specialBlockRendering;
  }

  public BlockType(Vector2 uv, boolean transparent, double speedModifier, SpecialBlockRendering specialBlockRendering) {
    this(uv, uv, uv, transparent, speedModifier, specialBlockRendering);
  }

  public BufferedImage getTexture(Orientation orientation) {
    if (orientation == Orientation.UP) {
      return TEXTURE.getSubimage((int) topUV.getX() * TEXTURE_SIZE, (int) topUV.getY() * TEXTURE_SIZE, TEXTURE_SIZE,
          TEXTURE_SIZE);
    }
    if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH || orientation == Orientation.EAST
        || orientation == Orientation.WEST) {
      return TEXTURE.getSubimage((int) sideUV.getX() * TEXTURE_SIZE, (int) sideUV.getY() * TEXTURE_SIZE, TEXTURE_SIZE,
          TEXTURE_SIZE);
    }
    if (orientation == Orientation.DOWN) {
      return TEXTURE.getSubimage((int) bottomUV.getX() * TEXTURE_SIZE, (int) bottomUV.getY() * TEXTURE_SIZE,
          TEXTURE_SIZE, TEXTURE_SIZE);
    }
    return null;
  }

  public boolean isTransparent() {
    return transparent;
  }

  public double getSpeedModifier() {
    return speedModifier;
  }

  public SpecialBlockRendering getSpecialBlockRendering() {
    return specialBlockRendering;
  }

  public static BlockType getBlockType(int id) {
    return BLOCK_TYPES[id];
  }

  public static int getBlockTypeId(BlockType blockType) {
    for (int i = 0; i < BLOCK_TYPES.length; i++) {
      if (BLOCK_TYPES[i] == blockType) {
        return i;
      }
    }
    return -1;
  }
}
