package me.demo.util.geo.test;

import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;

import static com.github.ipcjs.coordtransform.CoordinateTransformUtil.bd09ToGcj02;
import static com.github.ipcjs.coordtransform.CoordinateTransformUtil.bd09ToWgs84;
import static com.github.ipcjs.coordtransform.CoordinateTransformUtil.gcj02ToBd09;
import static com.github.ipcjs.coordtransform.CoordinateTransformUtil.gcj02ToWgs84;
import static com.github.ipcjs.coordtransform.CoordinateTransformUtil.wgs84ToBd09;
import static com.github.ipcjs.coordtransform.CoordinateTransformUtil.wgs84ToGcj02;

/**
 * Created by ipcjs on 2017/9/28.
 */

public class CoordinateConverterTest {

    public static void p(Object... objs) {
        System.out.println(Arrays.deepToString(objs));
    }

    public static String toUrl(LatLng location) {
        String coordTypeStr;
        switch (location.getCoordType()) {
            default:
            case WGS84:
                coordTypeStr = "wgs84";
                break;
            case GCJ02:
                coordTypeStr = "gcj02";
                break;
            case BD09:
                coordTypeStr = "bd09ll";
                break;
        }
        return String.format(Locale.getDefault(),
                "http://api.map.baidu.com/marker?location=%1$s,%2$s&title=%3$s&content=beijing&output=html&coord_type=%4$s&src=test",
                location.getLat(),
                location.getLng(),
                location.getCoordType(),
                coordTypeStr);
    }

    @Test
    public void testWgs84AndGcj02() {
        // wgs84 桃园路
        double lat = 22.535697;
        double lng = 113.915547;

        p("wgs84 <-> gcj02");
        double[] gcj02 = wgs84ToGcj02(lng, lat);
        double[] wgs84 = gcj02ToWgs84(gcj02[0], gcj02[1]);
        p(toUrl(LatLng.from(CoordType.WGS84, lat, lng)));
        p(toUrl(LatLng.from(CoordType.GCJ02, gcj02[1], gcj02[0]))); // 没有偏移
        p(toUrl(LatLng.from(CoordType.WGS84, wgs84[1], wgs84[0]))); // 没有偏移
    }

    @Test
    public void testWgs84AndBd09() {
        // wgs84 桃园路
        double lat = 22.535697;
        double lng = 113.915547;

        p("wgs84 <-> bd09");
        double[] bd09_1 = wgs84ToBd09(lng, lat);
        double[] wgs84_4 = bd09ToWgs84(bd09_1[0], bd09_1[1]);
        p(toUrl(LatLng.from(CoordType.WGS84, lat, lng)));
        p(toUrl(LatLng.from(CoordType.BD09, bd09_1[1], bd09_1[0]))); // 没有偏移
        p(toUrl(LatLng.from(CoordType.WGS84, wgs84_4[1], wgs84_4[0]))); // 没有偏移
    }

    @Test
    public void testBd09AndGcj02() {
        // bd09ll 116.403959,39.915132, 天安门
        double lat = 39.915132;
        double lng = 116.403959;

        p("bd09 <-> gcj02");
        double[] gcj02_2 = bd09ToGcj02(lng, lat);
        double[] bd09_3 = gcj02ToBd09(gcj02_2[0], gcj02_2[1]);
        p(toUrl(LatLng.from(CoordType.BD09, lat, lng)));
        p(toUrl(LatLng.from(CoordType.GCJ02, gcj02_2[1], gcj02_2[0]))); // 没有偏移
        p(toUrl(LatLng.from(CoordType.BD09, bd09_3[1], bd09_3[0]))); // 没有偏移
    }

    private enum CoordType {
        BD09, GCJ02, WGS84
    }

    private static class LatLng {

        private final CoordType coordType;

        private final double lat;

        private final double lng;

        public LatLng(CoordType coordType, double lat, double lng) {
            this.coordType = coordType;
            this.lat = lat;
            this.lng = lng;
        }

        public static LatLng from(CoordType coordType, double lat, double lnt) {
            return new LatLng(coordType, lat, lnt);
        }

        public CoordType getCoordType() {
            return coordType;
        }

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }
}
