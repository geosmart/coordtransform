package me.demo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.github.ipcjs.coordtransform.CoordinateTransformUtil;

import java.util.ArrayList;

/**
 * Created by ipcjs on 2017/9/29.
 */

public class MainActivity extends AppCompatActivity {

    public static final CoordinateTransformUtil.Rectangle[] RECTANGLES = {
            new CoordinateTransformUtil.Rectangle(72.004, 0.8293, 137.8347, 55.8271)
    };

    public static final String TAG_MAP = "map";
    private static final String TAG = "MainActivity";

    private BaiduMap mBaiduMap;

    private SupportMapFragment mMapFragment;
    private Overlay mDotMarker = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();

        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.GCJ02);

        mMapFragment = (SupportMapFragment) fm.findFragmentByTag(TAG_MAP);
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance(new BaiduMapOptions()
                    .mapStatus(new MapStatus.Builder().zoom(2).build())
            );
            fm.beginTransaction()
                    .add(android.R.id.content, mMapFragment, TAG_MAP)
                    .commitNow(); // 立即提交
        }
        getWindow().getDecorView().post(this::initMap); // 下个事件循环中才能读取到MapView
    }

    private void initMap() {
        mBaiduMap = mMapFragment.getBaiduMap();
        drawRectangle(CoordinateTransformUtil.REGION, Color.BLUE); // 属于中国的范围
        drawRectangle(CoordinateTransformUtil.EXCLUDE, Color.RED); // 不属于中国的范围
        drawRectangle(RECTANGLES, Color.YELLOW); // 属于中国的范围

        mBaiduMap.setOnMapLoadedCallback(() -> {
            LatLngBounds.Builder boundBuilder = new LatLngBounds.Builder();
            for (CoordinateTransformUtil.Rectangle rect : RECTANGLES) {
                boundBuilder.include(new LatLng(rect.north, rect.west));
                boundBuilder.include(new LatLng(rect.south, rect.east));
            }
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(boundBuilder.build()));
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mDotMarker != null) {
                    mDotMarker.remove();
                }
                Log.i(TAG, "add dot on " + latLng.toString());
                mDotMarker = mBaiduMap.addOverlay(new MarkerOptions()
                        .position(latLng)
                        .anchor(0.5f, 0.5f)
                        .title(latLng.toString())
                        .icon(createDotIcon())
                );
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        mBaiduMap.setOnMarkerClickListener(marker -> {
            if (marker.getTitle() != null) {
                mBaiduMap.showInfoWindow(new InfoWindow(createTextIcon(marker.getTitle(), Color.BLACK), marker.getPosition(), 0, () -> {
                    Log.i(TAG, "infoWindow clicked");
                }));
                return true;
            }
            return false;
        });
    }

    @NonNull
    private BitmapDescriptor createDotIcon() {
        Bitmap dot = Bitmap.createBitmap(9, 9, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(dot);
        c.drawColor(Color.RED);
        return BitmapDescriptorFactory.fromBitmap(dot);
    }

    private BitmapDescriptor createTextIcon(CharSequence text, int textColor) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(textColor);
        return BitmapDescriptorFactory.fromView(view);
    }

    private void drawRectangle(CoordinateTransformUtil.Rectangle[] rectangles, int color) {
        for (int i = 0, rectanglesLength = rectangles.length; i < rectanglesLength; i++) {
            CoordinateTransformUtil.Rectangle r = rectangles[i];
            ArrayList<LatLng> points = new ArrayList<>();
            points.add(new LatLng(r.north, r.west));
            points.add(new LatLng(r.north, r.east));
            points.add(new LatLng(r.south, r.east));
            points.add(new LatLng(r.south, r.west));
            points.add(points.get(0)); // 闭合 线
            mBaiduMap.addOverlay(new PolylineOptions().points(points).color(color));
            mBaiduMap.addOverlay(new MarkerOptions().position(points.get(0)).icon(createTextIcon(String.valueOf(i), color)));
        }
    }
}
