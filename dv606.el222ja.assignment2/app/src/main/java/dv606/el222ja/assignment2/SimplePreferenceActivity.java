package dv606.el222ja.assignment2;

import android.preference.PreferenceActivity;
import java.util.List;

public class SimplePreferenceActivity extends PreferenceActivity{

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_header, target);

    }
    @Override
    protected boolean isValidFragment (String fragmentName) {
        return (SimplePreferenceFragment.class.getName().equals(fragmentName));
    }
}
