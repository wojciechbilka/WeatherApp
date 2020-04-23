import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MainAppForecast implements Runnable{

    private Scanner scanner;
    private HttpService httpService = new HttpService();
    private String keyAppend = "appid=" + Config.APP_ID;
    private String unitsAppend = "units=" + Config.UNITS;
    private String langAppend = "lang=" + Config.LANG;


    private void startApp() {
        scanner = new Scanner(System.in);
        System.out.println("Wybierz rodzaj parametru określającego miejsce, dla którego wyświetlisz pogodę " +
                "\n0 - Zakończ działanie " +
                "\n1 - Nazwa Miasta" +
                "\n2 - Kod pocztowy " +
                "\n3 - Identyfikator miasta" +
                "\n4 - Położenie geograficzne" +
                "\n5 - Nazwa miasta i liczba dni (max 5 dni)");
        Integer name = scanner.nextInt();
        chooseTypeSearching(name);
    }

    private void chooseTypeSearching(Integer typeNumber) {
        switch (typeNumber) {
            case 0:
                break;
            case 1:
                System.out.println("Podaj nazwę miasta");
                String city = scanner.next();
                connectByCityName(city);
                startApp();
                break;
            case 2:
                System.out.println("Podaj kod pocztowy miasta");
                String zipCode = scanner.next();
                System.out.println("Podaj kod państwa (Polska -->pl)");
                String countryCode = scanner.next();
                connectByZipCode(zipCode, countryCode);
                startApp();
                break;
            case 3:
                System.out.println("Podaj identyfikator miasta");
                String id = scanner.next();
                connectByCityId(id);
                startApp();
                break;
            case 4:
                System.out.println("Podaj szerokość geograficzną (0-90)");
                String latitude = Double.valueOf(scanner.nextDouble()).toString();
                System.out.println("Podaj długość geograficzną (0-180)");
                String longitude =  Double.valueOf(scanner.nextDouble()).toString();
                connectByCoordinates(latitude, longitude);
                startApp();
                break;
            case 5:
                System.out.println("Podaj nazwę miasta");
                city = scanner.next();
                System.out.println("Podaj liczbę dni");
                int days = scanner.nextInt();
                connectByCityName(city, days);
                startApp();
                break;
            default:
                System.out.println("Zły parametr");
                startApp();
        }
    }

    public void connectByCityName(String city) {
        String cityAppend = "q=" + city;
        String source = httpService.connect(Config.APP_LONG_TERM_URL + "?" + cityAppend + "&" + keyAppend + "&" + unitsAppend + "&" + langAppend);

        if (source == null) {
            System.out.println("Błąd połączenia.");
            return;
        }
        getWeatherData(source);
    }

    public void connectByCityName(String city, int days) {
        String cityAppend = "q=" + city;
        String source = httpService.connect(Config.APP_LONG_TERM_URL + "?" + cityAppend + "&" + keyAppend + "&" + unitsAppend + "&" + langAppend);

        if (source == null) {
            System.out.println("Błąd połączenia.");
            return;
        }
        getWeatherData(source, days);
    }

    public void connectByZipCode(String zipCode, String countryCode) {
        String zipAppend = "zip=" + zipCode + "," + countryCode;
        String source = httpService.connect(Config.APP_LONG_TERM_URL + "?" + zipAppend + "&" + keyAppend + "&" + unitsAppend + "&" + langAppend);

        if (source == null) {
            System.out.println("Błąd połączenia.");
            return;
        }
        getWeatherData(source);
    }

    public void connectByCityId(String id) {
        String cityIdAppend = "id=" + id;
        String source = httpService.connect(Config.APP_LONG_TERM_URL + "?" + cityIdAppend + "&" + keyAppend + "&" + unitsAppend + "&" + langAppend);

        if (source == null) {
            System.out.println("Błąd połączenia.");
            return;
        }
        getWeatherData(source);
    }

    public void connectByCoordinates(String latitude, String longitude) {
        String appendLatitude = "lat=" + latitude;
        String appendLongitude = "lon=" + longitude;
        String source = httpService.connect(Config.APP_LONG_TERM_URL + "?" + appendLatitude + "&" + appendLongitude + "&" + keyAppend + "&" + unitsAppend + "&" + langAppend);

        if (source == null) {
            System.out.println("Błąd połączenia.");
            return;
        }
        getWeatherData(source);
    }

    public void getWeatherData(String json) {
        JSONObject weatherData = new JSONObject(json);
        JSONObject cityData = weatherData.getJSONObject("city");
        String cityName = cityData.getString("name");
        System.out.println("Pogoda w " + cityName + "\n");

        JSONArray forecastData = weatherData.getJSONArray("list");
        for(int i = 0; i < forecastData.length(); i++) {
            parseJSONWeather((JSONObject) forecastData.get(i));
        }
    }

    public void getWeatherData(String json, int days) {
        JSONObject weatherData = new JSONObject(json);
        JSONObject cityData = weatherData.getJSONObject("city");
        String cityName = cityData.getString("name");
        System.out.println("Pogoda w " + cityName + "\n");

        JSONArray forecastData = weatherData.getJSONArray("list");
        int size = 8 * days + 1;
        if(size > forecastData.length()) {
            size = forecastData.length();
        }
        for(int i = 0; i < size; i++) {
            parseJSONWeather((JSONObject) forecastData.get(i));
        }
    }


    public void parseJSONWeather(JSONObject jsonObject) {

        JSONObject tempData = jsonObject.getJSONObject("main");
        int clouds = jsonObject.getJSONObject("clouds").getInt("all");
        double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
        double temperature = tempData.getDouble("temp");
        double temperatureMax = tempData.getDouble("temp_max");
        double temperatureMin = tempData.getDouble("temp_min");
        int pressure = tempData.getInt("pressure");
        long date = jsonObject.getInt("dt");
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(date, 0 , ZoneOffset.UTC);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String dateTimeString = dateTime.format(dateTimeFormatter);
        String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

        System.out.println("Data i godzina: " + String.format("%20s",dateTimeString) + "\t"
                + "Temperatura: " + String.format("%5s",temperature) + "\t"
                + "Temperatura maksymalna: " + String.format("%5s",temperatureMax) + "\t"
                + "Temperatura średnia: " + String.format("%5s", String.format("%3.2f", (temperatureMax + temperatureMin) / 2).replace(",", ".")) + "\t"
                + "Zachmurzenie: " + String.format("%2s",clouds) + "%" + "\t"
                + "Prędkość wiatru: " + String.format("%5s",windSpeed) + "m/s" + "\t"
                + "Ciśnienie: " + String.format("%4s", pressure) + "HPa" + "\t"
                + "Opis: " + description);
    }

    @Override
    public void run() {
        startApp();
    }
}
