package lab;

public class StringToBit {
    public static String bitsOfText(String Text, int start, int end, int lastBits) {
        StringBuilder bits = new StringBuilder();
        String _8bit;
        for (int i = start; i < end - 1; i++) {
            _8bit = Integer.toBinaryString(Text.charAt(i));
            bits.append("0".repeat(8 - _8bit.length())).append(_8bit);
        }
        _8bit = Integer.toBinaryString(Text.charAt(end - 1));
        bits.append(("0".repeat(8 - _8bit.length())+_8bit), 0, lastBits);
        return bits.toString();
    }
}
