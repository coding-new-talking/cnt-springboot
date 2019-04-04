package org.cnt.springboot.application.postprocessor;

import java.util.StringJoiner;
import java.util.stream.Stream;

import org.cnt.springboot.sngene.RedisGroupSnGenerator;
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
public class GroupSnGeneratorRegistry implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

	private Environment environment;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		GroupSnGeneInfo[] geneInfos = parseGroupSnGeneInfos();
		System.out.println(logInfo(geneInfos));
		for (GroupSnGeneInfo geneInfo : geneInfos) {
			BeanDefinitionBuilder bdb = BeanDefinitionBuilder.genericBeanDefinition(RedisGroupSnGenerator.class);
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

	private GroupSnGeneInfo[] parseGroupSnGeneInfos() {
		String prefix = "groupsngenerator";
		String[] generators = environment.getProperty(prefix + ".generators", String[].class);
		GroupSnGeneInfo[] infos = new GroupSnGeneInfo[generators.length];
		for (int i = 0; i < generators.length; i++) {
			infos[i] = buildGroupSnGeneInfo(prefix, generators[i]);
		}
		return infos;
	}
	
	private GroupSnGeneInfo buildGroupSnGeneInfo(String prefix, String generator) {
		return new GroupSnGeneInfo(
				prefix + ":" + generator,
				environment.getProperty(prefix + "." + generator + ".bean-name"),
				environment.getProperty(prefix + "." + generator + ".init-num", long.class),
				environment.getProperty(prefix + "." + generator + ".step", long.class),
				environment.getProperty(prefix + "." + generator + ".max-num", long.class)
			);
	}
	
	private String logInfo(GroupSnGeneInfo[] geneInfos) {
		StringJoiner sj = new StringJoiner(",");
		Stream.of(geneInfos).forEachOrdered(sgi -> sj.add(sgi.toString()));
		return sj.toString();
	}
	
	static class GroupSnGeneInfo {
		
		private String keyName;
		private String beanName;
		private long initNum;
		private long step;
		private long maxNum;
		
		public GroupSnGeneInfo(String keyName, String beanName, long initNum, long step, long maxNum) {
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
			return "GroupSnGeneInfo [keyName=" + keyName + ", beanName=" + beanName + ", initNum=" + initNum + ", step="
					+ step + ", maxNum=" + maxNum + "]";
		}
		
	}
}
