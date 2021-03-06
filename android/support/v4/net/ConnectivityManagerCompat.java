package android.support.v4.net;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.app.NotificationCompat.WearableExtender;
import com.ghenry22.substream2.C0173R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.apache.cordova.PluginResult;
import org.apache.cordova.file.FileUtils;

public final class ConnectivityManagerCompat {
    private static final ConnectivityManagerCompatImpl IMPL;
    public static final int RESTRICT_BACKGROUND_STATUS_DISABLED = 1;
    public static final int RESTRICT_BACKGROUND_STATUS_ENABLED = 3;
    public static final int RESTRICT_BACKGROUND_STATUS_WHITELISTED = 2;

    interface ConnectivityManagerCompatImpl {
        int getRestrictBackgroundStatus(ConnectivityManager connectivityManager);

        boolean isActiveNetworkMetered(ConnectivityManager connectivityManager);
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RestrictBackgroundStatus {
    }

    static class ConnectivityManagerCompatBaseImpl implements ConnectivityManagerCompatImpl {
        ConnectivityManagerCompatBaseImpl() {
        }

        public boolean isActiveNetworkMetered(ConnectivityManager cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null) {
                return true;
            }
            switch (info.getType()) {
                case FileUtils.ACTION_GET_FILE /*0*/:
                case ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_WHITELISTED /*2*/:
                case ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_ENABLED /*3*/:
                case FileUtils.READ /*4*/:
                case WearableExtender.SIZE_FULL_SCREEN /*5*/:
                case FragmentManagerImpl.ANIM_STYLE_FADE_EXIT /*6*/:
                    return true;
                case ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_DISABLED /*1*/:
                case PluginResult.MESSAGE_TYPE_BINARYSTRING /*7*/:
                case C0173R.styleable.Toolbar_contentInsetStartWithNavigation /*9*/:
                    return false;
                default:
                    return true;
            }
        }

        public int getRestrictBackgroundStatus(ConnectivityManager cm) {
            return ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_ENABLED;
        }
    }

    @RequiresApi(16)
    static class ConnectivityManagerCompatApi16Impl extends ConnectivityManagerCompatBaseImpl {
        ConnectivityManagerCompatApi16Impl() {
        }

        public boolean isActiveNetworkMetered(ConnectivityManager cm) {
            return cm.isActiveNetworkMetered();
        }
    }

    @RequiresApi(24)
    static class ConnectivityManagerCompatApi24Impl extends ConnectivityManagerCompatApi16Impl {
        ConnectivityManagerCompatApi24Impl() {
        }

        public int getRestrictBackgroundStatus(ConnectivityManager cm) {
            return cm.getRestrictBackgroundStatus();
        }
    }

    static {
        if (VERSION.SDK_INT >= 24) {
            IMPL = new ConnectivityManagerCompatApi24Impl();
        } else if (VERSION.SDK_INT >= 16) {
            IMPL = new ConnectivityManagerCompatApi16Impl();
        } else {
            IMPL = new ConnectivityManagerCompatBaseImpl();
        }
    }

    public static boolean isActiveNetworkMetered(ConnectivityManager cm) {
        return IMPL.isActiveNetworkMetered(cm);
    }

    public static NetworkInfo getNetworkInfoFromBroadcast(ConnectivityManager cm, Intent intent) {
        NetworkInfo info = (NetworkInfo) intent.getParcelableExtra("networkInfo");
        if (info != null) {
            return cm.getNetworkInfo(info.getType());
        }
        return null;
    }

    public static int getRestrictBackgroundStatus(ConnectivityManager cm) {
        return IMPL.getRestrictBackgroundStatus(cm);
    }

    private ConnectivityManagerCompat() {
    }
}
