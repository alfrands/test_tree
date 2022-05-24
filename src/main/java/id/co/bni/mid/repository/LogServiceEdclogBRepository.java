package id.co.bni.mid.repository;

import id.co.bni.mid.model.LogServiceEdclogB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogServiceEdclogBRepository extends JpaRepository<LogServiceEdclogB, Long> {
}
