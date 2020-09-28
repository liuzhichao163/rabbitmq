package org.liuzhichao.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq的配置类
 * @author ASUS
 *
 */

@Configuration
public class RabbitmqConfig {
	private static final Logger logger = LoggerFactory.getLogger(RabbitmqConfig.class);
	//邮件对列名称
	public final static String QUEUE = "mall_queue";
	
	//邮件交换机名称
	public final static String EXCHANGE_NAME = "mall_exchange";

	//routingkey
	public final static String ROUTING_KEY = "mall_key";
	
	//死信对列名称
	public final static String DEAD_QUEUE = "dead_queue";
	
	
	//死信交换机名称
	public final static String DEAD_EXCHANGE_NAME = "dead_exchange";
	
	//死信routingkey
	public final static String DEAD_ROUTEING_KEY = "dead_key";
	
	//死信对列交换机标识符
	public final static String DEAD_LETTER_EXCHANGE_KEY = "x-dead-letter-exchange";
	
	//死信对列交换机绑定键标识符
	public final static String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
	
	//声明rabbitmq连接池工厂
	@Autowired
	private CachingConnectionFactory connectionFactory;
	
	
	/**
	 * 创建一个邮件对列（邮件对列绑定一个死信交换机，并指定routingkey）
	 * new Queue(canshu1,canshu2，canshu3,canshu4,canshu5)
	 * canshu1:对列的名字
	 * canshu2：对列是否持久化(重启后是否依然有效)
	 * canshu3：是否排外：a:当关闭连接时connection.close()该队列是否会自动删除
	 * 				  b：该队列是否私有，非私有：可以有两个消费者访问同一个对列；私有：会对当前对列枷锁，其他通道channel不能访问。
	 * canshu4：长期未使用是否自动删除
	 * canshu5：死信队列参数
	 * @return
	 */
	@Bean
	public Queue mallQueue() {
		Map<String,Object> map = new HashMap<String,Object>(4);
//		map.put("x-message-ttl", 3000);
		map.put(DEAD_LETTER_EXCHANGE_KEY, DEAD_EXCHANGE_NAME);
		map.put(DEAD_LETTER_ROUTING_KEY, DEAD_ROUTEING_KEY);
		return new Queue(QUEUE,true,false,false,map);
	}
	
	/**
	 * 创建一个direct交换机
	 */
	@Bean
	public DirectExchange mallDirectExchange() {
		
		return new DirectExchange(DEAD_EXCHANGE_NAME);
	}
	
	/**
	 * 绑定queue到exchange，并指定routingkey
	 * @return
	 */
	@Bean
	public Binding mallBinding() {
		return BindingBuilder.bind(mallQueue()).to(mallDirectExchange()).with(ROUTING_KEY);
		
	} 
	
	/**
	 * 创建一个死信对列
	 */
	@Bean
	public Queue deadQueue() {
		return new Queue(DEAD_QUEUE,true);
	}
	
	/**
	 * 创建一个死信交换机
	 */
	@Bean
	public DirectExchange deadDirectExchange() {
		return new DirectExchange(DEAD_EXCHANGE_NAME);
		
	}
	
	/**
	 * 死信对列和死信交换机绑定
	 */
	@Bean
	public Binding deadBinding() {
		return BindingBuilder.bind(deadQueue()).to(deadDirectExchange()).with(DEAD_ROUTEING_KEY);
		
	}
	/**
	 * rabbitmq的消息确认机制
	 * ConfirmCallback接口用于实现消息发送到RabbitMQ交换器后接收回调ack  即消息发送到exchange
	 * ReturnCallback接口用于实现消息发送到RabbitMQ 交换器，但无相应队列与交换器绑定时的回调  即消息发送不到任何一个队列中 
	 * @return
	 */
	@Bean
	public RabbitTemplate rabbitTemplate() {
		//使用confirm-callback(发送方消息确认机制)和return-callback()时必须设置为true或在配置文件中配置
		//publisher-returns=true和publisher-returns=true
		connectionFactory.setPublisherConfirms(true);
		connectionFactory.setPublisherReturns(true);
		
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		
		//使用return-callback时必须设置mandatory为true，或者在配置中设置mandatory-expression的值为true
		rabbitTemplate.setMandatory(true);
	
		//如果消息没有到exchange,则confirm回调(执行此方法),ack=false; 如果消息到达exchange,则confirm回调(执行此方法),ack=true
		rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
			
			public void confirm(CorrelationData correlationData, boolean ack, String cause) {
				if(ack) {
					logger.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
				}else {
					logger.info("消息发送失败:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
				}
			}
		});
	
		//如果exchange到queue成功,则不回调return;如果exchange到queue失败,则回调return(需设置mandatory=true,否则不回回调,消息就丢了)
		rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
			
			public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
				logger.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",exchange,routingKey,replyCode,replyText,message);
			}
		});
		return rabbitTemplate;
	}
	
	
}
