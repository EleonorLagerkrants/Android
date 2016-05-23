package dv606.el222ja.assignment1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Eleonor on 2016-04-01.
 *
 * My custom Fragment class
 */
public class BeerFragment extends Fragment {

    public static final String BEER_MESSAGE = "EXTRA_MESSAGE";
    public static final String BEER_NAME = "BEER_NAME";
    public static final String BEER_NUMBER = "BEER_NUMBER";

    public static final BeerFragment newInstance(String message, int beerID, String beerNumber) {
        BeerFragment bf = new BeerFragment();
        Bundle bdl = new Bundle();
        bdl.putString(BEER_MESSAGE, message);
        bdl.putInt(BEER_NAME, beerID);
        bdl.putString(BEER_NUMBER, beerNumber);
        bf.setArguments(bdl);
        return bf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String message = getArguments().getString(BEER_MESSAGE);
        String number = getArguments().getString(BEER_NUMBER);
        int name = getArguments().getInt(BEER_NAME);

        String listSize = String.valueOf(BeerActivity.getListSize());

        View v = inflater.inflate(R.layout.beer_fragment, container, false);
        TextView titleText = (TextView)v.findViewById(R.id.beerTitle);
        titleText.setText(getString(R.string.beerTitle, number, listSize));

        ImageView beerImage = (ImageView)v.findViewById(R.id.beerImage);
        beerImage.setImageResource(name);

        TextView beerText = (TextView)v.findViewById(R.id.fragmentText);
        beerText.setText(message);
        return v;

        }

    }


