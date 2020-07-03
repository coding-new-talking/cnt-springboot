package org.cnt.springboot.redispubsub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lixinjie
 * @since 2020-06-15
 */
@RequestMapping("/redispub")
@RestController
public class RedisPubController {

	@Autowired
	protected RedisTemplate<Object, Object> redisTemplate;
	
	@RequestMapping("/publishMessage")
	public String publishMessage(String channel, String message) {
		redisTemplate.convertAndSend(channel, message);
		return channel + "->" + message;
	}
}
