package org.bitshoroscope.bd;

import org.testcontainers.containers.MySQLContainer;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceFactory {

	
	//TODO: Don't use on production is just for examples
	@SuppressWarnings("rawtypes")
	public static HikariDataSource getHikariDataSourceWithDriverClassName(MySQLContainer containerRule) {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setUsername("testing");
		hikariConfig.setPassword("testing");
		hikariConfig.setMinimumIdle(10);
		hikariConfig.setConnectionTimeout(30000L);
		hikariConfig.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
		hikariConfig.setMaximumPoolSize(10);
		hikariConfig.setPoolName("poolTesting");

		hikariConfig.addDataSourceProperty("url", containerRule.getJdbcUrl());
		hikariConfig.addDataSourceProperty("user", "testing");
		hikariConfig.addDataSourceProperty("password", "testing");
		hikariConfig.addDataSourceProperty("pinGlobalTxToPhysicalConnection", "true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", "150");
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");

		return new HikariDataSource(hikariConfig);
	}
}
