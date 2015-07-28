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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

public class PreviewAddPhotoDialog extends DialogFragment {
	
	public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
	
	private Bitmap selfy;
	NoticeDialogListener mListener;
	
	public PreviewAddPhotoDialog() {
		
	}
	
	public PreviewAddPhotoDialog(Bitmap selfy) {
		this.selfy = selfy;
	}
	 
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

		View dialog = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_photo, null);
        
        ImageView image = (ImageView) dialog.findViewById(R.id.preview_image);
        image.setImageBitmap(selfy);
        
        DisplayMetrics display = getActivity().getResources().getDisplayMetrics(); 
        image.getLayoutParams().width = display.widthPixels;  
        
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());        
        builder
        .setView(dialog)
        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            	mListener.onDialogPositiveClick(PreviewAddPhotoDialog.this);
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {          
            	mListener.onDialogNegativeClick(PreviewAddPhotoDialog.this);
            }
        })       
        .setTitle(R.string.save_preview_dialog_title); 
        
        // Create the AlertDialog object and return it
        return builder.create();
    }	
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

}