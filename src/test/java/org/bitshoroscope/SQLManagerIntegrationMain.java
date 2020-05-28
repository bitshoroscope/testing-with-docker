package org.bitshoroscope;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

@SuppressWarnings(value = { "rawtypes", "unchecked" })
public class SQLManagerIntegrationMain {

        private static final Logger logger = LoggerFactory.getLogger(SQLManagerIntegrationMain.class);

        private static MySQLContainer mysql;

        public static void main(String[] args) throws IOException, SQLException, InterruptedException {

                QueryUtils queryUtils = new QueryUtils();
                mysql = new MySQLContainer<>("mariadb:10.1.41");
                mysql.withDatabaseName("propiedades").withUsername("propiedades").withPassword("propiedades")
                                .withLogConsumer(new Slf4jLogConsumer(logger)).withExposedPorts(3306)
                                .waitingFor(Wait.forLogMessage("poolPropiedades - Added connection", 10));
                mysql.start();

                logger.debug("Creando tablas...");
                queryUtils.setupTables(mysql);
                logger.debug("Fin creación tablas...");

                //Para probar con hilos y datasource la clase SQLManager descomentar esta línea
                SQLManager manager = new SQLManager(queryUtils.getHikariDataSourceWithDriverClassName(mysql));
                //String url = mysql.getJdbcUrl() + "?user=propiedades&password=propiedades";
                //SQLManager manager = new SQLManager(url, mysql.getDriverClassName());

                Runnable thread1 = new QueryProcessor(1, 1000, manager);
                Thread t1 = new Thread(thread1, "Hilo 1");
                t1.start();

                Runnable thread2 = new QueryProcessor(1001, 2000, manager);
                Thread t2 = new Thread(thread2, "Hilo 2");
                t2.start();

                Runnable thread3 = new QueryProcessor(2001, 3000, manager);
                Thread t3 = new Thread(thread3, "Hilo 3");
                t3.start();

                Runnable thread4 = new QueryProcessor(3001, 4000, manager);
                Thread t4 = new Thread(thread4, "Hilo 4");
                t4.start();

                Runnable thread5 = new QueryProcessor(4001, 5000, manager);
                Thread t5 = new Thread(thread5, "Hilo 5");
                t5.start();

                Runnable thread6 = new QueryProcessor(5001, 6000, manager);
                Thread t6 = new Thread(thread6, "Hilo 6");
                t6.start();

                Runnable thread7 = new QueryProcessor(6001, 7000, manager);
                Thread t7 = new Thread(thread7, "Hilo 7");
                t7.start();

                Runnable thread8 = new QueryProcessor(7001, 8000, manager);
                Thread t8 = new Thread(thread8, "Hilo 8");
                t8.start();

                Runnable thread9 = new QueryProcessor(8001, 9000, manager);
                Thread t9 = new Thread(thread9, "Hilo 9");
                t9.start();

                Runnable thread10 = new QueryProcessor(9001, 10000, manager);
                Thread t10 = new Thread(thread10, "Hilo 10");
                t10.start();

        }

        static class QueryProcessor implements Runnable {

                private int start;
                private int end;
                private SQLManager manager;

                public QueryProcessor(int start, int end, SQLManager manager) {
                        this.start = start;
                        this.end = end;
                        this.manager = manager;
                }

                @Override
                public void run() {
                        for (int i = this.start; i < this.end; i++) {
                                try (Connection conn = manager.getConnection()) {

                                        Random r = new Random();
                                        int idProperty = r.nextInt(20000);
                                        String queryOp = "";

                                        if (manager.executeExists(
                                                        "SELECT id FROM galleries where property_id = " + idProperty + " AND position = 1", conn)) {
                                                queryOp = "UPDATE galleries SET image = 1, size_bytes = 1000, sha = 'ADSADAS23423', on_amazon = true WHERE property_id = "
                                                                + idProperty + " AND position = 1";
                                                logger.debug(queryOp);

                                        } else {
                                                queryOp = "INSERT INTO galleries (description,image_crop,coord_y2,coord_x2,coord_y,coord_x,image,alt,title,main_image,property_id,dev_property_id,height,width,original,position,on_amazon,owner_type,showroom_id,unit_id,sha,size_bytes) VALUES ('Antecomedor y cocina equipada',null,240,360,0,0,'32e740339f32d87e4a2d8e03ab148922.JPG',null,null,0,"
                                                                + idProperty + ",null,640.0,480.0,null,1,1,1,null,null,null,null);";
                                                logger.debug(queryOp);
                                        }
                                        int res = manager.executeUpsert(queryOp);
                                        if (res == 1) {
                                                logger.debug("EJECUTANDO query en: " + Thread.currentThread().getName());
                                        }
                                } catch (IllegalArgumentException | SQLException e) {
                                        e.printStackTrace();
                                }
                        }

                }

        }

}