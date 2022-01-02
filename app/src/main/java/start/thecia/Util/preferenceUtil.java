package start.thecia.Util;

import android.content.SharedPreferences;

/**
 * Created by temp on 4/17/2017.
 */

public class preferenceUtil {


    public static void addPref (SharedPreferences.Editor mShared, Boolean aBool){
        mShared.putBoolean("SetConfig", aBool);
        mShared.commit();
    }
    public static void addPref (SharedPreferences.Editor mShared, Boolean aBool,String a){
        mShared.putBoolean(a, aBool);
        mShared.commit();
    }
    public static void addPrefString (SharedPreferences.Editor mShared, String a){
        mShared.putString("theConfig", a);
        mShared.commit();
    }
    public static void addPrefInt (SharedPreferences.Editor mShared,int b, String a){
        mShared.putInt(a,b);
        mShared.commit();
    }
}

