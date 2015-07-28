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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.view.View.OnClickListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SelfyListAdapter extends BaseAdapter {

	static final String SELFY_VIEW_IMAGE_TAG = "SELFY_VIEW_IMAGE_TAG";
	static final String SELFY_VIEW_TEXT_TAG = "SELFY_VIEW_TEXT_TAG";
	
	private final List<Selfy> mItems = new ArrayList<Selfy>();
	private final Context mContext;

	public SelfyListAdapter(Context context) {
		mContext = context;
	}

	public void add(Selfy item) {
		mItems.add(item);
		notifyDataSetChanged();
	}

	public void clear() {
		mItems.clear();
		notifyDataSetChanged();
	}
	
	private void showSelfy(byte[] image) {
		Intent intent = new Intent(mContext, ViewSelfyActivity.class);		
		intent.putExtra(SELFY_VIEW_IMAGE_TAG, image);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int pos) {
		return mItems.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final Selfy selfyItem = (Selfy)getItem(position);


		LinearLayout itemLayout = null;
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
					(Context.LAYOUT_INFLATER_SERVICE);
			itemLayout = (LinearLayout)inflater.inflate(R.layout.selfy_item, parent, false);
		} else {
			itemLayout = (LinearLayout) convertView;
		}

		final ImageView image_view = (ImageView)itemLayout.findViewById(R.id.selfy_image);
		image_view.setImageBitmap(selfyItem.getImage());
		TextView text = (TextView)itemLayout.findViewById(R.id.selfy_date_created);
		text.setText(selfyItem.getDateCreated());
		
		itemLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
            	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
            	Bitmap img = ((BitmapDrawable)image_view.getDrawable()).getBitmap();
            	if (null != img) {
            		img.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
            		showSelfy(baos.toByteArray());
            	} else {
            		Toast.makeText(mContext, "There is no image, sorry!", Toast.LENGTH_SHORT).show();
            	}
            }
        });

		return itemLayout;
	}	
}
