import com.google.gson.reflect.TypeToken;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class TemperatureUtil {

    // 设置信号量大小
    private final Semaphore semaphore = new Semaphore(100);

    private final HttpClientUtil httpClientUtil = new HttpClientUtil();

    public TemperatureUtil() {

    }

    /**
     * 获取温度
     * 因为题目强制要求返回 Optional<Integer> 类型，所以一旦发生异常，返回 Optional.empty()，不区分错误类型
     */
    public Optional<Integer> getTemperature(String province, String city, String county) {
        try {
            semaphore.acquire(); // 尝试获取信号量，当获取不到时线程进入block状态

            // 参数校验
            if (!argsCheck(province, city, county)) return Optional.empty();

            // 1. 获取 province code
            Optional<String> provinceCode = getProvinceCode(province.trim());
            if (!provinceCode.isPresent()) return Optional.empty();
            // 2. 获取 city code
            Optional<String> cityCode = getCityCode(city.trim(), provinceCode.get());
            if (!cityCode.isPresent()) return Optional.empty();
            // 3. 获取 county code
            Optional<String> countyCode = getCountyCode(county.trim(), provinceCode.get() + cityCode.get());
            if (!countyCode.isPresent()) return Optional.empty();
            // 4. 获取天气
            return getWeatherInfo(provinceCode.get() + cityCode.get() + countyCode.get());

        } catch (InterruptedException e) {
            e.printStackTrace();
            return Optional.empty();
        } finally {
            // 释放信号量
            semaphore.release();
        }
    }

    /**
     * 基本参数校验，没有引入工具类，只能手写
     */
    private boolean argsCheck(String... args) {
        for (String arg : args) {
            if (null == arg || arg.isEmpty()) return false;
        }
        return true;
    }

    /**
     * 获取中文位置与数字的映射
     */
    private Map<String, String> getLocationResponse(HttpGet httpGet) throws IOException {
        CloseableHttpResponse response = httpClientUtil.getCloseableHttpClient().execute(httpGet);
        String body = EntityUtils.toString(response.getEntity(), "utf-8");
        return GsonUtil.toObject(body, new TypeToken<Map<String, String>>() {
                })
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    /**
     * 通过中文的 province 名称获取对应的 code
     */
    private Optional<String> getProvinceCode(String province) {
        HttpGet httpGet = new HttpGet(getProvinceApi());
        try {
            Map<String, String> map = getLocationResponse(httpGet);
            return Optional.ofNullable(map.get(province));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * 通过 中文的 city 名称 和 所属 provinceCode 获取对应的 city code
     */
    private Optional<String> getCityCode(String city, String provinceCode) {
        HttpGet httpGet = new HttpGet(getCityApi(provinceCode));
        try {
            Map<String, String> map = getLocationResponse(httpGet);
            return Optional.ofNullable(map.get(city));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * 通过 中文的 county 名称 和 所属 city code 获取对应的 county code
     */
    private Optional<String> getCountyCode(String county, String cityCode) {
        HttpGet httpGet = new HttpGet(getCountyApi(cityCode));
        try {
            Map<String, String> map = getLocationResponse(httpGet);
            return Optional.ofNullable(map.get(county));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * 通过 code 获取 当地天气状况
     */
    private Optional<Integer> getWeatherInfo(String code) {
        HttpGet httpGet = new HttpGet(getWeatherApi(code));
        try {
            CloseableHttpResponse response = httpClientUtil.getCloseableHttpClient().execute(httpGet);
            String body = EntityUtils.toString(response.getEntity(), "utf-8");
            Weather weather = GsonUtil.toObject(body, Weather.class);
            return Optional.of(Double.valueOf(weather.getWeatherinfo().temp).intValue());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


    private static String getProvinceApi() {
        return "http://www.weather.com.cn/data/city3jdata/china.html";
    }

    private static String getCityApi(String provinceCode) {
        return "http://www.weather.com.cn/data/city3jdata/provshi/" + provinceCode + ".html";
    }

    private static String getCountyApi(String cityCode) {
        return "http://www.weather.com.cn/data/city3jdata/station/" + cityCode + ".html";
    }

    private static String getWeatherApi(String code) {
        return "http://www.weather.com.cn/data/sk/" + code + ".html";
    }

}
