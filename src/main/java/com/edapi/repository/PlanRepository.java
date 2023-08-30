package com.edapi.repository;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.edapi.entity.PlanEntity;

public interface PlanRepository extends JpaRepository<PlanEntity, Serializable>{

}
