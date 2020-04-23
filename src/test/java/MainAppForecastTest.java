import TestResources.JSONSample;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MainAppForecastTest {

    @Spy
    @InjectMocks
    private MainAppForecast mainAppForecast;
    @Mock
    private HttpService httpService;
    @Captor
    private ArgumentCaptor<String> argumentCaptor;

    @Test
    public void connectionByCityNameShouldInvokeGetWeatherMethod() {
        // given
        given(httpService.connect(anyString())).willReturn(JSONSample.forecastSample);
        doNothing().when(mainAppForecast).getWeatherData(anyString());

        // when
        mainAppForecast.connectByCityName("Warszawa");

        // then
        verify(mainAppForecast).getWeatherData(any(String.class));
    }

    @Test
    public void connectionByCityNameShouldAppendProperString() {
        // given
        given(httpService.connect(anyString())).willReturn(JSONSample.forecastSample);
        doNothing().when(mainAppForecast).getWeatherData(anyString());

        // when
        mainAppForecast.connectByCityName("Warszawa");

        // then
        then(httpService).should().connect(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue(), containsString("q=Warszawa"));

    }

    @Test
    public void connectionByZipCodeShouldInvokeGetWeatherMethod() {
        // given
        given(httpService.connect(anyString())).willReturn(JSONSample.forecastSample);
        doNothing().when(mainAppForecast).getWeatherData(anyString());

        // when
        mainAppForecast.connectByZipCode("43-512", "pl");

        // then
        verify(mainAppForecast).getWeatherData(any(String.class));
    }

    @Test
    public void connectionByZipCodeShouldAppendProperString() {
        // given
        given(httpService.connect(anyString())).willReturn(JSONSample.forecastSample);
        doNothing().when(mainAppForecast).getWeatherData(anyString());

        // when
        mainAppForecast.connectByZipCode("43-512", "pl");

        // then
        then(httpService).should().connect(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue(), containsString("zip=43-512,pl"));
    }

    @Test
    public void connectionByCityIdShouldInvokeGetWeatherMethod() {
        // given
        given(httpService.connect(anyString())).willReturn(JSONSample.forecastSample);
        doNothing().when(mainAppForecast).getWeatherData(anyString());

        // when
        mainAppForecast.connectByCityId("2643743");

        // then
        verify(mainAppForecast).getWeatherData(any(String.class));
    }

    @Test
    public void connectionByCityIdShouldAppendProperString() {
        // given
        given(httpService.connect(anyString())).willReturn(JSONSample.forecastSample);
        doNothing().when(mainAppForecast).getWeatherData(anyString());

        // when
        mainAppForecast.connectByCityId("2643743");

        // then
        then(httpService).should().connect(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue(), containsString("id=2643743"));
    }

    @Test
    public void connectionByCoordinatesShouldInvokeGetWeatherMethod() {
        // given
        given(httpService.connect(anyString())).willReturn(JSONSample.forecastSample);
        doNothing().when(mainAppForecast).getWeatherData(anyString());

        // when
        mainAppForecast.connectByCoordinates("33", "34");

        // then
        verify(mainAppForecast).getWeatherData(any(String.class));
    }

    @Test
    public void connectionByCoordinatesShouldAppendProperString() {
        // given
        given(httpService.connect(anyString())).willReturn(JSONSample.forecastSample);
        doNothing().when(mainAppForecast).getWeatherData(anyString());

        // when
        mainAppForecast.connectByCoordinates("33", "34");

        // then
        then(httpService).should().connect(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue(), containsString("lat=33"));
        assertThat(argumentCaptor.getValue(), containsString("lon=34"));
    }

    @Test
    public void connectionWithDaysNumberShouldInvokeParsingProperNumberOfTimes() {
        // given
        given(httpService.connect(anyString())).willReturn(JSONSample.forecastSample);
        doNothing().when(mainAppForecast).parseJSONWeather(any(JSONObject.class));
        // when
        mainAppForecast.connectByCityName("Warszawa", 2);
        // then
        verify(mainAppForecast, times(2*8+1)).parseJSONWeather(any(JSONObject.class));
    }
}