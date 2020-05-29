package org.bitshoroscope;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;

import org.bitshoroscope.bd.DataSourceFactory;
import org.bitshoroscope.bd.SQLManager;
import org.bitshoroscope.service.Zombificator;
import org.bitshoroscope.service.impl.ZombificatorImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;


@SuppressWarnings({"rawtypes", "unchecked"})
public class SqlManagerTest {

        private MySQLContainer mysql;
        private static final Logger LOG = LoggerFactory.getLogger(SQLManagerIntegrationByLastname.class);
        private QueryUtils queryUtils = new QueryUtils();

        @Test
        public void testReadingFromFile() throws IOException {
                String queryCreatePropertiesTable = queryUtils.readQueryFromFile("/database/createCharactersTable.sql");
                assertNotNull(queryCreatePropertiesTable);

                String inserts = queryUtils.readQueryFromFile("/database/insertCharacters.sql");
                assertNotNull(inserts);
        }

        @Test
        public void testManagerWithDataSource() throws IOException, SQLException {
                createContainer();
                LOG.info("Creando tablas...");
                queryUtils.setupTables(mysql);
                LOG.info("Fin creación tablas...");

                SQLManager manager = new SQLManager(DataSourceFactory.getHikariDataSourceWithDriverClassName(mysql));
                Zombificator zombificator = new ZombificatorImpl(manager);
                zombificator.zombify("Simpson");
                
                Long res = manager.querySingleValue("SELECT count(*) from characters WHERE status = 'ZOMBIE'", manager.getConnection(),
                                Long.class);
                assertEquals(Long.valueOf(7l), res);

        }

        private void createContainer() {
                mysql = new MySQLContainer<>("mariadb:10.1.41");
                mysql.withDatabaseName("testing")
                	.withUsername("testing")
                	.withPassword("testing")
                    .withLogConsumer(new Slf4jLogConsumer(LOG))
                    .withExposedPorts(3306)
                    .waitingFor(Wait.forLogMessage("poolPropiedades - Added connection", 10));
                mysql.start();
        }

        /**
         * Clase de utilería para guardar el número de queries ejecutados
         * @author andybravo
         *
         */
        public class QueryCounter {
                int updates;
                int inserts;

                public QueryCounter(int inserts, int updates) {
                        this.inserts = inserts;
                        this.updates = updates;
                }

                public int getUpdates() {
                        return updates;
                }

                public void setUpdates(int updates) {
                        this.updates = updates;
                }

                public int getInserts() {
                        return inserts;
                }

                public void setInserts(int inserts) {
                        this.inserts = inserts;
                }
        }

}