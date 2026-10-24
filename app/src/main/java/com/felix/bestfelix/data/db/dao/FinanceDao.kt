package com.felix.bestfelix.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.felix.bestfelix.data.db.entity.CreditEntity
import com.felix.bestfelix.data.db.entity.IncomeEntity
import com.felix.bestfelix.data.db.entity.RepaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {

    @Query("SELECT * FROM credits")
    fun observeCredits(): Flow<List<CreditEntity>>

    @Query("SELECT * FROM repayments ORDER BY date ASC")
    fun observeAllRepayments(): Flow<List<RepaymentEntity>>

    @Query("SELECT * FROM incomes ORDER BY date DESC")
    fun observeIncomes(): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM credits")
    suspend fun getCredits(): List<CreditEntity>

    @Query("SELECT * FROM repayments ORDER BY date ASC")
    suspend fun getRepayments(): List<RepaymentEntity>

    @Query("SELECT * FROM incomes ORDER BY date DESC")
    suspend fun getIncomes(): List<IncomeEntity>

    @Upsert
    suspend fun upsertCredit(credit: CreditEntity): Long

    @Delete
    suspend fun deleteCredit(credit: CreditEntity)

    @Upsert
    suspend fun upsertRepayment(repayment: RepaymentEntity): Long

    @Delete
    suspend fun deleteRepayment(repayment: RepaymentEntity)

    @Upsert
    suspend fun upsertIncome(income: IncomeEntity): Long

    @Delete
    suspend fun deleteIncome(income: IncomeEntity)

    @Query("DELETE FROM credits")
    suspend fun clearCredits()

    @Query("DELETE FROM incomes")
    suspend fun clearIncomes()

    @Insert
    suspend fun insertCredits(credits: List<CreditEntity>)

    @Insert
    suspend fun insertRepayments(repayments: List<RepaymentEntity>)

    @Insert
    suspend fun insertIncomes(incomes: List<IncomeEntity>)
}
