import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PNGToJPG {
    static final int START_NUM = 0;
    static final int LAST_NUM = 126;
    static final String SET = "MRD";
    static final String REGION = "NA";
    static final int WIDTH = 300;
    static final int HEIGHT = 450;

    public static void main(String[] args) throws IOException {

        for (int i=START_NUM; i<LAST_NUM; i++) {
            try {
                File f = new File(String.format("%s/%s%03d.png", SET, REGION, i));
                BufferedImage origI = ImageIO.read(f);
                BufferedImage newI = new BufferedImage(origI.getWidth(), origI.getHeight(), BufferedImage.TYPE_INT_RGB);
                newI.createGraphics().drawImage(origI, 0, 0, Color.WHITE, null);
                Image scaledI = newI.getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
                BufferedImage finalI = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
                finalI.getGraphics().drawImage(scaledI, 0, 0, null);

                ImageIO.write(finalI, "jpg", new File(String.format("%s/%s%03d.jpg", SET, REGION, i)));
                f.delete();

            }
            catch (IOException e) {
                System.out.printf("Card %03d is a .jpg\n", i);
                BufferedImage origI = ImageIO.read(new File(String.format("%s/%s%03d.jpg", SET, REGION, i)));
                BufferedImage newI = new BufferedImage(origI.getWidth(), origI.getHeight(), BufferedImage.TYPE_INT_RGB);
                newI.createGraphics().drawImage(origI, 0, 0, Color.WHITE, null);
                Image scaledI = newI.getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
                BufferedImage finalI = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
                finalI.getGraphics().drawImage(scaledI, 0, 0, null);

                ImageIO.write(finalI, "jpg", new File(String.format("%s/%s%03d.jpg", SET, REGION, i)));
            }
        }

    }

}

