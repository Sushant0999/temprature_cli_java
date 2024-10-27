package com.company;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {


    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        Jedis jedis = null;
        try {
            // Connect to Redis
            String redisUri = "";
            jedis = new Jedis(redisUri);
            System.out.println("Connection to server successful");
        } catch (Exception e) {
            System.err.println("Redis is offline..");
        }

        // write your code here
        Scanner s = new Scanner(System.in);
        System.out.println("  ______       _               _____ _ _           _   _                       \n" +
                " |  ____|     | |             / ____(_) |         | \\ | |                      \n" +
                " | |__   _ __ | |_ ___ _ __  | |     _| |_ _   _  |  \\| | __ _ _ __ ___   ___  \n" +
                " |  __| | '_ \\| __/ _ \\ '__| | |    | | __| | | | | . ` |/ _` | '_ ` _ \\ / _ \\ \n" +
                " | |____| | | | ||  __/ |    | |____| | |_| |_| | | |\\  | (_| | | | | | |  __/ \n" +
                " |______|_| |_|\\__\\___|_|     \\_____|_|\\__|\\__, | |_| \\_|\\__,_|_| |_| |_|\\___| \n" +
                "                                            __/ |                              \n" +
                "                                           |___/                               " + "\nCREATED BY https://github.com/Sushant0999");
        boolean isFourHoursDiff = true;
        String city = s.nextLine();
        assert jedis != null;
        String resp = null;
        try{
           resp = jedis.get(city.toLowerCase());
        }catch (Exception e){
            System.out.println("Unable to retrieve data..");
        }
        JSONObject jsonObject = null;
        JSONObject jsonObject1 = null;
        JSONObject jsonObject2 = null;
        JSONArray jsonArray = null;
        if (resp != null) {
            String time = extractTime(resp);
            isFourHoursDiff = isTimeDifferenceFourHours(time);
            if (!isFourHoursDiff) {
                System.out.println("Retrieving Data...");
                jsonObject = new JSONObject(resp);
                jsonObject1 = new JSONObject(jsonObject.get("main").toString());
                jsonObject2 = new JSONObject(jsonObject.get("wind").toString());
                jsonArray = new JSONArray(jsonObject.get("weather").toString());
            }
        }
        if (isFourHoursDiff) {
            System.out.println("Fetching Data....");
            city = space(city);
            String rawUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid={{API_KEY_OPEN_WEATHER}}";
            var url = rawUrl;
            var request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
            var client = HttpClient.newBuilder().build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String result = response.body();
            result = result.concat("time ").concat(getCurrentTime());
            try {
                jedis.set(city.toLowerCase(), result);
                jsonObject = new JSONObject(result);
                jsonObject1 = new JSONObject(jsonObject.get("main").toString());
                jsonObject2 = new JSONObject(jsonObject.get("wind").toString());
                jsonArray = new JSONArray(jsonObject.get("weather").toString());
            }catch (NullPointerException e){
                System.out.println("City doesn't exist");
                System.exit(400);
            }catch (JSONException e){
                System.out.println("Unable to find city..");
                System.exit(404);
            }catch (Exception e){
                System.out.println("Unable to persist data..");
            }
        }
        getData data1 = new getData();
        if (resp != null) {
            System.out.print("\rDone!         \n");
            int total = 50; // Total length of the progress bar
            for (int i = 0; i <= total; i++) {
                int percent = (i * 100) / total;
                StringBuilder progressBar = new StringBuilder();

                progressBar.append("\r["); // Start of progress bar
                for (int j = 0; j < total; j++) {
                    if (j < i) progressBar.append("#");
                    else progressBar.append(" ");
                }
                progressBar.append("] ").append(percent).append("%"); // End of progress bar

                System.out.print(progressBar); // Print the progress bar
                Thread.sleep(100); // Delay for demonstration
            }
            System.out.println("\nComplete!");
        }
        Thread.sleep(500);
        System.out.println("100% " + "\n" + "Done");
        Thread.sleep(500);
        System.out.println("Loading Complete");
        //Name
        System.out.println("City : " + jsonObject.get("name"));
        System.out.println("\n");

        //Logo
        JSONObject jsonObject3 = jsonArray.getJSONObject(0);
        data1.logo((Integer) jsonObject3.get("id"));
        System.out.println("\n");

        //Temprature
        System.out.println("-----Temprature-----");
        data1.curr(jsonObject1.get("temp").toString());
        data1.min(jsonObject1.get("temp_min").toString());
        data1.max(jsonObject1.get("temp_max").toString());
        System.out.println("\n");

        //Humidity
        System.out.println("-----Humidity-----");
        data1.humid(jsonObject1.get("humidity").toString());
        System.out.println("\n");

        //Wind Speed
        System.out.println("-----Wind Speed-----");
        data1.wind(jsonObject2.get("speed").toString());
        System.out.println("\n");

        try{
            jedis.close();
        }catch (Exception e){
            System.out.println("Exit....");
        }
    }

    static String space(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                sb.append("%20");
            } else {
                sb.append(s.charAt(i));
            }
        }
        return sb.toString();
    }

    static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    public static boolean isTimeDifferenceFourHours(String storedTime) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date storedDate = dateFormat.parse(storedTime);
        Date currentTime = new Date();
        long timeDifferenceMillis = currentTime.getTime() - storedDate.getTime();
        long timeDifferenceHours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis);
        return timeDifferenceHours >= 4;
    }

    public static String extractTime(String input) {
        String timePattern = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
        Pattern pattern = Pattern.compile(timePattern);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return "No time found";
        }
    }

}

