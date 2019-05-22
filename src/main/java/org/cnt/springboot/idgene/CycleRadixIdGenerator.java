package org.cnt.springboot.idgene;

import java.util.Calendar;

/**
 * <p>基于snowflake的实现
 * <p>单节点每秒最大并发数65535
 * <p>超过该数后生成的ID会重复
 * <p>造成主键冲突
 * <p>可以一个表对应一个ID生成器
 * <p>或多个表共享一个ID生成器
 * <p>二进制位数:[开始数值,结束数值]
 * <p>1:[0,1]，2:[0,3]，3:[0,7]，4:[0,15]，5:[0,31]，6:[0,63]，7:[0,127]，8:[0,255]，9:[0,511]，10:[0,1023]
 * <p>11:[0,2047]，12:[0,4095]，13:[0,8191]，14:[0,16383]，15:[0,32767]，16:[0,65535]，17:[0,131071]，18:[0,262143]，19:[0,524287]，20:[0,1048575]
 * @author lixinjie
 * @since 2019-05-22
 */
public class CycleRadixIdGenerator implements IdGenerator {
	
	//共63位/20年=40位/127节点=7位/65535基数=16位
	//63位=年的位数+节点的位数+基数的位数
	//8个字节=64位，最高位总是为0，表示正数
	private long baseTime;//基准时间
	private int nodeBits;
	private long nodeNum;//节点编号
	private int cycleBits;
	private long maxCycleNum;//最大循环基数
	private long cycleNum = -1;//当前循环基数
	
	public CycleRadixIdGenerator(String baseTime, int nodeBits, int nodeNum, int cycleBits) {
		this.baseTime = initBaseTime(baseTime);
		assert this.baseTime < System.currentTimeMillis();
		
		assert nodeBits >= 3;
		int maxNodeNum = (int)(Math.pow(2, nodeBits) - 1);
		assert nodeNum >= 0;
		assert nodeNum <= maxNodeNum;
		this.nodeBits = nodeBits;
		this.nodeNum = nodeNum;
		
		assert cycleBits >= 16;
		this.cycleBits = cycleBits;
		this.maxCycleNum = (long)(Math.pow(2, cycleBits) - 1);
	}
	
	public long getCycleNum() {
		if (cycleNum >= maxCycleNum) {
			cycleNum = -1;
		}
		return ++cycleNum;
	}
	
	public long getNodeNum() {
		return nodeNum;
	}
	
	/**
	 * 从基准时间到现在经过的秒数，本来应该是毫秒数，
	 * 但那样最终生成的long数字太长，在前端js里无法
	 * 正确显示，所以退化为秒
	 */
	public long getTimeNum() {
		return (System.currentTimeMillis() - baseTime) / 1000;
	}
	
	@Override
	public synchronized long nextId() {
		long id = getTimeNum();
		id = (id << nodeBits) | getNodeNum();
		id = (id << cycleBits) | getCycleNum();
		return id;
	}
	
	private static long initBaseTime(String baseTime) {
		String[] bts = baseTime.split("-");
		int year = Integer.parseInt(bts[0]);
		int month = Integer.parseInt(bts[1]) - 1;
		int date = Integer.parseInt(bts[2]);
		Calendar now = Calendar.getInstance();
		//2018-01-01 00:00:00.000
		now.set(year, month, date, 0, 0, 0);
		now.set(Calendar.MILLISECOND, 0);
		return now.getTimeInMillis();
	}
	
}
