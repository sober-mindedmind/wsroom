package com.mindedmind.wsroom.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;

import com.mindedmind.wsroom.domain.Complaint;

public interface ComplaintRepository extends JpaRepository<Complaint, Long>
{
	@EntityGraph(value = "Complaint.withOffender", type = EntityGraphType.LOAD)
	@QueryHints(@QueryHint(name = "org.hibernate.annotations.QueryHints.READ_ONLY", value = "true"))
	@Override List<Complaint> findAll();
}
