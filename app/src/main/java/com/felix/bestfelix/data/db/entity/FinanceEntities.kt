package com.felix.bestfelix.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.felix.bestfelix.domain.model.CreditDirection
import java.time.LocalDate

@Entity(tableName = "credits")
data class CreditEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val direction: CreditDirection,
    /** Qui : la personne concernée. */
    val counterparty: String,
    val amount: Long,
    val reason: String? = null,
    val dueDate: LocalDate? = null,
)

@Entity(
    tableName = "repayments",
    foreignKeys = [
        ForeignKey(
            entity = CreditEntity::class,
            parentColumns = ["id"],
            childColumns = ["creditId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class RepaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val creditId: Long,
    val date: LocalDate,
    val amount: Long,
)

@Entity(tableName = "incomes", indices = [Index("date")])
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Long,
    val date: LocalDate,
    val source: String,
)
