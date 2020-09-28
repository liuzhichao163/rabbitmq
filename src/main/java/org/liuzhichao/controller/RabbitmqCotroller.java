package org.liuzhichao.controller;

import org.liuzhichao.service.RabbitmqProducersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rabbitmq")
public class RabbitmqCotroller {
	
	@Autowired
	RabbitmqProducersService rabbitmqProducersService;
	
	@GetMapping("/sendMessage")
	public void sendMessage() {
		rabbitmqProducersService.sendMessage();
	}
}
