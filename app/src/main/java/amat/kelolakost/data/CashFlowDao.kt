package amat.kelolakost.data

import amat.kelolakost.data.entity.Sum
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface CashFlowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cashFlow: CashFlow)

    @Query("SELECT * FROM CashFlow " +
            "WHERE isDelete=0 AND creditTenantId=:creditTenantId " +
            "ORDER BY createAt DESC")
    suspend fun getCreditTenantHistory(creditTenantId:String): List<CashFlow>

    @Query("SELECT * FROM CashFlow " +
            "WHERE isDelete=0 AND creditDebitId=:creditDebitId " +
            "ORDER BY createAt DESC")
    suspend fun getCreditDebitHistory(creditDebitId:String): List<CashFlow>

    @Update
    suspend fun update(cashFlow: CashFlow)


    //CHECK IN AREA
    @Query("UPDATE Unit SET unitStatusId=:unitStatusId, noteMaintenance=:noteMaintenance, tenantId=:tenantId WHERE id=:unitId")
    suspend fun updateUnit(
        unitId: String,
        tenantId: String,
        unitStatusId: Int,
        noteMaintenance: String
    )

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
    suspend fun prosesCheckIn(
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
        updateUnit(
            unitId = cashFlow.unitId,
            tenantId = cashFlow.tenantId,
            unitStatusId = 1,
            noteMaintenance = ""
        )
        //insert credit tenant if not full payment
        if (!isFullPayment) {
            insertCreditTenant(creditTenant)
        }
        //insert cashflow
        insert(cashFlow)
    }

    @Transaction
    suspend fun prosesCheckOut(
        cashFlow: CashFlow,
        priceGuarantee: Int,
        unitStatusId: Int,
        noteMaintenance: String
    ) {
        //update tenant
        updateTenant(
            tenantId = cashFlow.tenantId,
            limitCheckOut = "",
            additionalCost = 0,
            noteAdditionalCost = "",
            guaranteeCost = 0,
            unitId = "0"
        )
        //update unit
        updateUnit(
            unitId = cashFlow.unitId,
            tenantId = "0",
            unitStatusId = unitStatusId,
            noteMaintenance = noteMaintenance
        )

        if (priceGuarantee > 0) {
            //insert cashflow
            insert(cashFlow)
        }
    }

    //EXTEND AREA
    @Query(
        "UPDATE Tenant " +
                "SET limitCheckOut=:limitCheckOut, " +
                "additionalCost=:additionalCost, " +
                "noteAdditionalCost=:noteAdditionalCost " +
                "WHERE id=:tenantId"
    )
    suspend fun updateTenantExtend(
        tenantId: String,
        limitCheckOut: String,
        additionalCost: Int,
        noteAdditionalCost: String
    )

    @Transaction
    suspend fun prosesExtend(
        cashFlow: CashFlow,
        creditTenant: CreditTenant,
        isFullPayment: Boolean,
        limitCheckOut: String,
        additionalCost: Int,
        noteAdditionalCost: String
    ) {
        //eksekusi pakai transaction
        //update tenant
        updateTenantExtend(
            tenantId = cashFlow.tenantId,
            limitCheckOut = limitCheckOut,
            additionalCost = additionalCost,
            noteAdditionalCost = if (additionalCost == 0) "" else noteAdditionalCost
        )
        //insert credit tenant if not full payment
        if (!isFullPayment) {
            insertCreditTenant(creditTenant)
        }
        //insert cashflow
        insert(cashFlow)
    }

    //MOVE UNIT AREA
    @Query(
        "UPDATE Tenant " +
                "SET unitId=:unitId " +
                "WHERE id=:tenantId"
    )
    suspend fun updateTenantMove(
        tenantId: String,
        unitId: String
    )

    @Transaction
    suspend fun prosesMoveUnit(
        cashFlow: CashFlow,
        creditTenant: CreditTenant,
        unitIdOld: String,
        statusIdUnitOld: Int,
        noteMaintenanceUnitOld: String,
        moveType: String,
        isFullPayment: Boolean
    ) {
        //update unit old
        updateUnit(
            unitId = unitIdOld,
            tenantId = "0",
            unitStatusId = statusIdUnitOld,
            noteMaintenance = noteMaintenanceUnitOld
        )

        //update unitMove
        updateUnit(
            unitId = cashFlow.unitId,
            tenantId = cashFlow.tenantId,
            unitStatusId = 1,
            noteMaintenance = ""
        )
        //tenantMoveNew Unit
        updateTenantMove(
            cashFlow.tenantId,
            cashFlow.unitId
        )

        if (moveType == "Downgrade") {
            val cashFLowNew = cashFlow.copy(type = 1)
            insert(cashFLowNew)
        } else if (moveType == "Upgrade") {
            val cashFLowNew = cashFlow.copy(type = 0)
            insert(cashFLowNew)
            //insert credit tenant if not full payment
            if (!isFullPayment) {
                insertCreditTenant(creditTenant)
            }
        }

    }

    @Transaction
    suspend fun prosesFinishMaintenance(
        cashFlow: CashFlow,
    ) {
        //update unit
        updateUnit(
            unitId = cashFlow.unitId,
            tenantId = "0",
            unitStatusId = 2,
            noteMaintenance = ""
        )

        if (cashFlow.nominal != "0") {
            insert(cashFlow)
        }

    }

    //CASH FLOW HOME

    @Query(
        "SELECT * " +
                "FROM cashflow WHERE isDelete=0 AND cashflow.createAt >= :startDate AND cashflow.createAt <= :endDate ORDER BY cashflow.createAt DESC"
    )
    suspend fun getAllCashFlow(startDate: String, endDate: String): List<CashFlow>

    @Query(
        "SELECT (SUM (CASE WHEN type='0' THEN nominal ELSE 0 END ) - SUM (CASE WHEN type='1' THEN nominal ELSE 0 END )) AS total " +
                "FROM cashflow WHERE isDelete=0"
    )
    suspend fun getBalance(): Sum

    @Query(
        "SELECT SUM(nominal) AS total " +
                "FROM cashflow " +
                "WHERE cashflow.type='0' AND isDelete=0 AND cashflow.createAt >= :startDate AND cashflow.createAt <= :endDate"
    )
    suspend fun getTotalIncome(startDate: String, endDate: String): Sum

    @Query(
        "SELECT SUM(nominal) AS total " +
                "FROM cashflow " +
                "WHERE cashflow.type='1' AND isDelete=0 AND cashflow.createAt >= :startDate AND cashflow.createAt <= :endDate"
    )
    suspend fun getTotalOutcome(startDate: String, endDate: String): Sum

}