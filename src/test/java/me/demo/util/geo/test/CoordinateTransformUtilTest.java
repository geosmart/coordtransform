package me.demo.util.geo.test;

import com.github.ipcjs.coordtransform.CoordinateTransformUtil;

import org.junit.Test;

/**
 * 坐标转换-测试用例
 */
public class CoordinateTransformUtilTest {

    @Test
    public void test_bd09ToWgs84() {
        // 百度坐标转WGS84
        double[] wgs84 = CoordinateTransformUtil.bd09ToWgs84(120.698967, 31.324966);
        System.out.println(wgs84[0] + "," + wgs84[1]);
    }

    @Test
    public void test_wgs84ToBd09() {
        // WGS84转百度坐标
        double[] bd09 = CoordinateTransformUtil.wgs84ToBd09(118.3013077, 32.2719040);
        System.out.println(bd09[0] + "," + bd09[1]);
    }
}
