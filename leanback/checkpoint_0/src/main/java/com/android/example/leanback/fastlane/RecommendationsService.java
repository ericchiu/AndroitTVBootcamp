package com.android.example.leanback.fastlane;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.android.example.leanback.PlayerActivity;
import com.android.example.leanback.R;
import com.android.example.leanback.data.Video;
import com.android.example.leanback.data.VideoDataManager;
import com.android.example.leanback.data.VideoItemContract;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by eric on 19/11/14.
 */
public class RecommendationsService extends IntentService {
    private static final String TAG = "RecommendationsService";
    private static final int MAX_RECOMMENDATIONS = 3;
    public static final String EXTRA_BACKGROUND_IMAGE_URL = "background_image_url";
    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;
    private NotificationManager mNotificationManager;

    public RecommendationsService() {
        super("RecommendationsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ContentProviderClient client = getContentResolver().acquireContentProviderClient(VideoItemContract.VideoItem.buildDirUri());
        try {
            Cursor cursor = client.query(VideoItemContract.VideoItem.buildDirUri(), VideoDataManager.PROJECTION, null, null, VideoItemContract.VideoItem.DEFAULT_SORT);

            VideoDataManager.VideoItemMapper mapper = new VideoDataManager.VideoItemMapper();
            mapper.bindColumns(cursor);

            mNotificationManager = (NotificationManager) getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            int count = 1;
            while (cursor.moveToNext() && count <= MAX_RECOMMENDATIONS) {
                Video video = mapper.bind(cursor);
                PendingIntent pendingIntent = buildPendingIntent(video);
                Bundle extras = new Bundle();
                extras.putString(EXTRA_BACKGROUND_IMAGE_URL, video.getThumbUrl());
                count++;
                Bitmap image = Picasso.with(getApplicationContext())
                        .load(video.getThumbUrl())
                        .resize(VideoDetailsFragment.dpToPx(DETAIL_THUMB_WIDTH, getApplicationContext()), VideoDetailsFragment.dpToPx(DETAIL_THUMB_WIDTH, getApplicationContext()))
                        .get();

                Notification notification = new NotificationCompat.BigPictureStyle(
                        new NotificationCompat.Builder(getApplicationContext())
                                .setContentTitle(video.getTitle())
                                .setContentText(video.getDescription())
                                .setPriority(4)
                                .setLocalOnly(true)
                                .setOngoing(true)
                                .setColor(getApplicationContext().getResources().getColor(R.color.primary))
                                .setCategory(Notification.CATEGORY_RECOMMENDATION)
                                .setCategory("recommendation")
                                .setLargeIcon(image)
                                .setSmallIcon(R.drawable.ic_stat_f)
                                .setContentIntent(pendingIntent)
                                .setExtras(extras))
                        .build();
                mNotificationManager.notify(count, notification);
            }
            cursor.close();
        } catch (RemoteException re) {

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mNotificationManager = null;
        }
    }

    private PendingIntent buildPendingIntent(Video video) {
        Intent detailsIntent = new Intent(this, PlayerActivity.class);
        detailsIntent.putExtra(Video.INTENT_EXTRA_VIDEO, video);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(VideoDetailsActivity.class);
        stackBuilder.addNextIntent(detailsIntent);
        // Ensure a unique PendingIntents, otherwise all recommendations end up with the same
        // PendingIntent
        detailsIntent.setAction(Long.toString(video.getId()));

        PendingIntent intent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        return intent;
    }
}
