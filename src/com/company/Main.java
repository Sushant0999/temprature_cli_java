package com.company;

import org.json.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) throws IOException, InterruptedException {
	// write your code here
        Scanner s = new Scanner(System.in);
        System.out.println("  ______       _               _____ _ _           _   _                       \n" +
                " |  ____|     | |             / ____(_) |         | \\ | |                      \n" +
                " | |__   _ __ | |_ ___ _ __  | |     _| |_ _   _  |  \\| | __ _ _ __ ___   ___  \n" +
                " |  __| | '_ \\| __/ _ \\ '__| | |    | | __| | | | | . ` |/ _` | '_ ` _ \\ / _ \\ \n" +
                " | |____| | | | ||  __/ |    | |____| | |_| |_| | | |\\  | (_| | | | | | |  __/ \n" +
                " |______|_| |_|\\__\\___|_|     \\_____|_|\\__|\\__, | |_| \\_|\\__,_|_| |_| |_|\\___| \n" +
                "                                            __/ |                              \n" +
                "                                           |___/                               "+"CREATED BY SUSHANT0999");
        String city = s.nextLine();
        space(city);
        String rawUrl = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=59e30983d7b7f60075b0b823a0b41e1b";
        var url = rawUrl;
        //print Actual Url
//        System.out.println(rawUrl);
        var request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        var data = response.toString();
//        System.out.println(response.body());
        String result  = response.body().toString();
        JSONObject jsonObject = new JSONObject(result);
        //will print data avilaible in main.json
//        System.out.println(jsonObject.get("main"));
        JSONObject jsonObject1 = new JSONObject(jsonObject.get("main").toString());
        JSONObject jsonObject2 = new JSONObject(jsonObject.get("wind").toString());
        JSONArray jsonArray = new JSONArray(jsonObject.get("weather").toString());
        //        System.out.println(jsonObject1.get("temp"));
        //object Created
        getData data1 = new getData();
        for (int i = 0; i < 10; i++) {
            System.out.print(".");
            Thread.sleep(500);
        }
        Thread.sleep(500);
        System.out.println("100% "+"\n"+"Done");
        Thread.sleep(500);
        System.out.println("Loading Complete");
        //Name
        System.out.println("City : "+ jsonObject.get("name"));
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


    }
    static void space(String s){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<s.length();i++){
            if(s.charAt(i)==' '){
                sb.append("%20");
            }
            else{
                sb.append(s.charAt(i));
            }
        }
    }
}

