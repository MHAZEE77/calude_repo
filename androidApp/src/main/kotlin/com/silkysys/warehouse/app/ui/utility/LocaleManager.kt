package com.silkysys.warehouse.app.ui.utility

import android.content.Context
import android.content.res.Configuration
import java.util.*

object LocaleManager {

    const val SELECTED_LANGUAGE = "MEW_CURRENT_-- USER_LANGUAGE"

    var englishFlag = "en"
    var arabicFlag = "ar"
    var urduFlag = "ur"
    var hindiFlag = "hi"
//    val arrayLanguage = arrayOf("English", "العربية", "اردو", "भारतीय")
    val arrayLanguage = arrayOf("English", "العربية")
//    val arrayFlag = arrayOf(englishFlag, arabicFlag, urduFlag, hindiFlag)
    val arrayFlag = arrayOf(englishFlag, arabicFlag)

    fun setLocale(context: Context): Context {
        return updateResources(context, getCurrentLanguage(context))
    }

    fun setNewLocale(context: Context, language: String) {
        persistLanguagePreference(context, language)
        updateResources(context, language)
    }

    fun getCurrentLanguage(context: Context): String {
        return SavePref(context).language.ifBlank { englishFlag }
    }

    fun persistLanguagePreference(context: Context, language: String) {
        SavePref(context).language = language
    }

    fun updateResources(context: Context, language: String): Context {

        var contextFun = context

        var locale = Locale(language)
        Locale.setDefault(locale)

        var resources = context.resources
        var configuration = Configuration(resources.configuration)

        configuration.setLocale(locale)
        contextFun = context.createConfigurationContext(configuration)
        return contextFun
    }

    fun getLocaleIndex(language: String) : Int? {
        return arrayFlag.indexOfFirst { it.equals(language, true) }
    }
}
