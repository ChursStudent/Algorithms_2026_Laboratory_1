package lab;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static lab.StringToBit.bitsOfText;
import static lab.byteReader.readBytes;
import static lab.byteReader.writeBytes;
import static lab.usedSymbols.getSymList;

public class LZW {
    public static Map<String, Integer> getStartDict (String Text) {
        Map<String, Integer> Dict = new HashMap<>();
        List<Character> usedSym = getSymList(Text);
        int i = 0;
        for (char c : usedSym) {
            Dict.put(String.valueOf(c), i);
            i++;
        }
        return Dict;
    }

    public static Map<Integer, String> getStartDictReversed (String Text) {
        Map<Integer, String> Dict = new HashMap<>();
        List<Character> usedSym = getSymList(Text);
        int i = 0;
        for (char c : usedSym) {
            Dict.put(i, String.valueOf(c));
            i++;
        }
        return Dict;
    }

    public static String lzw (String Text, int Max) {   //мб dict > max, вместо >=
        StringBuilder Buffer = new StringBuilder();
        Map<String, Integer> Dict = getStartDict(Text);
        StringBuilder Encoded = new StringBuilder();
        Encoded.append(Max).append('\n').append(Dict.size()).append('\n');
        for (char c : getSymList(Text)) {
            Encoded.append(c);
        }
        List<Integer> Answer = new ArrayList<>();
        char sym;
        for (int i = 0; i < Text.length(); i++) {
            if (Dict.containsKey(Buffer.toString() + Text.charAt(i))) {
                Buffer.append(Text.charAt(i));
            }
            else {
                Answer.add(Dict.get(Buffer.toString()));
                if (Dict.size() < Max) {
                    Dict.put(Buffer.toString() + Text.charAt(i), Dict.size());
                }
                else {
                    Dict = getStartDict(Text);
                }
                Buffer.delete(0, Buffer.length());
                Buffer.append(Text.charAt(i));
            }
        }
        if (!Buffer.isEmpty()) {
            Answer.add(Dict.get(Buffer.toString()));
        }
        StringBuilder CharBuilder = new StringBuilder();
        int codeLen = Integer.toBinaryString(Max).length();
        for (int code : Answer) {
            CharBuilder.append("0".repeat(codeLen - Integer.toBinaryString(code).length()))
                    .append(Integer.toBinaryString(code));
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

    public static String ilzw (String Text) {
        StringBuilder Decoded = new StringBuilder();
        StringBuilder byteBuffer = new StringBuilder();
        int i = 0;
        StringBuilder number = new StringBuilder();
        while (Text.charAt(i) != '\n') {
            number.append(Text.charAt(i));
            i++;
        }
        int Max = Integer.parseInt(number.toString());
        int codeLen = Integer.toBinaryString(Max).length();
        i++;
        number = new StringBuilder();
        while (Text.charAt(i) != '\n') {
            number.append(Text.charAt(i));
            i++;
        }
        int symCount = Integer.parseInt(number.toString());
        i++;
        String Alphabet = Text.substring(i, i + symCount);
        i += symCount;
        byteBuffer.append(bitsOfText(Text, i, Text.length() - 1, Text.charAt(Text.length() - 1)));
        String entry;
        int prevCode;
        int curCode;
        char c;
        while (!byteBuffer.isEmpty()) {
            Map<Integer, String> Dict = getStartDictReversed(Alphabet);
            prevCode = Integer.parseInt(byteBuffer.substring(0, codeLen), 2);
            byteBuffer.delete(0, codeLen);
            Decoded.append(Dict.get(prevCode));
            while (!byteBuffer.isEmpty()) {
                if (Dict.size() >= Max) {
                    break;
                }
                curCode = Integer.parseInt(byteBuffer.substring(0, codeLen), 2);
                byteBuffer.delete(0, codeLen);
                if (Dict.containsKey(curCode)) {
                    entry = Dict.get(curCode);
                }
                else {
                    entry = Dict.get(prevCode) + Dict.get(prevCode).charAt(0);
                }
                Decoded.append(entry);
                if (Dict.size() < Max) {
                    Dict.put(Dict.size(), Dict.get(prevCode) + entry.charAt(0)); //что после очистки словаря?
                }
                prevCode = curCode;
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
        System.out.println(Text.length());
        String TextDone = "";
        for (int bufferSize = 4055; bufferSize < 4096; bufferSize+=100) {

            TextDone = lzw(Text, bufferSize);
            //System.out.println("Encoded\n" + TextDone);
            //writeBytes("lzssEnc.txt", TextDone);
            System.out.println(bufferSize + " " + TextDone.getBytes(StandardCharsets.ISO_8859_1).length);
        }
        System.out.println("Encoded\n" + TextDone);
        writeBytes("lzwEnc.txt", TextDone);
        TextDone = readBytes("lzwEnc.txt");
        String TextDecoded = ilzw(new String((TextDone.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1));
        System.out.println("Decoded\n" + TextDecoded);
        writeBytes("lzwDec.txt", TextDecoded);
    }
}
