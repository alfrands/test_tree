package id.co.bni.mid.repository;

import id.co.bni.mid.model.LogService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogServiceRepository extends JpaRepository<LogService, Long> {
}
