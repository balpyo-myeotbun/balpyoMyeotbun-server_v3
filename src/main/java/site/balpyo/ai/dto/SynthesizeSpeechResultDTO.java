package site.balpyo.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class SynthesizeSpeechResultDTO {

    private InputStream audioStream;
    private List<Map<String, Object>> speechMarks;
}
