package io.github.aoemerson.crimesmvp.model;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.github.aoemerson.crimesmvp.model.data.Crime;
import io.github.aoemerson.crimesmvp.util.FileUtils;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PoliceClientTests {

    public static final String BASE_URL = "/";
    @Mock PoliceClient.OnCrimesLoadedListener crimesLoadedListener;
    private MockWebServer mockWebServer;
    private PoliceRestClient policeRestClient;
    private PoliceClientImpl policeClient;
    private CountDownLatch countDownLatch;
    private String testUrl;

    @Before
    public void setup() throws IOException {
        setupMockWebServer();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .build();

        policeRestClient = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(testUrl)
                .build().create(PoliceRestClient.class);

        policeClient = new PoliceClientImpl(policeRestClient);
        countDownLatch = new CountDownLatch(1);


    }

    private void setupMockWebServer() {
        mockWebServer = new MockWebServer();
        testUrl = mockWebServer.url(BASE_URL).toString();
        Answer answer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                countDownLatch.countDown();
                return null;
            }
        };

        doAnswer(answer).when(crimesLoadedListener)
                        .onCrimesLoadComplete(Mockito.anyListOf(Crime.class));
        doAnswer(answer).when(crimesLoadedListener)
                        .onCrimesLoadError(Mockito.any(Throwable.class));
        doAnswer(answer).when(crimesLoadedListener).onServerError(anyString());
        doAnswer(answer).when(crimesLoadedListener).onOtherError(anyString());
        doAnswer(answer).when(crimesLoadedListener).onUserError(anyString());
        doAnswer(answer).when(crimesLoadedListener).onTooManyRequests();
        doAnswer(answer).when(crimesLoadedListener).onReadTimeOut(any(Throwable.class));
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void shouldCallbackOnSuccessfulResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("content-type", "application/json")
                .setBody(FileUtils.loadResourceAsString("test1Crimes.json")));


        policeClient.requestCrimesByPoint(55d, 0d, crimesLoadedListener);
        countDownLatch.await();

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(crimesLoadedListener, Mockito.times(1))
               .onCrimesLoadComplete(captor.capture());
        assertThat(captor.getValue().size(), CoreMatchers.is(2));
    }


    @Test
    public void shouldhandleServerError() throws Exception {
        String errMsg = "Server Error";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody(errMsg));

        policeClient.requestCrimesByPoint(55d, 0d, crimesLoadedListener);
        countDownLatch.await();
        Mockito.verify(crimesLoadedListener, Mockito.times(1))
               .onServerError(Matchers.eq("500: " + errMsg));
    }

    @Test
    public void shouldhandleUserError() throws Exception {
        String errMsg = "Not found";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody(errMsg));

        policeClient.requestCrimesByPoint(55d, 0d, crimesLoadedListener);
        countDownLatch.await();
        Mockito.verify(crimesLoadedListener, Mockito.times(1))
               .onUserError(Matchers.eq("404: " + errMsg));
    }

    @Test
    public void shouldhandleTooManyRequests() throws Exception {
        String errMsg = "Too many requests";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(429)
                .setBody(errMsg));

        policeClient.requestCrimesByPoint(55d, 0d, crimesLoadedListener);
        countDownLatch.await();
        Mockito.verify(crimesLoadedListener, Mockito.times(1)).onTooManyRequests();
    }

    @Test
    public void shouldhandleRedirect() throws Exception {
        String errMsg = "Redirect";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(301)
                .setHeader("Location", mockWebServer.url("/redirect"))
                .setBody(errMsg));
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("content-type", "application/json")
                .setBody(FileUtils.loadResourceAsString("test1Crimes.json")));

        policeClient.requestCrimesByPoint(55d, 0d, crimesLoadedListener);
        countDownLatch.await();

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(crimesLoadedListener, Mockito.times(1))
               .onCrimesLoadComplete(captor.capture());
        assertThat(captor.getValue().size(), CoreMatchers.is(2));
    }

    @Test
    public void shouldHandleReadTimeout() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBodyDelay(2200, TimeUnit.MILLISECONDS)
                .setHeader("content-type", "application/json")
                .setBody(FileUtils.loadResourceAsString("test1Crimes.json")));

        policeClient.requestCrimesByPoint(55d, 0d, crimesLoadedListener);
        countDownLatch.await();
        verify(crimesLoadedListener, times(1)).onReadTimeOut(any(SocketTimeoutException.class));
    }

    @Test
    public void shouldHandleSlowConnection() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .throttleBody(200, 2050, TimeUnit.MILLISECONDS)
                .setHeader("content-type", "application/json")
                .setBody(FileUtils.loadResourceAsString("test1Crimes.json")));
        policeClient.requestCrimesByPoint(55d, 0d, crimesLoadedListener);
        countDownLatch.await();
        verify(crimesLoadedListener, times(1)).onCrimesLoadError(any(Throwable.class));
    }


    @Test
    public void shouldHandleOtherError() throws Exception {
        String errorMsg = "Some weird other error";
        mockWebServer.enqueue(new MockResponse()
                .setBody(errorMsg)
                .setResponseCode(360));

        policeClient.requestCrimesByPoint(55d, 0d, crimesLoadedListener);
        countDownLatch.await();
        verify(crimesLoadedListener, times(1)).onOtherError(eq("360: " + errorMsg));
    }

    @Test
    public void shouldHandleConnectionTimeout() throws Exception {
        mockWebServer.shutdown();
        policeClient.requestCrimesByPoint(55d, 0d, crimesLoadedListener);
        countDownLatch.await();
        verify(crimesLoadedListener, times(1)).onReadTimeOut(any(SocketTimeoutException.class));
    }


}
