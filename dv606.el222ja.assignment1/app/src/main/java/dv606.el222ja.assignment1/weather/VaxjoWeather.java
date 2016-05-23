/**
 * VaxjoWeather.java
 * Created: May 9, 2010
 * Jonas Lundberg, LnU
 */

package dv606.el222ja.assignment1.weather;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import dv606.el222ja.assignment1.R;

/**
 * This is a first prototype for a weather app. It is currently 
 * only downloading weather data for Växjö. 
 * 
 * This activity downloads weather data and constructs a WeatherReport,
 * a data structure containing weather data for a number of periods ahead.
 * 
 * The WeatherHandler is a SAX parser for the weather reports 
 * (forecast.xml) produced by www.yr.no. The handler constructs
 * a WeatherReport containing meta data for a given location
 * (e.g. city, country, last updated, next update) and a sequence 
 * of WeatherForecasts.
 * Each WeatherForecast represents a forecast (weather, rain, wind, etc)
 * for a given time period.
 * 
 * The next task is to construct a list based GUI where each row 
 * displays the weather data for a single period.
 * 
 *  
 * @author jlnmsi
 *
 */

public class VaxjoWeather extends AppCompatActivity {
	public static String TAG = "dv606.el222ja.assignment1.weather";
	ArrayList<Object> weatherList = new ArrayList<>();
	private InputStream input;
	private WeatherReport report = null;
	IconicAdapter adapter;
	ListView listView;

	private Cursor cursor;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize the layout
		setContentView(R.layout.weather_main);
		// Initialize the toolbar
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		listView = (ListView) findViewById(R.id.weatherList);
		adapter = new IconicAdapter();
		listView.setAdapter(adapter);

