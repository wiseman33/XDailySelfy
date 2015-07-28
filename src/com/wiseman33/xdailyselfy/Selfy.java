/**
 * This file is part of XDailySelfy.
 *
 *   Foobar is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Foobar is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *    
 *    Danil Knysh, 2015
 */
package com.wiseman33.xdailyselfy;

import android.graphics.Bitmap;

public class Selfy {
	private Bitmap image;
	private String dateCreated;
	
	public Selfy(Bitmap image, String dateCreated) {
		this.image = image;
		this.dateCreated = dateCreated;
	}
	
	public Bitmap getImage() {
		return image;
	}
	
	public String getDateCreated() {
		return dateCreated;
	}
}