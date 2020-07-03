package org.cnt.springboot.redispubsub;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * @author lixinjie
 * @since 2020-06-15
 */
@Configuration
public class RedisPubConfig {
	
	@Autowired
	protected List<MessageConsumer> messageConsumers;
	
	/*@Bean
	public static LikeManMessageConsumer likeMan() {
		return new LikeManMessageConsumer();
	}
	
	@Bean
	public static LikeWomanMessageConsumer likeWoman() {
		return new LikeWomanMessageConsumer();
	}
	
	@Bean
	public static LikeBothMessageConsumer likeBoth() {
		return new LikeBothMessageConsumer();
	}*/

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
		RedisMessageListenerContainer rmlc = new RedisMessageListenerContainer();
		rmlc.setConnectionFactory(redisConnectionFactory);
		for (MessageConsumer messageConsumer : messageConsumers) {
			rmlc.addMessageListener(messageConsumer.getMessageListener(), messageConsumer.getTopic());
		}
		return rmlc;
	}
}
