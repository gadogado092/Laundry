package amat.laundrysederhana.data.repository

import amat.laundrysederhana.data.CashFlowDao

class CashFlowRepository(private val cashFlowDao: CashFlowDao) {

    companion object {
        @Volatile
        private var instance: CashFlowRepository? = null

        fun getInstance(cashFlowDao: CashFlowDao): CashFlowRepository =
            instance ?: synchronized(this) {
                CashFlowRepository(cashFlowDao).apply {
                    instance = this
                }
            }
    }
}