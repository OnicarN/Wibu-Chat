package com.example.wibuchat100;

import android.app.Application;

import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;

public class MyApplication extends Application {

    public static final long APP_ID = 1543817125L;
    public static final String APP_SIGN = "3ee8e5eb34fe5f9bb1496c76bc348c596074c76753769995899cf41ea76cb058";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void iniciarZegoParaUsuario(Application app, String userId, String userName) {
        ZegoUIKitPrebuiltCallInvitationConfig config =
                new ZegoUIKitPrebuiltCallInvitationConfig();

        ZegoUIKitPrebuiltCallService.init(app, APP_ID, APP_SIGN, userId, userName, config);
    }
}