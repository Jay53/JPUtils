package methods.all.com.jputils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class JPUtils {

    private static String TAG = "JPUtils";

    public static void showToast_LONG(Context con, String string) {
        Toast.makeText(con, string, Toast.LENGTH_LONG).show();
    }

    public static void showToast_SHORT(Context con, String string) {
        Toast.makeText(con, string, Toast.LENGTH_SHORT).show();
    }

    public static boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    public static void hideKeyboard(Activity con) {
        // Check if no view has focus
        try {
            con.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            View view = con.getCurrentFocus();
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) con
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void storeStateOfFloat(SharedPreferences mediaPrefs,
                                         String prefsKeys, float prefsValue) {
        SharedPreferences.Editor prefEditor = mediaPrefs.edit();
        prefEditor.putFloat(prefsKeys, prefsValue);
        prefEditor.commit();
    }

    public static void storeStateOfString(SharedPreferences mediaPrefs,
                                          String prefsKeys, String prefsValue) {
        SharedPreferences.Editor prefEditor = mediaPrefs.edit();
        prefEditor.putString(prefsKeys, prefsValue);
        prefEditor.commit();
    }

    public static void storeStateOfLong(SharedPreferences mediaPrefs,
                                        String prefsKeys, long prefsValue) {
        SharedPreferences.Editor prefEditor = mediaPrefs.edit();
        prefEditor.putLong(prefsKeys, prefsValue);
        prefEditor.commit();
    }

    public static void storeStateOfBoolean(SharedPreferences mediaPrefs,
                                           String prefsKeys, boolean prefsValue) {
        SharedPreferences.Editor prefEditor = mediaPrefs.edit();
        prefEditor.putBoolean(prefsKeys, prefsValue);
        prefEditor.commit();
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.i(TAG, "PackageName=" + context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "KeyHash=" + key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e(TAG, "Name not found" + e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "No such an algorithm" + e.toString());
        } catch (Exception e) {
            Log.e(TAG, "Exception" + e.toString());
        }
        return key;
    }

    public static void sendGmail(Activity con, String subject, String message) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            sendMailAbove22(con, subject, message);
        } else {
            // do something for phones running an SDK before lollipop
            sendMailBelow22(con, subject, message);
        }
    }

    private static void sendMailAbove22(Activity con, String subject, String message) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/html");
        final PackageManager pm = con.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        String className = null;
        for (final ResolveInfo info : matches) {
            if (info.activityInfo.packageName.equals("com.google.android.gm")) {
                className = info.activityInfo.name;
                if (className != null && !className.isEmpty()) {
                    break;
                }
            }
        }
        emailIntent.setClassName("com.google.android.gm", className);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        con.startActivity(emailIntent);
    }

    private static void sendMailBelow22(Activity con, String subject, String message) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        con.startActivity(Intent.createChooser(intent, "Send mail..."));
    }

    public static String getBase64(String imagePath) {
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, baos); //bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return encodedImage;
    }
}
