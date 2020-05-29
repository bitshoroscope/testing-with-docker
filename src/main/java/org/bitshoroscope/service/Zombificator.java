package org.bitshoroscope.service;

import java.sql.SQLException;

public interface Zombificator {
	
	void zombify(String lastname) throws SQLException;
	
	String zombifyById(Long id) throws SQLException;

}
