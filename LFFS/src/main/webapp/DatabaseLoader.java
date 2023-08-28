package main.webapp;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.webapp.pojo.DataLoader;
import main.webapp.pojo.Game;
import main.webapp.pojo.Province;
import main.webapp.pojo.Season;
import main.webapp.pojo.Serie;
import main.webapp.pojo.Team;

@WebServlet("/hello")
public class DatabaseLoader extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.init();
		System.out.println("Loading data...");
		try {
			Season s = DataLoader.getSeason();
			for (Province p : s.getProvinces()) {
				System.out.println(p.getProvince());
				for (Serie s2 : p.getSeries()) {
					System.out.println("   " + s2.getSerie_name() + "=>" + s2.getId());
					for(Team t : s2.getTeams())
						if(t.getName().equals("BF DINANT C")) {
							System.out.println(t.getName() + "=>" + t.getId());
							for(List<Game> list : t.getGames().values())
								for(Game g : list)
									System.out.println(g.getDate());
						}
				}
			}
		} catch (Exception | Error e) {
			e.printStackTrace();
		}
		System.out.println("Data loaded");

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println(req.getParameter("test"));
		resp.setContentType("text/plain");
		resp.getWriter().write("Hello World! Maven Web Project Example.");
	}

	public static void main(String[] args) throws ServletException {
		new DatabaseLoader().init();
	}
}