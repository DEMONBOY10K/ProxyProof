package com.mact.proxyproof

import android.content.Context
import android.content.SharedPreferences


class Intromanager(var context: Context) {
    var pref: SharedPreferences
    var editor: SharedPreferences.Editor
    fun setFirst(isFirst: Boolean) {
        editor.putBoolean("check", isFirst)
        editor.commit()
    }

    fun Check(): Boolean {
        return pref.getBoolean("check", true)
    }

    init {
        pref = context.getSharedPreferences("first", 0)
        editor = pref.edit()
    }
}
