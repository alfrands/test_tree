package id.co.bni.mid.repository;

import id.co.bni.mid.model.LogServiceTransCC;
import id.co.bni.mid.model.LogServiceValidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface
LogServiceTransCCRepository extends JpaRepository<LogServiceTransCC, Long> {
}
