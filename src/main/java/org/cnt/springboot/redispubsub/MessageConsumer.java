package org.cnt.springboot.redispubsub;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.Topic;

/**
 * @author lixinjie
 * @since 2020-06-15
 */
public interface MessageConsumer {
	
	Topic getTopic();

	MessageListener getMessageListener();
}
