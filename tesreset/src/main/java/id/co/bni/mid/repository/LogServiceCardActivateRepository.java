package id.co.bni.mid.repository;

import id.co.bni.mid.model.LogServiceCardActivate;
import id.co.bni.mid.model.LogServiceCardBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface
LogServiceCardActivateRepository extends JpaRepository<LogServiceCardActivate, Long> {
}
