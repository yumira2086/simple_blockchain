package com.blockchain.crypto;

import cn.hutool.crypto.digest.DigestUtil;
import com.blockchain.bean.block.PairKey;
import com.blockchain.common.App;
import com.blockchain.common.Constants;
import com.blockchain.common.ResultCode;
import com.blockchain.crypto.algorithm.Base58Algorithm;
import com.blockchain.crypto.algorithm.BaseAlgorithm;
import com.blockchain.crypto.algorithm.ECDSAAlgorithm;
import com.blockchain.crypto.exception.ErrorNum;
import com.blockchain.crypto.exception.TrustSDKException;
import com.blockchain.exception.ApiException;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.util.StringUtils;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by: Yumira.
 * Created on: 2018/8/3-上午10:29.
 * Description: 修改原有不规则密钥对，改为中文私钥规则
 *              中文私钥为12个常用汉字，汉字之间可以有任意多个空格
 */
public class Cryptor {

    /**
     * test
     */
    public static void main(String[] args) throws Exception {
        PairKey pairKey = generatePairKey();
//        String prK = generatePrivateWords();
        System.out.println("私钥:"+pairKey.getKeyWords());
        System.out.println("私钥:"+pairKey.getPrivateKey());
        System.out.println("公钥:"+pairKey.getPublicKey());

        System.out.println(checkPairKey(pairKey.getKeyWords(), pairKey.getPublicKey()));
        System.out.println("私钥生成的addr:"+getAddressByPrivateWords(pairKey.getKeyWords()));
        System.out.println("公钥生成的addr:"+generateAddress(pairKey.getPublicKey()));
        String sign = signString(pairKey.getPrivateKey(), "被签名的字符串");
        System.out.println("私钥签名:"+sign);
        System.out.println("公钥校验签名结果:"+verifyString(pairKey.getPublicKey(),"被签名的字符串",sign));
        //葛 互 冈 境 沥 您 鸡 花 癌 倡 沸 戴
        System.out.println(checkPairKey("6JGb5LqS5YaI5aKD5rKl5oKo6bih6Iqx", "AkC4NhQwwgP9FdgHyoz397Hp/iTrv1R3Sd3UFY3xKaS1"));
    }


