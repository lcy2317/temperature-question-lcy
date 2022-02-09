public class Weather {

    private WeatherInfo weatherinfo;

    public Weather(WeatherInfo weatherinfo) {
        this.weatherinfo = weatherinfo;
    }

    public WeatherInfo getWeatherinfo() {
        return weatherinfo;
    }

    public void setWeatherinfo(WeatherInfo weatherinfo) {
        this.weatherinfo = weatherinfo;
    }

    static class WeatherInfo {
        String city;
        String cityid;
        String temp;
        String WD;
        String WS;
        String SD;
        String AP;
        String njd;
        String WSE;
        String time;
        String sm;
        Integer isRadar;
        String Radar;
    }
}

