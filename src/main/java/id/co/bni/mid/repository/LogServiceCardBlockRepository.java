package id.co.bni.mid.repository;

import id.co.bni.mid.model.LogServiceCardBlock;
import id.co.bni.mid.model.LogServiceEcoll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface
LogServiceCardBlockRepository extends JpaRepository<LogServiceCardBlock, Long> {
}
