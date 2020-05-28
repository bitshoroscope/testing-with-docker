package org.bitshoroscope;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SqlManagerTest {

        private MySQLContainer mysql;
        private static final Logger logger = LoggerFactory.getLogger(SQLManagerIntegrationMain.class);
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

                int samplingSize = 100;
                createContainer();

                logger.debug("Creando tablas...");
                queryUtils.setupTables(mysql);
                logger.debug("Fin creación tablas...");

                SQLManager manager = new SQLManager(queryUtils.getHikariDataSourceWithDriverClassName(mysql));

                executeOperations(manager, 100);

                QueryCounter queryCounter = executeOperations(manager, samplingSize);
                assertEquals(queryCounter.getInserts() + queryCounter.getUpdates(), samplingSize);

                Integer res = manager.querySingleValue("SELECT count(*) from characters", manager.getConnection(),
                                Integer.class);
                assertTrue(res > 0);

        }

        private void createContainer() {
                mysql = new MySQLContainer<>("mariadb:10.1.41");
                mysql.withDatabaseName("testing")
                	.withUsername("testing")
                	.withPassword("testing")
                    .withLogConsumer(new Slf4jLogConsumer(logger))
                    .withExposedPorts(3306)
                    .waitingFor(Wait.forLogMessage("poolPropiedades - Added connection", 10));
                mysql.start();
        }

        private QueryCounter executeOperations(SQLManager manager, final int samplingSize) {

                int inserts = 0;
                int updates = 0;

                for (int i = 0; i < samplingSize; i++) {
                        try (Connection conn = manager.getConnection()) {

                                Random r = new Random();
                                int idUser = r.nextInt(samplingSize);
                                String queryOp = "";

                                if (manager.executeExists("SELECT id FROM characters where id = " + idUser + ";", conn)) {
                                        queryOp = "UPDATE characters SET status = 'ZOMBIE' WHERE id = " + idUser + ";";
                                        updates++;

                                } else {
                                        queryOp = "INSERT INTO characters (name, lastname, status) VALUES ('Anonymous', 'Character', 'healthy');";
                                        inserts++;
                                }
                                manager.executeUpsert(queryOp);
                        } catch (IllegalArgumentException | SQLException e) {
                                e.printStackTrace();
                        }
                }

                return new QueryCounter(inserts, updates);
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