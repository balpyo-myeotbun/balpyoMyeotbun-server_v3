package site.balpyo.script.repository;

import org.springframework.data.jpa.domain.Specification;
import site.balpyo.script.entity.Script;

public class ScriptSpecifications {
    public static Specification<Script> hasTag(String tag) {
        return (root, query, criteriaBuilder) -> tag == null ? null : criteriaBuilder.like(root.get("tags"), "%" + tag + "%");
    }

    public static Specification<Script> isGenerating(Boolean isGenerating) {
        return (root, query, criteriaBuilder) -> isGenerating == null ? null : criteriaBuilder.equal(root.get("isGenerating"), isGenerating);
    }

    public static Specification<Script> containsSearchValue(String searchValue) {
        return (root, query, criteriaBuilder) -> searchValue == null ? null : criteriaBuilder.or(
                criteriaBuilder.like(root.get("title"), "%" + searchValue + "%"),
                criteriaBuilder.like(root.get("content"), "%" + searchValue + "%")
        );
    }
}
