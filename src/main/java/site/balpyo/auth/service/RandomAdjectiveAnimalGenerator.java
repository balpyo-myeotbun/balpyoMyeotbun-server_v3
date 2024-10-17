package site.balpyo.auth.service;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
public class RandomAdjectiveAnimalGenerator {

    public String generateNickname() {
        Random random = new Random();


        String[] adjectives = {"즐거운", "재밌는", "행복한", "멋진", "신나는", "활기찬", "기쁜", "사랑스러운",
                "상쾌한", "평화로운", "용감한", "빛나는", "친절한", "매력적인", "긍정적인",
                "화려한", "따뜻한", "기운찬", "쾌활한", "유쾌한", "희망찬", "사려깊은", "배려심 깊은",
                "풍요로운", "충만한", "평온한", "달콤한", "기분 좋은", "친숙한", "자유로운"};


        String[] animals = {"곰", "사자", "토끼", "코끼리", "호랑이", "여우", "늑대", "고양이", "개",
                "기린", "원숭이", "다람쥐", "판다", "코알라", "악어", "펭귄", "부엉이", "수달",
                "고래", "돌고래", "참새", "공작", "두루미", "타조", "앵무새", "오리", "까마귀",
                "문어", "해마", "상어", "고등어", "송어", "도마뱀", "개구리", "두더지", "사슴"};


        String[] colors = {"빨간", "주황색", "노란", "초록색", "파란", "남색", "보라색", "분홍색",
                "갈색", "검은색", "흰색", "회색", "연두색", "하늘색", "자주색", "금색", "은색",
                "청록색", "밤색", "연보라색", "연한 파란색", "아이보리색", "크림색", "카키색",
                "올리브색", "베이지색", "다홍색", "라벤더색", "연두빛 노란색", "암청색"};


        String uuid = UUID.randomUUID().toString().replaceAll("[^0-9]", "");
        String randomString = uuid.substring(0, 6);


        String randomAdjective = adjectives[random.nextInt(adjectives.length)];
        String randomColor = colors[random.nextInt(colors.length)];
        String randomAnimal = animals[random.nextInt(animals.length)];


        String result = randomAdjective + randomColor  + randomAnimal + randomString;
        return result;
    }
}
