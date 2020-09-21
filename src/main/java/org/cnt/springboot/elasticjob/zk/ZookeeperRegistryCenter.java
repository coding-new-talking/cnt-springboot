package org.cnt.springboot.elasticjob.zk;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.exception.RegExceptionHandler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * <p>基于Zookeeper的注册中心.
 * <p><b>jar包中的这个类在编译时是基于curator-2.10.0的，
 * <b>现在用的curator-4.2.0，版本太高报错了，
 * <b>经检查发现curator本身是兼容的，报错的原因可能是编译时已经确定了方法的返回值类型，
 * <b>而现在返回的是它的子类，理论上是没有问题的，应该基于新版本的重新编译一下就可以了。
 * <b>本来想继承的，发现是final类，于是从官方源码中复制出来，没有做任何修改，
 * <b>只是重新编译一下，果然可以啦。
 * @author zhangliang
 */
public final class ZookeeperRegistryCenter implements CoordinatorRegistryCenter {
	private static final Logger log = LoggerFactory.getLogger(ZookeeperRegistryCenter.class);
	
    private ZookeeperConfiguration zkConfig;
    
    private final Map<String, TreeCache> caches = new HashMap<>();
    
    private CuratorFramework client;
    
    
    public ZookeeperConfiguration getZkConfig() {
		return zkConfig;
	}

	public void setZkConfig(ZookeeperConfiguration zkConfig) {
		this.zkConfig = zkConfig;
	}

	public CuratorFramework getClient() {
		return client;
	}

	public void setClient(CuratorFramework client) {
		this.client = client;
	}

	public ZookeeperRegistryCenter(final ZookeeperConfiguration zkConfig) {
        this.zkConfig = zkConfig;
    }
    
    @Override
    public void init() {
        log.debug("Elastic job: zookeeper registry center init, server lists is: {}.", zkConfig.getServerLists());
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(zkConfig.getServerLists())
                .retryPolicy(new ExponentialBackoffRetry(zkConfig.getBaseSleepTimeMilliseconds(), zkConfig.getMaxRetries(), zkConfig.getMaxSleepTimeMilliseconds()))
                .namespace(zkConfig.getNamespace());
        if (0 != zkConfig.getSessionTimeoutMilliseconds()) {
            builder.sessionTimeoutMs(zkConfig.getSessionTimeoutMilliseconds());
        }
        if (0 != zkConfig.getConnectionTimeoutMilliseconds()) {
            builder.connectionTimeoutMs(zkConfig.getConnectionTimeoutMilliseconds());
        }
        if (!Strings.isNullOrEmpty(zkConfig.getDigest())) {
            builder.authorization("digest", zkConfig.getDigest().getBytes(Charsets.UTF_8))
                    .aclProvider(new ACLProvider() {
                    
                        @Override
                        public List<ACL> getDefaultAcl() {
                            return ZooDefs.Ids.CREATOR_ALL_ACL;
                        }
                    
                        @Override
                        public List<ACL> getAclForPath(final String path) {
                            return ZooDefs.Ids.CREATOR_ALL_ACL;
                        }
                    });
        }
        client = builder.build();
        client.start();
        try {
            if (!client.blockUntilConnected(zkConfig.getMaxSleepTimeMilliseconds() * zkConfig.getMaxRetries(), TimeUnit.MILLISECONDS)) {
                client.close();
                throw new KeeperException.OperationTimeoutException();
            }
            //CHECKSTYLE:OFF
        } catch (final Exception ex) {
            //CHECKSTYLE:ON
            RegExceptionHandler.handleException(ex);
        }
    }
    
    @Override
    public void close() {
        for (Entry<String, TreeCache> each : caches.entrySet()) {
            each.getValue().close();
        }
        waitForCacheClose();
        CloseableUtils.closeQuietly(client);
    }
    
