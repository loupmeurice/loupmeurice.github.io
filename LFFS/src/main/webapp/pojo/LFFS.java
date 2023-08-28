package main.webapp.pojo;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LFFS  implements Serializable {
	
	private Map<Integer, Season> seasons = new HashMap<Integer, Season>();
	
	
	public void addSeason(Season s) {
		seasons.put(s.getEndYear(), s);
	}
	

}
