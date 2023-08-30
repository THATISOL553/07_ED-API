package com.edapi.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edapi.entity.ChildrenEntity;


public interface ChildRepository extends JpaRepository<ChildrenEntity, Serializable>{
	
	public List<ChildrenEntity> findByCaseNum(Long caseNum);
}
