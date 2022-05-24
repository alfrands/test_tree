package id.co.bni.mid.repository;

import id.co.bni.mid.model.LogServiceIcl;
import id.co.bni.mid.model.LogServiceInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface
LogServiceIclRepository extends JpaRepository<LogServiceIcl, Long> {
}
