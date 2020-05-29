package org.bitshoroscope;

import java.io.IOException;
import java.sql.SQLException;

import org.bitshoroscope.bd.DataSourceFactory;
import org.bitshoroscope.bd.SQLManager;
import org.bitshoroscope.service.Zombificator;
import org.bitshoroscope.service.impl.ZombificatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

@SuppressWarnings(value = { "rawtypes", "unchecked" })
public class SQLManagerIntegrationByLastname {

	private static final Logger logger = LoggerFactory.getLogger(SQLManagerIntegrationByLastname.class);

	private static MySQLContainer mysql;

	public static void main(String[] args) throws IOException, SQLException, InterruptedException {

		QueryUtils queryUtils = new QueryUtils();
		mysql = new MySQLContainer<>("mariadb:10.1.41");
		mysql.withDatabaseName("testing")
			.withUsername("testing")
			.withPassword("testing")
			.withLogConsumer(new Slf4jLogConsumer(logger))
			.withExposedPorts(3306)
			.waitingFor(Wait.forLogMessage("poolTesting - Added connection", 10));
		mysql.start();

		logger.debug("Creando tablas...");
		queryUtils.setupTables(mysql);
		logger.debug("Fin creación tablas...");

		// Para probar con hilos y datasource la clase SQLManager descomentar esta línea
		SQLManager manager = new SQLManager(DataSourceFactory.getHikariDataSourceWithDriverClassName(mysql));

		Runnable thread1 = new QueryProcessor(manager, "Simpson");
		Thread t1 = new Thread(thread1, "Hilo 1");
		t1.start();

		Runnable thread2 = new QueryProcessor(manager, "Flanders");
		Thread t2 = new Thread(thread2, "Hilo 2");
		t2.start();

		Runnable thread3 = new QueryProcessor(manager, "Bouvier");
		Thread t3 = new Thread(thread3, "Hilo 3");
		t3.start();

		Runnable thread4 = new QueryProcessor(manager, "");
		Thread t4 = new Thread(thread4, "Hilo 4");
		t4.start();

	}

	static class QueryProcessor implements Runnable {

		private SQLManager manager;
		private String lastname;

		public QueryProcessor(SQLManager manager, String lastname) {
			this.manager = manager;
			this.lastname = lastname;
		}

		@Override
		public void run() {
			Zombificator zombificator = new ZombificatorImpl(manager);
			try {
				logger.info("Zombifing in " + Thread.currentThread().getName());
				zombificator.zombify(this.lastname);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}