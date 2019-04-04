package org.cnt.springboot.controller;

import org.cnt.springboot.sngene.GroupSnGenerator;
import org.cnt.springboot.sngene.SnGenerator;
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
	
	@Autowired
	private SnGenerator first;
	@Autowired
	private SnGenerator second;
	@Autowired
	private SnGenerator third;
	
	@Autowired
	private GroupSnGenerator gfirst;
	@Autowired
	private GroupSnGenerator gsecond;
	@Autowired
	private GroupSnGenerator gthird;
	
	@GetMapping("/show")
	public String show() {
		return redisTemplate + "<br />" + stringRedisTemplate;
	}
	
	@GetMapping("/test")
	public String test() {
		stringRedisTemplate.opsForValue().set("name", "李新杰");
		return stringRedisTemplate.opsForValue().get("name");
	}
	
	@GetMapping("/sng")
	public String sng() {
		return first.nextNum() + "<br/>" + second.nextNum()
			+ "<br/>" + third.nextNum();
	}
	
	@GetMapping("/gsng")
	public String gsng() {
		return gfirst.nextNum("f1") + "<br/>"
				+ gfirst.nextNum("f2") + "<br/>"
				+ gfirst.nextNum("f3") + "<br/><br/>"
				+ gsecond.nextNum("s1") + "<br/>"
				+ gsecond.nextNum("s2") + "<br/>"
				+ gsecond.nextNum("s3") + "<br/><br/>"
				+ gthird.nextNum("t1") + "<br/>"
				+ gthird.nextNum("t2") + "<br/>"
				+ gthird.nextNum("t3");
	}
}
