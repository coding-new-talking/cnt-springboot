package org.cnt.springboot.elasticjob;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lixinjie
 * @since 2019-06-11
 */
@ConfigurationProperties(prefix = "elasticjob")
public class ElasticJobProperties {

	
	private ZookeeperConfig zookeeperConfig = new ZookeeperConfig();
	
	private Map<String, SimpleJobConfig> simpleJobConfigs = new LinkedHashMap<>();
	
	private Map<String, DataflowJobConfig> dataflowJobConfigs = new LinkedHashMap<>();
	
	
	public ZookeeperConfig getZookeeperConfig() {
		return zookeeperConfig;
	}

	public void setZookeeperConfig(ZookeeperConfig zookeeperConfig) {
		this.zookeeperConfig = zookeeperConfig;
	}

	public Map<String, SimpleJobConfig> getSimpleJobConfigs() {
		return simpleJobConfigs;
	}

	public void setSimpleJobConfigs(Map<String, SimpleJobConfig> simpleJobConfigs) {
		this.simpleJobConfigs = simpleJobConfigs;
	}

	public Map<String, DataflowJobConfig> getDataflowJobConfigs() {
		return dataflowJobConfigs;
	}

	public void setDataflowJobConfigs(Map<String, DataflowJobConfig> dataflowJobConfigs) {
		this.dataflowJobConfigs = dataflowJobConfigs;
	}

	public SimpleJobConfig getSimpleJobConfig(String jobName) {
		return simpleJobConfigs.get(jobName);
	}
	
	public DataflowJobConfig getDataflowJobConfig(String jobName) {
		return dataflowJobConfigs.get(jobName);
	}
	
	public static class DataflowJobConfig {
		
		private JobCoreConfig jobCoreConfig = new JobCoreConfig();
		
		private LiteJobConfig liteJobConfig = new LiteJobConfig();
		
		private boolean streamingProcess = true;

		public JobCoreConfig getJobCoreConfig() {
			return jobCoreConfig;
		}

		public void setJobCoreConfig(JobCoreConfig jobCoreConfig) {
			this.jobCoreConfig = jobCoreConfig;
		}

		public LiteJobConfig getLiteJobConfig() {
			return liteJobConfig;
		}

		public void setLiteJobConfig(LiteJobConfig liteJobConfig) {
			this.liteJobConfig = liteJobConfig;
		}

		public boolean isStreamingProcess() {
			return streamingProcess;
		}

		public void setStreamingProcess(boolean streamingProcess) {
			this.streamingProcess = streamingProcess;
		}
	}
	
	public static class SimpleJobConfig {
		
		private JobCoreConfig jobCoreConfig = new JobCoreConfig();
		
		private LiteJobConfig liteJobConfig = new LiteJobConfig();

		public JobCoreConfig getJobCoreConfig() {
			return jobCoreConfig;
		}

		public void setJobCoreConfig(JobCoreConfig jobCoreConfig) {
			this.jobCoreConfig = jobCoreConfig;
		}

		public LiteJobConfig getLiteJobConfig() {
			return liteJobConfig;
		}

		public void setLiteJobConfig(LiteJobConfig liteJobConfig) {
			this.liteJobConfig = liteJobConfig;
		}
	}
	
	public static class LiteJobConfig {
		
        private boolean monitorExecution = true;
        
        private int maxTimeDiffSeconds = -1;
        
        private int monitorPort = -1;
        
        private String jobShardingStrategyClass = "";
        
        private boolean disabled;
        
        private boolean overwrite;
        
        private int reconcileIntervalMinutes = 10;

		public boolean isMonitorExecution() {
			return monitorExecution;
		}

		public void setMonitorExecution(boolean monitorExecution) {
			this.monitorExecution = monitorExecution;
		}

		public int getMaxTimeDiffSeconds() {
			return maxTimeDiffSeconds;
		}

		public void setMaxTimeDiffSeconds(int maxTimeDiffSeconds) {
			this.maxTimeDiffSeconds = maxTimeDiffSeconds;
		}

		public int getMonitorPort() {
			return monitorPort;
		}

		public void setMonitorPort(int monitorPort) {
			this.monitorPort = monitorPort;
		}

		public String getJobShardingStrategyClass() {
			return jobShardingStrategyClass;
		}

		public void setJobShardingStrategyClass(String jobShardingStrategyClass) {
			this.jobShardingStrategyClass = jobShardingStrategyClass;
		}

