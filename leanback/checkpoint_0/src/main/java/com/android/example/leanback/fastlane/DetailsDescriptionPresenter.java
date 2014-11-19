package com.android.example.leanback.fastlane;

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.util.Log;

import com.android.example.leanback.data.Video;

/**
 * Created by eric on 19/11/14.
 */
public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {
    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object o) {
        Video video = (Video) o;

        if (video != null) {
            Log.d("DetailsDescriptionPresenter", String.format("%s, %s, %s", video.getTitle(), video.getThumbUrl(), video.getDescription()));
            viewHolder.getTitle().setText(video.getTitle());
            viewHolder.getSubtitle().setText(String.valueOf(video.getRating()));
            viewHolder.getBody().setText(video.getDescription());
        }
    }

}
