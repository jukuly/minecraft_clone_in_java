import java.util.Random;

public class WorldGenUtils {
  private static final int[] PERMUTATION_ARRAY = generatePermutationArray(512);

  public static double fractalNoise2(double x, double y, int numberOfOctaves) {
    double result = 0;
    double amplitude = 1;
    double frequency = 0.005;

    for (int i = 0; i < numberOfOctaves; i++) {
      result += perlinNoise2(x * frequency, y * frequency) * amplitude;
      amplitude /= 2;
      frequency *= 2;
    }

    return result;
  }

  private static double perlinNoise2(double x, double y) {
    int X = (int) Math.floor(x) & 255;
    int Y = (int) Math.floor(y) & 255;

    double xf = x - Math.floor(x);
    double yf = y - Math.floor(y);

    Vector2 topRight = new Vector2(xf - 1, yf - 1);
    Vector2 topLeft = new Vector2(xf, yf - 1);
    Vector2 bottomRight = new Vector2(xf - 1, yf);
    Vector2 bottomLeft = new Vector2(xf, yf);

    int valueTopRight = PERMUTATION_ARRAY[PERMUTATION_ARRAY[X + 1] + Y + 1];
    int valueTopLeft = PERMUTATION_ARRAY[PERMUTATION_ARRAY[X] + Y + 1];
    int valueBottomRight = PERMUTATION_ARRAY[PERMUTATION_ARRAY[X + 1] + Y];
    int valueBottomLeft = PERMUTATION_ARRAY[PERMUTATION_ARRAY[X] + Y];

    double dotTopRight = getConstantVector(valueTopRight).dot(topRight);
    double dotTopLeft = getConstantVector(valueTopLeft).dot(topLeft);
    double dotBottomRight = getConstantVector(valueBottomRight).dot(bottomRight);
    double dotBottomLeft = getConstantVector(valueBottomLeft).dot(bottomLeft);

    double u = fade(xf);
    double v = fade(yf);

    return lerp(u, lerp(v, dotBottomLeft, dotTopLeft), lerp(v, dotBottomRight, dotTopRight));
  }

  private static void shuffleArray(int[] array) {
    Random random = new Random();
    for (int i = array.length - 1; i > 0; i--) {
      int index = random.nextInt(i);
      int temp = array[i];
      array[i] = array[index];
      array[index] = temp;
    }
  }

  private static int[] generatePermutationArray(int size) {
    int[] array = new int[size / 2];
    for (int i = 0; i < size / 2; i++) {
      array[i] = i;
    }
    shuffleArray(array);
    int[] finalArray = new int[size];
    for (int i = 0; i < size / 2; i++) {
      finalArray[i] = array[i];
      finalArray[i + size / 2] = array[i];
    }
    return finalArray;
  }

  private static double lerp(double t, double a, double b) {
    return a + t * (b - a);
  }

  private static double fade(double t) {
    return t * t * t * (t * (t * 6 - 15) + 10);
  }

  private static Vector2 getConstantVector(int v) {
    int h = v & 3;
    if (h == 0) {
      return new Vector2(1, 1);
    } else if (h == 1) {
      return new Vector2(-1, 1);
    } else if (h == 2) {
      return new Vector2(-1, -1);
    } else {
      return new Vector2(1, -1);
    }
  }
}
