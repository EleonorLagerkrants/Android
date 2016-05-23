package dv606.el222ja.assignment1;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BeerActivity extends FragmentActivity {

    MyPageAdapter pageAdapter;
    public static List<Fragment> fragmentList = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer);

        List<Fragment> fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        ViewPager pager = (ViewPager) findViewById(R.id.beerPager);
        pager.setAdapter(pageAdapter);

    }

    /*
    In this method a string is created by calling the readFromFile method. The String is then split
    into a stringarray where a new beer starts (they are sepereated by the tag <beer>).
    The strings in the array is the split into a new array. The two first rows contain beer number
    and the name of the image that belongs to the beer. The rest is then added to a new string.
    A method, getDrawableID, is called to get the ID of the drawable object. Beer number, image path
    and the information about the beer is used to create a new BeerFragment that is the added to the list.
     */

    private List<Fragment> getFragments() {

        String fileText = readFromFile();


        String[] beerList = fileText.split(("<beer>"));

        for(String items : beerList) {
            String[] lines = items.split("\n");
            String beerNumber = lines[0];
            String imageName = lines[1];

            int imageID = getDrawableId(imageName);

            String[] beerArray = Arrays.copyOfRange(lines,2, lines.length);
            StringBuilder sb = new StringBuilder();
            for(String string : beerArray) {
                sb.append(string);
                sb.append("\n");
                sb.append("\n");
            }
            String beer = sb.toString();

            fragmentList.add(BeerFragment.newInstance(beer, imageID, beerNumber));
        }

        return fragmentList;
    }

    /*
    Method that returns the int value of the size of the fList
     */
    public static int getListSize() {
        return fragmentList.size();
    }

    /*
    Reads the content of the "beer_data.txt" file, which contains a list of all the different beers
    that should be added to the list
    */

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = getResources().openRawResource(getResources().getIdentifier("beer_data", "raw", getPackageName()));

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                    stringBuilder.append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        return ret;
    }

    /*
    Method that returns the id value of a drawable object. The input parameter is the name of the image
    which is present in the raw text file.
     */

    public int getDrawableId(String name) {
        try{
            return getResources().getIdentifier(name, "drawable", getPackageName());
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    /*
    My custom adapter class
    */

    class MyPageAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position)  {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }

}
