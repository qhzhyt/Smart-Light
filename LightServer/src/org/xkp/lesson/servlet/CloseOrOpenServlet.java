package org.xkp.lesson.servlet;

import java.io.BufferedWriter;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xkp.lesson.socket.SocketService;

/**
 * Servlet implementation class CloseOrOpenServlet
 */
@WebServlet("/CloseOrOpenServlet")
public class CloseOrOpenServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CloseOrOpenServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		String flag = request.getParameter("flag");
		if(flag.equals("true")) {
			//开灯成功  int result = openLight();
			int result = SocketService.openOrCloseLight(true);
			//获取response的输出流，相应前端页面的请求
			BufferedWriter writer = new BufferedWriter(response.getWriter());
			writer.write(""+result);





			writer.flush();
			writer.close();
			
		}
		
		if(flag.equals("false")) {
			//关灯
			//关灯成功  int result = closeLight();
			int result = SocketService.openOrCloseLight(false);
			//获取response的输出流，相应前端页面的请求
			BufferedWriter writer = new BufferedWriter(response.getWriter());
			writer.write(""+result);
			writer.flush();
			writer.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
