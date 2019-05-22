package org.cnt.springboot.application.postprocessor;

import java.util.StringJoiner;
import java.util.stream.Stream;

import org.cnt.springboot.idgene.CycleRadixIdGenerator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
/**
 * 根据application.yml中的配置，自动向容器中注册Id生成器
 * @author lixinjie
 * @since 2019-05-22
 */
public class IdGeneratorRegistry implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

	private Environment environment;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		IdGeneInfo[] geneInfos = parseIdGeneInfos();
		System.out.println(logInfo(geneInfos));
		for (IdGeneInfo geneInfo : geneInfos) {
			BeanDefinitionBuilder bdb = BeanDefinitionBuilder.genericBeanDefinition(CycleRadixIdGenerator.class);
			bdb.addConstructorArgValue(geneInfo.getBaseTime());
			bdb.addConstructorArgValue(geneInfo.getNodeBits());
			bdb.addConstructorArgValue(geneInfo.getNodeNum());
			bdb.addConstructorArgValue(geneInfo.getCycleBits());
			registry.registerBeanDefinition(geneInfo.getBeanName(), bdb.getBeanDefinition());
		}
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	private IdGeneInfo[] parseIdGeneInfos() {
		String prefix = "idgenerator";
		String[] generators = environment.getProperty(prefix + ".generators", String[].class);
		IdGeneInfo[] infos = new IdGeneInfo[generators.length];
		for (int i = 0; i < generators.length; i++) {
			infos[i] = buildIdGeneInfo(prefix, generators[i]);
		}
		return infos;
	}
	
	private IdGeneInfo buildIdGeneInfo(String prefix, String generator) {
		return new IdGeneInfo(
				prefix + ":" + generator,
				environment.getProperty(prefix + "." + generator + ".bean-name"),
				environment.getProperty(prefix + "." + generator + ".base-time"),
				environment.getProperty(prefix + "." + generator + ".node-bits", int.class),
				environment.getProperty(prefix + "." + generator + ".node-num", int.class),
				environment.getProperty(prefix + "." + generator + ".cycle-bits", int.class)
			);
	}
	
	private String logInfo(IdGeneInfo[] geneInfos) {
		StringJoiner sj = new StringJoiner(",");
		Stream.of(geneInfos).forEachOrdered(sgi -> sj.add(sgi.toString()));
		return sj.toString();
	}
	
	static class IdGeneInfo {
		
		private String name;
		private String beanName;
		private String baseTime;
		private int nodeBits;
		private int nodeNum;
		private int cycleBits;
		
		public IdGeneInfo(String name, String beanName, String baseTime, int nodeBits, int nodeNum, int cycleBits) {
			this.name = name;
			this.beanName = beanName;
			this.baseTime = baseTime;
			this.nodeBits = nodeBits;
			this.nodeNum = nodeNum;
			this.cycleBits = cycleBits;
		}
		
		public String getName() {
			return name;
		}

		public String getBeanName() {
			return beanName;
		}

		public String getBaseTime() {
			return baseTime;
		}

		public int getNodeBits() {
			return nodeBits;
		}

		public int getNodeNum() {
			return nodeNum;
		}

		public int getCycleBits() {
			return cycleBits;
		}

		@Override
		public String toString() {
			return "IdGeneInfo [name=" + name + ", beanName=" + beanName + ", baseTime=" + baseTime + ", nodeBits="
					+ nodeBits + ", nodeNum=" + nodeNum + ", cycleBits=" + cycleBits + "]";
		}
		
	}
}
