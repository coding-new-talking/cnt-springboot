package org.cnt.springboot.application.postprocessor;

import java.util.StringJoiner;
import java.util.stream.Stream;

import org.cnt.springboot.sngene.RedisSnGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
/**
 * 根据application.yml中的配置，自动向容器中注册序列号生成器
 * @author lixinjie
 * @since 2019-04-04
 */
public class SnGeneratorRegistry implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

	private static final Logger log = LoggerFactory.getLogger(SnGeneratorRegistry.class);
	
	private Environment environment;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		SnGeneInfo[] geneInfos = parseSnGeneInfos();
		log.info(logInfo(geneInfos));
		for (SnGeneInfo geneInfo : geneInfos) {
			BeanDefinitionBuilder bdb = BeanDefinitionBuilder.genericBeanDefinition(RedisSnGenerator.class);
			bdb.addConstructorArgValue(geneInfo.getKeyName());
			bdb.addConstructorArgValue(geneInfo.getBeanName());
			bdb.addConstructorArgValue(geneInfo.getInitNum());
			bdb.addConstructorArgValue(geneInfo.getStep());
			bdb.addConstructorArgValue(geneInfo.getMaxNum());
			registry.registerBeanDefinition(geneInfo.getBeanName(), bdb.getBeanDefinition());
		}
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	private SnGeneInfo[] parseSnGeneInfos() {
		String prefix = "sngenerator";
		if (!environment.containsProperty(prefix + ".generators")) {
			return new SnGeneInfo[0];
		}
		String[] generators = environment.getProperty(prefix + ".generators", String[].class);
		SnGeneInfo[] infos = new SnGeneInfo[generators.length];
		for (int i = 0; i < generators.length; i++) {
			infos[i] = buildSnGeneInfo(prefix, generators[i]);
		}
		return infos;
	}
	
	private SnGeneInfo buildSnGeneInfo(String prefix, String generator) {
		return new SnGeneInfo(
				prefix + ":" + generator,
				environment.getProperty(prefix + "." + generator + ".bean-name"),
				environment.getProperty(prefix + "." + generator + ".init-num", long.class),
				environment.getProperty(prefix + "." + generator + ".step", long.class),
				environment.getProperty(prefix + "." + generator + ".max-num", long.class)
			);
	}
	
	private String logInfo(SnGeneInfo[] geneInfos) {
		StringJoiner sj = new StringJoiner(",");
		Stream.of(geneInfos).forEachOrdered(sgi -> sj.add(sgi.toString()));
		return sj.toString();
	}
	
	static class SnGeneInfo {
		
		private String keyName;
		private String beanName;
		private long initNum;
		private long step;
		private long maxNum;
		
		public SnGeneInfo(String keyName, String beanName, long initNum, long step, long maxNum) {
			this.keyName = keyName;
			this.beanName = beanName;
			this.initNum = initNum;
			this.step = step;
			this.maxNum = maxNum;
		}

		public String getKeyName() {
			return keyName;
		}

		public String getBeanName() {
			return beanName;
		}

		public long getInitNum() {
			return initNum;
		}

		public long getStep() {
			return step;
		}

		public long getMaxNum() {
			return maxNum;
		}

		@Override
		public String toString() {
			return "SnGeneInfo [keyName=" + keyName + ", beanName=" + beanName + ", initNum=" + initNum + ", step="
					+ step + ", maxNum=" + maxNum + "]";
		}
		
	}
}
