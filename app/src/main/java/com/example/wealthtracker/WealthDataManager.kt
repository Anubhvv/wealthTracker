package com.example.wealthtracker

import android.content.Context
import android.util.Log
import androidx.compose.runtime.ProvidableCompositionLocal

data class WealthData(
    var categories: List<CategoryData> = emptyList()
)

data class CategoryData(
    val name: String = "",
    var value: Double = 0.0
)

const val WEALTH_PREFS_KEY = "wealth_prefs_key"

fun loadWealthData(context: Context): WealthData {
    val sharedPreferences = context.getSharedPreferences(WEALTH_PREFS_KEY, Context.MODE_PRIVATE)
    val categoriesSet = sharedPreferences.getStringSet("categories", emptySet())
    Log.d("asset WealthData", "Loaded categoriesSet: $categoriesSet")
    val otherCategoriesList = categoriesSet?.map { categoryName ->
        CategoryData(categoryName, sharedPreferences.getFloat(categoryName, 0.0f).toDouble())
    } ?: emptyList()

    return WealthData(
        categories = otherCategoriesList
    )
}

fun saveWealthData(context: Context, wealthData: WealthData) {
    val sharedPreferences = context.getSharedPreferences(WEALTH_PREFS_KEY, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()


    val categoriesSet = mutableSetOf<String>()

    wealthData.categories.forEach { categoryData ->
        val categoryName = categoryData.name
        val categoryValue = categoryData.value
        editor.putFloat(categoryName, categoryValue.toFloat())
        categoriesSet.add(categoryName)
    }
    Log.d("WealthData", "Saving categoriesSet: $categoriesSet")
    editor.putStringSet("categories", categoriesSet)
    editor.apply()
}
