package com.web2wave;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Web2WaveWebListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Web2Wave.getInstance().initWith("your api key");

        Map<String, Object> status = Web2Wave.getInstance().fetchSubscriptionStatus("your user id");
        System.out.println("W2W_status:" + status.toString());

        boolean activeSubs = Web2Wave.getInstance().hasActiveSubscription("your user id");
        System.out.println("W2W_activeSubs: " + activeSubs);

        List<Map<String, Object>> subscriptions = Web2Wave.getInstance().fetchSubscriptions("your user id");
        System.out.println("W2W_subs: " + subscriptions);

        Result<Boolean> updateResult = Web2Wave.getInstance().updateUserProperty(
                "your user id",
                "age",
                "11"
        );

        System.out.println("W2W_update_result: " + updateResult);

        Map<String, String> userProp = Web2Wave.getInstance().fetchUserProperties("your user id");
        System.out.println("W2W_properties: " + userProp);

        Result<Boolean> cancelResult = Web2Wave.getInstance().cancelSubscription(
                "pay system id",
                "no_money"
        );
        System.out.println("W2W_cancel_result: " + cancelResult);

        Result<Boolean> chargeResult = Web2Wave.getInstance().chargeUser("your user id", 22057);
        System.out.println("W2W_charge_result: " + chargeResult);


        Web2Wave.showWebView(

                getSupportFragmentManager(),
                "https://app.web2wave.com/",
                this, 0, 0
        );
    }

    @Override
    public void onQuizFinished(Map<String, Object> data) {
        System.out.println("W2W_on_quiz_finished_" + data.toString());
    }

    @Override
    public void onClose(Map<String, Object> data) {
        System.out.println("W2W_on_close_" + data.toString());
    }

    @Override
    public void onEvent(String event, Map<String, Object> data) {
        System.out.println("W2W_on_event_" + event + " " + data.toString());
    }
}
