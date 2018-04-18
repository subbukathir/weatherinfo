package subbukathir.weatherinfo;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import subbukathir.weatherinfo.fragments.FirstDayFragment;
import subbukathir.weatherinfo.fragments.SecondDayFragment;
import subbukathir.weatherinfo.fragments.ThirdDayFragment;

public class MainActivity extends AppCompatActivity {

    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;

    //Fragments

    FirstDayFragment firstDayFragment;
    SecondDayFragment secondDayFragment;
    ThirdDayFragment thirdDayFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position,false);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);
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
                return true;
            case R.id.action_fahrenheit:
                Toast.makeText(this, "Home fahrenheit Click", Toast.LENGTH_SHORT).show();
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
        adapter.addFragment(secondDayFragment,getDayFromCalendar(0).toUpperCase());
        adapter.addFragment(firstDayFragment,getDayFromCalendar(1).toUpperCase());
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
}
