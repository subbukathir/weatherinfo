package subbukathir.weatherinfo;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static subbukathir.weatherinfo.Constants.BASE_URL;
import static subbukathir.weatherinfo.Constants.OPEN_WEATHER_MAP_API;


/**
 * Created by subbukathir on 18-04-2018.
 */
public class GetForecastData {

    public static JSONObject getJSON(Context context) {
        try {
            Log.e("getWeather data1","getjson");
           // URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city));
            String strUrl = BASE_URL + MyPreferences.getPreference(MyPreferences.SHARED_CITY) +"&cnt=3&units="+MyPreferences.getPreference(MyPreferences.SHARED_UNITS);
            URL url = new URL(strUrl);
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();
            System.out.println("GetForecastData.getJSON " + url);
            connection.addRequestProperty("x-api-key",
                    Constants.WEATHER_API);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if (data.getInt("cod") != 200) {
                return null;
            }else{
                try {
                    JSONArray array = data.getJSONArray("list");
                    if(array.length()>0){
                        MyPreferences.savePreference(MyPreferences.SHARED_1DAY,array.getString(0));
                        MyPreferences.savePreference(MyPreferences.SHARED_2DAY,array.getString(1));
                        MyPreferences.savePreference(MyPreferences.SHARED_3DAY,array.getString(2));
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
