package org.cnt.springboot.redispubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

/**
 * @author lixinjie
 * @since 2020-06-15
 */
@Component
public class LikeManMessageConsumer implements MessageConsumer {

	protected static final Logger log = LoggerFactory.getLogger(LikeManMessageConsumer.class);

	@Override
	public Topic getTopic() {
		return ChannelTopic.of("like-man");
	}

	@Override
	public MessageListener getMessageListener() {
		return new LikeManMessageListener();
	}
	
	public class LikeManMessageListener implements MessageListener {
		@Override
		public void onMessage(Message message, byte[] pattern) {
			log.info("body={}，channel=，pattern={}",
					new String(message.getBody()),
					new String(message.getChannel()),
					pattern == null ? null : new String(pattern));
		}
		
	}
}
