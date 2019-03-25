package com.node.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.node.beans.Node;

@Repository
public interface NodeRepository extends CrudRepository<Node, String>{

	public Node getNodeByName(String name);
}
