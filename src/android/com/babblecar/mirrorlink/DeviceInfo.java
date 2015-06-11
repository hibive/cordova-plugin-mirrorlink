package com.babblecar.mirrorlink;

import android.os.Bundle;
import android.os.RemoteException;
import com.mirrorlink.android.commonapi.IDeviceInfoListener;
import com.mirrorlink.android.commonapi.IDeviceInfoManager;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

public class DeviceInfo extends AbstractMirrorLinkPlugin {

    private volatile IDeviceInfoManager mDeviceInfoManager = null;

    private CallbackContext callbackOnDeviceInfoChanged = null;

    private final IDeviceInfoListener mDeviceInfoListener = new IDeviceInfoListener.Stub() {
        @Override
        public void onDeviceInfoChanged(Bundle clientInformation) throws RemoteException {
            if(callbackOnDeviceInfoChanged!=null) {
                PluginResult result = new PluginResult(PluginResult.Status.OK, BundleToJSONObject(clientInformation));
                result.setKeepCallback(true);
                callbackOnDeviceInfoChanged.sendPluginResult(result);
            }
        }
    };

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if(!isconnected) {
            callbackContext.error("service is not connected");
            return false;
        }

        if("onDeviceInfoChanged".equals(action)) {
            callbackOnDeviceInfoChanged = callbackContext;
        }else if("getMirrorLinkClientInformation".equals(action)) {
            try {
                callbackContext.success(BundleToJSONObject(getDeviceInfoManager().getMirrorLinkClientInformation()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else if("getServerVirtualKeyboardSupport".equals(action)) {
            try {
                callbackContext.success(BundleToJSONObject(getDeviceInfoManager().getServerVirtualKeyboardSupport()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else if("getMirrorLinkSessionVersionMajor".equals(action)) {
            try {
                callbackContext.success(getDeviceInfoManager().getMirrorLinkSessionVersionMajor());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else if("getMirrorLinkSessionVersionMinor".equals(action)) {
            try {
                callbackContext.success(getDeviceInfoManager().getMirrorLinkSessionVersionMinor());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            callbackContext.error("AlertPlugin." + action + " not found !");
            return false;
        }

        return true;
    }

    protected IDeviceInfoManager getDeviceInfoManager() {
        if (mDeviceInfoManager == null) {
            try {
                mDeviceInfoManager = mCommonAPI.getDeviceInfoManager(activity.getPackageName(), mDeviceInfoListener);
            } catch (RemoteException e) {
                mDeviceInfoManager = null;
            }
        }

        return mDeviceInfoManager;
    }
}