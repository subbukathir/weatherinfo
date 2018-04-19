package subbukathir.weatherinfo.fragments;


import android.app.FragmentTransaction;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import subbukathir.weatherinfo.Constants;
import subbukathir.weatherinfo.GetForecastData;
import subbukathir.weatherinfo.GetWeatherData;
import subbukathir.weatherinfo.MainActivity;
import subbukathir.weatherinfo.MyPreferences;
import subbukathir.weatherinfo.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdDayFragment extends Fragment {

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

    private JSONObject mResult;
    private Handler handler;


    public ThirdDayFragment() {
        handler = new Handler();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        try {
            mResult = new JSONObject(MyPreferences.getPreference(MyPreferences.SHARED_3DAY));
            Log.e("onCreate ","result " + mResult);
            new MyPreferences(getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weathers,container,false);
        ButterKnife.bind(this,rootView);
        updateUI();
        return rootView;
    }

    private void updateUI(){
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR,2);
            Date date = calendar.getTime();
            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy");

            JSONObject weather = mResult.getJSONArray("weather").getJSONObject(0);
            JSONObject temp = mResult.getJSONObject("temp");

            tvCurrentDate.setText(format.format(date));
            tvPressure.setText(mResult.getString("pressure")+"");
            tvHumidity.setText(mResult.getString("humidity")+"%");
            tvSpeed.setText(mResult.getString("speed")+"");
            tvClouds.setText(mResult.getString("clouds")+ (char) 0x00B0);
            Picasso.get().
                    load(Constants.WEATHER_ICON_URL+weather.getString("icon")+".png").into(ivWeatherIcon);
            tvCurrentTemp.setText(temp.getString("day")+"");
            tvCelsiusOrF.setText(MyPreferences.getUnits(getActivity())+"");
            tvWeatherStatus.setText(weather.getString("description")+"");
            tvDayNight.setText("Day "+ temp.getString("day")+ (char) 0x00B0 +"."+" Night "+ temp.getString("night")+ (char) 0x00B0);
            tvTempStatus.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mResult = new JSONObject(MyPreferences.getPreference(MyPreferences.SHARED_3DAY));
            Log.e("onCreate ","result " + mResult);
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
        switch (item.getItemId()) {
            case R.id.action_celsius:
                Toast.makeText(getActivity(), "Home celsius Click", Toast.LENGTH_SHORT).show();
                MyPreferences.savePreference(MyPreferences.SHARED_UNITS,"metric");
                updateWeatherData();
                refreshFragment();
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

    private void refreshFragment() {

    }


    private void updateWeatherData(){
        new Thread(){
            public void run(){
                if(MyPreferences.isNetworkAvailable(getActivity())){
                    final JSONObject json = GetForecastData.getJSON(getActivity());
                    final JSONObject data = GetWeatherData.getJSON(getActivity());
                    if(json == null){
                        handler.post(new Runnable(){
                            public void run(){
                                Toast.makeText(getActivity(),
                                        getActivity().getString(R.string.place_not_found),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }else {
                        try {
                            mResult = new JSONObject(MyPreferences.getPreference(MyPreferences.SHARED_3DAY));
                            updateUI();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.check_internet_connection),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }.start();
    }
}
