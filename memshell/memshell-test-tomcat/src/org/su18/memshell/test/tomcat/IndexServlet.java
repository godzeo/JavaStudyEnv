package src.org.su18.memshell.test.tomcat;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * @author su18
 */
public class IndexServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String message = "tomcat index servlet test";
		String id      = req.getParameter("id");

		StringBuilder sb = new StringBuilder();
		sb.append(message);
		if (id != null && !id.isEmpty()) {
			sb.append("\nid: ").append(id);
		}

		resp.getWriter().println(sb);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String message = "tomcat post readobject test";
		String ser      = req.getParameter("ser");

		try{
			// 打开一个文件输入流
			FileInputStream fileIn = new FileInputStream("/Users/zy/Desktop/ser.bin");
			// 建立对象输入流
			ObjectInputStream in = new ObjectInputStream(fileIn);
			// 读取对象
			String e = (String) in.readObject();
			in.close();
			fileIn.close();
		}catch(IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		resp.getWriter().println(message);
	}
}
