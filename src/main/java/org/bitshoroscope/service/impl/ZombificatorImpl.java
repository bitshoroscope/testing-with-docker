package org.bitshoroscope.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bitshoroscope.bd.SQLManager;
import org.bitshoroscope.dto.MyCharacter;
import org.bitshoroscope.service.Zombificator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZombificatorImpl implements Zombificator{
	
	private static final Logger LOG = LoggerFactory.getLogger(ZombificatorImpl.class);
	private SQLManager manager;
	
	public ZombificatorImpl(SQLManager manager) {
		this.manager = manager;
	}
	
    public void zombify(String lastname) throws SQLException {
    	Connection conn = manager.getConnection();
    	ResultSet rs = manager.executeSelect("SELECT * FROM characters WHERE lastname = '" + lastname+"';", conn);
    	Stream<MyCharacter> stream = createStream(rs);
    	List<MyCharacter> myCharacters = stream.collect(Collectors.toList());
    	myCharacters.forEach(myCharacter -> {
    		String queryZombify = "UPDATE characters SET status = 'ZOMBIE' WHERE id = " + myCharacter.getId() + ";";
    		try {
				manager.executeUpsert(queryZombify);
				LOG.info(myCharacter.getName() + " " + myCharacter.getLastname() + " has been zombified!!");
			} catch (IllegalArgumentException | SQLException e) {
				e.printStackTrace();
			}
    	});
    }
    
    private Stream<MyCharacter> createStream(ResultSet rs) throws SQLException{
    	Stream.Builder<MyCharacter> builder = Stream.builder();
    	while(rs.next()) {
    		MyCharacter myCharacter = convertRow(rs);
    		builder.add(myCharacter);
    	}
    	return builder.build();
    }
    
	public MyCharacter convertRow(ResultSet rs) throws SQLException {
		return new MyCharacter(rs.getInt("id"), rs.getString("name"), rs.getString("lastname"));
	}

	@Override
	public String zombifyById(Long id) throws SQLException {
		Connection conn = manager.getConnection();
		ResultSet rs = manager.executeSelect("SELECT * FROM characters WHERE id = " + id+ ";", conn);
		rs.next();
		MyCharacter myCharacter = convertRow(rs);
		String queryZombify = "UPDATE characters SET status = 'ZOMBIE' WHERE id = " + id + ";";
		manager.executeUpsert(queryZombify);
		conn.close();
		return myCharacter.getName() + " " + myCharacter.getLastname();
	}

}
