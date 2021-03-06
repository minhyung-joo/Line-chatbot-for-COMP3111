package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	String search(String text) throws Exception {
		String response = null;
		Connection connection = this.getConnection();
		PreparedStatement stmt = connection.prepareStatement("UPDATE KEYWORDS SET hits=hits+1 WHERE concat('%', ?, '%') LIKE '%' || keyword || '%' RETURNING response, hits");
		stmt.setString(1, text);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			response = rs.getString(1) + rs.getInt(2);
		}
		
		rs.close();
		stmt.close();
		connection.close();
		if (response != null) {
			return response;
		}

		throw new Exception("NOT FOUND");
	}
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);
		System.out.println("Connection established");

		return connection;
	}

}
