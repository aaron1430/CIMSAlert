package com.alert.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alert.entity.AlarmLevel;

public interface AlarmLevelRepository extends JpaRepository<AlarmLevel, Integer> {
	@Query("select a from AlarmLevel a where alle_rank = :alle_rank ")
	List<AlarmLevel> getAlarmSettingsByRank(@Param("alle_rank") Integer alle_rank);
}
