package com.blockchain.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by: Yumira.
 * Created on: 2018/8/2-下午10:38.
 * Description:
 */
public class WordUtil {

    private static List<String> WORD_LIST = null;


    private static List<String> populateWordList() throws IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("dictionary.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        List<String> data = new ArrayList<>();
        for (String line; (line = br.readLine()) != null; ) {
            data.add(line);
        }
        return data;
    }


    /**
     * QBcZ9f/ccvSuynw4VXFDFavzHDiJlt13hAHBO36hhsj2PAs8wfIZHxpoEItGPoDsELbvKi2H36Df6Hbk/oYQfA==
     */
    public static void generateMnemonic(String privateKey){

//        int ent = initialEntropy.length * 8;
//        int checksumLength = ent / 32;
//
////        byte checksum = calculateChecksum(initialEntropy);
////        boolean[] bits = convertToBits(initialEntropy, checksum);
//
//        int iterations = (ent + checksumLength) / 11;
//        StringBuilder mnemonicBuilder = new StringBuilder();
//        for (int i = 0; i < iterations; i++) {
//            int index = toInt(nextElevenBits(bits, i));
//            mnemonicBuilder.append(WORD_LIST.get(index));
//
//            boolean notLastIteration = i < iterations - 1;
//            if (notLastIteration) {
//
//                mnemonicBuilder.append(" ");
//            }
//        }

    }


    private static int toInt(boolean[] bits) {
        int value = 0;
        for (int i = 0; i < bits.length; i++) {
            boolean isSet = bits[i];
            if (isSet)  {
                value += 1 << bits.length - i - 1;
            }
        }

        return value;
    }


    private static boolean[] nextElevenBits(boolean[] bits, int i) {
        int from = i * 11;
        int to = from + 11;
        return Arrays.copyOfRange(bits, from, to);
    }

}
