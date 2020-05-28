package org.bitshoroscope;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLManager {

	private static final Logger LOG = LoggerFactory.getLogger(SQLManager.class);

	private String connString;
	private String driverClassName;
	private DataSource ds;

	public SQLManager(String connString, String driverClassName) {
		this.connString = connString;
		this.driverClassName = driverClassName;
	}

	public SQLManager(DataSource ds) {
		this.ds = ds;
	}

	public int executeUpsert(String qry) throws IllegalArgumentException, SQLException {
		try (Connection conn = getConnection()) {
			return executeUpsert(qry, conn);
		}
	}

	public ResultSet executeSelect(String qry, Connection conn) throws IllegalArgumentException {
		try {
			PreparedStatement stmt = this.generateStatement(conn, qry);
			return stmt.executeQuery();
		} catch (SQLException ex) {
			throw new IllegalArgumentException("Error running SELECT: " + ex + " - Query: " + qry, ex);
		}
	}

	public int executeUpsert(String qry, Connection conn) throws IllegalArgumentException {
		try (PreparedStatement stmt = generateStatement(conn, qry)) {
			int result = stmt.executeUpdate();
			return result;
		} catch (SQLException ex) {
			throw new IllegalArgumentException("Error running statement: " + ex.getMessage() + "\nQuery: " + qry, ex);
		}
	}

	private PreparedStatement generateStatement(Connection conn, String qry) throws IllegalArgumentException {
		try {
			PreparedStatement stmt = conn.prepareStatement(qry);
			return stmt;
		} catch (SQLException e) {
			throw new IllegalArgumentException("Error preparing statement: " + e.getMessage(), e);
		}
	}

	public boolean executeExists(String qry, Connection conn) throws IllegalArgumentException {
		try (ResultSet rs = executeSelect(qry, conn)) {
			boolean first = rs.next();
			return first;
		} catch (SQLException ex) {
			throw new IllegalArgumentException("Error running exists" + ex + " - Query: " + qry, ex);
		}
	}

	public <T> T querySingleValue(String qry, Connection conn, Class<T> clz) {
		try (ResultSet rs = executeSelect(qry, conn)) {
			if (rs.next())
				return rs.getObject(1, clz);

			return null;
		} catch (SQLException ex) {
			throw new IllegalArgumentException("Error running statement: " + ex.getMessage() + "\nQuery: " + qry, ex);
		}
	}

	public Connection getConnection() throws IllegalArgumentException {
		try {
			if (ds == null) {
				Class.forName(this.driverClassName);
				return DriverManager.getConnection(this.connString);
			}
			return this.ds.getConnection();
		} catch (SQLException ex) {
			throw new IllegalArgumentException("Error while connecting: " + ex.getMessage(), ex);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Driver not found: " + e.getMessage(), e);
		}
	}

}