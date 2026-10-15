package lab;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class byteReader {
    public static String readBytes(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        return new String(bytes, StandardCharsets.ISO_8859_1);
    }

    public static void writeBytes(String path, String text) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.print("Файл на английском, русском или utf-8 ? ");
        int choice = in.nextInt();
        switch(choice) {
            case 1:
                Files.writeString(Paths.get(path), new String((text.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1);
                break;
            case 2:
                Files.writeString(Paths.get(path), new String((text.getBytes(StandardCharsets.ISO_8859_1)), "windows-1251"), Charset.forName("windows-1251"));
                break;
            case 3:
                Files.writeString(Paths.get(path), new String((text.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                break;
            default:
                break;
        }
    }
}
