/*
 * #%L
 * tmmodel
 * %%
 * Copyright (C) 2019 Michael Berger
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package com.mbb.TicketMaven.model.jdbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.mbb.TicketMaven.model.KeyedEntityDB;
import com.mbb.TicketMaven.model.entity.KeyedEntity;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;

/**
 * Abstract Base class for all JDBC database classes. A JdbcDB class will
 * provide a JDBC CRUD layer for a particular ticketMaven Entity class. This
 * class provides any common JDBC logic. The child classes provide the Entity
 * specific logic. The user of DB and Database is probably a misnomer left over
 * from old code. Each JdbcDB deals with one Table in the common DB.
 */
public abstract class JdbcDB<T extends KeyedEntity> implements KeyedEntityDB<T> {

	/** the one shared JDBC connection among all "DB" classes */
	static protected Connection connection_ = null;

	/** common JDBC URL */
	static private String url_ = getDbUrl();

	static public void setDbUrl(String url) {
		url_ = url;
	}

	/**
	 * do a clean shutdown of the database
	 */
	static public void cleanup() {
		try {
			if (connection_ != null && !connection_.isClosed() && url_.startsWith("jdbc:hsqldb")) {

				execSQL("SHUTDOWN");

			}
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
		connection_ = null;

	}

	/**
	 * Commit transaction.
	 *
	 * @throws Exception
	 *             the exception
	 */
	static public void commitTransaction() throws Exception {
		try (PreparedStatement stmt = connection_.prepareStatement("COMMIT")) {
			stmt.execute();
			connection_.setAutoCommit(true);
		}
	}

	/**
	 * Connect.
	 *
	 * @param create
	 *            the create flag - if false, fail if the db does not exist
	 *
	 * @throws Exception
	 *             the exception
	 */
	static public void connect(boolean create) throws Exception {

		if (connection_ != null)
			return;

		// for now - leave in support for mysql
		// it will be simple to switch Drivers in the future
		// based on the url to support other DBs - but this
		// may never happen
		if (url_.startsWith("jdbc:mysql")) {
			Class.forName("com.mysql.jdbc.Driver");
			connection_ = DriverManager.getConnection(url_);
		} else if (url_.startsWith("jdbc:hsqldb") && !url_.startsWith("jdbc:hsqldb:mem")) {
			Class.forName("org.hsqldb.jdbcDriver");
			Properties props = new Properties();
			props.setProperty("user", "sa");
			props.setProperty("password", "");
			props.setProperty("shutdown", "true");
			if (!create)
				props.setProperty("ifexists", "true");
			connection_ = DriverManager.getConnection(url_, props);

		}

		// if running with a memory only db (only for unit testing)
		// always need to run the db creation scripts
		else if (url_.contains("hsqldb:mem")) {
			// need to create the db
			try {
				if (url_.startsWith("jdbc:log4jdbc"))
					Class.forName("net.sf.log4jdbc.DriverSpy");
				else
					Class.forName("org.hsqldb.jdbcDriver");
				System.out.println("Creating Database");
				InputStream is = new Errmsg().getClass().getResourceAsStream("/schema/create_hsql.sql");

				Properties props = new Properties();
				props.setProperty("user", "sa");
				props.setProperty("password", "");
				props.setProperty("ifexists", "false");
				connection_ = DriverManager.getConnection(url_, props);
				try (InputStreamReader r = new InputStreamReader(is)) {
					executeMultiSQL(new BufferedReader(r));
				}
			} catch (Exception e2) {
				throw e2;
			}
		}

	}

	static public void executeMultiSQL(BufferedReader reader) throws Exception {
		StringBuffer sb = new StringBuffer();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				if( line.startsWith("--")) continue;

				if( line.contains("NaN"))
					line = line.replace("NaN", "0");

				sb.append(line);

				if (line.trim().endsWith(";")) {
					execSQL(sb.toString());
					sb.setLength(0);
				}
			}
			if (sb.length() > 0)
				execSQL(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Errmsg.getErrorHandler().errmsg(e);
		}
	}

	/**
	 * Execute arbitrary sql. This is only used for very specific purposes. no
	 * Entity CRUD should be done via this method.
	 *
	 * @param sql
	 *            the sql
	 *
	 * @return the result set returned, if any
	 *
	 * @throws Exception
	 *             the exception
	 */
	static public ResultSet execSQL(String sql) throws Exception {
		try (PreparedStatement stmt = connection_.prepareStatement(sql)) {
			//System.out.println(sql);
			stmt.execute();
			return stmt.getResultSet();
		}
	}

	/**
	 * get the standard JDBC URL for TicketMaven based on user prefs
	 *
	 * @return - the url
	 */
	public static String getDbUrl() {
		return ("jdbc:hsqldb:file:" + Prefs.getPref(PrefName.DBDIR) + "/tm_");
	}

	/**
	 * Rollback transaction.
	 *
	 * @throws Exception
	 *             the exception
	 */
	static public void rollbackTransaction() throws Exception {
		try (PreparedStatement stmt = connection_.prepareStatement("ROLLBACK")) {
			stmt.execute();
			connection_.setAutoCommit(true);
		}
	}

	/**
	 * Start a transaction. This is only needed when the default auto-commit
	 * mechanism is not enough. The default mechanism commits after every db
	 * operation. A transaction is needed if it will be necessary to rollback
	 * multiple db operations.
	 *
	 * @throws Exception
	 *             the exception
	 */
	static public void startTransaction() throws Exception {
		connection_.setAutoCommit(false);
	}

	/** the cache of Entitys */
	private HashMap<Integer, T> objectCache_; // the cache

	/** on/off dlag for object caching */
	private boolean objectCacheOn_; // is caching on?

