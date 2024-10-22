package site.balpyo.ai.dto.upload;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResultDTO {
    private String profileUrl;
    private int playTime;
    private int speed;
    private List<Map<String, Object>> speechMarks;

}