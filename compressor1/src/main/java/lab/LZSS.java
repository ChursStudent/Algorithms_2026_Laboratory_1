package lab;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static lab.StringToBit.bitsOfText;
import static lab.byteReader.readBytes;
import static lab.byteReader.writeBytes;

public class LZSS {

    public record Pair<O, L>(O offset, L length) {
    }

    static Pair<Integer,Integer> findMatching(String Buffer, int Position, String Text, int Max) {
        StringBuilder sub = new StringBuilder();
        int offset = 0;
        int length = 0;
        for (int i = Position; i < Text.length(); i++) {
            if (Buffer.contains(sub.toString() + Text.charAt(i))) {
                sub.append(Text.charAt(i));
                offset = Buffer.length() - Buffer.indexOf(sub.toString());
                length++;
            }
            else {
                break;
            }
        }
        if (offset == length && length != 0) {
            int length1 = length;
            while (Position + length + length1 < Text.length() && length1 + length <= Max) {
                if (Text.substring(Position + length1, Position + length + length1).equals(Text.substring(Position, Position + length))) {
                    length1 += length;
                }
                else {
                    break;
                }
            }
            length = length1;
        }
        return new Pair<>(offset, length);
    }

    static String lzss(String S, int Max) {
        StringBuilder Encoded = new StringBuilder();
        Encoded.append(Max).append('\n');
        int Position = 0;
        Pair<Integer,Integer> BufferOutput;
        int offset = 0;
        int length = 0;
        StringBuilder Buffer = new StringBuilder();
        StringBuilder CharBuilder = new StringBuilder();
        int codeLen = Integer.toBinaryString(Max).length();
        while (Position < S.length()) {
            if (!Buffer.isEmpty()) {
                BufferOutput = findMatching(Buffer.toString(), Position, S, Max);
                offset = BufferOutput.offset;
                length = BufferOutput.length;
            }
            if (length == 0) {
                Buffer.append(S.charAt(Position));
                if (Buffer.length() > Max) {
                    Buffer.deleteCharAt(0);
                }
            }
            else {
                for (int i = Position; i < Position + length; i++) {
                    if (i == S.length()) { //26
                        length--;
                        break;
                    }
                    Buffer.append(S.charAt(i));
                    if (Buffer.length() > Max) {
                        Buffer.deleteCharAt(0);
                    }
                }
            }
            if (length == 0) {
                CharBuilder.append('0')
                        .append("0".repeat(8 - Integer.toBinaryString(S.charAt(Position)).length()))
                        .append(Integer.toBinaryString(S.charAt(Position)));
                Position++;
            }
            else if (length == 1) {
                CharBuilder.append('0')
                        .append("0".repeat(8 - Integer.toBinaryString(S.charAt(Position - offset)).length()))
                        .append(Integer.toBinaryString(S.charAt(Position - offset)));
                Position++;
            }
            else {
                CharBuilder.append('1')
                        .append("0".repeat(codeLen - Integer.toBinaryString(offset).length()))
                        .append(Integer.toBinaryString(offset))
                        .append("0".repeat(codeLen - Integer.toBinaryString(length).length()))
                        .append(Integer.toBinaryString(length));
                Position += length;
            }
            while (CharBuilder.length() >= 8) {
                Encoded.append((char)Integer.parseInt(CharBuilder.substring(0,8), 2));
                CharBuilder.delete(0,8);
            }
        }
        if (!CharBuilder.isEmpty()) {
            Encoded.append((char)Integer.parseInt(CharBuilder.toString() + "0".repeat(8 - CharBuilder.length()), 2));
            Encoded.append((char)CharBuilder.length());
        }
        else {
            Encoded.append((char)8);
        }
        return Encoded.toString();
    }

    static String ilzss(String S) {
        StringBuilder Decoded = new StringBuilder();
        StringBuilder byteBuffer = new StringBuilder();
        int i = 0;
        StringBuilder number = new StringBuilder();
        while (S.charAt(i) != '\n') {
            number.append(S.charAt(i));
            i++;
        }
        int Max = Integer.parseInt(number.toString());
        int codeLen = Integer.toBinaryString(Max).length();
        i++;
        int offset;
        int length;
        int currentLen;
        byteBuffer.append(bitsOfText(S, i, S.length() - 1, S.charAt(S.length() - 1)));
        while (!byteBuffer.isEmpty()) {
            if (byteBuffer.charAt(0) == '0') {
                byteBuffer.deleteCharAt(0);
                Decoded.append((char)Integer.parseInt(byteBuffer.substring(0, 8), 2));
                byteBuffer.delete(0, 8);
            }
            else if (byteBuffer.charAt(0) == '1') {
                byteBuffer.deleteCharAt(0);
                offset = Integer.parseInt(byteBuffer.substring(0, codeLen), 2);
                byteBuffer.delete(0, codeLen);
                length = Integer.parseInt(byteBuffer.substring(0, codeLen), 2);
                byteBuffer.delete(0, codeLen);
                currentLen = Decoded.length();
                for (int j = 0; j < length; j++) {
                    Decoded.append(Decoded.charAt(currentLen - offset + j)); //мб -1
                }
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
        String TextDone = "";
        /*
        for (int bufferSize = 4030; bufferSize < 4096; bufferSize+=100) {

            TextDone = lzss(Text, bufferSize);
            //System.out.println("Encoded\n" + TextDone);
            //writeBytes("lzssEnc.txt", TextDone);
            System.out.println(bufferSize + " " + TextDone.getBytes(StandardCharsets.ISO_8859_1).length);
        }
        */
        TextDone = lzss(Text, 2047);
        writeBytes("lzssEnc.txt", TextDone);
        String TextDecoded = ilzss(new String((TextDone.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1));
        System.out.println("Decoded\n" + TextDecoded);
        writeBytes("lzssDec.txt", TextDecoded);
    }
}
