package com.crush.util

import android.content.Context
import com.crush.BuildConfig
import com.crush.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleUtil {
    fun googleLogin(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestId()
            .requestProfile()
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        //google login setting
        return GoogleSignIn.getClient(context, gso)
    }
}