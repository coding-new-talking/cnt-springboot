package org.cnt.springboot.elasticjob;

import org.cnt.springboot.elasticjob.ElasticJobProperties.DataflowJobConfig;
import org.cnt.springboot.elasticjob.ElasticJobProperties.JobCoreConfig;
import org.cnt.springboot.elasticjob.ElasticJobProperties.LiteJobConfig;
import org.cnt.springboot.elasticjob.ElasticJobProperties.SimpleJobConfig;
import org.cnt.springboot.elasticjob.ElasticJobProperties.ZookeeperConfig;
import org.cnt.springboot.elasticjob.samples.FirstSimpleJob;
import org.cnt.springboot.elasticjob.zk.ZookeeperRegistryCenter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;

/**
 * @author lixinjie
 * @since 2019-06-11
 */
@ConditionalOnProperty(prefix = "elasticjob", name = "enabled", havingValue = "true")
@Configuration
@EnableConfigurationProperties(ElasticJobProperties.class)
public class ElasticJobConfiguration {

	private ElasticJobProperties elasticJobProperties;
	
	public ElasticJobConfiguration(ElasticJobProperties elasticJobProperties) {
		this.elasticJobProperties = elasticJobProperties;
	}

	@Bean(initMethod = "init")
	public ZookeeperRegistryCenter registryCenter() {
		ZookeeperConfig zkCfg = elasticJobProperties.getZookeeperConfig();
		ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(zkCfg.getServerLists(), zkCfg.getNamespace());
		zkConfig.setBaseSleepTimeMilliseconds(zkCfg.getBaseSleepTimeMilliseconds());
		zkConfig.setMaxSleepTimeMilliseconds(zkCfg.getMaxSleepTimeMilliseconds());
		zkConfig.setMaxRetries(zkCfg.getMaxRetries());
		zkConfig.setSessionTimeoutMilliseconds(zkCfg.getSessionTimeoutMilliseconds());
		zkConfig.setConnectionTimeoutMilliseconds(zkCfg.getConnectionTimeoutMilliseconds());
		return new ZookeeperRegistryCenter(zkConfig);
	}
	
	//
	
	//在这个地方注册，需要把相关配置放在application.yml里面，也可以直接使用注解注册
	//注册job调度
	@Bean(initMethod = "init")
	public SpringJobScheduler firstSimpleJobSpringJobScheduler(FirstSimpleJob jobBean) {
		String jobName = "firstSimpleJob";
		Class<? extends SimpleJob> jobClass = jobBean.getClass();
		return buildSimpleJobSpringJobScheduler(jobName, jobClass, jobBean);
	}
	
	//===================辅助方法========================

	private SpringJobScheduler buildSimpleJobSpringJobScheduler(String jobName, Class<? extends SimpleJob> jobClass, SimpleJob jobBean) {
		SimpleJobConfig sjCfg = elasticJobProperties.getSimpleJobConfig(jobName);
		JobCoreConfiguration jcc = buildJCC(sjCfg.getJobCoreConfig());
		SimpleJobConfiguration sjc = buildSJC(jcc, jobClass);
		LiteJobConfiguration ljc = buildLJC(sjCfg.getLiteJobConfig(), sjc);
		return new SpringJobScheduler(jobBean, registryCenter(), ljc);
	}

	private SpringJobScheduler buildDataflowJobSpringJobScheduler(String jobName, Class<? extends DataflowJob<?>> jobClass, DataflowJob<?> jobBean) {
		DataflowJobConfig djCfg = elasticJobProperties.getDataflowJobConfig(jobName);
		JobCoreConfiguration jcc = buildJCC(djCfg.getJobCoreConfig());
		DataflowJobConfiguration djc = buildDJC(jcc, jobClass, djCfg.isStreamingProcess());
		LiteJobConfiguration ljc = buildLJC(djCfg.getLiteJobConfig(), djc);
		return new SpringJobScheduler(jobBean, registryCenter(), ljc);
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

	private SimpleJobConfiguration buildSJC(JobCoreConfiguration jcc, Class<? extends SimpleJob> jobClass) {
		return new SimpleJobConfiguration(jcc, jobClass.getCanonicalName());
	}

	private DataflowJobConfiguration buildDJC(JobCoreConfiguration jcc, Class<? extends DataflowJob<?>> jobClass, boolean streamingProcess) {
		return new DataflowJobConfiguration(jcc, jobClass.getCanonicalName(), streamingProcess);
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
