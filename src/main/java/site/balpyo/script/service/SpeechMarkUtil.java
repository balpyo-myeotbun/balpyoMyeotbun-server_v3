package site.balpyo.script.service;

import org.hibernate.mapping.Map;
import site.balpyo.script.entity.ETag;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpeechMarkUtil {

    public String listToString(List<String> list) {
        // ETag enum 값들을 Set으로 저장
        Set<String> validTags = Set.of(ETag.values())
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        // List의 각 요소가 ETag에 속하는지 확인
        for (String value : list) {
            if (!validTags.contains(value)) {
                throw new IllegalArgumentException("Invalid tag: " + value);
            }
        }

        // List를 쉼표로 구분된 문자열로 변환
        return String.join(",", list);
    }

    // 쉼표로 구분된 문자열을 List로 변환하는 메서드 (ETag 검증 추가)
    public List<String> stringToList(String str) {
        List<String> list = new ArrayList<>();

        if (str == null || str.isEmpty()) {
            return list;  // 빈 문자열이면 빈 리스트 반환
        }

        // ETag enum 값들을 Set으로 저장
        Set<String> validTags = Set.of(ETag.values())
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        // 문자열을 쉼표로 구분하여 List로 변환하고 ETag 검증
        String[] values = str.split(",");
        for (String value : values) {
            value = value.trim();
            if (!validTags.contains(value)) {
                throw new IllegalArgumentException("Invalid tag: " + value);
            }
            list.add(value);
        }

        return list;
    }

    // 리스트 B에 없는 요소를 리스트 A에 추가하고 결과를 반환하는 메서드 (A가 내가 결국 저장해야할 리스트, B가 추가될 리스트)
    public List<String> addMissingElements(List<String> listA, List<String> listB) {
        List<String> result = new ArrayList<>(listA); // A 리스트의 복사본을 생성

        // 리스트 B에 없는 리스트 A의 요소만 추가
        for (String element : listB) {
            if (!listA.contains(element)) {
                result.add(element);  // B에 있는 요소가 A에 없으면 A에 추가
            }
        }

        return result; // 결과 리스트 반환
    }



}
