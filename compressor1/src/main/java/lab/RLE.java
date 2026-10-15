package lab;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static lab.byteReader.readBytes;
import static lab.byteReader.writeBytes;
import static lab.filter.filterTextWithReplace;

public class RLE {
    public static int getSymNum(String S, int index, int symLen) {
        int symNum = 0;
        for (int i = index; i < index + symLen && i < S.length(); i++) {
            symNum += S.charAt(i) * (int)Math.pow(256, index + symLen - 1 - i);
        }
        return symNum;
    }

    public static String getNumSym(int Num, int symLen) {
        StringBuilder Sym = new StringBuilder();
        for (int i = 0; i < symLen; i++) {
            Sym.append((char) ((Num / Math.pow(256, symLen - 1 - i)) % 256));
        }
        return Sym.toString();
    }

    public static int findSpecSym (String S) {
        byte[] CheckSyms = new byte[256];
        for (int i = 0; i < S.length(); i++) {
            CheckSyms[S.charAt(i)] = 1;
        }
        for (int i = 255; i >= 0; i--) {
            if (CheckSyms[i] == 0) {
                return i;
            }
        }
        return -1;
    }

    public static String rle(String S, int symLen) {
        int SpecialSym;
        int specS = findSpecSym(S);
        if (specS > 2) {
            SpecialSym = specS;
        }
        else {
            S = filterTextWithReplace(S, 255);
            SpecialSym = 255;
        }
        int Max = specS;

        int Len = S.length();
        StringBuilder Encoded = new StringBuilder();
        Encoded.append((char)symLen).append((char)SpecialSym);
        int RepeatCount;
        boolean Repeating = true;
        int Sym;

        String SpecSym = (""+(char) SpecialSym).repeat(1);

        for (int i = 0; i < Len;) {
            Sym = getSymNum(S, i, symLen);
            i += symLen;
            RepeatCount = 1;
            while (Sym == getSymNum(S, i, symLen) && RepeatCount < Max - 1 && i < Len) {
                RepeatCount++;
                i += symLen;
            }
            if (RepeatCount > 1 && !Repeating) {
                Repeating = true;
                Encoded.append(SpecSym);
            }
            if (RepeatCount == 1 && Repeating) {
                Repeating = false;
                Encoded.append(SpecSym);
            }
            if (RepeatCount > 1) {
                Encoded.append((char)RepeatCount);
            }
            Encoded.append(getNumSym(Sym, symLen));
        }
        if (!Repeating) {
            Encoded.append(SpecSym);
        }
        return Encoded.toString();
    }

    public static String irle(String S) {
        int Len = S.length();
        StringBuilder RepeatingSym;
        StringBuilder Decoded = new StringBuilder();
        int symLen = S.charAt(0);
        int SpecialSym = S.charAt(1);

        int RepeatCount;
        for (int i = 2; i < Len;) {
            if (getSymNum(S, i, 1) == SpecialSym) {
                i++;
                while (getSymNum(S, i, 1) != SpecialSym) {
                    for (int j = i; j < i + symLen; j++) {
                        Decoded.append(S.charAt(j));
                    }
                    i+=symLen;
                }
                i++;
            }
            else {
                RepeatCount = getSymNum(S, i, 1);
                i++;
                RepeatingSym = new StringBuilder();
                for (int j = i; j < i + symLen; j++) {
                    RepeatingSym.append(S.charAt(j));
                }
                for (;RepeatCount > 0; RepeatCount--) {
                    Decoded.append(RepeatingSym.toString());
                }
                i += symLen;
            }
        }
        return Decoded.toString();
    }

