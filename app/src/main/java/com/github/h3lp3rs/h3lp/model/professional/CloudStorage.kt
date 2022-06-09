package com.github.h3lp3rs.h3lp.model.professional

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object CloudStorage {
    // CloudStorage contains the currently used cloud storage
    private var storageRef: StorageReference? = null

    /**
     * Returns the current cloud storage (the default  cloud storage is with Firebase storage, unless set
     * otherwise)
     * @return The cloud storage
     */
    fun get(): StorageReference {
        storageRef = storageRef ?: FirebaseStorage.getInstance().getReference("uploads")
        return storageRef!!
    }

    /**
     * Used for testing purposes to give mock cloud storage instances, can also be used to enable
     * multiple cloud storages for the app
     */
    fun set(newStorage: StorageReference) {
        storageRef = newStorage
    }

}