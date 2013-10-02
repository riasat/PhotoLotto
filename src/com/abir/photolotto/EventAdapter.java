package com.abir.photolotto;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventAdapter extends ArrayAdapter<EventModel> {
	private List<EventModel>	mListItems;
	private Activity			mContext;
	public ImageLoader			imageLoader;

	static class ViewHolder {
		public ImageView	imageViewEvent;
		public TextView		textViewHeading;
		public TextView		textViewDescription;
		public TextView		textViewDescription2;
	}

	public EventAdapter(Activity context, List<EventModel> list) {
		super(context, R.layout.row_layout, list);
		this.mListItems = list;
		this.mContext = context;
		imageLoader = new ImageLoader(context.getApplicationContext());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = null;
		if (convertView == null) {
			LayoutInflater inflater = mContext.getLayoutInflater();
			rowView = inflater.inflate(R.layout.row_layout, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.imageViewEvent = (ImageView) rowView.findViewById(R.id.imageViewEvent);
			viewHolder.textViewHeading = (TextView) rowView.findViewById(R.id.textViewHeading);
			viewHolder.textViewDescription = (TextView) rowView.findViewById(R.id.textViewDescription);
			viewHolder.textViewDescription2 = (TextView) rowView.findViewById(R.id.textViewDescription2);
			
			rowView.setTag(viewHolder);
		} else {
			rowView = convertView;
		}
		ViewHolder holder = (ViewHolder) rowView.getTag();
		imageLoader.displayImage(mListItems.get(position).getsImgThumb(), holder.imageViewEvent);
		holder.textViewHeading.setText(mListItems.get(position).getsName());
		holder.textViewDescription.setText(mListItems.get(position).getsShortDescription1());
		holder.textViewDescription2.setText(mListItems.get(position).getsShortDescription2());
		return rowView;
	}
}
