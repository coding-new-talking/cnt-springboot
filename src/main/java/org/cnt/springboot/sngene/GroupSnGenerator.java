package org.cnt.springboot.sngene;
/**
 * 分组序列号生成器
 * @author lixinjie
 * @since 2019-04-04
 */
public interface GroupSnGenerator {
	
	/**名称，根据实际情况使用*/
	String getName();
	/**注册到容器中的bean名称*/
	String getBeanName();
	/**初始值*/
	long getInitNum();
	/**步长*/
	long getStep();
	/**获取下一个序列号*/
	long nextNum(String identifier);
	/**最大值*/
	long getMaxNum();
}
