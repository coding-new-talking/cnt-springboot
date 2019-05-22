package org.cnt.springboot.controller;

import org.cnt.springboot.idgene.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lixinjie
 * @since 2019-05-22
 */
@RequestMapping("/id")
@RestController
public class IdController {

	@Autowired
	private IdGenerator firstId;
	
	@Autowired
	private IdGenerator secondId;
	
	@Autowired
	private IdGenerator thirdId;
	
	@RequestMapping("/test")
	public String test() {
		return "firstId: " + firstId.nextId() + "<br/><br/>"
				+ "secondId: " + secondId.nextId() + "<br/><br/>"
				+ "thirdId: " + thirdId.nextId() + "<br/><br/>";
	}
}
