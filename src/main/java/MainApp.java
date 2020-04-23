import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

public class MainApp implements Runnable {

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
                "\n4 - Położenie geograficzne");
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
            default:
                System.out.println("Zły parametr");
                startApp();
        }
    }

    public void connectByCityName(String cityName) {
        String cityAppend = "q=" + cityName;
        String source = httpService.connect(Config.APP_URL + "?" + cityAppend + "&" + keyAppend + "&" + unitsAppend + "&" + langAppend);
        if (source == null) {
            System.out.println("Błąd połączenia.");
            return;
        }
        getWeatherData(source);
    }

    public void connectByZipCode(String zipCode, String countryCode) {
        String zipAppend = "zip=" + zipCode + "," + countryCode;
        String source = httpService.connect(Config.APP_URL + "?" + zipAppend + "&" + keyAppend + "&" + unitsAppend + "&" + langAppend);

        if (source == null) {
            System.out.println("Błąd połączenia.");
            return;
        }
        getWeatherData(source);
    }

    public void connectByCityId(String id) {
        String cityIdAppend = "id=" + id;
        String source = httpService.connect(Config.APP_URL + "?" + cityIdAppend + "&" + keyAppend + "&" + unitsAppend + "&" + langAppend);

        if (source == null) {
            System.out.println("Błąd połączenia.");
            return;
        }
        getWeatherData(source);
    }

    public void connectByCoordinates(String latitude, String longitude) {
        String appendLatitude = "lat=" + latitude;
        String appendLongitude = "lon=" + longitude;
        String source = httpService.connect(Config.APP_URL + "?" + appendLatitude + "&" + appendLongitude + "&" + keyAppend + "&" + unitsAppend + "&" + langAppend);

        if (source == null) {
            System.out.println("Błąd połączenia.");
            return;
        }
        getWeatherData(source);
    }

    @Override
    public void run() {
        startApp();
    }

    public void getWeatherData(String source) {
        JSONObject jsonObject = new JSONObject(source);
        if (jsonObject.has("main")) {
            JSONObject jsonTemperature = jsonObject.getJSONObject("main");

            int visibility;
            Double temp = Double.valueOf(jsonTemperature.get("temp").toString());
            Double tempMin = Double.valueOf(jsonTemperature.get("temp_min").toString());
            Double tempMax = Double.valueOf(jsonTemperature.get("temp_max").toString());
            try {
                visibility = jsonObject.getInt("visibility");
            } catch (JSONException e) {
                visibility = -1;
            }
            String cityName = jsonObject.get("name").toString();
            String clouds = jsonObject.getJSONObject("clouds").get("all").toString();
            int windSpeed = jsonObject.getJSONObject("wind").getInt("speed");
            String pressure = jsonTemperature.get("pressure").toString();
            String description = jsonObject.getJSONArray("weather").getJSONObject(0).get("description").toString();

            System.out.println("Pogoda w " + cityName + "\n"
                    + "Temperatura: " + temp + "\n"
                    + "Temperatura maksymalna: " + tempMax + "\n"
                    + "Temperatura średnia: " + String.format("%.2f", (tempMax + tempMin) / 2).replace(",", ".") + "\n"
                    + "Widoczność: " + visibility + "m" + "\n"
                    + "Zachmurzenie: " + clouds + "%" + "\n"
                    + "Prędkość wiatru: " + windSpeed + "m/s" + "\n"
                    + "Ciśnienie: " + pressure + "HPa" + "\n"
                    + "Opis: " + description);
        } else {
            System.out.println(jsonObject.get("message"));
        }
    }
}
