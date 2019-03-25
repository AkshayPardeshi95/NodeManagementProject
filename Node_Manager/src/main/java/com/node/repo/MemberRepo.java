package com.node.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.node.beans.Members;

public interface MemberRepo extends JpaRepository<Members, Long> {

}
