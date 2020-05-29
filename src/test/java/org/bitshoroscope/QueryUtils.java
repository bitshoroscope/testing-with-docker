package org.bitshoroscope;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

import org.bitshoroscope.bd.DataSourceFactory;
import org.testcontainers.containers.MySQLContainer;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@SuppressWarnings(value = { "rawtypes" })
public class QueryUtils {

	/**
	 * Método de utilería para inicializar los datos dentro de nuestra base
	 *
	 * @param mysql
	 * @param ds
	 * @throws IOException
	 * @throws SQLException
	 */
	public void setupTables(MySQLContainer mysql) throws IOException, SQLException {

		try (HikariDataSource ds = DataSourceFactory.getHikariDataSourceWithDriverClassName(mysql);
				Connection conn = ds.getConnection();) {

			String queryCreateUsersTable = readQueryFromFile("/database/createCharactersTable.sql");
			performQuery(ds, mysql, queryCreateUsersTable);

			String queryInsertUsers = readQueryFromFile("/database/insertCharacters.sql");
			setupInserts(conn, queryInsertUsers);

		}
	}

	/**
	 * Método de utilería para leer un archivo de texto en un String. Es utilizado
	 * para evitar cadenas muy largas dentro del programa
	 *
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public String readQueryFromFile(String filename) throws IOException {
		try (InputStream inputStream = this.getClass().getResourceAsStream(filename);
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String contents = reader.lines().collect(Collectors.joining(System.lineSeparator()));

			return contents;
		}
	}

	/**
	 * Método de utilería para separar en líneas cada de uno de los inserts
	 *
	 * @param conn
	 * @param inserts
	 * @throws SQLException
	 */
	public static void setupInserts(Connection conn, String inserts) throws SQLException {
		String[] splitted = inserts.split("\n");
		for (String query : splitted) {
			PreparedStatement ps = conn.prepareStatement(query);
			ps.executeUpdate();
		}
	}

	protected static ResultSet performQuery(HikariDataSource ds, MySQLContainer containerRule, String sql)
			throws SQLException {

		Statement statement = ds.getConnection().createStatement();
		statement.execute(sql);
		ResultSet resultSet = statement.getResultSet();
		return resultSet;
	}

}
