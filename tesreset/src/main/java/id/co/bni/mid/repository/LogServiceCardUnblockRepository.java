package id.co.bni.mid.repository;

import id.co.bni.mid.model.LogServiceCardUnblock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogServiceCardUnblockRepository extends JpaRepository<LogServiceCardUnblock, Long> {
}
