package com.edapi.repository;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.edapi.entity.IncomeEntity;

public interface IncomeRepository extends JpaRepository<IncomeEntity,Serializable>{

	public IncomeEntity findByCaseNum(Long caseNum);
}
