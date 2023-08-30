package com.edapi.repository;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.edapi.entity.DcCasesEntity;

public interface DcCasesRepository extends JpaRepository<DcCasesEntity, Serializable>{

}
