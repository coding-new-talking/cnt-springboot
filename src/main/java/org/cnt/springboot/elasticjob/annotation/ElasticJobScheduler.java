package org.cnt.springboot.elasticjob.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>把该注解放在JobBean上，主要是为Job注册一个负责调度的调度器
 * @author lixinjie
 * @since 2020-09-21
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface ElasticJobScheduler {

	String cron();
	
	int shardingTotalCount();
	
	boolean dataFlowJob() default false;
	
	boolean streamingProcess() default false;
}
