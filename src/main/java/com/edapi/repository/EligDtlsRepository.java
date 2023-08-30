package com.edapi.repository;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.edapi.entity.EligDtlsEntity;

public interface EligDtlsRepository extends JpaRepository<EligDtlsEntity, Serializable> {

}
