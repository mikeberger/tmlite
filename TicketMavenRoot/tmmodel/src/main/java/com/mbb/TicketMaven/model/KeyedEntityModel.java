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
package com.mbb.TicketMaven.model;



import java.util.Collection;

import com.mbb.TicketMaven.model.entity.KeyedEntity;
import com.mbb.TicketMaven.model.jdbc.JdbcDBFactory;

/**
 * This is the base class for TicketMaven data models. Each KeyedEntityModel deals with one Entity type.
 */
public abstract class KeyedEntityModel<T extends KeyedEntity> extends Model {

	protected KeyedEntityDB<T> db_; // the low level database
	private Class<? extends KeyedEntity> entityClass_;
	
	@SuppressWarnings("unchecked")
	protected KeyedEntityModel(Class<? extends KeyedEntity> entityClass)
	{
		entityClass_ = entityClass;
		db_ = (KeyedEntityDB<T>) JdbcDBFactory.getInstance().create(entityClass);
	}

	/**
	 * Gets all objects.
	 * 
	 * @return a collection of all objects managed by this Model
	 * 
	 * @throws Exception the exception
	 */
	public Collection<T> getRecords() throws Exception {
		Collection<T> records = db_.readAll();
		return records;
	}
	
	/**
	 * Gets the entity name that this Model manages.
	 * 
	 * @return the bean name
	 */
	public String getEntityName()
	{
		String n = entityClass_.getName();
		int idx = n.lastIndexOf(".");
		if( idx != -1 )
			return( n.substring(idx + 1));
		return n;
	}
	
	/**
	 * Checks if this Model's DB is open
	 * 
	 * @return true, if this Model's DB is open
	 */
	public boolean is_open()
	{
		return (db_ != null);
	}

	/**
	 * Delete an object
	 * 
	 * @param bean the object
	 * 
	 * @throws Exception the exception
	 */
	public void delete(KeyedEntity bean) throws Exception {
			db_.delete(bean.getKey());	
			refresh();
	}
	
	/**
	 * Delete and object given its key.
	 * 
	 * @param key the key
	 * 
	 * @throws Exception the exception
	 */
	public void delete(int key) throws Exception {
		db_.delete(key);
		refresh();
}

	/**
	 * Save an object. Will insert new objects and update existing ones
	 * 
	 * @param bean the bean
	 * 
	 * @return the int - the object key
	 * 
	 * @throws Exception the exception
	 */
	public int saveRecord(T bean) throws Exception {

		int num = bean.getKey();
		if (bean.isNew()) {
			num = db_.addObj(bean);
		} else {
			db_.updateObj(bean);
		}

		// inform views of data change
		refresh();
		
		return num;
	}


	/**
	 * create a brand new object that is not yet persisted
	 * 
	 * @return the object
	 */
	public T newRecord() {
		return (db_.newObj());
	}

	/**
	 * Gets a record by key
	 * 
	 * @param num the key
	 * 
	 * @return the record
	 * 
	 * @throws Exception the exception
	 */
	public T getRecord(int num) throws Exception {
		return (db_.readObj(num));
	}

	/**
	 * notify out listeners that the model data has changed
	 */
	public void refresh() {
		refreshListeners();
	}
	
	/**
	 * get the number of objects in the Model
	 * 
	 * @return the number of objects
	 * 
	 * @throws Exception the exception
	 */
	public int numRows() throws Exception {
		return db_.numRows();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void sync() throws Exception
	{
		// create newdb to force invocation of any db upgrades
		db_ = (KeyedEntityDB<T>) JdbcDBFactory.getInstance().create(entityClass_);
	}
}
