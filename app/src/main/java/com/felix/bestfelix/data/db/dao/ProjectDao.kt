package com.felix.bestfelix.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.felix.bestfelix.data.db.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query("SELECT * FROM projects")
    fun observeProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects")
    suspend fun getProjects(): List<ProjectEntity>

    @Upsert
    suspend fun upsert(project: ProjectEntity): Long

    @Delete
    suspend fun delete(project: ProjectEntity)

    @Query("DELETE FROM projects")
    suspend fun clear()

    @Insert
    suspend fun insertAll(projects: List<ProjectEntity>)
}
