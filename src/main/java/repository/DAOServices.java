package repository;

import java.util.List;

public interface DAOServices<E> {
	
	/*
	 * This method inserts the specified object into the relative table.
	 * 
	 * @param ojb the entity we want to insert into the database
	 */
    void save(E obj);
	
	/*
	 * This method retrieves an object by their id.
	 * 
	 * @param id attribute used to identify the needed object 
	 * @return the requested entity
	 */
    E getById(String id);
	
	/*
	 * This method get all the entities saved into the table.
	 * 
	 * @return a list of all the asked entities.
	 */
    List<E> getAll();
	
	/*
	 * This method update the attributes of the specified object.
	 * 
	 * @param obj the object we want to modify
	 */
    void update(E obj);
	
	/*
	 * This method removes the chosen object from the database.
	 * 
	 * @param id the identifier of the record we want to remove from the database
	 */
    void delete(String id);
}
