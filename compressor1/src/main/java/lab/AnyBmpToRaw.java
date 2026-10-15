package lab;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class AnyBmpToRaw {
    public static void main(String[] args) throws IOException {
        int width;
        int height;
        String wid;
        String hei;
        Scanner in = new Scanner(System.in);
        System.out.print("Введите имя bmp файла ");
        String filename = in.nextLine();
        File inputFile = new File(filename + ".bmp");
        BufferedImage image = ImageIO.read(inputFile);
        System.out.println("Введите:\n1) для записи в цветное\n2) для записи в серое\n3) для записи в черно-белое ");
        int Choice = in.nextInt();
        switch (Choice) {
            case 1:
                width = image.getWidth();
                height = image.getHeight();
                wid = String.valueOf(width);
                hei = String.valueOf(height);
                byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
                try (FileOutputStream fos = new FileOutputStream(filename + "RGB.raw")) {
                    for (int i = 0; i < wid.length(); i++) {
                        fos.write((byte) wid.charAt(i));
                    }
                    fos.write((byte) 'x');
                    for (int i = 0; i < hei.length(); i++) {
                        fos.write((byte) hei.charAt(i));
                    }
                    fos.write((byte) 'R');
                    fos.write(pixels);
                }
                System.out.println("Конвертация в Raw завершена."+width+"x"+height+'R');
            case 2:
                BufferedImage grayImage = new BufferedImage(
                        image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                grayImage.getGraphics().drawImage(image, 0, 0, null);

                width = grayImage.getWidth();
                height = grayImage.getHeight();
                wid = String.valueOf(width);
                hei = String.valueOf(height);

                byte[] pixels2 = ((java.awt.image.DataBufferByte)
                        grayImage.getRaster().getDataBuffer()).getData();

                try (FileOutputStream fos = new FileOutputStream(filename + "GREY.raw")) {
                    for (int i = 0; i < wid.length(); i++) {
                        fos.write((byte) wid.charAt(i));
                    }
                    fos.write((byte) 'x');
                    for (int i = 0; i < hei.length(); i++) {
                        fos.write((byte) hei.charAt(i));
                    }
                    fos.write((byte) 'G');
                    fos.write(pixels2);
                }
                System.out.println("Конвертация в Raw завершена."+width+"x"+height+'G');
            case 3:
                BufferedImage grayImage2 = new BufferedImage(
                        image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                grayImage2.getGraphics().drawImage(image, 0, 0, null);

                width = grayImage2.getWidth();
                height = grayImage2.getHeight();
                wid = String.valueOf(width);
                hei = String.valueOf(height);

                byte[] pixels3 = ((java.awt.image.DataBufferByte)
                        grayImage2.getRaster().getDataBuffer()).getData();

                for (int i = 0; i < pixels3.length; i++) {
                    pixels3[i] = (byte)(pixels3[i] < 0 ? 255 : 0);
                }

                try (FileOutputStream fos = new FileOutputStream(filename + "BW.raw")) {
                    for (int i = 0; i < wid.length(); i++) {
                        fos.write((byte) wid.charAt(i));
                    }
                    fos.write((byte) 'x');
                    for (int i = 0; i < hei.length(); i++) {
                        fos.write((byte) hei.charAt(i));
                    }
                    fos.write((byte) 'B');
                    fos.write(pixels3);
                }
                System.out.println("Конвертация в Raw завершена."+width+"x"+height+'B');
            default:
                System.out.println("Выход из программы.");
        }
    }
}
