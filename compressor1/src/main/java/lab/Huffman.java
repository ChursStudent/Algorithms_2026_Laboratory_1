package lab;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static lab.Entropy.prob;
import static lab.byteReader.readBytes;
import static lab.byteReader.writeBytes;

public class Huffman implements Comparable<Huffman>{
    private char symbol;
    private double prob = 0;
    private Huffman parent = null;
    private Huffman left = null;
    private Huffman right = null;

    public Huffman(char sym, double c, Huffman l, Huffman r, Huffman p) {
        this.symbol = sym;
        this.prob = c;
        this.parent = p;
        this.left = l;
        this.right = r;
    }

    @Override
    public int compareTo(Huffman other){
        return Double.compare(this.prob, other.prob);
    }

    public static Map<Character, String> dictionaryFromLen(Map<Character,Integer> D) {
        Map<Character, Integer> sortedLenD = D.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        int i = 0;
        int prevLen = 0;
        boolean firstSym = true;
        Map<Character, String> Dictionary = new HashMap<>();
        StringBuilder code;
        String prevCode = "";
        for (Map.Entry<Character, Integer> entry : sortedLenD.entrySet()) {
            code = new StringBuilder();
            if (firstSym) {
                for (int j = 0; j < entry.getValue(); j++) {
                    code.append("0");
                }
                Dictionary.put(entry.getKey(), code.toString());
                prevCode = code.toString();
                prevLen = entry.getValue();
                firstSym = false;
            }
            else {
                
String newCode = Integer.toBinaryString((Integer.parseInt(prevCode, 2) + 1) * (int)Math.pow(2, entry.getValue() - prevLen));
code.append("0".repeat(entry.getValue() - newCode.length())).append(newCode);
                Dictionary.put(entry.getKey(), code.toString());
                prevCode = code.toString();
                prevLen = entry.getValue();
            }
        }
        return Dictionary;
    }

    public static Map<Character, String> createDictionary(String S, int Max) {
        double[] P = prob(S, Max, 1);
        PriorityQueue<Huffman> PQ = new PriorityQueue<>();
        List<Huffman> List = new ArrayList<>();
        Huffman Elem;
        Huffman Left;
        Huffman Right;
        Huffman Parent;
        for (int i = 0; i < Max; i++) {
            if (P[i] > 0) {
                Elem = new Huffman((char) i, P[i], null, null, null);
                List.add(Elem);
                PQ.add(Elem);
            }
        }
        while (PQ.size() > 1) {
            Left = PQ.poll();
            Right = PQ.poll();
            Parent = new Huffman(' ', Left.prob + Right.prob, Left, Right, null);
            Left.parent = Parent;
            Right.parent = Parent;
            PQ.add(Parent);
        }
        Map<Character, Integer> CodesL = new HashMap<>();
        int CodeLen;
        for (Huffman Element : List) {
            Elem = Element;
            CodeLen = 0;
            while (Elem.parent != null) {
                CodeLen++;
                Elem = Elem.parent;
            }
            CodesL.put(Element.symbol, CodeLen);
        }
        Map<Character, String> Codes = dictionaryFromLen(CodesL);
        return Codes;
    }

    public static Map<Character, String> readDictionary(String S) {
        Map<Character, Integer> CodesL = new HashMap<>();
        char sym;
        int len;
        int i = 0;
        for (; i < S.length(); i++) {
            sym = S.charAt(i);
            i++;
            len = S.charAt(i);
            CodesL.put(sym, len);
            i++;
        }
        Map<Character, String> Codes = dictionaryFromLen(CodesL);
        return Codes;
    }

    public static String encodeHuffman(String S, int Max) {
        Map<Character, String> Codes = createDictionary(S, Max);
        StringBuilder Encoded = new StringBuilder();
        StringBuilder AlphabetStr = new StringBuilder();
        for (Map.Entry<Character, String> symCode : Codes.entrySet()) {
            AlphabetStr.append(symCode.getKey()).append((char) symCode.getValue().length()).append(",");
        }
        int SpecialSym = AlphabetStr.length();
        Encoded.append(SpecialSym).append('\n').append(AlphabetStr);
        StringBuilder byteMaker = new StringBuilder();
        for (int i = 0; i < S.length(); i++) {
            byteMaker.append(Codes.get(S.charAt(i)));
             while (byteMaker.length() >= 8) {
                Encoded.append((char)Integer.parseInt(byteMaker.substring(0, 8), 2));
                byteMaker.delete(0, 8);
            }
        }
        if (!byteMaker.isEmpty()) {
            Encoded.append((char)Integer.parseInt(byteMaker.toString() + "0".repeat(8 - byteMaker.length()), 2));
            Encoded.append((char)byteMaker.length());
        }
        else {
            Encoded.append((char)8);
        }
        return Encoded.toString();
    }

    public static String decodeHuffman(String S) {
        byte[] bytes = S.getBytes(StandardCharsets.ISO_8859_1);
        char[] Text = new char[bytes.length];
        for (int k = 0; k < Text.length; k++) {
            Text[k] = (char)(bytes[k] & 0xFF);
        }
        StringBuilder AlphabetStr = new StringBuilder();
        int i = 0;
        StringBuilder number = new StringBuilder();
        while (Text[i] != '\n') {
            number.append(Text[i]);
            i++;
        }
        i++;
        int SpecialSym = Integer.parseInt(number.toString());
        int skip = i;
        while (i < skip + SpecialSym) {
            AlphabetStr.append(Text[i]);
            i++;
        }
        Map<Character, String> Codes = readDictionary(AlphabetStr.toString());
        Map<String, Character> iCodes = new HashMap<>();
        for (Map.Entry<Character, String> entry : Codes.entrySet()) {
            iCodes.put(entry.getValue(), entry.getKey());
        }

        StringBuilder Decoded = new StringBuilder();
        StringBuilder byteBuffer = new StringBuilder();
        StringBuilder byteCheck = new StringBuilder();
        String symBytes;
        for (; i != Text.length - 1; i++) {
            if (i != Text.length - 2) {
                symBytes = Integer.toBinaryString(Text[i]);
                symBytes = "0".repeat(8 - symBytes.length()) + symBytes;
                byteBuffer.append(symBytes);
                while (!byteBuffer.isEmpty()) {
                    byteCheck.append(byteBuffer.charAt(0));
                    byteBuffer.deleteCharAt(0);
                    if (iCodes.containsKey(byteCheck.toString())) {
                        Decoded.append(iCodes.get(byteCheck.toString()));
                        byteCheck.delete(0, byteCheck.length());
                    }
                }
            }
            else {
                symBytes = Integer.toBinaryString(Text[i]);
                symBytes = "0".repeat(8 - symBytes.length()) + symBytes;
                byteBuffer.append(symBytes, 0, Text[i + 1]);
                while (!byteBuffer.isEmpty()) {
                    byteCheck.append(byteBuffer.charAt(0));
                    byteBuffer.deleteCharAt(0);
                    if (iCodes.containsKey(byteCheck.toString())) {
                        Decoded.append(iCodes.get(byteCheck.toString()));
                        byteCheck.delete(0, byteCheck.length());
                    }
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
        String TextDone = encodeHuffman(Text, 256);
        System.out.println("Encoded\n" + TextDone);
        writeBytes("huEnc.txt", TextDone);
        TextDone = readBytes("huEnc.txt");
        String TextDecoded = decodeHuffman(TextDone);
        System.out.println(new String((TextDecoded.getBytes(StandardCharsets.ISO_8859_1)), "windows-1251"));
        writeBytes("huDec.txt", TextDecoded);
    }
}
