package org.cnt.springboot.application.postprocessor;

import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * @author lixinjie
 * @since 2019-04-04
 */
@Configuration
public class BeanDefinitionRegistrySource {

	@Bean
	public static BeanDefinitionRegistryPostProcessor snGeneratorRegistry() {
		return new SnGeneratorRegistry();
	}
	
	@Bean
	public static BeanDefinitionRegistryPostProcessor groupSnGeneratorRegistry() {
		return new GroupSnGeneratorRegistry();
	}
}
