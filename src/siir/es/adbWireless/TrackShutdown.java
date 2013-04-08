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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class TrackShutdown extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		SharedPreferences settings = context.getSharedPreferences("wireless", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("mState", false);
		editor.commit();

		if (Utils.prefsOnBoot(context)) {
			
			if (!Utils.hasRootPermission()) {
				Toast.makeText(context, R.string.no_root, Toast.LENGTH_LONG).show();
				return;
			}

			if (!Utils.checkWifiState(context)) {
				adbWireless.wifiState = false;
				Utils.saveWiFiState(context, adbWireless.wifiState);

				if (Utils.prefsWiFiOn(context)) {
					Utils.enableWiFi(context, true);
				} else {
					Toast.makeText(context, R.string.no_wifi, Toast.LENGTH_LONG).show();
					return;
				}
			} else {
				adbWireless.wifiState = true;
				Utils.saveWiFiState(context, adbWireless.wifiState);
			}

			try {
				Utils.adbStart(context);
			} catch (Exception e) {
				Debug.error("call adbStart() ERROR ********", e);
			}
			
		}

	}
}