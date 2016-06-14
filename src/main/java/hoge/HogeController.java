package hoge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HogeController {
	
	Logger logger = LogManager.getLogger(HogeController.class);

	@RequestMapping(method=RequestMethod.GET, value="/")
	public String say() {
		logger.debug("Hi\nhello");
		logger.error("Error", new Exception("exception"));
		return "hi";
	}
}
