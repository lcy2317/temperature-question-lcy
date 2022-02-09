import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class TestCase {

    private TemperatureUtil temperatureUtil;

    @Before
    public void before() {
        temperatureUtil = new TemperatureUtil();
    }

    @Test
    public void getTemperatureTest_0() {
        String province = "吉林";
        String city = "延边";
        String county = "安图";
        Optional<Integer> res = temperatureUtil.getTemperature(province, city, county);
        Assert.assertNotEquals(res, Optional.empty());
    }

    @Test
    public void getTemperatureTest_1() {
        String province = "@@@##";
        String city = "";
        String county = null;
        Optional<Integer> res = temperatureUtil.getTemperature(province, city, county);
        Assert.assertEquals(res, Optional.empty());
    }
}