	/** the DB table name that this instance is working with */
	protected String tablename_;

	/**
	 * Creates a new instance of JdbcDB.
	 *
	 * @param tablename
	 *            the tablename
	 *
	 */
	public JdbcDB(String tablename) {
		tablename_ = tablename;

		objectCacheOn_ = true; // always turn caching on
		objectCache_ = new HashMap<Integer, T>(); // allocate the cache

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.model.entity.KeyedEntityDB#close()
	 */
	@Override
	public void close() throws Exception {
		if (connection_ != null)
			connection_.close();
		connection_ = null;
	}

	/**
	 * Derived class methos to create an object T from a ResultSet
	 *
	 * @param rs
	 *            the ResultSet
	 *
	 * @return the created object
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	abstract T createFrom(ResultSet rs) throws SQLException;

	/**
	 * Delete an object from the cache.
	 *
	 * @param key
	 *            the object's key
	 */
	protected void delCache(int key) {
		// remove the bean from the cache
		if (objectCacheOn_) {
			objectCache_.remove(Integer.valueOf(key));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.model.entity.KeyedEntityDB#delete(int)
	 */
	@Override
	public void delete(int key) throws Exception {
		try (PreparedStatement stmt = connection_
				.prepareStatement("DELETE FROM " + tablename_ + " WHERE record_id = ?")) {
			stmt.setInt(1, key);
			stmt.executeUpdate();

			delCache(key);
		}
	}

	/**
	 * Empty the cache.
	 */
	protected void emptyCache() {
		if (objectCacheOn_)
			objectCache_.clear();
	}

	/**
	 * Gets the auto-generated key from the latest INSERT.
	 *
	 * @return the identity
	 */
	protected int getIdentity() {
		int id = 0;
		ResultSet r = null;
		try (PreparedStatement stmt = connection_.prepareStatement("CALL IDENTITY()")) {

			r = stmt.executeQuery();
			if (r != null && r.next()) {
				id = r.getInt(1);
			}
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		} finally {
			if (r != null)
				try {
					r.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return id;

	}

	/**
	 * Derived Class PreparedStatement to query all rows
	 *
	 * @return the PreparedStatement
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	abstract PreparedStatement getPSAll() throws SQLException;

	/**
	 * Derived Class PreparedStatement to query one row by primary key
	 *
	 * @param key
	 *            the key
	 *
	 * @return the PreparedStatement
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	abstract PreparedStatement getPSOne(int key) throws SQLException;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.model.entity.KeyedEntityDB#numRows()
	 */
	@Override
	public int numRows() throws Exception {
		try (PreparedStatement stmt = connection_.prepareStatement("SELECT COUNT(*) FROM " + tablename_)) {
			int ret = -1;
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				ret = rs.getInt(1);
			}

			return ret;
		}

	}

	/**
	 * Run a Query and convert the results to a list of T objects
	 *
	 * @param stmt
	 *            the Prepared Statement
	 *
	 * @return the lsit of objects
	 *
	 * @throws Exception
	 *             the exception
	 */
	protected ArrayList<T> query(PreparedStatement stmt) throws Exception {
		ResultSet r = null;
		try {
			r = stmt.executeQuery();
			ArrayList<T> lst = new ArrayList<T>();
			while (r.next()) {
				T bean = createFrom(r);
				lst.add(bean);
				// store in cache as well
				writeCache(bean);
			}
			return lst;
		} finally {
			if (r != null)
				r.close();
			if (stmt != null)
				stmt.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.model.entity.KeyedEntityDB#readAll()
	 */
	@Override
	public Collection<T> readAll() throws Exception {
		PreparedStatement stmt = null;
		ResultSet r = null;
		try {
			stmt = getPSAll();
			r = stmt.executeQuery();
			List<T> lst = new ArrayList<T>();
			while (r.next()) {
				T bean = createFrom(r);
				lst.add(bean);
				writeCache(bean);
			}
			return lst;
		} finally {
			if (r != null)
				r.close();
			if (stmt != null)
				stmt.close();
		}
	}

	/**
	 * Read an object from the cache.
	 *
	 * @param key
	 *            the object key
	 *
	 * @return the object
	 */
	@SuppressWarnings("unchecked")
	protected T readCache(int key) {

		// if the bean is in the cache - return it
		if (objectCacheOn_) {
			T o = objectCache_.get(Integer.valueOf(key));

			// always copy objects from the cache - never return
			// the original or there will be trouble
			if (o != null) {
				return (T) o.copy();
			}
		}

		return (null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.model.entity.KeyedEntityDB#readObj(int)
	 */
	@Override
	public T readObj(int key) throws Exception {
		T bean = readCache(key);

		if (bean != null)
			return bean;

		PreparedStatement stmt = null;
		ResultSet r = null;
		try {
			stmt = getPSOne(key);
			r = stmt.executeQuery();
			if (r.next()) {
				bean = createFrom(r);
				writeCache(bean);
			}
			return bean;
		} finally {
			if (r != null)
				r.close();
			if (stmt != null)
				stmt.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.model.entity.KeyedEntityDB#sync()
	 */
	@Override
	public void sync() {
		emptyCache();
	}

	/**
	 * Write an object to the cache.
	 *
	 * @param bean
	 *            the bean
	 */
	@SuppressWarnings("unchecked")
	protected void writeCache(T bean) {
		// put a copy of the bean in the cache
		if (objectCacheOn_) {
			objectCache_.put(Integer.valueOf(bean.getKey()), (T) bean.copy());
		}
	}

	/**
	 * get the global connection
	 *
	 * @return the connection
	 */
	public static Connection getConnection() {
		return connection_;
	}
}
