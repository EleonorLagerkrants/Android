package dv606.el222ja.assignment2;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Eleonor on 2016-04-14.
 */
public class SimplePreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.simple_prefs);

    }
}
