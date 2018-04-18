package subbukathir.weatherinfo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by subbukathir on 18-04-2018.
 */
public class MyPreferences {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private int PRIVATE_MODE = 0;
    private static final String MYPREFERENCE = "WeatherInfo";

    public static final String SHARED_CITY = "city";
    public static final String SHARED_1DAY = "first_day";
    public static final String SHARED_2DAY = "second_day";
    public static final String SHARED_3DAY = "third_day";

    public MyPreferences(Context context) {
        mPreferences = context.getSharedPreferences(MYPREFERENCE, PRIVATE_MODE);
        mEditor = mPreferences.edit();
    }

    // If the user has not chosen a city yet, return
    // Sydney as the default city
    public String getCity() {
        return mPreferences.getString("city", "Sydney, AU");
    }

    public void setCity(String city) {
        mEditor.putString("city", city);
        mEditor.commit();
    }

    public void savePreference(String key,String data){
        mEditor.putString(key,data);
        mEditor.commit();
    }

    public String getPreference(String key){
        return mPreferences.getString(key,null);
    }

}
