package com.avoscloud.sns;

import com.avos.avoscloud.AVOSCloud;

import android.app.Application;

public class SNSApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    AVOSCloud.initialize(this, "gqd0m4ytyttvluk1tnn0unlvmdg8h4gxsa2ga159nwp85fks",
        "7gd2zom3ht3vx6jkcmaamm1p2pkrn8hdye2pn4qjcwux1hl1");

  }
}
