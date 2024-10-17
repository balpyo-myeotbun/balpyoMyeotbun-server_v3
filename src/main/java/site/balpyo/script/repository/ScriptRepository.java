package site.balpyo.script.repository;

import site.balpyo.script.entity.Script;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScriptRepository extends JpaRepository<Script, Long> {

}
