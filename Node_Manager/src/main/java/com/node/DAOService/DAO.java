package com.node.DAOService;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.node.beans.Node;
import com.node.repo.NodeRepository;

@Service
public class DAO {

	@Autowired
	private NodeRepository repo;
	
	public void add(Node node) {
		
		repo.save(node);
	}
	
	public ArrayList<Node> getAll()
	{
		return (ArrayList<Node>) repo.findAll();
	}

	public Node getNodeByName(String name) {
	
		return repo.getNodeByName(name);
	}
	
	public void deleteNode(Node n)
	{
		repo.delete(n);
	}
}

