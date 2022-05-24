package id.co.bni.mid.repository;

import id.co.bni.mid.model.LogService;
import id.co.bni.mid.model.LogServiceEcoll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface
LogServiceEcollRepository extends JpaRepository<LogServiceEcoll, Long> {
}
