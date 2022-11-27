package com.example.album.data.livedata;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

/**
 * @param <T> the type of the value
 */
abstract class ContentProviderLiveData<T> extends MutableLiveData<T> {

    private final Context context;
    private final Uri uri;


    public ContentProviderLiveData(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
    }

    private ContentObserver observer;

    @Override
    protected void onActive() {
        observer = new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange) {
                postValue(getContentProviderValue());
                Log.d("TAG", "onChange: called");
            }
        };
        context.getContentResolver().registerContentObserver(uri, true, observer);
    }

    @Override
    protected void onInactive() {
        context.getContentResolver().unregisterContentObserver(observer);
    }

    abstract T getContentProviderValue();
}
