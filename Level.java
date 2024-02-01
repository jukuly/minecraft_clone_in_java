import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Level {
  private static final int WATER_LEVEL = 65;

  private String mapFilePath;
  private Chunk[][] chunks;
  private Game game;
  private int seed;

  public Level(Game game, String mapFilePath) {
    this.game = game;
    this.mapFilePath = mapFilePath;
    this.chunks = new Chunk[game.getRenderDistance() * 2 + 1][game.getRenderDistance() * 2 + 1];
    updateChunks(game.getPlayer().getChunkX(), game.getPlayer().getChunkZ());
    this.seed = (int) (Math.random() * Integer.MAX_VALUE);
  }

  public Level(Game game) {
    this(game, null);
    this.mapFilePath = "map";
    File mapFolder = new File(mapFilePath);
    if (mapFolder.exists()) {
      // delete all files in the folder
      for (File file : mapFolder.listFiles()) {
        file.delete();
      }
      mapFolder.delete();
    }
    mapFolder.mkdir();
  }

  private void saveChunk(Chunk chunk) {
    File file = new File(mapFilePath + "/" + chunk.getX() + "_" + chunk.getZ() + ".chunk");
    if (file.exists()) {
      file.delete();
    }
    try {
      file.createNewFile();
      FileWriter writer = new FileWriter(file);
      writer.write(chunk.toSaveString());
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Chunk loadChunk(int x, int z) {
    File file = new File(mapFilePath + "/" + x + "_" + z + ".chunk");
    if (!file.exists()) {
      return generateChunk(x, z);
    }
    try {
      Scanner reader = new Scanner(file);
      String[] chunkData = reader.nextLine().split(";");
      if (chunkData.length != 2) {
        reader.close();
        throw new RuntimeException("Invalid chunk file");
      }
      Block[][][] blocks = new Block[Chunk.CHUNK_SIZE][Integer.parseInt(chunkData[0])][Chunk.CHUNK_SIZE];
      String[] blocStrings = chunkData[1].split("_");
      for (int i = 0; i < blocStrings.length; i++) {
        String[] blockData = blocStrings[i].split("-");
        int blockX = Integer.parseInt(blockData[0]);
        int blockY = Integer.parseInt(blockData[1]);
        int blockZ = Integer.parseInt(blockData[2]);
        int blockTypeId = Integer.parseInt(blockData[3]);
        blocks[blockX][blockY][blockZ] = new Block(new Vector3(blockX + x * Chunk.CHUNK_SIZE, blockY,
            blockZ + z * Chunk.CHUNK_SIZE), BlockType.getBlockType(blockTypeId));
      }
      reader.close();
      return new Chunk(x, z, blocks);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return null;
  }

  private Chunk generateChunk(int x, int z) {
    Chunk chunk = new Chunk(x, z);
    for (int i = 0; i < Chunk.CHUNK_SIZE; i++) {
      for (int j = 0; j < Chunk.CHUNK_SIZE; j++) {
        int xBlock = i + x * Chunk.CHUNK_SIZE;
        int zBlock = j + z * Chunk.CHUNK_SIZE;
        int topY = (int) Math.floor((WorldGenUtils.fractalNoise2(xBlock, zBlock, 6) + 1) * 30) + 35;

        for (int k = 0; k < topY; k++) {
          BlockType blockType = BlockType.STONE;
          if (k == topY - 1) {
            if (k <= WATER_LEVEL) {
              blockType = BlockType.SAND;
            } else {
              blockType = BlockType.GRASS;
            }
          } else if (k > topY - 5) {
            if (k <= WATER_LEVEL) {
              blockType = BlockType.SAND;
            } else {
              blockType = BlockType.DIRT;
            }
          }
          chunk.setBlock(new Block(new Vector3(i + x * Chunk.CHUNK_SIZE, k, j + z * Chunk.CHUNK_SIZE),
              blockType));
        }
        for (int k = topY; k <= WATER_LEVEL; k++) {
          chunk.setBlock(new Block(new Vector3(i + x * Chunk.CHUNK_SIZE, k, j + z * Chunk.CHUNK_SIZE),
              BlockType.WATER));
        }
      }
    }
    return chunk;
  }

  private void updateChunks(int chunkX, int chunkZ) {

    Chunk[][] newChunks = new Chunk[game.getRenderDistance() * 2 + 1][game.getRenderDistance() * 2 + 1];
    for (int i = 0; i < chunks.length; i++) {
      for (int j = 0; j < chunks[0].length; j++) {
        if (chunks[i][j] == null) {
          continue;
        }
        int newI = chunks[i][j].getX() - chunkX + game.getRenderDistance();
        int newJ = chunks[i][j].getZ() - chunkZ + game.getRenderDistance();
        if (newI >= 0 && newI < newChunks.length && newJ >= 0 && newJ < newChunks[0].length) {
          newChunks[newI][newJ] = chunks[i][j];
        } else {
          saveChunk(chunks[i][j]);
        }
      }
    }
    chunks = newChunks;

    Thread loadChunksThread = new Thread(new Runnable() {

      @Override
      public void run() {
        for (int i = 0; i < newChunks.length; i++) {
          for (int j = 0; j < newChunks[0].length; j++) {
            if (chunks[i][j] == null) {
              chunks[i][j] = loadChunk(chunkX + i - game.getRenderDistance(),
                  chunkZ + j - game.getRenderDistance());
              ;
              if (i > 0 && chunks[i - 1][j] != null) {
                chunks[i - 1][j].setMeshDirty();
              }
              if (i < chunks.length - 1 && chunks[i + 1][j] != null) {
                chunks[i + 1][j].setMeshDirty();
              }
              if (j > 0 && chunks[i][j - 1] != null) {
                chunks[i][j - 1].setMeshDirty();
              }
              if (j < chunks[0].length - 1 && chunks[i][j + 1] != null) {
                chunks[i][j + 1].setMeshDirty();
              }
            }
          }
        }
        updateMeshes();
      }
    });

    loadChunksThread.start();
  }

  public void updateChunksAfterCameraMovement(Vector3 cameraMovement, int oldChunkX, int oldChunkZ, int newChunkX,
      int newChunkZ) {
    if (cameraMovement.getX() == 0 && cameraMovement.getY() == 0 && cameraMovement.getZ() == 0) {
      return;
    }

    if (cameraMovement.getY() != 0) {
      for (int i = 0; i < chunks.length; i++) {
        for (int j = 0; j < chunks[0].length; j++) {
          if (chunks[i][j] != null) {
            chunks[i][j].setMeshDirty();
          }
        }
      }
    } else {
      if (cameraMovement.getX() != 0) {
        int middleChunk = game.getRenderDistance();
        for (int i = 0; i < chunks[0].length; i++) {
          if (chunks[middleChunk][i] != null) {
            chunks[middleChunk][i].setMeshDirty();
          }
        }
      }
      if (cameraMovement.getZ() != 0) {
        int middleChunk = game.getRenderDistance();
        for (int i = 0; i < chunks[0].length; i++) {
          if (chunks[i][middleChunk] != null) {
            chunks[i][middleChunk].setMeshDirty();
          }
        }
      }
    }

    // if changed chunks, update the chunks array so that the player is still at its
    // center, and generate new chunks if necessary
    if (oldChunkX != newChunkX || oldChunkZ != newChunkZ) {
      updateChunks(newChunkX, newChunkZ);
    } else {
      new Thread(new Runnable() {

        @Override
        public void run() {
          updateMeshes();
        }
      }).start();
    }
  }

  // Assumes that the player is in the center of the chunks array
  // So assumes that updateChunksAfterCameraMovement has been called and was
  // successful
  //
  // TODO: Save a copy so that we don't have to recalculate every time but only
  // when the player moves to a new chunk
  private int getSortedChunksHelper(Chunk[] sortedChunks, int index, int x, int z) {
    if (chunks[x][z] == null) {
      return index;
    }
    sortedChunks[index] = chunks[x][z];
    return index + 1;
  }

  public Chunk[] getSortedChunks() {
    Chunk[] sortedChunks = new Chunk[chunks.length * chunks[0].length];
    int index = 0;
    for (int x = 0; x < chunks.length / 2; x++) {
      for (int z = 0; z < chunks[0].length / 2; z++) {
        index = getSortedChunksHelper(sortedChunks, index, x, z);
      }
      for (int z = chunks[0].length - 1; z >= chunks[0].length / 2; z--) {
        index = getSortedChunksHelper(sortedChunks, index, x, z);
      }
    }
    for (int x = chunks.length - 1; x >= chunks.length / 2; x--) {
      for (int z = 0; z < chunks[0].length / 2; z++) {
        index = getSortedChunksHelper(sortedChunks, index, x, z);
      }
      for (int z = chunks[0].length - 1; z >= chunks[0].length / 2; z--) {
        index = getSortedChunksHelper(sortedChunks, index, x, z);
      }
    }

    return sortedChunks;
  }

  public Chunk getChunk(int x, int z) {
    if (x < game.getPlayer().getChunkX() - game.getRenderDistance()
        || x > game.getPlayer().getChunkX() + game.getRenderDistance()
        || z < game.getPlayer().getChunkZ() - game.getRenderDistance()
        || z > game.getPlayer().getChunkZ() + game.getRenderDistance()) {
      return null;
    }
    return chunks[x - game.getPlayer().getChunkX() + game.getRenderDistance()][z - game.getPlayer().getChunkZ()
        + game.getRenderDistance()];
  }

  public Block getBlock(int x, int y, int z) {
    Chunk c = getChunk((x / Chunk.CHUNK_SIZE), (z / Chunk.CHUNK_SIZE));
    if (c != null) {
      return c.getBlock(x % Chunk.CHUNK_SIZE, y, z % Chunk.CHUNK_SIZE);
    }
    return null;
  }

  public Block getBlock(Vector3 position) {
    return getBlock((int) position.getX(), (int) position.getY(), (int) position.getZ());
  }

  public void updateMeshes() {
    Vector3 cameraPosition = game.getPlayer().getCamera().getPosition();
    for (int i = 0; i < chunks.length; i++) {
      for (int j = 0; j < chunks[0].length; j++) {
        if (chunks[i][j] != null) {
          chunks[i][j].updateMesh(cameraPosition);
        }
      }
    }
  }
}
