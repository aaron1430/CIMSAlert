package com.alert.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alert.entity.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Integer> {

}