		try {
			URL url = new URL("http://www.yr.no/sted/Sverige/Kronoberg/V%E4xj%F6/forecast.xml");
			AsyncTask task = new WeatherRetriever().execute(url);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.weather_menu, menu);
		return true;
	}

	private void printReportToLog() {
		if (this.report != null) {

            /*Print some meta data to the UI for the testing purposes*/
			//TextView placeholder = (TextView) findViewById(R.id.placeholder);
			//placeholder.append(" " + report.getLastUpdated());

        	/* Print location meta data */
			Log.i(TAG, report.toString());

        	/* Print forecasts */
			int count = 0;
			for (WeatherForecast forecast : report) {
				count++;
				Log.i(TAG, "Forecast #" + count);
				Log.i(TAG, forecast.toString());
				//System.out.println("Forecast "+count);
				//System.out.println( forecast.toString() );
			}
		} else {
			Log.e(TAG, "Weather report has not been loaded.");
		}
	}

	/*
	Calls the method arrayContains to check if the date is present in the arraylist. If it is not
	a new HeaderItem is created and added to the arraylist. The method also adds the forecast to
	the array.
	 */

	private void printResultToList() {
		for (WeatherForecast forecast : report) {
			String date = forecast.getStartYYMMDD();
			HeaderItem header = new HeaderItem(date);
			if (arrayContains(date)) {
				adapter.add(forecast);
			} else {
				adapter.add(header);
				System.out.print(header.getDate());
				adapter.add(forecast);
			}
		}
	}

	/*
	 Method that checks if the date of a forecast is already present in the array
	 */
	public boolean arrayContains(String date) {
		for (Object o : weatherList) {
			if (o instanceof HeaderItem) {
				HeaderItem hi = (HeaderItem) o;
				if (date.equals(hi.getDate())) {
					return true;
				}
			}
		}
		return false;
	}

	private class WeatherRetriever extends AsyncTask<URL, Void, WeatherReport> {
		protected WeatherReport doInBackground(URL... urls) {
			try {
				return WeatherHandler.getWeatherReport(urls[0]);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		protected void onProgressUpdate(Void... progress) {
		}

		protected void onPostExecute(WeatherReport result) {
			Toast.makeText(getApplicationContext(), "WeatherRetriever task finished", Toast.LENGTH_LONG).show();


			report = result;

			printReportToLog();
			printResultToList();

		}
	}

	/*
	Custom adapter for arraylist
	 */
	class IconicAdapter extends ArrayAdapter {
		String startTime;
		String endTime;
		String weatherName;
		String tempInt;
		String rain;
		String windSpeed;

		IconicAdapter() {
			super(VaxjoWeather.this, R.layout.row_layout, weatherList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = getLayoutInflater();
			Object o = weatherList.get(position);
			/*
				Checks if the object is a HeaderItem. If it is, a new header is created in the list
			 */
			if (o instanceof HeaderItem) {
				HeaderItem hi = (HeaderItem) o;

				convertView = inflater.inflate(R.layout.list_section, null);

				TextView sectionText = (TextView) convertView.findViewById(R.id.sectionHeader);
				sectionText.setText(hi.getDate());


			}
			/*
			If the object is not a HeaderItem, a forecast row in the list is created
			 */
			else {
				convertView = inflater.inflate(R.layout.row_layout, null);

				WeatherForecast forecast = (WeatherForecast) o;
				TextView time = (TextView) convertView.findViewById(R.id.timeText);
				startTime = forecast.getStartHHMM();
				endTime = forecast.getEndHHMM();
				time.setText(getString(R.string.time, startTime, endTime));
				time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.clock, 0, 0, 0);

				TextView temp = (TextView) convertView.findViewById(R.id.tempText);
				tempInt = String.valueOf(forecast.getTemperature());
				temp.setText(getString(R.string.celsius, tempInt));
				temp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.temp, 0, 0, 0);

				TextView weather = (TextView) convertView.findViewById(R.id.weatherText);
				weather.setCompoundDrawablesWithIntrinsicBounds(getWeatherSymbol(forecast), 0, 0, 0);
				weather.setText(weatherName);
				getWeatherSymbol(forecast);

				TextView downpour = (TextView) convertView.findViewById(R.id.pourText);
				rain = String.valueOf(forecast.getRain());
				downpour.setText(getString(R.string.pour, rain));
				downpour.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pour, 0, 0, 0);

				TextView speed = (TextView) convertView.findViewById(R.id.speedText);
				windSpeed = String.valueOf(forecast.getWindSpeed());
				speed.setText(getString(R.string.speed, windSpeed));
				speed.setCompoundDrawablesWithIntrinsicBounds(getWindIcon(forecast),0,0,0);

			}
			return convertView;
		}

		public int getWeatherSymbol(WeatherForecast forecast) {
			if (forecast.getWeatherCode() >= 1 && forecast.getWeatherCode() <= 3) {
				weatherName = getString(R.string.sunny);
				return R.drawable.sun;
			} else if (forecast.getWeatherCode() == 4) {
				weatherName = (getString(R.string.cloud));
				return R.drawable.cloud;
			} else {
				weatherName = getString(R.string.rain);
				return R.drawable.rain;
			}
		}

		public int getWindIcon(WeatherForecast forecast) {
			if(forecast.getWindDirection().equals("N") || forecast.getWindDirection().equals("NNW") || forecast.getWindDirection().equals("NNE")) {
				return  R.drawable.north;
			}
			else if(forecast.getWindDirection().equals("S") || forecast.getWindDirection().equals("SSW") || forecast.getWindDirection().equals("SSE")) {
				return R.drawable.south;
			}
			else if(forecast.getWindDirection().equals("SE")) {
				return R.drawable.southeast;
			}
			else if(forecast.getWindDirection().equals("SW")) {
				return R.drawable.southwest;
			}
			else if(forecast.getWindDirection().equals("E")) {
				return R.drawable.east;
			}
			else if(forecast.getWindDirection().equals("NE")) {
				return R.drawable.northeast;
			}
			else if(forecast.getWindDirection().equals("NW")) {
				return R.drawable.northwest;
			}
			else
				return R.drawable.west;
		}
	}
}