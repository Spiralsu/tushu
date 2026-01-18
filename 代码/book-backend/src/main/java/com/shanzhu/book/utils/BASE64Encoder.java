package com.shanzhu.book.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Base64工具类 (完美替换原sun.misc版本，无任何外部依赖，兼容所有Java版本)
 * 包名、类名不变，原项目调用代码无需任何修改
 * @author: ShanZhu
 * @date: 2023-12-31
 */
public class BASE64Encoder {
    private static final char[] PEM_ARRAY = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    public BASE64Encoder() {
    }

    /**
     * 核心编码方法 - 原项目调用的就是这个方法，完全保留
     * @param bytes 要编码的字节数组
     * @return Base64编码后的字符串
     */
    public String encode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int len = bytes.length;
        int i = 0;
        int b1, b2, b3;
        while (i < len) {
            // 读取第一个字节
            b1 = bytes[i++] & 0xff;
            if (i == len) {
                sb.append(PEM_ARRAY[b1 >>> 2]);
                sb.append(PEM_ARRAY[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            // 读取第二个字节
            b2 = bytes[i++] & 0xff;
            if (i == len) {
                sb.append(PEM_ARRAY[b1 >>> 2]);
                sb.append(PEM_ARRAY[((b1 & 0x3) << 4) | ((b2 & 0xF0) >>> 4)]);
                sb.append(PEM_ARRAY[(b2 & 0xF) << 2]);
                sb.append("=");
                break;
            }
            // 读取第三个字节
            b3 = bytes[i++] & 0xff;
            sb.append(PEM_ARRAY[b1 >>> 2]);
            sb.append(PEM_ARRAY[((b1 & 0x3) << 4) | ((b2 & 0xF0) >>> 4)]);
            sb.append(PEM_ARRAY[((b2 & 0xF) << 2) | ((b3 & 0xC0) >>> 6)]);
            sb.append(PEM_ARRAY[b3 & 0x3F]);
        }
        return sb.toString();
    }

    /**
     * 保留原类中的OutputStream编码方法，兼容可能的流调用场景
     */
    protected void encodeAtom(OutputStream out, byte[] data, int offset, int len) throws IOException {
        byte b1, b2, b3;
        if (len == 1) {
            b1 = data[offset];
            out.write(PEM_ARRAY[b1 >>> 2 & 63]);
            out.write(PEM_ARRAY[(b1 << 4 & 48)]);
            out.write(61);
            out.write(61);
        } else if (len == 2) {
            b1 = data[offset];
            b2 = data[offset + 1];
            out.write(PEM_ARRAY[b1 >>> 2 & 63]);
            out.write(PEM_ARRAY[(b1 << 4 & 48) + (b2 >>> 4 & 15)]);
            out.write(PEM_ARRAY[(b2 << 2 & 60)]);
            out.write(61);
        } else {
            b1 = data[offset];
            b2 = data[offset + 1];
            b3 = data[offset + 2];
            out.write(PEM_ARRAY[b1 >>> 2 & 63]);
            out.write(PEM_ARRAY[(b1 << 4 & 48) + (b2 >>> 4 & 15)]);
            out.write(PEM_ARRAY[(b2 << 2 & 60) + (b3 >>> 6 & 3)]);
            out.write(PEM_ARRAY[b3 & 63]);
        }
    }
}