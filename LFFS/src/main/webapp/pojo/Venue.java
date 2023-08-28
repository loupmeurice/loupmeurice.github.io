package main.webapp.pojo;

import java.io.Serializable;

public class Venue  implements Serializable {
	
	private Integer id;
	private Room room;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Room getRoom() {
		return room;
	}
	public void setRoom(Room room) {
		this.room = room;
	}

}
