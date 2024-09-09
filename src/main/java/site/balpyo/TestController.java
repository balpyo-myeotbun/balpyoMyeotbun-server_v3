package site.balpyo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/health-check")
	public String healthCheck() {
		
		return "{ping: pong}";
	}

    
}
