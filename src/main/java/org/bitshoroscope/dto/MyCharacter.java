package org.bitshoroscope.dto;

public class MyCharacter {

	private Integer id;
	private String name;
	private String lastname;

	public MyCharacter(Integer id, String name, String lastname) {
		super();
		this.id = id;
		this.name = name;
		this.lastname = lastname;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

}
