package subbukathir.weatherinfo.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import subbukathir.weatherinfo.Constants;
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


    public ThirdDayFragment() {
        // Required empty public constructor
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
            JSONObject weather = mResult.getJSONArray("weather").getJSONObject(0);
            JSONObject temp = mResult.getJSONObject("temp");
            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(mResult.getLong("dt")*1000));
            tvCurrentDate.setText("Last update: " + updatedOn);
            tvPressure.setText(mResult.getString("pressure")+"");
            tvHumidity.setText(mResult.getString("humidity")+"%");
            tvSpeed.setText(mResult.getString("speed")+"");
            tvClouds.setText(mResult.getString("clouds")+ (char) 0x00B0);
            Picasso.get().
                    load(Constants.WEATHER_ICON_URL+weather.getString("icon")+".png").into(ivWeatherIcon);
            tvCurrentTemp.setText(temp.getString("day")+"");
            tvCelsiusOrF.setText(MyPreferences.getUnits(getActivity())+"");
            tvWeatherStatus.setText(weather.getString("description")+"");

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

   }
