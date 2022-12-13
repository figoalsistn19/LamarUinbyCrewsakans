package com.pmsi.lamaruin.data

import android.content.Context

class LoginPref(context : Context) {
    private val prefIsLogin = context.getSharedPreferences(PREFS_ISLOGIN, Context.MODE_PRIVATE)
    private val prefIdMahasiswa = context.getSharedPreferences(PREFS_ID_MHS, Context.MODE_PRIVATE)
    private val prefNamaMahasiswa = context.getSharedPreferences(PREFS_NAMA_MHS, Context.MODE_PRIVATE)
    private val prefRole = context.getSharedPreferences(PREFS_ROLE, Context.MODE_PRIVATE)

    fun setSession(isLogin: Boolean){
        val editor = prefIsLogin.edit()
        editor.putBoolean(SESSION, isLogin)
        editor.apply()
    }

    fun setIdMhs(idMhs: String){
        val editor = prefIdMahasiswa.edit()
        editor.putString(ID_MHS, idMhs)
        editor.apply()
    }

    fun setNamaMhs(namaMhs: String){
        val editor = prefNamaMahasiswa.edit()
        editor.putString(NAMA_MHS, namaMhs)
        editor.apply()
    }

    fun setRole(role: String){
        val editor = prefRole.edit()
        editor.putString(ROLE, role)
        editor.apply()
    }

    fun getSession() : Boolean{
        return prefIsLogin.getBoolean(SESSION, false)
    }

    fun getIdMhs() : String? {
        return prefIdMahasiswa.getString(ID_MHS, null)
    }

    fun getRole() : String? {
        return prefRole.getString(ROLE, null)
    }

    fun getNamaMhs() : String? {
        return prefNamaMahasiswa.getString(NAMA_MHS, null)
    }

    fun logout(){
        prefIsLogin.edit().clear().apply()
        prefIdMahasiswa.edit().clear().apply()
        prefNamaMahasiswa.edit().clear().apply()
        prefRole.edit().clear().apply()
    }

    companion object {
        private const val PREFS_ISLOGIN = "isLogin_pref"
        private const val SESSION = "session"
        private const val PREFS_ID_MHS = "idMhs_pref"
        private const val ID_MHS = "id_mhs"
        private const val PREFS_NAMA_MHS = "namaMhs_pref"
        private const val NAMA_MHS = "nama_mhs"
        private const val PREFS_ROLE = "role_prefs"
        private const val ROLE = "role"
    }

}