package site.balpyo.script.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import site.balpyo.script.entity.Script;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScriptRepository extends JpaRepository<Script, Long>, JpaSpecificationExecutor<Script> {
    // 태그와 isGenerating 조건으로 검색
    List<Script> findByTagsContainingAndIsGenerating(String tag, Boolean isGenerating);

    // 태그로만 검색
    List<Script> findByTagsContaining(String tag);

    // isGenerating 값만으로 검색
    List<Script> findByIsGenerating(Boolean isGenerating);
}
