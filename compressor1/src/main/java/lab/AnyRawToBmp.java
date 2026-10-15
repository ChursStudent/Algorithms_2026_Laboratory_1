package lab;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class AnyRawToBmp {
    public static void main(String[] args) throws IOException {
        int width;
        int height;
        int skip = 0;
        byte color;
        StringBuilder wid = new StringBuilder();
        StringBuilder hei = new StringBuilder();
        Scanner in = new Scanner(System.in);
        System.out.print("Введите имя raw файла с расширением ");
        String filename = in.nextLine();
        byte[] Text = Files.readAllBytes(Paths.get(filename));
        while (Text[skip] != (byte)'x' && skip < Text.length - 1) {
            wid.append((char)Text[skip]);
            skip++;
        }
        skip++;
        while (Text[skip] != (byte)'R' && Text[skip] != (byte)'G' && Text[skip] != (byte)'B' && skip < Text.length - 1) {
            hei.append((char)Text[skip]);
            skip++;
        }
        color = Text[skip];
        width = Integer.parseInt(wid.toString());
        height = Integer.parseInt(hei.toString());
        BufferedImage image;
        byte[] text = new byte[Text.length - 1 - skip];
        System.arraycopy(Text, skip + 1, text, 0, text.length);
        if (color == (byte) 'R') {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

            byte[] imgData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.arraycopy(text, 0, imgData, 0, Math.min(text.length, imgData.length));

            ImageIO.write(image, "bmp", new File(filename + "FromRaw.bmp"));
            System.out.print("Конвертация из Raw завершена.");
        }
        else if (color == (byte) 'G' || color == (byte) 'B') {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            byte[] imageBytes = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.arraycopy(text, 0, imageBytes, 0, text.length);

            ImageIO.write(image, "bmp", new File(filename + "FromRaw.bmp"));
            System.out.print("Конвертация из Raw завершена.");
        }
        System.out.println(width+"x"+height+(char)color);
    }
}