		public boolean isDisabled() {
			return disabled;
		}

		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}

		public boolean isOverwrite() {
			return overwrite;
		}

		public void setOverwrite(boolean overwrite) {
			this.overwrite = overwrite;
		}

		public int getReconcileIntervalMinutes() {
			return reconcileIntervalMinutes;
		}

		public void setReconcileIntervalMinutes(int reconcileIntervalMinutes) {
			this.reconcileIntervalMinutes = reconcileIntervalMinutes;
		}
	}
	
	public static class JobCoreConfig {
		
        private String jobName;
        
        private String cron;
        
        private int shardingTotalCount;
        
        private String shardingItemParameters = "";
        
        private String jobParameter = "";
        
        private boolean failover;
        
        private boolean misfire = true;
        
        private String description = "";

		public String getJobName() {
			return jobName;
		}

		public void setJobName(String jobName) {
			this.jobName = jobName;
		}

		public String getCron() {
			return cron;
		}

		public void setCron(String cron) {
			this.cron = cron;
		}

		public int getShardingTotalCount() {
			return shardingTotalCount;
		}

		public void setShardingTotalCount(int shardingTotalCount) {
			this.shardingTotalCount = shardingTotalCount;
		}

		public String getShardingItemParameters() {
			return shardingItemParameters;
		}

		public void setShardingItemParameters(String shardingItemParameters) {
			this.shardingItemParameters = shardingItemParameters;
		}

		public String getJobParameter() {
			return jobParameter;
		}

		public void setJobParameter(String jobParameter) {
			this.jobParameter = jobParameter;
		}

		public boolean isFailover() {
			return failover;
		}

		public void setFailover(boolean failover) {
			this.failover = failover;
		}

		public boolean isMisfire() {
			return misfire;
		}

		public void setMisfire(boolean misfire) {
			this.misfire = misfire;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}
	
	public static class ZookeeperConfig {
		
	    private String serverLists;
	    
	    /**
	     * 命名空间.
	     */
	    private String namespace;
	    
	    /**
	     * 等待重试的间隔时间的初始值.
	     * 单位毫秒.
	     */
	    private int baseSleepTimeMilliseconds = 1000;
	    
	    /**
	     * 等待重试的间隔时间的最大值.
	     * 单位毫秒.
	     */
	    private int maxSleepTimeMilliseconds = 3000;
	    
	    /**
	     * 最大重试次数.
	     */
	    private int maxRetries = 3;
	    
	    /**
	     * 会话超时时间.
	     * 单位毫秒.
	     */
	    private int sessionTimeoutMilliseconds;
	    
	    /**
	     * 连接超时时间.
	     * 单位毫秒.
	     */
	    private int connectionTimeoutMilliseconds;

		public String getServerLists() {
			return serverLists;
		}

		public void setServerLists(String serverLists) {
			this.serverLists = serverLists;
		}

		public String getNamespace() {
			return namespace;
		}

		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}

		public int getBaseSleepTimeMilliseconds() {
			return baseSleepTimeMilliseconds;
		}

		public void setBaseSleepTimeMilliseconds(int baseSleepTimeMilliseconds) {
			this.baseSleepTimeMilliseconds = baseSleepTimeMilliseconds;
		}

		public int getMaxSleepTimeMilliseconds() {
			return maxSleepTimeMilliseconds;
		}

		public void setMaxSleepTimeMilliseconds(int maxSleepTimeMilliseconds) {
			this.maxSleepTimeMilliseconds = maxSleepTimeMilliseconds;
		}

		public int getMaxRetries() {
			return maxRetries;
		}

		public void setMaxRetries(int maxRetries) {
			this.maxRetries = maxRetries;
		}

		public int getSessionTimeoutMilliseconds() {
			return sessionTimeoutMilliseconds;
		}

		public void setSessionTimeoutMilliseconds(int sessionTimeoutMilliseconds) {
			this.sessionTimeoutMilliseconds = sessionTimeoutMilliseconds;
		}

		public int getConnectionTimeoutMilliseconds() {
			return connectionTimeoutMilliseconds;
		}

		public void setConnectionTimeoutMilliseconds(int connectionTimeoutMilliseconds) {
			this.connectionTimeoutMilliseconds = connectionTimeoutMilliseconds;
		}
	}
}
