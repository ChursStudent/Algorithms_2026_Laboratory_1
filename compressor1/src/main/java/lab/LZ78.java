package lab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LZ78 {
    private int pos;
    private char next;

    LZ78(int a, char b) {
        this.pos = a;
        this.next = b;
    }

    static String lz78(String S, int Max) {
        StringBuilder Buffer = new StringBuilder();
        Map<String, Integer> Dict = new HashMap<>();
        List<LZ78> Answer = new ArrayList<>();
        for (int i = 0; i < S.length(); i++) {
            if (Dict.containsKey(Buffer.toString() + S.charAt(i))) {
                Buffer.append(S.charAt(i));
            }
            else {
                Answer.add(new LZ78(Dict.get(Buffer.toString()), S.charAt(i)));
                Dict.put(Buffer.toString() + S.charAt(i), Dict.size() + 1);
                Buffer.delete(0, Buffer.length());
                if (Dict.size() >= Max) {
                    Dict = new HashMap<>();
                }
            }
        }
        if (!Buffer.isEmpty()) {
            char Last = Buffer.charAt(Buffer.length() - 1);
            Buffer.deleteCharAt(Buffer.length() - 1);
            Answer.add(new LZ78(Dict.get(Buffer.toString()), Last));
        }
        StringBuilder Encoded = new StringBuilder();
        int lengthOfMax = Integer.toBinaryString(Max).length();
        int lengthOfMaxInBytes;
        if (lengthOfMax % 8 == 0) {
            lengthOfMaxInBytes = lengthOfMax / 8;
        } else {
            lengthOfMaxInBytes = lengthOfMax / 8 + 1;
        }
        for (int j = 0; j < lengthOfMaxInBytes; j++) {
            Encoded.append((char) ((Max / Math.pow(256, lengthOfMaxInBytes - 1 - j)) % Math.pow(256, j)));
        }

        for (LZ78 Elem : Answer) {
            Encoded.append('(');
            for (int j = 0; j < lengthOfMaxInBytes; j++) {
                Encoded.append((char) ((Elem.pos / Math.pow(256, lengthOfMaxInBytes - 1 - j)) % Math.pow(256, j)));
            }
            Encoded.append(',' + Elem.next + ')');
        }
        return Encoded.toString();
    }

    static String ilz78(String S) {
        List<LZ78> list = new ArrayList<>();
        StringBuilder number = new StringBuilder();
        int i = 0;
        while (S.charAt(i) != '(') {
            number.append(S.charAt(i));
            i++;
        }
        int Max = 0;
        for (int j = 0; j < number.length(); j++) {
            Max += number.charAt(j) * Math.pow(256, number.length() - 1 - j);
        }
        int pos;
        char next;
        for (; i < S.length(); i++) {
            if (S.charAt(i) == '(') {
                i++;
                number = new StringBuilder();
                while (S.charAt(i) != ',') {
                    number.append(S.charAt(i));
                    i++;
                }
                pos = 0;
                for (int j = 0; j < number.length(); j++) {
                    pos += number.charAt(j) * Math.pow(256, number.length() - 1 - j);
                    i++;
                }
                i++;
                next = S.charAt(i);
                list.add(new LZ78(pos, next));
            }
        }
        List<String> Dict = new ArrayList<>();
        Dict.add(0, ""); //?
        StringBuilder Decoded = new StringBuilder();
        String Word;
        for (LZ78 Elem : list) {
            Word = Dict.get(Elem.pos) + Elem.next;
            Decoded.append(Word);
            Dict.add(Word);
            if (Dict.size() - 1 >= Max) {
                Dict = new ArrayList<>();
                Dict.add(0, "");
            }
        }
        return Decoded.toString();
    }
}