    public static String getUTFsym(String S, int index) {
        if (index >= S.length()) {
            return "";
        }
        StringBuilder UTFSym = new StringBuilder();
        String symBytes = Integer.toBinaryString(S.charAt(index));
        symBytes = "0".repeat(8 - symBytes.length()) + symBytes;
        if (symBytes.charAt(0) == '0') {
            UTFSym.append(S.charAt(index));
        }
        else if (symBytes.charAt(2) == '0') {
            UTFSym.append(S, index, index + 2);
        }
        else if (symBytes.charAt(3) == '0') {
            UTFSym.append(S, index, index + 3);
        }
        else if (symBytes.charAt(4) == '0') {
            UTFSym.append(S, index, index + 4);
        }
        else if (symBytes.charAt(5) == '0') {
            UTFSym.append(S, index, index + 5);
        }
        else if (symBytes.charAt(6) == '0') {
            UTFSym.append(S, index, index + 6);
        }
        return UTFSym.toString();
    }

    public static String rleUTF8(String S, int Max, String SpecialSym) {
        int Len = S.length();
        StringBuilder Encoded = new StringBuilder();
        int RepeatCount;
        boolean Repeating = true;
        String Sym;
        for (int i = 0; i < Len;) {
            Sym = getUTFsym(S, i);
            i += Sym.length();
            RepeatCount = 1;
            while (Sym.equals(getUTFsym(S, i)) && RepeatCount < Max - 1 && i < Len) {
                RepeatCount++;
                i += Sym.length();
            }
            if (RepeatCount > 1 && !Repeating) {
                Repeating = true;
                Encoded.append(SpecialSym);
            }
            if (RepeatCount == 1 && Repeating) {
                Repeating = false;
                Encoded.append(SpecialSym);
            }
            if (RepeatCount > 1) {
                Encoded.append((char)RepeatCount);
            }
            Encoded.append(Sym);
        }
        if (!Repeating) {
            Encoded.append(SpecialSym);
        }
        return Encoded.toString();
    }

    public static String irleUTF8(String S, String SpecialSym) {
        int Len = S.length();
        StringBuilder Decoded = new StringBuilder();
        int RepeatCount;
        String Sym;
        for (int i = 0; i < Len; ) {
            if (("" + S.charAt(i)).equals(SpecialSym)) {
                i++;
                if (i >= Len) {
                    break;
                }
                Sym = getUTFsym(S, i);
                while (!("" + S.charAt(i)).equals(SpecialSym)) {
                    Sym = getUTFsym(S, i);
                    Decoded.append(Sym);
                    i += Sym.length();
                }
                i++;
            }
            else {
                RepeatCount = S.charAt(i);
                i++;
                Sym = getUTFsym(S, i);
                for (;RepeatCount > 0; RepeatCount--) {
                    Decoded.append(Sym);
                }
                i+= Sym.length();
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
        String TextDecoded;
        System.out.print("rle или rleUTF8 ? ");
        int mainChoice = in.nextInt();
        if (mainChoice == 1) {
            String TextDone = rle(Text, 1);
            System.out.println("Encoded\n" + TextDone);
            System.out.print("Файл на английском, русском или utf-8 ? ");
            int choice = in.nextInt();
            switch (choice) {
                case 1:
                    TextDecoded = irle(TextDone);
                    System.out.println("Decoded\n" + TextDecoded);
                    writeBytes("rleDec.txt", TextDecoded);
                    break;
                case 2:
                    TextDecoded = irle(TextDone);
                    System.out.println(new String(("Decoded\n" + TextDecoded).getBytes(StandardCharsets.ISO_8859_1), "windows-1251"));
                    writeBytes("rleDecRus.txt", TextDecoded);
                    break;
                case 3:
                    System.out.println(new String(("Decoded\n" + irle(TextDone)).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                    break;
                default:
                    break;
            }
        }
        else {
            String TextDone2 = rleUTF8(Text, 128, "" + (char) 255);
            System.out.println("Initial\n" + Text);
            writeBytes("utf8Interpretation.txt", Text);
            System.out.println("Encoded\n" + TextDone2);
            writeBytes("rleUtf8ENC.txt", TextDone2);
            TextDecoded = irleUTF8(readBytes("rleUtf8ENC.txt"), "" + (char) 255);
            System.out.println("Decoded\n" + TextDecoded);
            writeBytes("rleUtf8DEC.txt", TextDecoded);
        }
    }
}
