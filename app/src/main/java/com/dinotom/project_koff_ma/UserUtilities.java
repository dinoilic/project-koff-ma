package com.dinotom.project_koff_ma;

import android.content.Context;
import android.content.SharedPreferences;

public class UserUtilities
{
    static String getCurrentUserToken(Context context)
    {
        String preferenceFileName = context.getResources().getString(R.string.user_accounts_file); // name of preference file
        String userTokenKey = context.getResources().getString(R.string.user_token_key); // key value where token is stored
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE);
        String token = sharedPref.getString(userTokenKey, ""); // get the token

        return token;
    }

    static void setNewUserToken(Context context, String newToken)
    {
        String preferenceFileName = context.getResources().getString(R.string.user_accounts_file); // name of preference file
        String userTokenKey = context.getResources().getString(R.string.user_token_key); // key value where token is stored
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(userTokenKey, newToken);
        editor.commit(); // use "apply" if we want asynchronous later
    }
}
