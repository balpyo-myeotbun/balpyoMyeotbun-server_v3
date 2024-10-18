package site.balpyo.ai.controller;

import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;
import site.balpyo.ai.dto.PollyDTO;
import site.balpyo.ai.dto.SynthesizeSpeechResultDTO;
import site.balpyo.ai.dto.upload.UploadResultDTO;
import site.balpyo.ai.service.PollyService;


import java.io.IOException;
import java.io.InputStream;


/**
 * @author dongheonlee
 * AWS polly를 활용한 tts 구현 컨트롤러
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/polly")
public class PollyController {


    private final PollyService pollyService;
    private final View error;

//    /**
//     * @param pollyDTO
//     * @return 호출 시, 요청정보에 따른 mp3 음성파일을 반환(audioBytes)한다.
//     */
//    @PostMapping("/generateAudio")
//    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
//    public ResponseEntity<?> synthesizeText(@RequestBody PollyDTO pollyDTO) {
//
//        log.info("--------------------controller로 텍스트 음성 변환 요청");
//
//
//        try {
//            // Amazon Polly와 통합하여 텍스트를 음성으로 변환
//            SynthesizeSpeechResultDTO synthesizeSpeechResultDTO = pollyService.synthesizeSpeech(pollyDTO);
//
//
//            if (synthesizeSpeechResultDTO.getAudioStream() == null || synthesizeSpeechResultDTO.getSpeechMarks() == null) {
//                log.error("Amazon Polly 음성 변환 실패: 반환된 오디오 정보가 null입니다.");
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//            }
//
//            // InputStream을 byte 배열로 변환
//            byte[] audioBytes = IOUtils.toByteArray(synthesizeSpeechResultDTO.getAudioStream());
//
//            // MP3 파일을 클라이언트에게 반환
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//            headers.setContentDispositionFormData("GeneratedAudio", "speech.mp3");
//
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(audioBytes);
//
//        } catch (IOException e) {
//            log.error("내부 서버 오류: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//        }
//    }

    @PostMapping("/uploadSpeech")
    public ResponseEntity<UploadResultDTO> synthesizeAndUploadSpeech(@RequestBody PollyDTO pollyDTO) throws IOException, CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException {
        UploadResultDTO uploadResultDTO = pollyService.synthesizeAndUploadSpeech(pollyDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(uploadResultDTO, headers, HttpStatus.OK);
    }

}
