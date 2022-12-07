package com.pmsi.lamaruin.data

import android.content.Context

class LoginPref(context : Context) {
    private val prefIsLogin = context.getSharedPreferences(PREFS_ISLOGIN, Context.MODE_PRIVATE)

    fun setSession(isLogin: Boolean){
        val editor = prefIsLogin.edit()
        editor.putBoolean(SESSION, isLogin)
        editor.apply()
    }

    fun getSession() : Boolean{
        return prefIsLogin.getBoolean(SESSION, false)
    }

    fun logout(){
        prefIsLogin.edit().clear().apply()
    }

    companion object {
        private const val PREFS_ISLOGIN = "isLogin_pref"
        private const val SESSION = "session"
    }

}