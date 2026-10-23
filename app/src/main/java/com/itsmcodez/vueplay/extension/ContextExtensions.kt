package com.itsmcodez.vueplay.extension

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Global [DataStore] instance for the application settings.
 * It is lazily initialized and shared across the application via the [Context].
 */
val Context.dataStore by preferencesDataStore(name = "settings")

/**
 * Remembers a preference value and provides a setter function to update it.
 *
 * This function observes changes to the specified [key] and returns the current value
 * along with a lambda to update it. It uses [collectAsStateWithLifecycle] to ensure
 * the observation is lifecycle-aware.
 *
 * @param T The Type of the value being stored.
 * @param key The [Preferences.Key] used to identify the preference.
 * @param defaultValue The value to use if the key is not present in the DataStore.
 * @return A [Pair] containing the current value and a function to update the preference.
 */
@Composable
fun <T> Context.rememberPreference(key: Preferences.Key<T>, defaultValue: T): Pair<T, (T) -> Unit> {
    val scope = rememberCoroutineScope()

    // Optimization 1: Use runBlocking for the initial value to avoid UI flicker (bad UX).
    // DataStore caches the value in memory after the first read, making this very fast.
    val initialValue = remember(key) {
        runBlocking { dataStore.data.first()[key] ?: defaultValue }
    }

    // Optimization 2: Use distinctUntilChanged() to prevent unnecessary recompositions 
    // when other preferences in the same DataStore change.
    val state by remember(key, initialValue) {
        dataStore.data
            .map { it[key] ?: defaultValue }
            .distinctUntilChanged()
    }.collectAsStateWithLifecycle(initialValue = initialValue)

    // Optimization 3: Remember the update lambda to avoid unnecessary recompositions 
    // of child components that receive this function.
    val onValueChange: (T) -> Unit = remember(key, scope) {
        { newValue ->
            scope.launch {
                dataStore.edit { preferences ->
                    preferences[key] = newValue
                }
            }
        }
    }

    return state to onValueChange
}
