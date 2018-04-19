package subbukathir.weatherinfo;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static subbukathir.weatherinfo.Constants.BASE_URL;
import static subbukathir.weatherinfo.Constants.OPEN_WEATHER_MAP_API;
import static subbukathir.weatherinfo.Constants.WEATHER_API;


/**
 * Created by subbukathir on 18-04-2018.
 */
public class GetWeatherData {

    public static JSONObject getJSON(Context context) {
        try {
            //URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city));
            String strUrl = WEATHER_API + MyPreferences.getPreference(MyPreferences.SHARED_CITY) +"&units="+MyPreferences.getPreference(MyPreferences.SHARED_UNITS);
            URL url = new URL(strUrl);
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

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
            }else {
                MyPreferences.savePreference(MyPreferences.SHARED_1DAY,data.toString());
            }

            return data;
        } catch (Exception e) {
            return null;
        }
    }
}
