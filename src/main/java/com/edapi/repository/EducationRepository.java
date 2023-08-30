package com.edapi.repository;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.edapi.entity.EducationEntity;


public interface EducationRepository extends JpaRepository<EducationEntity, Serializable>{
	
	public EducationEntity findByCaseNum(Long caseNum);
}
