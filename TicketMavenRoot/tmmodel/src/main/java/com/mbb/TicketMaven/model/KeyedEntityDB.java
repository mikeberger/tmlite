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


import com.mbb.TicketMaven.model.entity.KeyedEntity;

import java.util.Collection;


/**
 * interface for a database that manages KeyedEntitys
 * the term DB or database is used for legacy reasons, but in reality,
 * a KeyedEntityDB is a class that manages a Single table in the DB which has an integer primary key
 * all primary TM objects are KeyedEntitys and are stored in a their own table with an integer key
 * Each KeyedEntityDB implementing class will likely provide additional, more advanced queries and updates.
 */
public interface KeyedEntityDB<T extends KeyedEntity>
{
	
	/**
     * Adds a new object T to the database
     * 
     * @param bean the T
     * 
     * @return the generated primary key
     * 
     * @throws Exception the exception
     */
    public int addObj( T bean) throws Exception; // add a new object to the DB
    
    /**
     * Close the database
     * 
     * @throws Exception the exception
     */
    public void close() throws Exception; // close the DB
    
    /**
     * Delete the object of type T from the database given its key
     * 
     * @param key the primary key
     * 
     * @throws Exception the exception
     */
    public void delete( int key ) throws Exception; // delete an object given the primary key
    
    /**
     * Create a new instance of T
     * 
     * @return the T
     */
    public T newObj(); // return a new object
    
    /**
     * return the number of objects of type T in the database
     * 
     * @return the number of objects of type T
     * 
     * @throws Exception the exception
     */
    public int numRows() throws Exception; //return the number of DB rows
    
    /**
	 * Read all Records.
	 * 
	 * @return a collection of T
	 * 
	 * @throws Exception the exception
	 */
	public Collection<T> readAll() throws Exception; // read all records
    
    /**
     * Read one T by primary key
     * 
     * @param key the key
     * 
     * @return the T
     * 
     * @throws Exception the exception
     */
    public T readObj( int key ) throws Exception; // read 1 record by primary key
    
    /**
     * Sync the database (just clears the memory cache)
     * 
     * @throws Exception the exception
     */
    public void sync() throws Exception; // reload any DB cache
    
    /**
     * Update an existing object of type T in the database
     * 
     * @param bean the bean of type T
     * 
     * @throws Exception the exception
     */
    public void updateObj( T bean ) throws Exception; // update an object in the DB
}
