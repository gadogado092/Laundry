package amat.laundry

import amat.laundry.data.entity.AccountBackupEntity
import android.content.Context

class AccountBackupPreference(context: Context) {
    companion object {
        private const val PREFS_NAME = "owner_offline_pref"
        private const val isLogin = "isLogin"
        private const val token = "token"
        private const val name = "name"
        private const val noWa = "noWa"
    }

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setAccount(value: AccountBackupEntity) {
        val editor = preferences.edit()
        editor.putBoolean(isLogin, value.isLogin)
        editor.putString(token, value.token)
        editor.putString(name, value.name)
        editor.putString(noWa, value.noWa)
        editor.apply()
    }

    fun logOut() {
        val editor = preferences.edit()
        editor.putBoolean(isLogin, false)
        editor.putString(token, "")
        editor.putString(name, "")
        editor.putString(noWa, "")
        editor.apply()
    }

    fun getAccount(): AccountBackupEntity {
        return AccountBackupEntity(
            preferences.getBoolean(isLogin, false),
            preferences.getString(token, "").toString(),
            preferences.getString(name, "").toString(),
            preferences.getString(noWa, "").toString(),
        )
    }

}