package com.node.DAOService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.node.beans.Members;
import com.node.repo.MemberRepo;

@Service
public class MemberService {

	@Autowired
	private MemberRepo repo;
	
	public List<Members> getList()
	{
		return repo.findAll();
	}
	
	public void save(Members m)
	{
		repo.save(m);
	}
}
