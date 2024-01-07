package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CashFlowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cashFlow: CashFlow)

    @Query("SELECT * FROM CashFlow")
    fun getAllCashFlow(): Flow<List<CashFlow>>

    @Update
    suspend fun update(cashFlow: CashFlow)


    //CHECK IN AREA
    @Query("UPDATE Unit SET unitStatusId=1, tenantId=:tenantId WHERE id=:unitId")
    suspend fun updateUnit(unitId: String, tenantId: String)

    @Query(
        "UPDATE Tenant " +
                "SET limitCheckOut=:limitCheckOut, " +
                "additionalCost=:additionalCost, " +
                "noteAdditionalCost=:noteAdditionalCost, " +
                "guaranteeCost=:guaranteeCost, " +
                "unitId=:unitId " +
                "WHERE id=:tenantId"
    )
    suspend fun updateTenant(
        tenantId: String, limitCheckOut: String,
        additionalCost: Int, noteAdditionalCost: String, guaranteeCost: Int, unitId: String
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreditTenant(creditTenant: CreditTenant)

    @Transaction
    suspend fun insertCheckIn(
        cashFlow: CashFlow,
        creditTenant: CreditTenant,
        isFullPayment: Boolean,
        limitCheckOut: String,
        additionalCost: Int,
        noteAdditionalCost: String,
        guaranteeCost: Int
    ) {
        //eksekusi pakai transaction
        //update tenant
        updateTenant(
            tenantId = cashFlow.tenantId,
            limitCheckOut = limitCheckOut,
            additionalCost = additionalCost,
            noteAdditionalCost = if (additionalCost == 0) "" else noteAdditionalCost,
            guaranteeCost = guaranteeCost,
            unitId = cashFlow.unitId
        )
        //update unit
        updateUnit(unitId = cashFlow.unitId, tenantId = cashFlow.tenantId)
        //insert credit tenant if not full payment
        if (!isFullPayment) {
            insertCreditTenant(creditTenant)
        }
        //insert cashflow
        insert(cashFlow)
    }

}