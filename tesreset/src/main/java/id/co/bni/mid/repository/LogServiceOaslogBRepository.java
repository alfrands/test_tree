package id.co.bni.mid.repository;

import id.co.bni.mid.model.LogServiceOaslogB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogServiceOaslogBRepository extends JpaRepository<LogServiceOaslogB, Long> {
}
