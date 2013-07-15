package com.NewPower.Shake;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.NewPower.MyApp.R;

public class Preference {

	public static final String KEY_THEME = "theme";
	
    private static SharedPreferences.Editor editor = null;
    private static SharedPreferences preferences = null;
    
	public Preference(Context context) {
	}
    private static SharedPreferences.Editor getEditor(Context paramContext) {
        if (editor == null)
            editor = PreferenceManager.getDefaultSharedPreferences(paramContext).edit();
        return editor;
    }

    private static SharedPreferences getSharedPreferences(Context paramContext) {
        if (preferences == null)
            preferences = PreferenceManager.getDefaultSharedPreferences(paramContext);
        return preferences;
    }
    public static int getTheme(Context context) {
        return Preference.getSharedPreferences(context).getInt(KEY_THEME, R.style.AppTheme_Default);
    }

    public static void setTheme(Context context, int theme) {
        getEditor(context).putInt(KEY_THEME, theme).commit();
    }
}
