package barqsoft.footballscores.service;

import android.content.Intent;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.WidgetDataProvider;

/**
 * Created by Andreas on 28.09.2015.
 * Source: http://dharmangsoni.blogspot.no/2014/03/collection-widget-with-event-handling.html
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        WidgetDataProvider dataProvider = new WidgetDataProvider(getApplicationContext(), intent);
        return dataProvider;
    }
}
