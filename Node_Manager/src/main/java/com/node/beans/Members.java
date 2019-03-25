package com.node.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Members {

	@Id
	private long empId;
	@Column(name="Employee_Name")
	private String name;
	public String getName() {
		return name;
	}
	public Members() {
		super();
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getEmpId() {
		return empId;
	}
	
	
}
