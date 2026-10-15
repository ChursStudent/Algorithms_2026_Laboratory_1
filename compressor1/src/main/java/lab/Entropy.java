package lab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static lab.byteReader.readBytes;

public class Entropy {
public long sym;
public int count;
Entropy (long s) {
sym = s;
count = 1;
}
    public static double[] probForDiffLen(String S, int symLen) {
        int Len = S.length();
        List<Entropy> Count = new ArrayList<>();
        int Sym;
        boolean Exists;
        for (int i = 0; i < Len; i += symLen) {
            Sym = 0;
            for (int j = i; j < i + symLen && j < S.length(); j++) {
                Sym += S.charAt(j) * (int) Math.pow(256, i + symLen - 1 - j);
            }
            Exists = false;
            for (Entropy elem : Count) {
                if (elem.sym == Sym) {
                    elem.count++;
                    Exists = true;
                    break;
                }
            }
            if (!Exists) {
                Count.add(new Entropy(Sym));
            }
        }
        double[] Probabilities = new double[Count.size()];
        for (int i = 0; i < Probabilities.length; i++) {
            Probabilities[i] = (double) Count.get(i).count / Len * symLen;
        }
        return Probabilities;
    }

    public static double[] prob(String S, int Max, int symLen) {
        int Len = S.length();
        double[] Probabilities = new double[Max];
        int[] Count = new int[Max];
        int Sym;
        for (int i = 0; i < Len; i += symLen) {
            Sym = 0;
            for (int j = i; j < i + symLen && j < S.length(); j++) {
                Sym += S.charAt(j) * (int)Math.pow(256, i + symLen - 1 - j);
            }
            Count[Sym]++;
        }
        for (int i = 0; i < Max; i++) {
            Probabilities[i] = (double) Count[i] / Len * symLen;
        }
        return Probabilities;
    }

    public static double entropy(double[] P) {
        double Entropy = 0;
        for (int i = 0; i < P.length; i++) {
            Entropy += P[i] * Math.log(P[i]) / Math.log(2);
        }
        return -Entropy;
    }

    public static void main (String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        String filename;
        System.out.print("Введите путь до файла Latin1 ");
        filename = in.nextLine();
        String Text = readBytes(filename);
        String TextDone1;
        /*for (int chunksize = 30; chunksize < 2048; chunksize+=100) {
            TextDone1 = BWt(Text, chunksize);
            TextDone1 = mtf(new String((TextDone1.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1), 256);
            System.out.println("for chunk = "+chunksize + ", entropy = "+entropy(probForDiffLen(TextDone1, 1)));
        }*/
        System.out.println(entropy(probForDiffLen(Text, 1)));
    }
}