    /**
     * 生成12个随机汉字，作为私钥的seed
     * @return
     */
    public static String generatePrivateWords(){
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstance(Constants.RANDOM_NUMBER_ALGORITHM,
                    Constants.RANDOM_NUMBER_ALGORITHM_PROVIDER);
        } catch (Exception e) {
            secureRandom = new SecureRandom();
        }
        byte[] wordSeed = new byte[16];
        secureRandom.nextBytes(wordSeed);
        //根据字典生成汉字助记词
        String words = generateMnemonic(wordSeed);
        return words;
    }


    /**
     * 12个汉字 ==> 字节数组  ==>  BigInt(私钥) ==>  base64
     * @param words
     * @return
     */
    public static String convertToPrivateKey(String words){
        words = replacePlace(words);
        byte[] seed = words.getBytes();
        byte[] privateKey = new byte[(seed.length * 2) / 3];
        System.arraycopy(seed,0,privateKey,0,privateKey.length);
        String result = Base64.encodeBase64String(privateKey);
        result = result.replaceAll("[\\s*\t\n\r]", "");
        return result;
    }


    public static String replacePlace(String str){
        return str.replaceAll("\\s{1,}", "");
    }

    /**
     * 生成私钥公钥对.
     */
    public static PairKey generatePairKey() throws TrustSDKException {
        try {
            PairKey pair = new PairKey();
            String privateWords = generatePrivateWords();
            String privateKey = convertToPrivateKey(privateWords);
            String pubKey = generatePublicKey(privateWords.trim());
            pair.setKeyWords(privateWords);
            pair.setPrivateKey(privateKey);
            pair.setPublicKey(pubKey);
            return pair;
        } catch (Exception e) {
            throw new TrustSDKException(ErrorNum.ECDSA_ENCRYPT_ERROR.getRetCode(), ErrorNum.ECDSA_ENCRYPT_ERROR.getRetMsg(), e);
        }
    }


    /**
     * 校验公私钥是否匹配
     */
    public static boolean checkPairKey(String prvKey, String pubKey) throws TrustSDKException {
        if (StringUtils.isEmpty(prvKey) || StringUtils.isEmpty(pubKey)) {
            throw new TrustSDKException(ErrorNum.INVALID_PARAM_ERROR.getRetCode(), ErrorNum.INVALID_PARAM_ERROR.getRetMsg());
        }
        try {
            String correctPubKey = generatePublicKey(prvKey.trim());
            return pubKey.trim().equals(correctPubKey);
        } catch(Exception e) {
            throw new TrustSDKException(ErrorNum.ECDSA_ENCRYPT_ERROR.getRetCode(), ErrorNum.ECDSA_ENCRYPT_ERROR.getRetMsg(), e);
        }
    }


    /**
     * 生成公钥，encode为true时为短公钥
     * 是否使用base64缩短
     * @return
     * 公钥
     */
    public static String generatePublicKey(String words) {
        try {
            //解码Base64，得到原私钥
            String seed;
            if (replacePlace(words).length() > 12){
                seed = words;
            }else {
                seed = convertToPrivateKey(words);
            }
            byte[] privateKeyBytes = Base64.decodeBase64(seed);
            BigInteger privateKey = new BigInteger(1, privateKeyBytes);
            //声明一个椭圆曲线加密工具
            ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
            //求出私钥对应的椭圆曲线点
            ECPoint pointQ = spec.getG().multiply(privateKey);
            //根据坐标生成公钥，大整数
            byte[] publicKeyBytes = pointQ.getEncoded(true);//true 短公钥
            //将公钥Base64生成字符串
            String result = Base64.encodeBase64String(publicKeyBytes);
            result = result.replaceAll("[\\s*\t\n\r]", "");
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * 根据公钥生成address
     */
    public static String generateAddress(String publicKey) throws Exception {
        if (StringUtils.isEmpty(publicKey)) {
            throw new TrustSDKException(ErrorNum.INVALID_PARAM_ERROR.getRetCode(), ErrorNum.INVALID_PARAM_ERROR.getRetMsg());
        }
        return generateAddress(Base64.decodeBase64(publicKey),(byte) 0);
    }



    /**
     * 根据公钥生成地址
     * @param keyBytes
     * 公钥
     * 版本，可以不用
     * @return
     * address
     * @throws Exception
     * exception
     */
    public static String generateAddress(byte[] keyBytes, byte version) throws Exception {
        //公钥     例：02d5100e222697749f26cf1c516a737b611fb785d47813a899764405cdfeb12be5
        //1.对公钥求sha-256    例：b3ac58c7a44440dd214a6d0bc25595e5f7bd4911317a8d3c958d99a45c7f2dc3
        byte[] hashSha256 = BaseAlgorithm.encode("SHA-256", keyBytes);
        //2.再将结果计算RIPEMD-160哈希值     例：537feecb0397d9d16a025e942cac95e915faf737
        MessageDigest messageDigest = MessageDigest.getInstance("RipeMD160");
        messageDigest.update(hashSha256);
        byte[] hashRipeMD160 = messageDigest.digest();
        //新建一个byte数组接收拼接版本号之后的值，需要+1byte
        byte[] versionHashBytes = new byte[hashRipeMD160.length + 1];
        //判断版本号，如果小于0，作0处理
        if(version < 0) {
            versionHashBytes[0] = (byte) 0;
        } else {
            versionHashBytes[0] = version;
        }
        //3.拼接版本号，将版本号拼到原byte数组之前    例：00537feecb0397d9d16a025e942cac95e915faf737
        System.arraycopy(hashRipeMD160, 0, versionHashBytes, 1, hashRipeMD160.length);
        //4.将得到的新数组进行2次SHA-256计算    例：5891b240c1dca55d2322d07395fd5587819cea9c2b3d2918d23c95f86af8fcae
        byte[] checkSumBytes = BaseAlgorithm.encodeTwice("SHA-256", versionHashBytes);
        //新建一个byte数组，接收最终值
        byte[] rawAddr = new byte[versionHashBytes.length + 4];
        System.arraycopy(versionHashBytes, 0, rawAddr, 0, versionHashBytes.length);
        //5.取上一步结果的前4个字节，拼到第3步结果的后面     例：00537feecb0397d9d16a025e942cac95e915faf7375891b240
        System.arraycopy(checkSumBytes, 0, rawAddr, versionHashBytes.length, 4);
        //6.对结果进行Base58编码     例：18cWLrAaztN6BmAE1nqaZQHFB2oWqiDED5
        return Base58Algorithm.encode(rawAddr);
    }


    /**
     * 通过私钥计算相应地址
     */
    public static String getAddressByPrivateWords(String words) throws Exception {
        if (StringUtils.isEmpty(words)) {
            throw new TrustSDKException(ErrorNum.INVALID_PARAM_ERROR.getRetCode(), ErrorNum.INVALID_PARAM_ERROR.getRetMsg());
        }
        return generateAddress(generatePublicKey(words));
    }

    /**
     * 为字符串进行签名, 并返回签名结果
     */
    public static String signString(String words, byte[] data) throws TrustSDKException {
        if (StringUtils.isEmpty(words)) {
            throw new TrustSDKException(ErrorNum.INVALID_PARAM_ERROR.getRetCode(), ErrorNum.INVALID_PARAM_ERROR.getRetMsg());
        }
        String seed;
        if (replacePlace(words).length() > 12){
            seed = words;
        }else {
            seed = convertToPrivateKey(words);
        }
        return ECDSAAlgorithm.sign(seed, data);
    }

    public static String signString(String privateWords, String data) throws TrustSDKException, UnsupportedEncodingException {
        return signString(privateWords, data.getBytes("UTF-8"));
    }

    /**
     * 校验签名
     */
    public static boolean verifyString(String pubKey, String source, String sign) throws Exception {
        if (StringUtils.isEmpty(pubKey) || StringUtils.isEmpty(source) || StringUtils.isEmpty(sign)) {
            throw new TrustSDKException(ErrorNum.INVALID_PARAM_ERROR.getRetCode(), ErrorNum.INVALID_PARAM_ERROR.getRetMsg());
        }
        return ECDSAAlgorithm.verify(source, sign, pubKey);
    }



    /**
     * 随机从配置文件字典里取出12个常用汉字，作为私钥seed
     * @param initialEntropy
     * @return
     * @throws IOException
     */
    public static String generateMnemonic(byte[] initialEntropy) {
        if (App.WORD_LIST == null) {
            try {
                InputStream inputStream = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("dictionary.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                List<String> data = new ArrayList<>();
                for (String line; (line = br.readLine()) != null; ) {
                    data.add(line);
                }
                App.WORD_LIST = data;
            } catch (IOException e) {
                new ApiException(ResultCode.FAIL,"找不到 dictionary.txt ，请检查配置文件");
            }
        }

        int ent = initialEntropy.length * 8;
        int checksumLength = ent / 32;

        byte checksum = calculateChecksum(initialEntropy);
        boolean[] bits = convertToBits(initialEntropy, checksum);

        int iterations = (ent + checksumLength) / 11;
        StringBuilder mnemonicBuilder = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            int index = toInt(nextElevenBits(bits, i));
            mnemonicBuilder.append(App.WORD_LIST.get(index));

            boolean notLastIteration = i < iterations - 1;
            if (notLastIteration) {
                mnemonicBuilder.append("");
            }
        }

        return mnemonicBuilder.toString();
    }


    private static boolean toBit(byte value, int index) {
        return ((value >>> (7 - index)) & 1) > 0;
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

    private static byte calculateChecksum(byte[] initialEntropy) {
        int ent = initialEntropy.length * 8;
        byte mask = (byte) (0xff << 8 - ent / 32);
        byte[] bytes = DigestUtil.sha256(initialEntropy);

        return (byte) (bytes[0] & mask);
    }

    private static boolean[] convertToBits(byte[] initialEntropy, byte checksum) {
        int ent = initialEntropy.length * 8;
        int checksumLength = ent / 32;
        int totalLength = ent + checksumLength;
        boolean[] bits = new boolean[totalLength];

        for (int i = 0; i < initialEntropy.length; i++) {
            for (int j = 0; j < 8; j++) {
                byte b = initialEntropy[i];
                bits[8 * i + j] = toBit(b, j);
            }
        }

        for (int i = 0; i < checksumLength; i++) {
            bits[ent + i] = toBit(checksum, i);
        }

        return bits;
    }

    private static boolean[] nextElevenBits(boolean[] bits, int i) {
        int from = i * 11;
        int to = from + 11;
        return Arrays.copyOfRange(bits, from, to);
    }
}
