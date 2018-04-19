package subbukathir.weatherinfo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import subbukathir.weatherinfo.fragments.FirstDayFragment;
import subbukathir.weatherinfo.fragments.SecondDayFragment;
import subbukathir.weatherinfo.fragments.ThirdDayFragment;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;

    Handler handler;
    //Fragments

    FirstDayFragment firstDayFragment;
    SecondDayFragment secondDayFragment;
    ThirdDayFragment thirdDayFragment;

    //locationmanger
    LocationManager mLocationManager;

    private Location mLastLocation = null;
    private double latitude = 0.0, longitude = 0.0;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    private static final int REQUEST_GPS = 1001;

    private boolean isGpsEnabled = false;
    private boolean isNetworkEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "oncreate");

        //initialize mypreference
        new MyPreferences(MainActivity.this);

        handler = new Handler();
        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (MyPreferences.getPreference(MyPreferences.SHARED_UNITS).equals("null")) {
            MyPreferences.savePreference(MyPreferences.SHARED_UNITS, "metric");
        }

        if (MyPreferences.getPreference(MyPreferences.SHARED_CITY).equals("null")) {
            MyPreferences.savePreference(MyPreferences.SHARED_CITY, "Coimbatore,IN");
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_GPS);

        }

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position, false);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        getLocation();
        updateWeatherData();
        setupViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLocation();
        updateWeatherData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if(mLocationManager!=null)
                mLocationManager.removeUpdates(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLocation() {
        try {
            if (!isNetworkEnabled && !isGpsEnabled) {
                Log.e("MainActivity", "oncreate " + " no network");
            } else {
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Toast.makeText(MainActivity.this,"Without Gps location permission we cant't get exact city",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mLocationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (mLocationManager != null) {
                        mLastLocation = mLocationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (mLastLocation != null) {
                            latitude = mLastLocation.getLatitude();
                            longitude = mLastLocation.getLongitude();
                            getAddress(latitude,longitude);
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGpsEnabled) {

                    if (mLastLocation == null) {
                        mLocationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (mLocationManager != null) {
                            mLastLocation = mLocationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (mLastLocation != null) {
                                latitude = mLastLocation.getLatitude();
                                longitude = mLastLocation.getLongitude();
                                getAddress(latitude,longitude);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_GPS:
                if(grantResults.length>0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    getLocation();
                }else Toast.makeText(this,"Kindly enable GPS & Turn on Network",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void updateWeatherData(){
        new Thread(){
            public void run(){
                if(MyPreferences.isNetworkAvailable(MainActivity.this)){
                    final JSONObject json = GetForecastData.getJSON(MainActivity.this);
                    final JSONObject data = GetWeatherData.getJSON(MainActivity.this);
                    if(json == null || data ==null){
                        handler.post(new Runnable(){
                            public void run(){
                                Toast.makeText(MainActivity.this,
                                        MainActivity.this.getString(R.string.place_not_found),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }else {
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(MainActivity.this,
                                    MainActivity.this.getString(R.string.check_internet_connection),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        // Associate searchable configuration with the SearchView
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_celsius:
                Toast.makeText(this, "Home celsius Click", Toast.LENGTH_SHORT).show();
                MyPreferences.savePreference(MyPreferences.SHARED_UNITS,"metric");
                updateWeatherData();
                //if(viewPager!=null) setupViewPager(viewPager);
                return true;
            case R.id.action_fahrenheit:
                Toast.makeText(this, "Home fahrenheit Click", Toast.LENGTH_SHORT).show();
                MyPreferences.savePreference(MyPreferences.SHARED_UNITS,"imperial");
                updateWeatherData();
                //if(viewPager!=null) setupViewPager(viewPager);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        firstDayFragment =new FirstDayFragment();
        secondDayFragment =new SecondDayFragment();
        thirdDayFragment =new ThirdDayFragment();
        adapter.addFragment(firstDayFragment,getDayFromCalendar(0).toUpperCase());
        adapter.addFragment(secondDayFragment,getDayFromCalendar(1).toUpperCase());
        adapter.addFragment(thirdDayFragment,getDayFromCalendar(2).toUpperCase());
        viewPager.setAdapter(adapter);
    }

    private String getDayFromCalendar(int value){
        String day=null;
        Date date;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");

        switch (value){
            case 0:
                date = cal.getTime();
                day = simpleDateFormat.format(date);
                break;
            case 1:
                cal.add(Calendar.DAY_OF_YEAR,1);
                date = cal.getTime();
                day = simpleDateFormat.format(date);
                break;
            case 2:
                cal.add(Calendar.DAY_OF_YEAR,2);
                date = cal.getTime();
                day = simpleDateFormat.format(date);
                break;
        }
        return day;
    }

    @Override
    public void onLocationChanged(Location location) {
        getAddress(location.getLatitude(),location.getLongitude());
    }

    private void getAddress(double latitude, double longitude){
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Log.e(TAG,"getAddress" + "\n"+
                    addresses.get(0).getSubAdminArea() + ",." + addresses.get(0).getSubLocality() + " ,.." +addresses.get(0).getLocality());
            MyPreferences.savePreference(MyPreferences.SHARED_CITY,addresses.get(0).getSubAdminArea()+","+addresses.get(0).getCountryCode());
        }catch(Exception e)
        {

        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this,"Kindly enable GPS & Turn on Network",Toast.LENGTH_SHORT).show();
    }
}
