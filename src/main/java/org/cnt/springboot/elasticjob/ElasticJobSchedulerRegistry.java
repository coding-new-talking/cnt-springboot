package org.cnt.springboot.elasticjob;

import java.util.Arrays;
import java.util.Map;

import org.cnt.springboot.elasticjob.ElasticJobProperties.DataflowJobConfig;
import org.cnt.springboot.elasticjob.ElasticJobProperties.JobCoreConfig;
import org.cnt.springboot.elasticjob.ElasticJobProperties.LiteJobConfig;
import org.cnt.springboot.elasticjob.ElasticJobProperties.SimpleJobConfig;
import org.cnt.springboot.elasticjob.annotation.ElasticJobScheduler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Configuration;

import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;

/**
 * @author lixinjie
 * @since 2019-06-11
 */
@Configuration
public class ElasticJobSchedulerRegistry implements BeanDefinitionRegistryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}
	
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		ConfigurableListableBeanFactory beanFactory;
		if (registry instanceof ConfigurableListableBeanFactory) {
			beanFactory = (ConfigurableListableBeanFactory)registry;
			String[] names = beanFactory.getBeanNamesForType(ElasticJob.class);
			System.out.println(Arrays.toString(names));
			for (String name : names) {
				BeanDefinition bd = beanFactory.getBeanDefinition(name);
				AnnotatedBeanDefinition abd = (AnnotatedBeanDefinition)bd;
				if ((abd.getMetadata().hasAnnotation(ElasticJobScheduler.class.getName()))) {
					BeanDefinitionBuilder bdb = BeanDefinitionBuilder.genericBeanDefinition(SpringJobScheduler.class);
					bdb.addConstructorArgReference(name);
					bdb.addConstructorArgReference("registryCenter");
					bdb.addConstructorArgValue(buildLJCForJob(name, abd));
					bdb.addConstructorArgValue(new ElasticJobListener[0]);
					bdb.setInitMethodName("init");
					registry.registerBeanDefinition(name + "_SpringJobScheduler", bdb.getBeanDefinition());
				}
			}
		}
	}

	//===================辅助方法========================
	
	private LiteJobConfiguration buildLJCForJob(String beanName, AnnotatedBeanDefinition abd) {
		Map<String, Object> annotationAttributes = abd.getMetadata().getAnnotationAttributes(ElasticJobScheduler.class.getName());
		Boolean dataFlowJob = (Boolean)annotationAttributes.get("dataFlowJob");
		if (dataFlowJob) {
			return buildLJCForDataflowJob(beanName, annotationAttributes, abd.getMetadata().getClassName());
		}
		return buildLJCForSimpleJob(beanName, annotationAttributes, abd.getMetadata().getClassName());
	}

	private LiteJobConfiguration buildLJCForSimpleJob(String beanName, Map<String, Object> annotationAttributes, String jobClass) {
		SimpleJobConfig sjCfg = new SimpleJobConfig();
		sjCfg.getJobCoreConfig().setJobName(beanName);
		sjCfg.getJobCoreConfig().setCron((String)annotationAttributes.get("cron"));
		sjCfg.getJobCoreConfig().setShardingTotalCount((Integer)annotationAttributes.get("shardingTotalCount"));
		JobCoreConfiguration jcc = buildJCC(sjCfg.getJobCoreConfig());
		SimpleJobConfiguration sjc = buildSJC(jcc, jobClass);
		LiteJobConfiguration ljc = buildLJC(sjCfg.getLiteJobConfig(), sjc);
		return ljc;
	}

	private LiteJobConfiguration buildLJCForDataflowJob(String beanName, Map<String, Object> annotationAttributes, String jobClass) {
		DataflowJobConfig djCfg = new DataflowJobConfig();
		djCfg.getJobCoreConfig().setJobName(beanName);
		djCfg.getJobCoreConfig().setCron((String)annotationAttributes.get("cron"));
		djCfg.getJobCoreConfig().setShardingTotalCount((Integer)annotationAttributes.get("shardingTotalCount"));
		djCfg.setStreamingProcess((Boolean)annotationAttributes.get("streamingProcess"));
		JobCoreConfiguration jcc = buildJCC(djCfg.getJobCoreConfig());
		DataflowJobConfiguration djc = buildDJC(jcc, jobClass, djCfg.isStreamingProcess());
		LiteJobConfiguration ljc = buildLJC(djCfg.getLiteJobConfig(), djc);
		return ljc;
	}

	private JobCoreConfiguration buildJCC(JobCoreConfig jcCfg) {
		JobCoreConfiguration jcc = JobCoreConfiguration.newBuilder(jcCfg.getJobName(),
				jcCfg.getCron(), jcCfg.getShardingTotalCount())
				.shardingItemParameters(jcCfg.getShardingItemParameters())
				.jobParameter(jcCfg.getJobParameter())
				.failover(jcCfg.isFailover())
				.misfire(jcCfg.isMisfire())
				.description(jcCfg.getDescription())
				.build();
		return jcc;
	}

	private SimpleJobConfiguration buildSJC(JobCoreConfiguration jcc, String jobClass) {
		return new SimpleJobConfiguration(jcc, jobClass);
	}

	private DataflowJobConfiguration buildDJC(JobCoreConfiguration jcc, String jobClass, boolean streamingProcess) {
		return new DataflowJobConfiguration(jcc, jobClass, streamingProcess);
	}

	private LiteJobConfiguration buildLJC(LiteJobConfig ljCfg, JobTypeConfiguration jtc) {
		LiteJobConfiguration ljc = LiteJobConfiguration.newBuilder(jtc)
				.monitorExecution(ljCfg.isMonitorExecution())
				.maxTimeDiffSeconds(ljCfg.getMaxTimeDiffSeconds())
				.monitorPort(ljCfg.getMonitorPort())
				.jobShardingStrategyClass(ljCfg.getJobShardingStrategyClass())
				.reconcileIntervalMinutes(ljCfg.getReconcileIntervalMinutes())
				.disabled(ljCfg.isDisabled())
				.overwrite(ljCfg.isOverwrite())
				.build();
		return ljc;
	}
}