    /* TODO 等待500ms, cache先关闭再关闭client, 否则会抛异常
     * 因为异步处理, 可能会导致client先关闭而cache还未关闭结束.
     * 等待Curator新版本解决这个bug.
     * BUG地址：https://issues.apache.org/jira/browse/CURATOR-157
     */
    private void waitForCacheClose() {
        try {
            Thread.sleep(500L);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Override
    public String get(final String key) {
        TreeCache cache = findTreeCache(key);
        if (null == cache) {
            return getDirectly(key);
        }
        ChildData resultInCache = cache.getCurrentData(key);
        if (null != resultInCache) {
            return null == resultInCache.getData() ? null : new String(resultInCache.getData(), Charsets.UTF_8);
        }
        return getDirectly(key);
    }
    
    private TreeCache findTreeCache(final String key) {
        for (Entry<String, TreeCache> entry : caches.entrySet()) {
            if (key.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    @Override
    public String getDirectly(final String key) {
        try {
            return new String(client.getData().forPath(key), Charsets.UTF_8);
        //CHECKSTYLE:OFF
        } catch (final Exception ex) {
        //CHECKSTYLE:ON
            RegExceptionHandler.handleException(ex);
            return null;
        }
    }
    
    @Override
    public List<String> getChildrenKeys(final String key) {
        try {
            List<String> result = client.getChildren().forPath(key);
            Collections.sort(result, new Comparator<String>() {
                
                @Override
                public int compare(final String o1, final String o2) {
                    return o2.compareTo(o1);
                }
            });
            return result;
         //CHECKSTYLE:OFF
        } catch (final Exception ex) {
        //CHECKSTYLE:ON
            RegExceptionHandler.handleException(ex);
            return Collections.emptyList();
        }
    }
    
    @Override
    public int getNumChildren(final String key) {
        try {
            Stat stat = client.checkExists().forPath(key);
            if (null != stat) {
                return stat.getNumChildren();
            }
            //CHECKSTYLE:OFF
        } catch (final Exception ex) {
            //CHECKSTYLE:ON
            RegExceptionHandler.handleException(ex);
        }
        return 0;
    }

    @Override
    public boolean isExisted(final String key) {
        try {
            return null != client.checkExists().forPath(key);
        //CHECKSTYLE:OFF
        } catch (final Exception ex) {
        //CHECKSTYLE:ON
            RegExceptionHandler.handleException(ex);
            return false;
        }
    }
    
    @Override
    public void persist(final String key, final String value) {
        try {
            if (!isExisted(key)) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(key, value.getBytes(Charsets.UTF_8));
            } else {
                update(key, value);
            }
        //CHECKSTYLE:OFF
        } catch (final Exception ex) {
        //CHECKSTYLE:ON
            RegExceptionHandler.handleException(ex);
        }
    }
    
    @Override
    public void update(final String key, final String value) {
        try {
            client.inTransaction().check().forPath(key).and().setData().forPath(key, value.getBytes(Charsets.UTF_8)).and().commit();
        //CHECKSTYLE:OFF
        } catch (final Exception ex) {
        //CHECKSTYLE:ON
            RegExceptionHandler.handleException(ex);
        }
    }
    
    @Override
    public void persistEphemeral(final String key, final String value) {
        try {
            if (isExisted(key)) {
                client.delete().deletingChildrenIfNeeded().forPath(key);
            }
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(key, value.getBytes(Charsets.UTF_8));
        //CHECKSTYLE:OFF
        } catch (final Exception ex) {
        //CHECKSTYLE:ON
            RegExceptionHandler.handleException(ex);
        }
    }
    
    @Override
    public String persistSequential(final String key, final String value) {
        try {
            return client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(key, value.getBytes(Charsets.UTF_8));
        //CHECKSTYLE:OFF
        } catch (final Exception ex) {
        //CHECKSTYLE:ON
            RegExceptionHandler.handleException(ex);
        }
        return null;
    }
    
    @Override
    public void persistEphemeralSequential(final String key) {
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(key);
        //CHECKSTYLE:OFF
        } catch (final Exception ex) {
        //CHECKSTYLE:ON
            RegExceptionHandler.handleException(ex);
        }
    }
    
    @Override
    public void remove(final String key) {
        try {
            client.delete().deletingChildrenIfNeeded().forPath(key);
        //CHECKSTYLE:OFF
        } catch (final Exception ex) {
        //CHECKSTYLE:ON
            RegExceptionHandler.handleException(ex);
        }
    }
    
    @Override
    public long getRegistryCenterTime(final String key) {
        long result = 0L;
        try {
            persist(key, "");
            result = client.checkExists().forPath(key).getMtime();
        //CHECKSTYLE:OFF
        } catch (final Exception ex) {
        //CHECKSTYLE:ON
            RegExceptionHandler.handleException(ex);
        }
        Preconditions.checkState(0L != result, "Cannot get registry center time.");
        return result;
    }
    
    @Override
    public Object getRawClient() {
        return client;
    }
    
    @Override
    public void addCacheData(final String cachePath) {
        TreeCache cache = new TreeCache(client, cachePath);
        try {
            cache.start();
        //CHECKSTYLE:OFF
        } catch (final Exception ex) {
        //CHECKSTYLE:ON
            RegExceptionHandler.handleException(ex);
        }
        caches.put(cachePath + "/", cache);
    }
    
    @Override
    public void evictCacheData(final String cachePath) {
        TreeCache cache = caches.remove(cachePath + "/");
        if (null != cache) {
            cache.close();
        }
    }
    
    @Override
    public Object getRawCache(final String cachePath) {
        return caches.get(cachePath + "/");
    }
}
