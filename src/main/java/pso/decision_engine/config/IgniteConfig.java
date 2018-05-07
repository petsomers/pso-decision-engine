package pso.decision_engine.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pso.decision_engine.model.ignitedto.RuleSetDto;

//@Configuration
public class IgniteConfig {
	
	/*
	private boolean enableFilePersistence=true;
    private int igniteConnectorPort=20000;
    private String igniteServerPortRange;
    private String ignitePersistenceFilePath="c:\\temp\\ignite";
    
    @Bean
    IgniteConfiguration igniteConfiguration() {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setClientMode(false);
        if(enableFilePersistence){
	        DataStorageConfiguration dsCfg=new DataStorageConfiguration();
	        DataRegionConfiguration drc=new DataRegionConfiguration();
	        drc.setPersistenceEnabled(true);
	        dsCfg.setDefaultDataRegionConfiguration(drc);
	        dsCfg.setStoragePath("C:/temp/decision_engine/ignite/data/store");
	        dsCfg.setWalArchivePath("C:/temp/decision_engine/ignite/data/walArchive");
	        dsCfg.setWalPath("C:/temp/decision_engine/ignite/data/walStore");
	        igniteConfiguration.setDataStorageConfiguration(dsCfg);
        }
        // durable file memory persistence
        
    	        
        /*
        // connector configuration
        ConnectorConfiguration connectorConfiguration=new ConnectorConfiguration();
        connectorConfiguration.setPort(igniteConnectorPort);
        // common ignite configuration
        igniteConfiguration.setMetricsLogFrequency(0);
        igniteConfiguration.setQueryThreadPoolSize(2);
        igniteConfiguration.setDataStreamerThreadPoolSize(1);
        igniteConfiguration.setManagementThreadPoolSize(2);
        igniteConfiguration.setPublicThreadPoolSize(2);
        igniteConfiguration.setSystemThreadPoolSize(2);
        igniteConfiguration.setRebalanceThreadPoolSize(1);
        igniteConfiguration.setAsyncCallbackPoolSize(2);
        igniteConfiguration.setPeerClassLoadingEnabled(false);
        igniteConfiguration.setIgniteInstanceName("alertsGrid");
        BinaryConfiguration binaryConfiguration = new BinaryConfiguration();
        binaryConfiguration.setCompactFooter(false);
        igniteConfiguration.setBinaryConfiguration(binaryConfiguration);
        // cluster tcp configuration
        TcpDiscoverySpi tcpDiscoverySpi=new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder=new TcpDiscoveryVmIpFinder();
        // need to be changed when it come to real cluster
        tcpDiscoveryVmIpFinder.setAddresses(Arrays.asList("127.0.0.1:47500..47509"));
        tcpDiscoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);
        igniteConfiguration.setDiscoverySpi(new TcpDiscoverySpi());
        
        /*
        // cache configuration
        CacheConfiguration alerts=new CacheConfiguration();
        alerts.setCopyOnRead(false);
        // as we have one node for now
        alerts.setBackups(0);
        alerts.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        alerts.setName("Alerts");
        alerts.setIndexedTypes(String.class,AlertEntry.class);
        CacheConfiguration alertsConfig=new CacheConfiguration();
        alertsConfig.setCopyOnRead(false);
        // as we have one node for now
        alertsConfig.setBackups(0);
        alertsConfig.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        alertsConfig.setName("AlertsConfig");
        alertsConfig.setIndexedTypes(String.class,AlertConfigEntry.class);
        igniteConfiguration.setCacheConfiguration(alerts,alertsConfig);
        
        */
		/*
        CacheConfiguration<String, RuleSetDto> ruleSetCacheConfig = new CacheConfiguration<>("ruleSet");
        ruleSetCacheConfig.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        ruleSetCacheConfig.setIndexedTypes(RuleSetDto.class, String.class);
        igniteConfiguration.setCacheConfiguration(ruleSetCacheConfig);
        return igniteConfiguration;
    }
    */
    
    @Bean(destroyMethod = "close")
    Ignite ignite() throws IgniteException {
    	Ignition.setClientMode(true);
        final Ignite ignite = Ignition.start();
        ignite.active(true);
        return ignite;
    }

}
