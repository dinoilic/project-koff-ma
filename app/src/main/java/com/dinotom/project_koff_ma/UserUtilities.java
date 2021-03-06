package com.dinotom.project_koff_ma;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.session.MediaSession;
import android.util.Log;
import android.widget.Toast;

import com.dinotom.project_koff_ma.pojo.TokenDetail;
import com.dinotom.project_koff_ma.pojo.TokenValidation;
import com.dinotom.project_koff_ma.pojo.UserToken;
import com.dinotom.project_koff_ma.pojo.category.Category;
import com.dinotom.project_koff_ma.pojo.category.Result;

import java.util.ArrayList;
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

    static String getCurrentUserName()
    {
        Context context = KoffGlobal.getAppContext();
        String preferenceFileName = context.getResources().getString(R.string.user_accounts_file); // name of preference file
        String userNameKey = context.getResources().getString(R.string.user_name_key); // key value where token is stored
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE);
        String name = sharedPref.getString(userNameKey, "Koff"); // get the token or if it's not there, return empty string

        return name;
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

    static void setUserDetails(String userName)
    {
        Context context = KoffGlobal.getAppContext();
        String preferenceFileName = context.getResources().getString(R.string.user_accounts_file); // name of preference file
        String userNameKey = context.getResources().getString(R.string.user_name_key); // key value where token is stored
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(userNameKey, userName);
        editor.commit(); // use "apply" if we want asynchronous later
    }

    static void fetchNewToken(final String username, final String password)
    {
        APIInterface apiInterface = APIClient.getClientWithoutDefaultHeaders().create(APIInterface.class);
        Call<UserToken> userTokenCall = apiInterface.getUserToken(username, password); // add login procedure here later
        userTokenCall.enqueue(new Callback<UserToken>() {
            @Override
            public void onResponse(Call<UserToken> call, Response<UserToken> response) {
                UserToken token = response.body();

                if(token == null)
                {
                    KoffGlobal.bus.post(KoffGlobal.getAppContext().getResources().getString(R.string.login_failure_event));
                    Log.d(TAG, " Null token!");
                    return;
                }

                UserUtilities.setNewUserToken(token.getToken());

                Log.d(TAG + "fetchNewToken", UserUtilities.getCurrentUserToken());
                KoffGlobal.bus.post(KoffGlobal.getAppContext().getResources().getString(R.string.login_successful_event));
            }
            @Override
            public void onFailure(Call<UserToken> call, Throwable t) {
                call.cancel();
                KoffGlobal.bus.post(KoffGlobal.getAppContext().getResources().getString(R.string.login_failure_event));
                Log.d(TAG + "fetchNewToken", "Token fetching unsuccessful!");
            }
        });
    }

    static void checkTokenValidity(String token)
    {
        final String plainToken = token;
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

                    if(KoffGlobal.bus == null)
                        Log.d(TAG, "Otto Bus is uninitialized!");

                    // send "notification" to MainActivity so that it can initialize login procedure
                    KoffGlobal.bus.post(KoffGlobal.getAppContext().getResources().getString(R.string.main_activity_login_event));
                }
                else {
                    Log.d(TAG + "checkTokenValidity", "User auth token is valid!");
                    // send "notification" to MainActivity so that it can begin with loading its own content

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

    static void saveUserName(String token)
    {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<TokenDetail> tokenDetailCall = apiInterface.getTokenDetail(token);
        tokenDetailCall.enqueue(new Callback<TokenDetail>()
        {
            @Override
            public void onResponse(Call<TokenDetail> call, Response<TokenDetail> response)
            {
                TokenDetail tokenDetails = response.body(); // Category pojo is fetched

                if(tokenDetails != null)
                    setUserDetails(tokenDetails.getUser().getFullName());

                KoffGlobal.bus.post(KoffGlobal.getAppContext().getResources().getString(R.string.user_name_change_event));
            }

            @Override
            public void onFailure(Call<TokenDetail> call, Throwable t)
            {
                call.cancel();
            }
        });
    }
}
