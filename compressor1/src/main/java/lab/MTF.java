package lab;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static lab.byteReader.readBytes;
import static lab.byteReader.writeBytes;

public class MTF {
    public static int indexOf(char[] array, char key) {
        for (int i = 0; i < array.length; i++) {
            if (key == array[i]) return i;
        }
        return -1;
    }

    public static String mtf(String S, int Max) {
        int Len = S.length();
        StringBuilder Encoded = new StringBuilder();
        char[] T = new char[Max];
        char t;
        for (int i = 0; i < Max; i++) {
            T[i] = (char) i;
        }
        int Sym;
        for (int i = 0; i < Len; i++) {
            Sym = indexOf(T, S.charAt(i));
            Encoded.append((char) Sym);
            if (Sym > 0) {
                t = T[0];
                T[0] = T[Sym];
                for (int j = Sym; j > 1; j--) {
                    T[j] = T[j - 1];
                }
                T[1] = t;
            }
        }
        return Encoded.toString();
    }

    public static String imtf(String S, int Max) {
        int Len = S.length();
        StringBuilder Decoded = new StringBuilder();
        char[] T = new char[Max];
        char t;
        for (int i = 0; i < Max; i++) {
            T[i] = (char) i;
        }
        int Sym;
        for (int i = 0; i < Len; i++) {
            Sym = T[S.charAt(i)];
            Decoded.append((char) Sym);
            if (S.charAt(i) > 0) {
                t = T[0];
                T[0] = T[S.charAt(i)];
                for (int j = S.charAt(i); j > 1; j--) {
                    T[j] = T[j - 1];
                }
                T[1] = t;
            }
        }
        return Decoded.toString();
    }

    public static void main (String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        String filename;
        System.out.print("Введите путь до файла ");
        filename = in.nextLine();
        String Text = readBytes(filename);
        String TextDone = mtf(Text, 256);
        String TextDecoded;
        System.out.println("Encoded\n" + TextDone);
        System.out.print("Файл на английском, русском или utf-8 ? ");
        int choice = in.nextInt();
        switch(choice) {
            case 1:
                TextDecoded = imtf(TextDone, 256);
                System.out.println("Decoded\n" + TextDecoded);
                writeBytes("mtfDec.txt", TextDecoded);
                break;
            case 2:
                TextDecoded = imtf(TextDone, 256);
                System.out.println(new String(("Decoded\n" + TextDecoded).getBytes(StandardCharsets.ISO_8859_1), "windows-1251"));
                writeBytes("mtfDec.txt", TextDecoded);
                break;
            case 3:
                TextDecoded = imtf(TextDone, 256);
                System.out.println(new String(("Decoded\n" + TextDecoded).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                writeBytes("mtfDec.txt", TextDecoded);
                break;
            default:
                break;
        }
    }
}