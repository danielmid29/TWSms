package com.sms.manager;


import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plivo.api.Plivo;
import com.plivo.api.exceptions.PlivoRestException;
import com.plivo.api.models.message.Message;
import com.plivo.api.models.message.MessageCreateResponse;

@RestController
public class DummyController {

	@RequestMapping("/dummy")
	void dummyResponder() throws IOException, PlivoRestException {
	Plivo.init("MAODGYYJG4MMMYNGRHZD","MTdiZDk2ZjJjZTBkMDBhNzE0NTAxZDE3MWE4NmZj");
    MessageCreateResponse response = Message.creator("PRA234","917358341104",
            "Hello, from Java!")
            .create();
    System.out.println(response);
	}
}
