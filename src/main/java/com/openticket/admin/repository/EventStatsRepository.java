package com.openticket.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.openticket.admin.entity.EventStats;

public interface EventStatsRepository extends JpaRepository<EventStats, Long> {
    List<EventStats> findByIdIn(List<Long> eventIds);

    @Query("""
                SELECT COALESCE(SUM(es.views), 0)
                FROM EventStats es
                WHERE es.id IN :eventIds
            """)
    Long sumViewsByEventIds(@Param("eventIds") List<Long> eventIds);

}
