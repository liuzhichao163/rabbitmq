package org.liuzhichao.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.liuzhichao.config.RabbitmqConfig;
import org.liuzhichao.entity.Mall;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.java.SimpleFormatter;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;



@Service
public class RabbitmqProducersService {
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	public void sendMessage() {
		
		Mall mall = new Mall();
		mall.setEamil("liu_zc@163.com");
		mall.setMassage("hello liuzhichao");
		mall.setTimestamp(0);
		String mallString = JSON.toJSONString(mall);
		// 设置消息唯一id 保证每次重试消息id唯一  ;消息id设置在请求头里面 用UUID做全局ID 
		Message message = MessageBuilder.withBody(mallString.getBytes())
										.setContentType(MessageProperties.CONTENT_TYPE_JSON)
										.setContentEncoding("utf-8")
										.setMessageId(UUID.randomUUID() + "")
//										.setExpiration("6000") //设置单条消息的TTL
										.build();
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS" );
		System.out.println("消息发送时间："+sdf.format(new Date()));
		rabbitTemplate.convertAndSend(RabbitmqConfig.QUEUE,message);
	}
}
