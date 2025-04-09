package com.example.products.auth

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.Base64

private val JWT_KEY = stringPreferencesKey("jwt_token")
val Context.dataStore by preferencesDataStore(name = "jwt_prefs")

@Singleton
class JwtManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // store K-V data persistently and async using coroutines (not encrypted)
    // EncryptedDataStore or KeyStore (for higher security - refresh tokens/biometrics)
    private val dataStore = context.dataStore

    val token: Flow<String?> = dataStore.data.map { preferences ->
        preferences[JWT_KEY]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveToken(token: String) {
        val encoded = Base64.getEncoder().encodeToString(token.toByteArray())
        dataStore.edit { preferences ->
            preferences[JWT_KEY] = encoded
        }
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(JWT_KEY)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getDecodedToken(): String? {
        val prefs = dataStore.data.map { it[JWT_KEY] }.firstOrNull()
        return prefs?.let {
            String(Base64.getDecoder().decode(it))
        }
    }

    // use a hardcoded token for now
    fun refreshToken(): String {
        return "new_jwt_token_from_server"
    }
}