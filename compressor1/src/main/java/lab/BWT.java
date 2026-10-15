package lab;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static lab.byteReader.readBytes;
import static lab.byteReader.writeBytes;

public class BWT {
    public static int indexOf(String[] array, String key) {
        for (int i = 0; i < array.length; i++) {
            if (key.equals(array[i])) return i;
        }
        return -1;
    }

    public record Pair<C, N>(C column, N number) {
    }

    public static Pair<String, Integer> bwt(String S) {
        int Len = S.length();
        String[] BWM = new String[Len];
        StringBuilder BWL1;
        StringBuilder BWL2;
        for (int i = 0; i < Len; i++) {
            BWL1 = new StringBuilder();
            for (int j = Len - i; j < Len; j++) {
                BWL1.append(S.charAt(j));
            }
            BWL2 = new StringBuilder();
            for (int j = 0; j < Len - i; j++) {
                BWL2.append(S.charAt(j));
            }
            BWM[i] = BWL1.toString() + BWL2.toString();
        }
        Arrays.sort(BWM);
        StringBuilder lastBWC = new StringBuilder();
        for (int i = 0; i < Len; i++) {
            lastBWC.append(BWM[i].charAt(Len - 1));
        }
        int indexS = indexOf(BWM, S);
        return new Pair<>(lastBWC.toString(), indexS);
    }

    public static Pair<String, Integer> bwt2(String S) {    //через суффиксный массив
        StringBuilder Suffix;
        Map<String, Integer> sufArr = new HashMap<>();
        for (int i = 0; i < S.length(); i++) {
            Suffix = new StringBuilder();
            for (int j = i; j < S.length(); j++) {
                Suffix.append(S.charAt(j));
            }
            sufArr.put(Suffix.toString(), i);
        }
        Map<String, Integer> sortedSufArr = sufArr.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        int[] SA = new int[S.length()];
        int i = 0;
        int indexS = -1;
        for (Map.Entry<String, Integer> suf : sortedSufArr.entrySet()) {
            SA[i] = suf.getValue();
            if (suf.getKey().equals(S)) {
                indexS = i;
            }
            i++;
        }
        StringBuilder lastBWC = new StringBuilder();
        for (i = 0; i < S.length(); i++) {
            lastBWC.append(S.charAt((SA[i] - 1 + S.length()) % S.length()));
        }
        return new Pair<>(lastBWC.toString(), indexS);
    }

    public static String ibwt(String lastBWC, int indexS) {
        int Len = lastBWC.length();
        String[] BWM = new String[Len];
        for (int i = 0; i < Len; i++) {
            BWM[i] = "";
        }
        for (int i = 0; i < Len; i++) {
            for (int j = 0; j < Len; j++) {
                BWM[j] = lastBWC.charAt(j) + BWM[j];
            }
            Arrays.sort(BWM);
        }
        String Decoded = BWM[indexS];
        return Decoded;
    }

    public static String ibwt2(String lastBWC, int indexS, int Max) {   //через перестановку
        int[] Count = new int[Max];
        for (int i = 0; i < lastBWC.length(); i++) {
            Count[lastBWC.charAt(i)]++;
        }
        int Sum = 0;
        for (int i = 0; i < Max; i++) {
            Sum += Count[i];
            Count[i] = Sum - Count[i];
        }
        int[] T = new int[lastBWC.length()];
        for (int i = 0; i < lastBWC.length(); i++) {
            T[Count[lastBWC.charAt(i)]] = i;
            Count[lastBWC.charAt(i)]++;
        }
        int Position = T[indexS];
        StringBuilder Decoded = new StringBuilder();
        for (int i = 0; i < lastBWC.length(); i++) {
            Decoded.append(lastBWC.charAt(Position));
            Position = T[Position];
        }
        return Decoded.toString();
    }

    public static String BWt(String S, int ChunkSize) {
        StringBuilder Encoded = new StringBuilder();
        Encoded.append(ChunkSize).append('\n');
        StringBuilder Chunk = new StringBuilder();
        String[] Chunks;
        if (S.length() % ChunkSize == 0) {
            Chunks = new String[S.length() / ChunkSize];
            Encoded.append(Chunks.length).append('\n');
            System.out.println(Chunks.length);
        }
        else {
            Chunks = new String[S.length() / ChunkSize + 1];
            Encoded.append(Chunks.length).append('\n');
        }
        int index;
        int j = 0;
        for (int i = 0; i < S.length(); i++) {
            if (Chunk.length() < ChunkSize) {
                Chunk.append(S.charAt(i));
            }
            else {
                Chunks[j] = Chunk.toString();
                Chunk = new StringBuilder();
                j++;
                i--;
            }
        }
        if (!Chunk.isEmpty()) {
            Chunks[j] = Chunk.toString();
        }
        Pair<String, Integer> Output;
        for (int i = 0; i < Chunks.length; i++) {
            Output = bwt(Chunks[i]);
            Chunks[i] = Output.column;
            index = Output.number;
            Encoded.append(Chunks[i]).append(index).append('\n');
        }
        Encoded.append(Chunks[Chunks.length-1].length());
        return Encoded.toString();
    }

    public static String iBWt(String S) {
        StringBuilder Decoded = new StringBuilder();
        StringBuilder number = new StringBuilder();
        int i = 0;
        while (S.charAt(i) != '\n') {
            number.append(S.charAt(i));
            i++;
        }
        i++;
        int ChunkSize = Integer.parseInt(number.toString());
        number = new StringBuilder();
        while (S.charAt(i) != '\n') {
            number.append(S.charAt(i));
            i++;
        }
        i++;
        int ChunkNum = Integer.parseInt(number.toString());
        int ChunkCount = 1;
        StringBuilder Chunk = new StringBuilder();
        int index;
        for (; i < S.length(); i++) {
            if (ChunkCount == ChunkNum) {
                number = new StringBuilder();
                int j = S.length() - 1;
                while (S.charAt(j) != '\n') {
                    j--;
                }
                j++;
                while (j < S.length()) {
                    number.append(S.charAt(j));
                    j++;
                }
                ChunkSize = Integer.parseInt(number.toString());
                ChunkCount++;
            }
            if (Chunk.length() < ChunkSize) {
                Chunk.append(S.charAt(i));
            }
            else {
                ChunkCount++;
                number = new StringBuilder();
                while (S.charAt(i) != '\n') {
                    number.append(S.charAt(i));
                    i++;
                }
                index = Integer.parseInt(number.toString());
                Decoded.append(ibwt2(Chunk.toString(), index, 256));
                Chunk = new StringBuilder();
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
        String TextDone = BWt(Text, 1024);
        System.out.println("Encoded\n" + TextDone);
        String TextDecoded = iBWt(TextDone);
        System.out.println("Decoded\n" + TextDecoded);
        writeBytes("bwtDec.txt", TextDecoded);
    }
}