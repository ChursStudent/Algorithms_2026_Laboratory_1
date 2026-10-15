package lab;

import java.io.IOException;
import java.util.Scanner;

import static lab.byteReader.readBytes;
import static lab.byteReader.writeBytes;

public class filter {
    public static String filterText(String S, int Max) {
        StringBuilder filtered = new StringBuilder();
        for (int i = 0; i < S.length(); i++) {
            if (S.charAt(i) < Max) {
                filtered.append(S.charAt(i));
            }
        }
        return filtered.toString();
    }

    public static String filterTextWithReplace(String S, int Max) {
        StringBuilder filtered = new StringBuilder();
        for (int i = 0; i < S.length(); i++) {
            if (S.charAt(i) < Max) {
                filtered.append(S.charAt(i));
            }
            else {
                filtered.append((char)(Max - 1));
            }
        }
        return filtered.toString();
    }

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        String filename = in.nextLine();
        int Max = in.nextInt();
        String Filtered = filterTextWithReplace(readBytes(filename), Max);
        writeBytes(filename, Filtered);
        System.out.println("Фильтрация завершена.");
    }
}
