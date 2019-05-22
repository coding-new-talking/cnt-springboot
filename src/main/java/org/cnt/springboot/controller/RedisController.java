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
	private SnGenerator firstSn;
	@Autowired
	private SnGenerator secondSn;
	@Autowired
	private SnGenerator thirdSn;
	
	@Autowired
	private GroupSnGenerator gfirstSn;
	@Autowired
	private GroupSnGenerator gsecondSn;
	@Autowired
	private GroupSnGenerator gthirdSn;
	
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
		return "<br/>firstSn=" + firstSn.nextNum()
				+ "<br/>secondSn=" + secondSn.nextNum()
				+ "<br/>thirdSn=" + thirdSn.nextNum();
	}
	
	@GetMapping("/gsng")
	public String gsng() {
		return "gfirstSn.f1 = " + gfirstSn.nextNum("f1") + "<br/>"
				+ "gfirstSn.f2 = " + gfirstSn.nextNum("f2") + "<br/>"
				+ "gfirstSn.f3 = " + gfirstSn.nextNum("f3") + "<br/><br/>"
				+ "gsecondSn.s1 = " + gsecondSn.nextNum("s1") + "<br/>"
				+ "gsecondSn.s2 = " + gsecondSn.nextNum("s2") + "<br/>"
				+ "gsecondSn.s3 = " + gsecondSn.nextNum("s3") + "<br/><br/>"
				+ "gthirdSn.t1 = " + gthirdSn.nextNum("t1") + "<br/>"
				+ "gthirdSn.t2 = " + gthirdSn.nextNum("t2") + "<br/>"
				+ "gthirdSn.t3 = " + gthirdSn.nextNum("t3");
	}
}
