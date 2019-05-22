package org.cnt.springboot.idgene;

/**
 * 分布式ID生成器
 * @author lixinjie
 * @since 2019-05-22
 */
public interface IdGenerator {

	long nextId();
}
