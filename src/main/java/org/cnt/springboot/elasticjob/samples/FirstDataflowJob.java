package org.cnt.springboot.elasticjob.samples;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.cnt.springboot.elasticjob.annotation.ElasticJobScheduler;
import org.springframework.stereotype.Component;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;


/**
 * <p>示例，数据流类型的job
 * @author lixinjie
 * @since 2019-06-11
 */
@ElasticJobScheduler(cron = "10/20 * * * * ?", shardingTotalCount = 3, dataFlowJob = true, streamingProcess = true)
@Component
public class FirstDataflowJob implements DataflowJob<Object> {

	/**抓取数据*/
	@Override
	public List<Object> fetchData(ShardingContext shardingContext) {
		int shard = shardingContext.getShardingItem();
		int count = shardingContext.getShardingTotalCount();
		List<Object> data = (new Random().nextInt(10) > 5) ? Arrays.asList(new Object(), new Object()) : Collections.emptyList();
		System.out.println(String.format("fetch data shard={%d}, count={%d}, size={%d} at {%s}", shard, count, data.size(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
		return data;
	}

	/**处理数据*/
	@Override
	public void processData(ShardingContext shardingContext, List<Object> data) {
		int shard = shardingContext.getShardingItem();
		int count = shardingContext.getShardingTotalCount();
		System.out.println(String.format("process data shard={%d}, count={%d}, size={%d} at {%s}", shard, count, data.size(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
	}

}
