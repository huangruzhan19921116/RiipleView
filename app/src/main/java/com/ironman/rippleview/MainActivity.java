package com.ironman.rippleview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.ironman.rippleview.view.RippleView;

public class MainActivity extends Activity {

  private RippleView mRippleView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.content_main);
    mRippleView = (RippleView) findViewById(R.id.view_ripple);
  }

  public void start(View v) {
    mRippleView.rippleStart();
  }

  public void stop(View v) {
    mRippleView.rippleStop();
  }
}
