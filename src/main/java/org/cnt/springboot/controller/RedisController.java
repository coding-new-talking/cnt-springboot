package org.cnt.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lixinjie
 * @since 2019-03-13
 */
@RequestMapping("/redis")
@RestController
public class RedisController {

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@GetMapping("/show")
	public String show() {
		return redisTemplate + "<br />" + stringRedisTemplate;
	}
	
	@GetMapping("/test")
	public String test() {
		stringRedisTemplate.opsForValue().set("name", "李新杰");
		return stringRedisTemplate.opsForValue().get("name");
	}
}
