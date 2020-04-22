import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpService {

    public String connect(String url) {

        StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection connection;
        BufferedReader reader;
        String line = "";
        try {
            System.out.println("Opening " + url);
            connection = (HttpURLConnection) new URL(url).openConnection();
            reader = new BufferedReader(new InputStreamReader(
                    connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream()
            ));
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
        return stringBuilder.toString();
    }
}
