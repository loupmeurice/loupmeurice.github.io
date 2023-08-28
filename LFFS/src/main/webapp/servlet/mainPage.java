package main.webapp.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.webapp.model.Employee;
import main.webapp.pojo.DataLoader;
import main.webapp.pojo.Province;
import main.webapp.pojo.Season;

@WebServlet("/home")
public class mainPage extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Season s;
		try {
			s = DataLoader.getSeason();
			System.out.println("Provinces: " + s.getProvinces().size());
//			s.getProvinces().get(0).getSeries().get(0).getTeams().get(0).get
			
			Employee emp=new Employee(10,"Mr.A");
	        //Insert "model" object to request
	        req.setAttribute("emp",emp);
	        req.setAttribute("season", s);

	        //forward the request to view.jsp
	        req.getRequestDispatcher("/index.jsp").
	                 forward(req,resp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
