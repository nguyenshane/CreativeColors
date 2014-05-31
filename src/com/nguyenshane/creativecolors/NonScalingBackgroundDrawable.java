package com.nguyenshane.creativecolors;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;

public class NonScalingBackgroundDrawable extends Drawable {

	int padding_top = 0;
	int padding_bottom = 0;

	int padding_left = 0;
	int padding_right = 0;
	
	Context context;
	View view;
	Drawable hosted_drawable; 
	public NonScalingBackgroundDrawable(Context c, View v, int resource) {
		context = c;
		view = v;
		hosted_drawable = context.getResources().getDrawable(resource);
	}
	
	public void draw(Canvas canvas) {

		int w = hosted_drawable.getIntrinsicWidth();
		int h = hosted_drawable.getIntrinsicHeight();

		int view_w = view.getWidth();
		int view_h = view.getHeight();

		int padded_horizontal_room = view_w - (padding_left + padding_right);
		int padded_vertical_room = view_h - (padding_top + padding_bottom);
		
		float scale;
		float intrinsic_aspect_ratio = w / (float) h;
		float padded_canvas_aspect_ratio = padded_horizontal_room / (float) padded_vertical_room;
		if (intrinsic_aspect_ratio > padded_canvas_aspect_ratio)
			// Our source image is wider than the canvas, so we scale by width.
			scale = padded_horizontal_room / (float) w;
		else
			scale = padded_vertical_room / (float) h;
		
		int scaled_width = (int) (scale*w);
		int scaled_height = (int) (scale*h);

		// Here we fit the image into the bottom-right corner.
		//int left = view_w - scaled_width - padding_right;
		//int top = view_h - scaled_height - padding_bottom;
		int left = view_w - scaled_width - padding_right;
		int top = view_h/2 - scaled_height/2; // + scaled_height - padding_bottom ;
		int right = view_w - padding_right;
		int bottom = view_h/2 + scaled_height/2 - padding_bottom;

		hosted_drawable.setBounds(
			left,
			top,
			right,
			bottom
		);
		
		hosted_drawable.draw(canvas);
	}

	public int getOpacity() {
		return hosted_drawable.getOpacity();
	}

	public void setAlpha(int alpha) {
		hosted_drawable.setAlpha(alpha);
	}

	public void setColorFilter(ColorFilter cf) {
		hosted_drawable.setColorFilter(cf);
	}
}