package edu.ib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Test {

    public static void main(String[] args) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        StringBuffer response = new StringBuffer();
        String url = "https://api.openaq.org/v1/measurements?city=Warszawa&parameter[]=pm25&parameter[]=pm10%22%20+%20%22&parameter[]=so2&parameter[]=no2&parameter[]=o3&parameter[]=co";

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            System.out.println("Response: " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Damaged URL.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No connection with server");
        }

        String jsonFromResponse = response.toString();

        AirQualityObjects airQualityObjects = gson.fromJson(jsonFromResponse, AirQualityObjects.class);
        List<Results> resultsList = airQualityObjects.getResults();
        //resultsList.get(5);

        String day = airQualityObjects.getResults().get(2).getDate().getLocal();
        System.out.println(day);

        ArrayList<Double> bcValuesList = putValuesIntoListsRelationToTime("bc", airQualityObjects, 1);
        ArrayList<Double> coValuesList = putValuesIntoListsRelationToTime("co", airQualityObjects, 2);
        ArrayList<Double> coAllValuesList = putAllValuesIntoLists("co", airQualityObjects);
//        ArrayList<Double> no2ValuesList = putValuesIntoLists("no2", airQualityObjects, 2);
//        ArrayList<Double> o3ValuesList = putValuesIntoLists("o3", airQualityObjects, 2);
//        ArrayList<Double> pm10ValuesList = putValuesIntoLists("pm10", airQualityObjects, 2);
//        ArrayList<Double> pm25ValuesList = putValuesIntoLists("pm25", airQualityObjects, 2);
//        ArrayList<Double> so2ValuesList = putValuesIntoLists("so2", airQualityObjects, 2);

        Collections.reverse(coAllValuesList); //odwrócenie ArrayListy
        System.out.println(bcValuesList);
        System.out.println("CO w odpowiedniej godzinie: " + coValuesList);
        double averageCOGivenHour = average(coValuesList);
        System.out.println("średnia w odpowiedniej godzinie: " + averageCOGivenHour);
        System.out.println("CO w kazdej godzinie pomiaru: " + coAllValuesList);
        double maxValueCO = maxValue(coAllValuesList);
        double minValueCO = minValue(coAllValuesList);
        double averageCO = average(coAllValuesList);
        double sdCO = sd(coAllValuesList);


        System.out.println("Dla CO: max wartosc: " + maxValueCO + ", min wartosc: " + minValueCO + ", srednia wartosc: " + averageCO + ", odchylenie stand: " + sdCO);
//        System.out.println(no2ValuesList);
//        System.out.println(o3ValuesList);
//        System.out.println(pm10ValuesList);
//        System.out.println(pm25ValuesList);
//        System.out.println(so2ValuesList);

        TreeSet<String> uniqueLocalTime = uniqueRecords(airQualityObjects);
        System.out.println(uniqueLocalTime);
        System.out.println(uniqueLocalTime.size());

//        for (int i = 0; i < resultsList.size(); i++) {
//            double values = airQualityObjects.getResults().get(i).getValue();
//        }
//        double values = airQualityObjects.getResults().get(3).getValue();
//        System.out.println(values);

    }

    public static ArrayList<Double> putAllValuesIntoLists(String id, AirQualityObjects airQualityObject){
        ArrayList<Double> valuesList = new ArrayList<>();
        List<Results> resultsList = airQualityObject.getResults();
        for (int j = 0; j < resultsList.size(); j++) {
            if (resultsList.get(j).getParameter().equals(id))
                valuesList.add(airQualityObject.getResults().get(j).getValue());
        }
        return valuesList;
    }

    public static ArrayList<Double> putValuesIntoListsRelationToTime(String id, AirQualityObjects airQualityObject, int index){
        ArrayList<Double> valuesList = new ArrayList<>();
        List<Results> resultsList = airQualityObject.getResults();
        TreeSet<String> uniqueLocalTime = uniqueRecords(airQualityObject); //zapełniamy TreeSeta uniqueLocalTime unikalnymi łańcuchami lokalnych czasów
        for (int i = 0; i < index; i++) {
            String time = uniqueLocalTime.pollFirst(); //wyciągamy pierwszą wartość z uniqueLocalTime i przypisujemy do zmiennej time, treeSet się zmniejsza o taką ilość elementów, jaką wyciągneliśmy pollFirst
            valuesList.clear(); //czyscimy valueList, zeby zapisac tylko wartosci okreslone przez odpowiedni czas w TreeSecie
            for (int j = 0; j < resultsList.size(); j++) {
                if (resultsList.get(j).getParameter().equals(id) && resultsList.get(j).getDate().getLocal().equals(time))
                    valuesList.add(airQualityObject.getResults().get(j).getValue());
            }
        }

        return valuesList;
    }

    public static TreeSet<String> uniqueRecords(AirQualityObjects airQualityObject){
        TreeSet<String> stringsList = new TreeSet<>();
        List<Results> resultsList = airQualityObject.getResults();
        for (int i = 0; i < resultsList.size(); i++) {
                stringsList.add(airQualityObject.getResults().get(i).getDate().getLocal());
        }
        return stringsList;
    }

    public static double maxValue(ArrayList<Double> allValuesList) {
        double max = Collections.max(allValuesList);
        return max;
    }

    public static double minValue(ArrayList<Double> allValuesList) {
        double min = Collections.min(allValuesList);
        return min;
    }

    public static double average(ArrayList<Double> allValuesList) {
        OptionalDouble average = allValuesList
                .stream()
                .mapToDouble(a -> a)
                .average();
        return average.isPresent() ? average.getAsDouble() : 0;
    }

    public static double sd(ArrayList<Double> allValuesList)
    {
        // Step 1:
        double mean = average(allValuesList);
        double temp = 0;

        for (int i = 0; i < allValuesList.size(); i++)
        {
            double val = allValuesList.get(i);

            // Step 2:
            double sqrtDiffToMean = Math.pow(val - mean, 2);

            // Step 3:
            temp += sqrtDiffToMean;
        }

        // Step 4:
        double meanOfDiffs = (double) temp / (double) (allValuesList.size());

        // Step 5:
        return Math.sqrt(meanOfDiffs);
    }


}
