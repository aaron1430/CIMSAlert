package com.alert.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alert.entity.ReceiveNode;

public interface ReceiveNodeRepository extends JpaRepository<ReceiveNode, Integer> {

	@Query("select r from ReceiveNode r where reno_state = :reno_state ")
	List<ReceiveNode> getNodesByState(@Param("reno_state") Integer renoState);
}
