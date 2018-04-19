package subbukathir.weatherinfo.fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import subbukathir.weatherinfo.Constants;
import subbukathir.weatherinfo.MainActivity;
import subbukathir.weatherinfo.MyPreferences;
import subbukathir.weatherinfo.GetWeatherData;
import subbukathir.weatherinfo.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FirstDayFragment extends Fragment {

    Typeface weatherFont;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;
    @BindView(R.id.tvCurrentDate)
    TextView tvCurrentDate;

    @BindView(R.id.tvDayNight)
    TextView tvDayNight;

    @BindView(R.id.tvCurrentTemp)
    TextView tvCurrentTemp;

    @BindView(R.id.tvCelsiusOrF)
    TextView tvCelsiusOrF;

    @BindView(R.id.ivWeatherIcon)
    ImageView ivWeatherIcon;

    @BindView(R.id.tvWeatherStatus)
    TextView tvWeatherStatus;

    @BindView(R.id.tvHumidity)
    TextView tvHumidity;
    @BindView(R.id.tvPressure)
    TextView tvPressure;
    @BindView(R.id.tvSpeed)
    TextView tvSpeed;
    @BindView(R.id.tvClouds)
    TextView tvClouds;
    @BindView(R.id.tvTempStatus)
    TextView tvTempStatus;

    Handler handler;
    private JSONObject mResult;

    public FirstDayFragment(){
        handler = new Handler();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        new MyPreferences(getActivity());
        try {
            mResult = new JSONObject(MyPreferences.getPreference(MyPreferences.SHARED_1DAY));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateWeatherData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_weathers,container,false);
        ButterKnife.bind(this,rootView);
        updateUI();
        return rootView;
    }

    private void updateWeatherData(){
        new Thread(){
            public void run(){
                final JSONObject json = GetWeatherData.getJSON(getActivity());
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            updateUI();
                        }
                    });
                }
            }
        }.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mResult = new JSONObject(MyPreferences.getPreference(MyPreferences.SHARED_1DAY));
            updateUI();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp"))+ " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last update: " + updatedOn);

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    private void updateUI(){
        try {
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            SimpleDateFormat format = new SimpleDateFormat("dd.MMM.yyyy hh:mm aaa");

            JSONObject weather = mResult.getJSONArray("weather").getJSONObject(0);
            JSONObject temp = mResult.getJSONObject("main");

            tvCurrentDate.setText(format.format(date));
            tvPressure.setText(temp.getString("pressure")+"");
            tvHumidity.setText(temp.getString("humidity")+"%");
            tvSpeed.setText(mResult.getJSONObject("wind").getString("speed")+"");
            tvClouds.setText(mResult.getJSONObject("clouds").getString("all")+ (char) 0x00B0);
            Picasso.get().
                    load(Constants.WEATHER_ICON_URL+weather.getString("icon")+".png").into(ivWeatherIcon);
            tvCurrentTemp.setText(temp.getString("temp")+"");
            tvCelsiusOrF.setText(MyPreferences.getUnits(getActivity())+"");
            tvWeatherStatus.setText(weather.getString("description")+"");
            //tvTempStatus.setText("Day "+ temp.getString("day")+ (char) 0x00B0 +"."+" Night "+ temp.getString("night")+ (char) 0x00B0);
            tvTempStatus.setVisibility(View.GONE);
            tvDayNight.setVisibility(View.GONE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_celsius:
                Toast.makeText(getActivity(), "Home celsius Click", Toast.LENGTH_SHORT).show();
                MyPreferences.savePreference(MyPreferences.SHARED_UNITS,"metric");
                updateWeatherData();
                //((MainActivity)getActivity()).refreshViewPager();
                return true;
            case R.id.action_fahrenheit:
                Toast.makeText(getActivity(), "Home fahrenheit Click", Toast.LENGTH_SHORT).show();
                MyPreferences.savePreference(MyPreferences.SHARED_UNITS,"imperial");
                updateWeatherData();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
