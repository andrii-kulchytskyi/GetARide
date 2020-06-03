package com.example.getaride;
import com.parse.Parse;
import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("jzDU1epiUAqFgtbr5CP7DwGvtoUPP1fQZuInRxwL")
                // if defined
                .clientKey("nVqAdRWKyxKY63hyFuzgc28jY4b7ciKiG6wX6tJh")
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}