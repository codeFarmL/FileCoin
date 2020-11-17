package org.codefarml.filecoin;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * 作者    lgc
 * 时间    2020/11/17 14:51
 * 文件    FileCoin
 * 描述
 */
class NumericUtil {
    private final static SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String HEX_PREFIX = "0x";

    public static byte[] generateRandomBytes(int size) {
        byte[] bytes = new byte[size];
        SECURE_RANDOM.nextBytes(bytes);
        return bytes;
    }

    public static boolean isValidHex(String value) {
        if (value == null) {
            return false;
        }
        if (value.startsWith("0x") || value.startsWith("0X")) {
            value = value.substring(2, value.length());
        }

        if (value.length() == 0 || value.length() % 2 != 0) {
            return false;
        }

        String pattern = "[0-9a-fA-F]+";
        return Pattern.matches(pattern, value);
        // If TestRpc resolves the following issue, we can reinstate this code
        // https://github.com/ethereumjs/testrpc/issues/220
        // if (value.length() > 3 && value.charAt(2) == '0') {
        //    return false;
        // }
    }

    public static String cleanHexPrefix(String input) {
        if (hasHexPrefix(input)) {
            return input.substring(2);
        } else {
            return input;
        }
    }

    public static String prependHexPrefix(String input) {
        if (input.length() > 1 && !hasHexPrefix(input)) {
            return HEX_PREFIX + input;
        } else {
            return input;
        }
    }

    private static boolean hasHexPrefix(String input) {
        return input.length() > 1 && input.charAt(0) == '0' && input.charAt(1) == 'x';
    }

    public static BigInteger bytesToBigInteger(byte[] value, int offset, int length) {
        return bytesToBigInteger((Arrays.copyOfRange(value, offset, offset + length)));
    }

    public static BigInteger bytesToBigInteger(byte[] value) {
        return new BigInteger(1, value);
    }

    public static BigInteger hexToBigInteger(String hexValue) {
        String cleanValue = cleanHexPrefix(hexValue);
        return new BigInteger(cleanValue, 16);
    }

    public static String bigIntegerToHex(BigInteger value) {
        return value.toString(16);
    }
    public static byte[] bigIntegerToBytesWithZeroPadded(BigInteger value, int length) {
        byte[] result = new byte[length];
        byte[] bytes = value.toByteArray();

        int bytesLength;
        int srcOffset;
        if (bytes[0] == 0) {
            bytesLength = bytes.length - 1;
            srcOffset = 1;
        } else {
            bytesLength = bytes.length;
            srcOffset = 0;
        }

        if (bytesLength > length) {
            throw new RuntimeException("Input is too large to put in byte array of size " + length);
        }

        int destOffset = length - bytesLength;
        System.arraycopy(bytes, srcOffset, result, destOffset, bytesLength);
        return result;
    }

    public static byte[] hexToBytes(String input) {
        String cleanInput = cleanHexPrefix(input);

        int len = cleanInput.length();

        if (len == 0) {
            return new byte[]{};
        }

        byte[] data;
        int startIdx;
        if (len % 2 != 0) {
            data = new byte[(len / 2) + 1];
            data[0] = (byte) Character.digit(cleanInput.charAt(0), 16);
            startIdx = 1;
        } else {
            data = new byte[len / 2];
            startIdx = 0;
        }

        for (int i = startIdx; i < len; i += 2) {
            data[(i + 1) / 2] = (byte) ((Character.digit(cleanInput.charAt(i), 16) << 4)
                    + Character.digit(cleanInput.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte[] hexToBytesLittleEndian(String input) {
        byte[] bytes = hexToBytes(input);
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            return bytes;
        }
        int middle = bytes.length / 2;
        for (int i = 0; i < middle; i++) {
            byte b = bytes[i];
            bytes[i] = bytes[bytes.length - 1 - i];
            bytes[bytes.length - 1 - i] = b;
        }
        return bytes;
    }

    public static byte[] reverseBytes(byte[] bytes) {
        int middle = bytes.length / 2;
        for (int i = 0; i < middle; i++) {
            byte b = bytes[i];
            bytes[i] = bytes[bytes.length - 1 - i];
            bytes[bytes.length - 1 - i] = b;
        }
        return bytes;
    }

    //Hex16进制
    public static String bytesToHex(byte[] input) {
        StringBuilder stringBuilder = new StringBuilder();
        if (input.length == 0) {
            return "";
        }

        for (byte anInput : input) {
            stringBuilder.append(String.format("%02x", anInput));
        }

        return stringBuilder.toString();
    }


    public static String beBigEndianHex(String hex) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            return hex;
        }

        return reverseHex(hex);
    }

    public static String beLittleEndianHex(String hex) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            return hex;
        }

        return reverseHex(hex);
    }

    private static String reverseHex(String hex) {
        byte[] bytes = hexToBytes(hex);
        bytes = reverseBytes(bytes);
        return bytesToHex(bytes);
    }

    public static int bytesToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static byte[] intToBytes(int intValue) {

        byte[] intBytes = ByteBuffer.allocate(4).putInt(intValue).array();
        int zeroLen = 0;
        for (byte b : intBytes) {
            if (b != 0) {
                break;
            }
            zeroLen++;
        }
        if (zeroLen == 4) {
            zeroLen = 3;
        }
        return Arrays.copyOfRange(intBytes, zeroLen, intBytes.length);
    }

    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String bytesToHex1(byte[] bytes) {
        // 一个byte为8位，可用两个十六进制位标识
        char[] buf = new char[bytes.length * 2];
        int a = 0;
        int index = 0;
        for (byte b : bytes) { // 使用除与取余进行转换
            if (b < 0) {
                a = 256 + b;
            } else {
                a = b;
            }

            buf[index++] = HEX_CHAR[a / 16];
            buf[index++] = HEX_CHAR[a % 16];
        }

        return new String(buf);
    }

    /**
     * 字符串转化成为16进制字符串
     * @param s
     * @return
     */
    public static String stringToHex(String s) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s16 = Integer.toHexString(ch);
            str.append(s16);
        }
        return str.toString();
    }

    /**
     * 16进制转换成为string类型字符串
     * @param s
     * @return
     */
    public static String hexToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-8");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }
}
