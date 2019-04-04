package org.cnt.springboot.sngene;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
/**
 * 基于Redis的实现
 * @author lixinjie
 * @since 2019-04-04
 */
public class RedisGroupSnGenerator implements GroupSnGenerator {

	private Map<String, Boolean> existHashKeys = new ConcurrentHashMap<>();
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	private String name;
	private String beanName;
	private long initNum;
	private long step;
	private long maxNum;
	
	public RedisGroupSnGenerator(String name, String beanName, long initNum, long step, long maxNum) {
		this.name = name;
		this.beanName = beanName;
		this.initNum = initNum;
		this.step = step;
		this.maxNum = maxNum;
	}
	
	@PostConstruct
	public void init() {
		if (!stringRedisTemplate.hasKey(getName())) {
			stringRedisTemplate.opsForHash().putIfAbsent(getName(), "flag", String.valueOf(getInitNum()));
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public String getBeanName() {
		return beanName;
	}

	@Override
	public long getInitNum() {
		return initNum;
	}

	@Override
	public long getStep() {
		return step;
	}

	@Override
	public long nextNum(String identifier) {
		if (!existHashKeys.containsKey(identifier)) {
			stringRedisTemplate.opsForHash().putIfAbsent(getName(), identifier, String.valueOf(getInitNum()));
			existHashKeys.putIfAbsent(identifier, Boolean.TRUE);
		}
		return stringRedisTemplate.opsForHash().increment(getName(), identifier, getStep());
	}

	@Override
	public long getMaxNum() {
		return maxNum;
	}

}
