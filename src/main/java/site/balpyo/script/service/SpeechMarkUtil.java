package site.balpyo.script.service;


import org.json.JSONArray;
import org.json.JSONObject;
import site.balpyo.script.entity.ETag;

import java.util.*;
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

    public static List<Map<String, Object>> parseSpeechMarks(String speechMarkStr) {
        // speechMarkStr가 null이거나 빈 문자열인 경우 빈 리스트 반환
        if (speechMarkStr == null || speechMarkStr.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> speechMarks = new ArrayList<>();

        // JSON 배열로 변환
        JSONArray jsonArray = new JSONArray(speechMarkStr);

        // JSON 객체를 Map으로 변환하여 리스트에 담기
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Map<String, Object> speechMarkMap = new HashMap<>();

            speechMarkMap.put("start", jsonObject.getInt("start"));
            speechMarkMap.put("end", jsonObject.getInt("end"));
            speechMarkMap.put("time", jsonObject.getInt("time"));
            speechMarkMap.put("type", jsonObject.getString("type"));
            speechMarkMap.put("value", jsonObject.getString("value"));

            speechMarks.add(speechMarkMap);
        }

        return speechMarks;
    }

    public static String convertSpeechMarksToString(List<Map<String, Object>> speechMarks) {
        // speechMarks가 null이거나 빈 리스트인 경우 빈 배열 반환
        if (speechMarks == null || speechMarks.isEmpty()) {
            return "[]";
        }

        JSONArray jsonArray = new JSONArray();

        // Map을 JSON 객체로 변환하여 JSONArray에 추가
        for (Map<String, Object> speechMark : speechMarks) {
            JSONObject jsonObject = new JSONObject();

            // 각 값에 대해 null 검증 후 추가 (Optional)
            jsonObject.put("start", speechMark.getOrDefault("start", ""));
            jsonObject.put("end", speechMark.getOrDefault("end", ""));
            jsonObject.put("time", speechMark.getOrDefault("time", ""));
            jsonObject.put("type", speechMark.getOrDefault("type", ""));

            // value 필드에서 특수문자 처리 (String인지 확인 후)
            Object value = speechMark.get("value");
            if (value instanceof String) {
                jsonObject.put("value", escapeSpecialCharacters((String) value));
            } else {
                jsonObject.put("value", value);
            }

            jsonArray.put(jsonObject);
        }

        return jsonArray.toString(); // JSON 배열을 문자열로 반환
    }

    private static String escapeSpecialCharacters(String input) {
        // 기본적인 특수 문자 처리: 여기서는 쉼표(,)만을 예로 들지만, 필요에 따라 추가 가능
        return input.replace("\"", "\\\"").replace(",", "\\,");
    }
}
