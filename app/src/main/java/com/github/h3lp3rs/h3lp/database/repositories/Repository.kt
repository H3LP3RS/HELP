package com.github.h3lp3rs.h3lp.database.repositories

import java.util.concurrent.CompletableFuture

/**
 * General interface displaying the different methods required to store, retrieve and delete an object of T in the database
 */
interface Repository<T>{
    /**
     * Retrieves the value with the given ID
     *
     * @param id Key of the object in the database
     * @return A CompletableFuture wrapping the value
     */
    fun get(id: String): CompletableFuture<T>

    /**
     * Creates an entry in the database with the given value
     *
     * @param value The object value to create
     */
    fun insert(value: T)

    /**
     * Deletes the value at the given entry in the database
     *
     * @param id Key of the object in the database
     */
    fun delete(id: String)
}