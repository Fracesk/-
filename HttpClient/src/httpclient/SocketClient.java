package httpclient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SocketClient {
    private static final String GET_URL = "http://47.115.44.145:7000/api/Test/test1";
    private static final String GET_URL_CHINA_ENG = "http://47.115.44.145:7000/api/Test/test2";
    private static final String GET_URL_WITH_PARAMETER = "http://47.115.44.145:7000/api/Test/test3?name=WangZi";
    private static final String POST = "http://47.115.44.145:7000/api/Test/test4";
    private static final String POST_FORM = "http://47.115.44.145:7000/api/Test/test5";
    private static final String GET_WEATHER_JSON = "http://47.115.44.145:7000/WeatherForecast";
    public SocketClient() {
    }

    public static void main(String[] args) throws Exception {
        URL url1 = new URL(GET_URL);
        URL url2 = new URL(GET_URL_CHINA_ENG);
        URL url3 = new URL(GET_URL_WITH_PARAMETER);
        URL url4 = new URL(POST);
        URL url5 = new URL(POST_FORM);
        URL url6 = new URL(GET_WEATHER_JSON);
        callGetWebAPI(url1);
        callGetWebAPI(url2);
        callGetWebAPI(url3);
        callPostWebAPI(url4, "{\"userName\":\"WangZi\",\"passWord\":\"123456\"}",1);
        callPostWebAPI(url5, "userName=LiHua&passWord=123456",2);
        callGetWebAPI(url6);
    }

    public static void callGetWebAPI(URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        int responseCode = httpURLConnection.getResponseCode();
        String http = url.toString();
        System.out.println(http + "\nResponseCode: " + responseCode);
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuffer response = new StringBuffer();

            String inputLine;
            while((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            System.out.println(response.toString());
        } else {
            System.out.println("GET request not worked");
        }
        System.out.println();
    }
    public static void callPostWebAPI(URL url, String content,int type) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        if(type == 1){
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.connect();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
            writer.write(content);
            writer.close();
            int responseCode = httpURLConnection.getResponseCode();
            String http = url.toString();
            System.out.println(http + "\nResponseCode: " + responseCode);
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuffer response = new StringBuffer();
                String inputLine;
                while((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
                System.out.println(response.toString());
            } else {
                System.out.println("POST request not worked");
            }
        }
        else {
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.connect();
            byte[] data = content.getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = httpURLConnection.getOutputStream()) {
                os.write(data);
                os.flush();
            }

            // 获取响应
            int responseCode = httpURLConnection.getResponseCode();
            String http = url.toString();
            System.out.println(http + "\nResponseCode: " + responseCode);

            // 如果是二进制数据（如图片），你可以直接读取字节流：
            if(responseCode == HttpURLConnection.HTTP_OK) {
                try(BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))){
                    String inputline;
                    StringBuilder response = new StringBuilder();
                    while( (inputline = in.readLine())!=null){
                        response.append(inputline);
                    }
                    System.out.println(response.toString());
                }
            } else {
                System.out.println("POST request not worked");
            }
        }
        System.out.println();
    }
}
