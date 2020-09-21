package org.cnt.springboot.elasticjob.samples;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.cnt.springboot.elasticjob.annotation.ElasticJobScheduler;
import org.springframework.stereotype.Component;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

/**
 * <p>示例，简单类型的job
 * @author lixinjie
 * @since 2019-06-11
 */
//@ElasticJobScheduler(cron = "0/20 * * * * ?", shardingTotalCount = 3)
@Component
public class FirstSimpleJob implements SimpleJob {

	/**按照分片执行，分片之间并行执行*/
	@Override
	public void execute(ShardingContext shardingContext) {
		int shard = shardingContext.getShardingItem();//分片数据
		int count = shardingContext.getShardingTotalCount();//总片数量
		System.out.println(String.format("execute shard={%d}, count={%d} at {%s}", shard, count, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
	}

}
