package com.felix.bestfelix.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "quit_habits")
data class QuitHabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    /** Date de début du compteur : le solde se calcule jour par jour à partir d'elle. */
    val startDate: LocalDate,
    val position: Int = 0,
)

@Entity(
    tableName = "relapses",
    foreignKeys = [
        ForeignKey(
            entity = QuitHabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["quitHabitId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("quitHabitId", "date")],
)
data class RelapseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo val quitHabitId: Long,
    val date: LocalDate,
    val note: String? = null,
)
