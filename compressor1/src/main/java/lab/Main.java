package lab;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static lab.BWT.BWt;
import static lab.BWT.iBWt;
import static lab.Huffman.decodeHuffman;
import static lab.Huffman.encodeHuffman;
import static lab.LZSS.ilzss;
import static lab.LZSS.lzss;
import static lab.LZW.ilzw;
import static lab.LZW.lzw;
import static lab.MTF.imtf;
import static lab.MTF.mtf;
import static lab.RLE.*;
import static lab.byteReader.readBytes;
import static lab.byteReader.writeBytes;

public class Main {

    public static String reName(String filename, int choice) {
        StringBuilder path = new StringBuilder();
        StringBuilder format = new StringBuilder();
        char Sym;
        for (int i = 0; i < filename.length(); i++) {
            Sym = filename.charAt(i);
            if (Sym == '.') {
                for (; i < filename.length(); i++) {
                    format.append(filename.charAt(i));
                }
            }
            else {
                path.append(filename.charAt(i));
            }
        }
        return path + "Done" + choice + format;
    }

    public static void main(String[] args) throws IOException {
        char SpecSym = 'ÿ';
        Scanner in = new Scanner(System.in);
        String filename;
        String filenameDone;
        int blockLen;
        int localChoice;
        int choice;
        int iChoice;
        int mainChoice = 1;
        while (mainChoice != 0) {
            choice = 1;
            iChoice = 1;
            System.out.println("Введите 0, чтобы ЗАКРЫТЬ программу, 1, чтобы закодировать или 2, чтобы декодировать ");
            mainChoice = in.nextInt();
            if (mainChoice == 1) {
                while (choice != 0) {
                    System.out.println("Encode:\n0 exit\n1 HA\n2 RLE\n3 BWT+RLE\n4 BWT+MTF+HA\n5 BWT+MTF+RLE+HA\n6 LZSS\n7 LZSS+HA\n8 LZW\n9 LZW+HA");
                    choice = in.nextInt();
                    if (choice == 0) {
                        break;
                    }
                    in = new Scanner(System.in);
                    System.out.println("Введите путь до файла ");
                    filename = in.nextLine();
                    String Text1 = readBytes(filename);
                    String TextDone1;
                    switch (choice) {
                        case 1:
                            //algo
                            TextDone1 = encodeHuffman(Text1, 256);
                            filenameDone = reName(filename, 1);
                            writeBytes(filenameDone, TextDone1);
                            break;
                        case 2:
                            System.out.println("1) byte text 2) grey/bw image 3) rgb image 4) utf-8: ");
                            localChoice = in.nextInt();
                            switch (localChoice) {
                                case 1:
                                    TextDone1 = rle(Text1, 1);
                                    break;
                                case 2:
                                    TextDone1 = rle(Text1, 1);
                                    break;
                                case 3:
                                    TextDone1 = Text1.substring(0, Text1.indexOf('R') + 1)
                                            + rle(Text1.substring(Text1.indexOf('R') + 1), 3);
                                    break;
                                case 4:
                                    TextDone1 = rleUTF8(Text1, SpecSym, "" + SpecSym);
                                    break;
                                default:
                                    TextDone1 = "";
                                    break;
                            }
                            filenameDone = reName(filename, 2);
                            writeBytes(filenameDone, TextDone1);
                            break;
                        case 3:
                            System.out.println("enter chunk size in bytes: ");  //1024 - best
                            localChoice = in.nextInt();
                            TextDone1 = BWt(Text1, localChoice);
                            TextDone1 = rle(new String((TextDone1.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1), 1);
                            filenameDone = reName(filename, 3);
                            writeBytes(filenameDone, TextDone1);
                            break;
                        case 4:
                            System.out.println("enter chunk size in bytes: ");
                            localChoice = in.nextInt();
                            TextDone1 = BWt(Text1, localChoice);
                            TextDone1 = mtf(new String((TextDone1.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1), 256);
                            TextDone1 = encodeHuffman(new String((TextDone1.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1), 256);
                            filenameDone = reName(filename, 4);
                            writeBytes(filenameDone, TextDone1);
                            break;
                        case 5:
                            System.out.println("enter chunk size in bytes: ");
                            localChoice = in.nextInt();
                            TextDone1 = BWt(Text1, localChoice);
                            TextDone1 = mtf(new String((TextDone1.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1), 256);
                            TextDone1 = rle(new String((TextDone1.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1), 1);
                            TextDone1 = encodeHuffman(new String((TextDone1.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1), 256);
                            filenameDone = reName(filename, 5);
                            writeBytes(filenameDone, TextDone1);
                            break;
                        case 6:
                            System.out.println("enter buffer size in bytes: "); //2047 - best
                            localChoice = in.nextInt();
                            TextDone1 = lzss(Text1, localChoice);
                            filenameDone = reName(filename, 6);
                            writeBytes(filenameDone, TextDone1);
                            break;
                        case 7:
                            System.out.println("enter buffer size in bytes: ");
                            localChoice = in.nextInt();
                            TextDone1 = lzss(Text1, localChoice);
                            TextDone1 = encodeHuffman(new String((TextDone1.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1), 256);
                            filenameDone = reName(filename, 7);
                            writeBytes(filenameDone, TextDone1);
                            break;
                        case 8:
                            System.out.println("enter dictionary max size: ");  //2047 - best
                            localChoice = in.nextInt();
                            TextDone1 = lzw(Text1, localChoice);
                            filenameDone = reName(filename, 8);
                            writeBytes(filenameDone, TextDone1);
                            break;
                        case 9:
                            System.out.println("enter dictionary max size: ");
                            localChoice = in.nextInt();
                            TextDone1 = lzw(Text1, localChoice);
                            TextDone1 = encodeHuffman(new String((TextDone1.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1), 256);
                            filenameDone = reName(filename, 9);
                            writeBytes(filenameDone, TextDone1);
                            break;
                    }
                }
            }
            else if (mainChoice == 2) {
                while (iChoice != 0) {
                    System.out.println("Decode:\n0 exit\n1 HA\n2 RLE\n3 BWT+RLE\n4 BWT+MTF+HA\n5 BWT+MTF+RLE+HA\n6 LZSS\n7 LZSS+HA\n8 LZW\n9 LZW+HA");
                    iChoice = in.nextInt();
                    if (iChoice == 0) {
                        break;
                    }
                    in = new Scanner(System.in);
                    System.out.println("Введите путь до файла ");
                    filename = in.nextLine();
                    String Text2 = readBytes(filename);
                    String TextDone2;
                    for (int i = 0; i < filename.length(); i++) {
                        if (i == '.') {
                            iChoice = filename.charAt(i - 1) - 48;
                        }
                    }
                    switch (iChoice) {
                        case 0:
                            break;
                        case 1:
                            TextDone2 = decodeHuffman(Text2);
                            filenameDone = reName(filename, 0);
                            writeBytes(filenameDone, TextDone2);
                            break;
                        case 2:
                            System.out.println("1) symbol length = 1 2) RGB (symbol = 3 bytes) 3) utf-8: ");
                            localChoice = in.nextInt();
                            switch (localChoice) {
                                case 1:
                                    TextDone2 = irle(Text2);
                                    break;
                                case 2:
                                    TextDone2 = Text2.substring(0, Text2.indexOf('R') + 1)
                                            + irle(Text2.substring(Text2.indexOf('R') + 1));
                                    break;
                                case 3:
                                    TextDone2 = irleUTF8(Text2, "" + SpecSym);
                                    break;
                                default:
                                    TextDone2 = "";
                                    break;
                            }
                            filenameDone = reName(filename, 0);
                            writeBytes(filenameDone, TextDone2);
                            break;
                        case 3:
                            TextDone2 = irle(Text2);
                            TextDone2 = iBWt(new String((TextDone2.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1));
                            filenameDone = reName(filename, 0);
                            writeBytes(filenameDone, TextDone2);
                            break;
                        case 4:
                            TextDone2 = decodeHuffman(Text2);
                            TextDone2 = imtf(new String((TextDone2.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1), 256);
                            TextDone2 = iBWt(new String((TextDone2.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1));
                            filenameDone = reName(filename, 0);
                            writeBytes(filenameDone, TextDone2);
                            break;
                        case 5:
                            TextDone2 = decodeHuffman(Text2);
                            TextDone2 = irle(new String((TextDone2.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1));
                            TextDone2 = imtf(new String((TextDone2.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1), 256);
                            TextDone2 = iBWt(new String((TextDone2.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1));
                            filenameDone = reName(filename, 0);
                            writeBytes(filenameDone, TextDone2);
                            break;
                        case 6:
                            TextDone2 = ilzss(Text2);
                            filenameDone = reName(filename, 0);
                            writeBytes(filenameDone, TextDone2);
                            break;
                        case 7:
                            TextDone2 = decodeHuffman(Text2);
                            TextDone2 = ilzss(new String((TextDone2.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1));
                            filenameDone = reName(filename, 0);
                            writeBytes(filenameDone, TextDone2);
                            break;
                        case 8:
                            TextDone2 = ilzw(Text2);
                            filenameDone = reName(filename, 0);
                            writeBytes(filenameDone, TextDone2);
                            break;
                        case 9:
                            TextDone2 = decodeHuffman(Text2);
                            TextDone2 = ilzw(new String((TextDone2.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1));
                            filenameDone = reName(filename, 0);
                            writeBytes(filenameDone, TextDone2);
                            break;
                    }
                }
            }
        }
    }
}