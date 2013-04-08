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

import android.util.Log;

public class Debug {

	private static final String TAG = "adbWireless";

	public static void log(String msg) {
		Log.d(TAG, msg);
	}

	public static void error(String msg, Exception e) {
		Log.e(TAG, msg, e);
	}

	public static void info(String msg) {
		Log.i(TAG, msg);
	}

	public static void warn(String msg) {
		Log.w(TAG, msg);
	}
}