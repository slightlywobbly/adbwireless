/**
 * siir.es.adbWireless.adbWireless.java
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/

package siir.es.adbWireless;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class adbWireless extends Activity {

	public static final String PORT = "5555";
	public static final boolean USB_DEBUG = false;

	public static boolean mState = false;
	public static boolean wifiState;

	private TextView tv_footer_1;
	private TextView tv_footer_2;
	private TextView tv_footer_3;
	private ImageView iv_button;

	private Toast toastBack;

	public static RemoteViews remoteViews = new RemoteViews("siir.es.adbWireless", R.layout.adb_appwidget);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		this.iv_button = (ImageView) findViewById(R.id.iv_button);
		this.tv_footer_1 = (TextView) findViewById(R.id.tv_footer_1);
		this.tv_footer_2 = (TextView) findViewById(R.id.tv_footer_2);
		this.tv_footer_3 = (TextView) findViewById(R.id.tv_footer_3);

		if (Utils.mNotificationManager == null) {
			Utils.mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		}

		if (!Utils.hasRootPermission()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.no_root)).setCancelable(true)
					.setPositiveButton(getString(R.string.button_close), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							adbWireless.this.finish();
						}
					});
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.create();
			builder.setTitle(R.string.no_root_title);
			builder.show();
		}

		if (!Utils.checkWifiState(this)) {
			wifiState = false;
			Utils.saveWiFiState(this, wifiState);

			if (Utils.prefsWiFiOn(this)) {
				Utils.enableWiFi(this, true);
			} else {
				Utils.WiFidialog(this);
			}
		} else {
			wifiState = true;
			Utils.saveWiFiState(this, wifiState);
		}

		this.iv_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				if (Utils.prefsHaptic(adbWireless.this))
					vib.vibrate(45);

				try {
					if (!mState) {
						Utils.adbStart(adbWireless.this);
					} else {
						Utils.adbStop(adbWireless.this);
					}
					updateState();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences settings = getSharedPreferences("wireless", 0);
		mState = settings.getBoolean("mState", false);
		wifiState = settings.getBoolean("wifiState", false);
		updateState();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Debug.log("onPause()");
	}

	@Override
	protected void onDestroy() {
		try {
			Utils.adbStop(this);
		} catch (Exception e) {
		}
		try {
			Utils.mNotificationManager.cancelAll();
		} catch (Exception e) {
		}
		/*
		 * try { if(Utils.mWakeLock != null) { Utils.mWakeLock.release(); } }
		 * catch (Exception e) { }
		 */
		try {
			if (Utils.prefsWiFiOff(this) && !wifiState && Utils.checkWifiState(this)) {
				Utils.enableWiFi(this, false);
			}
		} catch (Exception e) {
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_prefs:
			Intent i = new Intent(this, ManagePreferences.class);
			startActivityForResult(i, Utils.ACTIVITY_SETTINGS);
			break;
		case R.id.menu_about:
			this.showHelpDialog();
			return true;
		case R.id.menu_exit:
			adbWireless.this.finish();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * onBackPressed() requires Android 2.0, API 5 (ECLAIR)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Do nothing, force them to hit the home button so it does not
			// close the application.
			if (toastBack == null || toastBack.getView().getWindowVisibility() != View.VISIBLE) {
				toastBack = Toast.makeText(this, R.string.exit_info, Toast.LENGTH_LONG);
				toastBack.show();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void updateState() {
		if (mState) {
			tv_footer_1.setText(R.string.footer_text_1);
			try {
				tv_footer_2.setText("adb connect " + Utils.getWifiIp(this));
			} catch (Exception e) {
				tv_footer_2.setText("adb connect ?");
			}
			tv_footer_2.setVisibility(View.VISIBLE);
			tv_footer_3.setVisibility(View.VISIBLE);
			iv_button.setImageResource(R.drawable.bt_on);
		} else {
			tv_footer_1.setText(R.string.footer_text_off);
			tv_footer_2.setVisibility(View.INVISIBLE);
			tv_footer_3.setVisibility(View.INVISIBLE);
			iv_button.setImageResource(R.drawable.bt_off);
		}
	}

	private void showHelpDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.about)).setCancelable(true)
				.setPositiveButton(getString(R.string.button_close), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		builder.setIcon(R.drawable.ic_launcher);
		builder.create();
		builder.setTitle(R.string.app_name);
		builder.show();
	}

}