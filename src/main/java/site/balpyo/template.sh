#!/bin/bash

# 파일을 생성하는 함수
create_file() {
    local dir=$1
    local filename=$2
    local content=$3

    # 지정된 경로가 없으면 해당 경로만 생성
    if [ ! -d "$dir" ]; then
        mkdir -p "$dir"
    fi

    echo "$content" > "$dir/$filename"
    echo "Created $dir/$filename"
}

# 패스칼케이스 변환 함수 (첫 글자만 대문자 처리, 나머지 그대로)
to_pascal_case() {
    local input=$1
    echo "$(echo "${input:0:1}" | tr '[:lower:]' '[:upper:]')${input:1}"
}

# 카멜케이스 변환 함수 (첫 글자만 소문자 처리, 나머지 그대로)
to_camel_case() {
    local input=$1
    echo "$(echo "${input:0:1}" | tr '[:upper:]' '[:lower:]')${input:1}"
}

# 엔티티 이름과 패키지 경로를 물어봄
read -p "엔티티 이름을 입력하세요 (카멜케이스로): " ENTITY_CAMEL
read -p "패키지 경로를 입력하세요 (예: com/junsikkim/module): " PACKAGE_PATH

ENTITY_PASCAL=$(to_pascal_case "$ENTITY_CAMEL")
ENTITY_LOWER=$(to_camel_case "$ENTITY_CAMEL")

# 패키지 경로에서 '/'를 '.'로 변환
PACKAGE=$(echo "$PACKAGE_PATH" | sed 's/\//./g')

# 최종 패키지 경로 생성, dto, entity 등의 경로 앞에 엔티티 이름 추가
ENTITY_PACKAGE="$PACKAGE.$ENTITY_LOWER"

# 파일 템플릿 정의
ENTITY_TEMPLATE="package $ENTITY_PACKAGE.entity;

import javax.persistence.*;

@Entity
public class $ENTITY_PASCAL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public $ENTITY_PASCAL() {}
}"

DTO_TEMPLATE="package $ENTITY_PACKAGE.dto;

public class ${ENTITY_PASCAL}Dto {

    private Long id;
}"

REPOSITORY_TEMPLATE="package $ENTITY_PACKAGE.repository;

import $ENTITY_PACKAGE.entity.$ENTITY_PASCAL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ${ENTITY_PASCAL}Repository extends JpaRepository<$ENTITY_PASCAL, Long> {

}"

SERVICE_TEMPLATE="package $ENTITY_PACKAGE.service;

import $ENTITY_PACKAGE.dto.${ENTITY_PASCAL}Dto;
import java.util.List;

public interface ${ENTITY_PASCAL}Service {

    List<${ENTITY_PASCAL}Dto> getAll${ENTITY_PASCAL}s();

    ${ENTITY_PASCAL}Dto get${ENTITY_PASCAL}ById(Long id);

    ${ENTITY_PASCAL}Dto create${ENTITY_PASCAL}(${ENTITY_PASCAL}Dto ${ENTITY_LOWER}Dto);

    ${ENTITY_PASCAL}Dto update${ENTITY_PASCAL}(Long id, ${ENTITY_PASCAL}Dto ${ENTITY_LOWER}Dto);

    void delete${ENTITY_PASCAL}(Long id);
}"

SERVICE_IMPL_TEMPLATE="package $ENTITY_PACKAGE.service;

import $ENTITY_PACKAGE.dto.${ENTITY_PASCAL}Dto;
import $ENTITY_PACKAGE.entity.$ENTITY_PASCAL;
import $ENTITY_PACKAGE.repository.${ENTITY_PASCAL}Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ${ENTITY_PASCAL}ServiceImpl implements ${ENTITY_PASCAL}Service {

    @Autowired
    private ${ENTITY_PASCAL}Repository repository;

    @Override
    public List<${ENTITY_PASCAL}Dto> getAll${ENTITY_PASCAL}s() {
        return repository.findAll();
    }

    @Override
    public ${ENTITY_PASCAL}Dto get${ENTITY_PASCAL}ById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public ${ENTITY_PASCAL}Dto create${ENTITY_PASCAL}(${ENTITY_PASCAL}Dto ${ENTITY_LOWER}Dto) {
        return repository.save(${ENTITY_LOWER}Dto);
    }

    @Override
    public ${ENTITY_PASCAL}Dto update${ENTITY_PASCAL}(Long id, ${ENTITY_PASCAL}Dto ${ENTITY_LOWER}Dto) {
        // 로직 구현
        return repository.save(${ENTITY_LOWER}Dto);
    }

    @Override
    public void delete${ENTITY_PASCAL}(Long id) {
        repository.deleteById(id);
    }
}"

CONTROLLER_TEMPLATE="package $ENTITY_PACKAGE.controller;

import $ENTITY_PACKAGE.dto.${ENTITY_PASCAL}Dto;
import $ENTITY_PACKAGE.service.${ENTITY_PASCAL}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(\"/${ENTITY_LOWER}s\")
public class ${ENTITY_PASCAL}Controller {

    @Autowired
    private ${ENTITY_PASCAL}Service service;

    @GetMapping
    public List<${ENTITY_PASCAL}Dto> getAll${ENTITY_PASCAL}s() {
        return service.getAll${ENTITY_PASCAL}s();
    }

    @GetMapping(\"/{id}\")
    public ${ENTITY_PASCAL}Dto get${ENTITY_PASCAL}ById(@PathVariable Long id) {
        return service.get${ENTITY_PASCAL}ById(id);
    }

    @PostMapping
    public ${ENTITY_PASCAL}Dto create${ENTITY_PASCAL}(@RequestBody ${ENTITY_PASCAL}Dto ${ENTITY_LOWER}Dto) {
        return service.create${ENTITY_PASCAL}(${ENTITY_LOWER}Dto);
    }

    @PutMapping(\"/{id}\")
    public ${ENTITY_PASCAL}Dto update${ENTITY_PASCAL}(@PathVariable Long id, @RequestBody ${ENTITY_PASCAL}Dto ${ENTITY_LOWER}Dto) {
        return service.update${ENTITY_PASCAL}(id, ${ENTITY_LOWER}Dto);
    }

    @DeleteMapping(\"/{id}\")
    public void delete${ENTITY_PASCAL}(@PathVariable Long id) {
        service.delete${ENTITY_PASCAL}(id);
    }
}"

# 경로 입력 없이 필요한 경로와 클래스 파일을 자동으로 생성
create_file "./$ENTITY_LOWER/entity" "${ENTITY_PASCAL}.java" "$ENTITY_TEMPLATE"
create_file "./$ENTITY_LOWER/dto" "${ENTITY_PASCAL}Dto.java" "$DTO_TEMPLATE"
create_file "./$ENTITY_LOWER/repository" "${ENTITY_PASCAL}Repository.java" "$REPOSITORY_TEMPLATE"
create_file "./$ENTITY_LOWER/service" "${ENTITY_PASCAL}Service.java" "$SERVICE_TEMPLATE"
create_file "./$ENTITY_LOWER/service" "${ENTITY_PASCAL}ServiceImpl.java" "$SERVICE_IMPL_TEMPLATE"
create_file "./$ENTITY_LOWER/controller" "${ENTITY_PASCAL}Controller.java" "$CONTROLLER_TEMPLATE"

echo "Spring 보일러플레이트 코드 생성 완료!"