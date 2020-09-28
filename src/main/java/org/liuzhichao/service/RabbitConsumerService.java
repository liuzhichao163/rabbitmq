package org.liuzhichao.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.liuzhichao.config.RabbitmqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;

@Component
public class RabbitConsumerService {
	private static final Logger logger = LoggerFactory.getLogger(RabbitConsumerService.class);
	
	private final String MALL_QUEUE_NAME =  RabbitmqConfig.QUEUE;
	
	
	@RabbitListener(queues = "mall_queue")
	public void consumerMessage(Message message,
								@Headers Map<String,Object> headers,
								Channel channel) throws IOException {
		
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS" );
		System.out.println("对列接受时间："+sdf.format(new Date()));
		try {
			//获取消息id
			String messageId = message.getMessageProperties().getMessageId();
			String msg = new String(message.getBody(), "UTF-8");
			logger.info("邮件消费者获取生产者消息msg:"+msg+",消息id"+messageId);
			JSONObject jsonObject = JSONObject.parseObject(msg);
	        Integer timestamp = jsonObject.getInteger("timestamp");
	        int result  = 1/timestamp;
	        //手动ack
            Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
            // 手动签收
            channel.basicAck(deliveryTag, false);
		} catch (Exception e) {
			//拒绝消费消息（丢失消息） 给死信队列,第三个参数 false 表示不会重回队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            logger.info("邮件消费者拒绝消息，消息进入死信对列！");
		}
	}
}
