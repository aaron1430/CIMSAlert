package com.alert.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alert.entity.ProjectStage;

public interface ProjectStageRepository extends JpaRepository<ProjectStage, Integer> {

	@Query("select p from ProjectStage p where prst_state = :prst_state ")
	List<ProjectStage> getNodesByState(@Param("prst_state") Integer prstState);

}
