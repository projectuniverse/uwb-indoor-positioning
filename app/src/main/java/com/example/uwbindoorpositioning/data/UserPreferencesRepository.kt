package com.example.uwbindoorpositioning.data

import androidx.annotation.StringRes
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.uwbindoorpositioning.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

// An enum class that represents the different app them modes
enum class AppTheme(@StringRes val title: Int) {
    MODE_DAY(title = R.string.light_mode),
    MODE_NIGHT(title = R.string.dark_mode),
    MODE_AUTO(title = R.string.auto_mode);
}

// A data class to store the user preference data
data class UserPreferences(
    val appTheme: AppTheme
)

interface UserPreferencesRepository {
    suspend fun setAppTheme(appTheme: AppTheme)
    val userPreferencesFlow: Flow<UserPreferences>
}

class DefaultUserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {
    /*
     * Declaring an app_theme key as a member in a private PreferencesKeys object.
     * Enums are stored as their string name representation.
     */
    private object PreferencesKeys {
        val APP_THEME = stringPreferencesKey("app_theme")
    }

    // Get the user preferences flow as a value
    override val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            /*
             * Retrieve app theme value and convert it to an AppTheme object. Default value
             * is auto mode, in case appTheme has not been set.
             */
            val appTheme = AppTheme.valueOf(
                preferences[PreferencesKeys.APP_THEME] ?: AppTheme.MODE_AUTO.name
            )
            UserPreferences(appTheme)
        }

    // A function that updates the appTheme property of UserPreferences
    override suspend fun setAppTheme(appTheme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = appTheme.name
        }
    }
}