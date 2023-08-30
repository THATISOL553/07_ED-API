package com.edapi.repository;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.edapi.entity.CitizenApiEntity;


public interface CitizenAppRepository extends JpaRepository<CitizenApiEntity, Serializable>{

}
