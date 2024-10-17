package site.balpyo.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.balpyo.script.dto.ScriptDto;
import site.balpyo.script.service.ScriptService;

@Service
@RequiredArgsConstructor
public class GenerateScriptService {

    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";

    private final ScriptService scriptService;

    @Value("${spring.ai.openai.api-key}")
    String apiKey;


    public String createPromptString(String topic, String keywords, Integer sec) {
        int initialCharacterCount = 425;
        double characterPerSecond = (double) initialCharacterCount / 60.0;

        int targetCharacterCount = (int) (sec * characterPerSecond);
        int targetByteCount = targetCharacterCount * 3;

        String requestPrompt = "# AI role : You are a Korean presentation script writer.\n" +
                "You should write a script for a presentation in Korean.\n" +
                "The topic is " + topic + ", and the keywords are " + keywords + ".\n" +
                "Please generate a script of " + targetByteCount + " bytes.\n" +
                "It must be exactly " + targetByteCount + " bytes long.\n";

        return requestPrompt;
    }


    public Generation getAiResultFromGpt(String input, Float temperature, Integer maxTokens) {

        OpenAiApi openAiApi = new OpenAiApi(apiKey);
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .withModel(OpenAiApi.ChatModel.GPT_4_O)
                .withTemperature(temperature != null ? temperature : 0.4f)
                .withMaxTokens(maxTokens != null ? maxTokens : 200)
                .build();
        OpenAiChatModel chatModel = new OpenAiChatModel(openAiApi, openAiChatOptions);

        return chatModel.call(
                new Prompt(input)).getResult();
    }

    public ScriptDto generateAiScriptAndSave(ScriptDto scriptDto){

        scriptDto.setIsGenerating(true);
        scriptDto.setUseAi(true);

        ScriptDto insertedScriptDto = scriptService.createScript(scriptDto);
        String inputValue = getAiResultFromGpt(createPromptString(scriptDto.getTopic(),scriptDto.getKeywords(), scriptDto.getSecTime()),null,null).getOutput().getContent();

        insertedScriptDto.setOriginalScript(inputValue);
        insertedScriptDto.setIsGenerating(false);
        return scriptService.updateScript(insertedScriptDto.getScriptId(), insertedScriptDto);
    }




}
