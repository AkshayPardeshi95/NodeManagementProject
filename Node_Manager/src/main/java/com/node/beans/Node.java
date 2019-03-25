package com.node.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Node_Details")
public class Node {

	
	@Id
	@Column
	private String name;
	
	@Column
	private String assignedActivity;
	
	@Column
	private String owner;
	
	@Column
	private String assignedBy;
	
	@Column
	private String installedBuild;
	
	@Column
	private String nodeType;
	
	
	@Column
	private String note;
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	@Column
	private String State;
	
	public Node(String name, String assignedActivity, String owner, String assignedBy, String installedBuild,
			String nodeType) {
		super();
		this.name = name;
		this.assignedActivity = assignedActivity;
		this.owner = owner;
		this.assignedBy = assignedBy;
		this.installedBuild = installedBuild;
		this.nodeType = nodeType;
	}
	
	public Node()
	{
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAssignedActivity() {
		return assignedActivity;
	}

	public void setAssignedActivity(String assignedActivity) {
		this.assignedActivity = assignedActivity;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(String assignedBy) {
		this.assignedBy = assignedBy;
	}

	public String getInstalledBuild() {
		return installedBuild;
	}

	public void setInstalledBuild(String installedBuild) {
		this.installedBuild = installedBuild;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	@Override
	public String toString() {
		return "Node [ name=" + name + ", assignedActivity=" + assignedActivity + ", owner=" + owner
				+ ", assignedBy=" + assignedBy + ", installedBuild=" + installedBuild + ", NodeType=" + nodeType + "]";
	}
	
}
