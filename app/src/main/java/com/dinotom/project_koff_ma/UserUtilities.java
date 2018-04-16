package com.dinotom.project_koff_ma;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dinotom.project_koff_ma.pojo.TokenValidation;
import com.dinotom.project_koff_ma.pojo.UserToken;

import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserUtilities
{
    private static final String TAG = UserUtilities.class.getSimpleName();

    static String getCurrentUserToken()
    {
        Context context = KoffGlobal.getAppContext();
        String preferenceFileName = context.getResources().getString(R.string.user_accounts_file); // name of preference file
        String userTokenKey = context.getResources().getString(R.string.user_token_key); // key value where token is stored
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE);
        String token = sharedPref.getString(userTokenKey, ""); // get the token or if it's not there, return empty string

        return token;
    }

    static void setNewUserToken(String newToken)
    {
        Context context = KoffGlobal.getAppContext();
        String preferenceFileName = context.getResources().getString(R.string.user_accounts_file); // name of preference file
        String userTokenKey = context.getResources().getString(R.string.user_token_key); // key value where token is stored
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(userTokenKey, newToken);
        editor.commit(); // use "apply" if we want asynchronous later
    }

    static void fetchNewToken()
    {
        APIInterface apiInterface = APIClient.getClientWithoutDefaultHeaders().create(APIInterface.class);
        Call<UserToken> userTokenCall = apiInterface.getUserToken("superuser", "superuser"); // add login procedure here later
        userTokenCall.enqueue(new Callback<UserToken>() {
            @Override
            public void onResponse(Call<UserToken> call, Response<UserToken> response) {
                UserToken token = response.body();
                UserUtilities.setNewUserToken(token.getToken());
                Log.d(TAG + "fetchNewToken", UserUtilities.getCurrentUserToken());
                // send "notification" to MainActivity so that it can begin with loading it's own content
                KoffGlobal.bus.post(KoffGlobal.getAppContext().getResources().getString(R.string.main_activity_event));
            }
            @Override
            public void onFailure(Call<UserToken> call, Throwable t) {
                call.cancel();
                Log.d(TAG + "fetchNewToken", "Token fetching unsuccessful!");
            }
        });
    }

    static void checkTokenValidity(String token)
    {
        token = "Token " + token;
        Log.d(TAG + "checkTokenValidity", "Token parameter is " + token);
        APIInterface apiInterface = APIClient.getClientWithoutDefaultHeaders().create(APIInterface.class);
        //APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<TokenValidation> tokenValidityCall = apiInterface.getTokenValidityStatus(token);
        tokenValidityCall.enqueue(new Callback<TokenValidation>() {
            @Override
            public void onResponse(Call<TokenValidation> call, Response<TokenValidation> response)
            {
                if(response.code() == 403) { // find a better way later
                    Log.d(TAG + "checkTokenValidity", "User auth token is not valid, fetching a new one.");
                    fetchNewToken();
                }
                else {
                    Log.d(TAG + "checkTokenValidity", "User auth token is valid!");
                    // send "notification" to MainActivity so that it can begin with loading it's own content
                    KoffGlobal.bus.post(KoffGlobal.getAppContext().getResources().getString(R.string.main_activity_event));
                }
            }
            @Override
            public void onFailure(Call<TokenValidation> call, Throwable t)
            {
                call.cancel();
            }
        });
    }
}
