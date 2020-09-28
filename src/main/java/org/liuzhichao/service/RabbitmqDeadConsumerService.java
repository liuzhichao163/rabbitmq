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

import com.rabbitmq.client.Channel;

@Component
public class RabbitmqDeadConsumerService {
	
	private static final Logger logger = LoggerFactory.getLogger(RabbitmqDeadConsumerService.class);
	
//	@RabbitListener(queues=RabbitmqConfig.DEAD_QUEUE)  
	@RabbitListener(queues="dead_queue")
	public void deadConsumerMessage(Message message,
								@Headers Map<String,Object> headers,
								Channel channel) throws IOException {
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS" );
		System.out.println("DLX死信对列接受时间："+sdf.format(new Date()));
		// 获取消息Id
        String messageId = message.getMessageProperties().getMessageId();
        String msg = new String(message.getBody(), "UTF-8");
        System.out.println("死信邮件消费者获取生产者消息msg:"+msg+",消息id"+messageId);
		
        // 手动ack
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        // 手动签收
        channel.basicAck(deliveryTag, false);
        logger.info("执行结束....");
		
	}

}
