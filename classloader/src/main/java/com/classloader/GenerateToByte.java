package com.classloader;

import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


public class GenerateToByte {

    public static void main(String[] args) throws IOException {
        FileInputStream fileInputStream = new FileInputStream("/Users/zy/Desktop/fastjson1224/target/classes/com/classloader/Command.class");
        byte[]          bytes           = toByteArray(fileInputStream);

        System.out.println(Arrays.toString(bytes));
//        BASE64Encoder encoder         = new BASE64Encoder();
//        System.out.println(encoder.encode(bytes).replace("\n", ""));


    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[]                buffer = new byte[10240];
        int                   n      = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

}
