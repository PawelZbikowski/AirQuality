package edu.ib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AirQualityController {

    String jsonResult = null;
    AirQualityObjects airQualityObjects2 = null;
    AirQualityObjects airQualityObjects = null;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField cityTextField;

    @FXML
    private JFXButton showParametresButton;

    @FXML
    private JFXButton saveButton;

    @FXML
    private JFXButton loadButton;

    @FXML
    private LineChart<String, Double> LineChart;

    @FXML
    private Label COmainLabel;

    @FXML
    private Label COmax;

    @FXML
    private Label COmin;

    @FXML
    private Label COaverage;

    @FXML
    private Label COstd;

    @FXML
    private Label NO2mainLabel;

    @FXML
    private Label NO2max;

    @FXML
    private Label NO2min;

    @FXML
    private Label NO2average;

    @FXML
    private Label NO2std;

    @FXML
    private Label O3mainLabel;

    @FXML
    private Label O3max;

    @FXML
    private Label O3min;

    @FXML
    private Label O3average;

    @FXML
    private Label O3std;

    @FXML
    private Label PM10mainLabel;

    @FXML
    private Label PM10max;

    @FXML
    private Label PM10min;

    @FXML
    private Label PM10average;

    @FXML
    private Label PM10std;

    @FXML
    private Label PM25mainLabel;

    @FXML
    private Label PM25max;

    @FXML
    private Label PM25min;

    @FXML
    private Label PM25average;

    @FXML
    private Label PM25std;

    @FXML
    private Label SO2mainLabel;

    @FXML
    private Label SO2max;

    @FXML
    private Label SO2min;

    @FXML
    private Label SO2average;

    @FXML
    private Label SO2std;

    @FXML
    private Label dayLabel;

    @FXML
    private JFXComboBox<String> parameterComboBox;

    @FXML
    private JFXButton showChartButton;

    @FXML
    void loadButtonClicked(ActionEvent event) {
        Stage stage = new Stage();

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("JSON files(*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(stage);
        System.out.println("Path: " + file);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();


        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            System.out.println();
            airQualityObjects2 = gson.fromJson(bufferedReader, AirQualityObjects.class);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Anuluj");
        }

        String day = airQualityObjects2.getResults().get(0).getDate().getLocal();
        String dayModified = day.substring(0, 10) + " " + day.substring(11, 19);
        dayLabel.setText(dayModified);

        // Umieszczenie wszystkich danych dla wszytskich parametrów w odpowiednich labelach
        puttingValuesIntoTextFields("co", airQualityObjects2, COmax, COmin, COaverage, COstd);
        puttingValuesIntoTextFields("no2", airQualityObjects2, NO2max, NO2min, NO2average, NO2std);
        puttingValuesIntoTextFields("o3", airQualityObjects2, O3max, O3min, O3average, O3std);
        puttingValuesIntoTextFields("pm10", airQualityObjects2, PM10max, PM10min, PM10average, PM10std);
        puttingValuesIntoTextFields("pm25", airQualityObjects2, PM25max, PM25min, PM25average, PM25std);
        puttingValuesIntoTextFields("so2", airQualityObjects2, SO2max, SO2min, SO2average, SO2std);

        double avugCO = averageReturn("co", airQualityObjects2);
        double avugNO2 = averageReturn("no2", airQualityObjects2);
        double avugO3 = averageReturn("o3", airQualityObjects2);
        double avugPM10 = averageReturn("pm10", airQualityObjects2);
        double avugPM25 = averageReturn("pm25", airQualityObjects2);
        double avugSO2 = averageReturn("so2", airQualityObjects2);

        averageCoColors(avugCO);
        averageNo2Colors(avugNO2);
        averageO3Colors(avugO3);
        averagePm10Colors(avugPM10);
        averagePm25Colors(avugPM25);
        averageSo2Colors(avugSO2);
    }

    @FXML
    void saveButtonClicked(ActionEvent event) {

        String dayName = dayLabel.getText();
        String dayNameModified = dayName.replaceAll("\\s+", "");
        String dayNameModified2 = dayNameModified.replaceAll(":", "");
        String fileName = cityTextField.getText().concat(dayNameModified2);
        if (fileName.isEmpty()) {
            fileName = "unnamed";
        }
        Stage stage = new Stage();

        final DirectoryChooser directoryChooser = new DirectoryChooser();

        File file = directoryChooser.showDialog(stage);
        if (file != null) {
            System.out.println("Path: " + file.getAbsolutePath());
            String path = file.getAbsolutePath();

            // sprawdzenie zawartosci jsonResult
            if (jsonResult != null) {

                //Zapis do pliku
                OutputStream outputStream = null;

                try {
                    System.out.println(jsonResult);
                    String pathname = path + "/" + fileName + ".json";
                    System.out.println(pathname);
                    outputStream = new FileOutputStream(new File(pathname));
                    outputStream.write(jsonResult.getBytes());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("saved successfully");
            } else {
                System.out.println("json null");
            }


        } else {
            System.out.println("Error");
        }

    }

    @FXML
    void showChartButtonClicked(ActionEvent event) {
        if (parameterComboBox.getValue().equals(null)) {
            errorLabel.setVisible(true);
            errorLabel.setText("Proszę wybrać parametr z listy");
            errorLabel.getStyleClass().add("wrong-label");
        } else {
            String parameter = parameterComboBox.getValue();
            String parameterModified = parameter.toLowerCase();
            TreeSet<String> uniqueLocalTime = uniqueRecords(airQualityObjects);
            ArrayList<Double> values = new ArrayList<>();
            double average = 0;
            for (int i = 1; i < uniqueLocalTime.size() + 1; i++) {
                ArrayList<Double> ValuesList = putValuesIntoListsRelationToTime(parameterModified, airQualityObjects, i);
                average = average(ValuesList);
                values.add(average);
            }

            System.out.println(values);
            TreeSet<String> uniqueLocalTimeSubstitute = uniqueLocalTime;
            System.out.println(uniqueLocalTime);
            ArrayList<String> hours = new ArrayList<>();
            while (!uniqueLocalTimeSubstitute.isEmpty()) {
                String hour = uniqueLocalTimeSubstitute.pollFirst().substring(11, 13);
                System.out.println(hour);
                hours.add(hour);
            }
            System.out.println(hours);

            LineChart.getData().clear();
            XYChart.Series<String, Double> series = new XYChart.Series();
            for (int i = 0; i < hours.size(); i++) {
                series.getData().add(new XYChart.Data<>(hours.get(i), values.get(i)));
            }

            LineChart.getData().addAll(series);

            series.setName(parameter);
        }
    }

    @FXML
    void showParametresButtonClicked(ActionEvent event) throws UnsupportedEncodingException {
        String cityName = cityTextField.getText();
        String cityNameModified = null;
        if (!cityName.isEmpty()) {
            cityName = URLEncoder.encode(cityName, "UTF-8");
            cityNameModified = cityName.substring(0, 1).toUpperCase() + cityName.substring(1).toLowerCase();
        } else {
            errorLabel.setText("Proszę wpisać miasto");
            errorLabel.getStyleClass().add("wrong-label");
        }

        System.out.println(cityNameModified);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        StringBuffer response = new StringBuffer();
        String url = "https://api.openaq.org/v1/measurements?city=" + cityNameModified + "&parameter[]=pm25&parameter[]=pm10%22%20+%20%22&parameter[]=so2&parameter[]=no2&parameter[]=o3&parameter[]=co";
        System.out.println(url);

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
            errorLabel.setVisible(false);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Damaged URL.");
            errorLabel.setText("Błąd URl");
            errorLabel.getStyleClass().add("wrong-label");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No connection with server");
            errorLabel.setText("Brak połączenia z internetem");
            errorLabel.getStyleClass().add("wrong-label");
        }

        String jsonFromResponse = response.toString();

        airQualityObjects = gson.fromJson(jsonFromResponse, AirQualityObjects.class);

        if (airQualityObjects.getResults().isEmpty()) {
            errorLabel.setVisible(true);
            errorLabel.setText("Nie ma takiego miasta w bazie");
        } else {
            // wyswietlenie dnia
            showChartButton.setDisable(false);
            saveButton.setDisable(false);
            loadButton.setDisable(false);

            jsonResult = gson.toJson(airQualityObjects);

            String day = airQualityObjects.getResults().get(0).getDate().getLocal();
            String dayModified = day.substring(0, 10) + " " + day.substring(11, 19);
            dayLabel.setText(dayModified);

            // Umieszczenie wszystkich danych dla wszytskich parametrów w odpowiednich labelach
            puttingValuesIntoTextFields("co", airQualityObjects, COmax, COmin, COaverage, COstd);
            puttingValuesIntoTextFields("no2", airQualityObjects, NO2max, NO2min, NO2average, NO2std);
            puttingValuesIntoTextFields("o3", airQualityObjects, O3max, O3min, O3average, O3std);
            puttingValuesIntoTextFields("pm10", airQualityObjects, PM10max, PM10min, PM10average, PM10std);
            puttingValuesIntoTextFields("pm25", airQualityObjects, PM25max, PM25min, PM25average, PM25std);
            puttingValuesIntoTextFields("so2", airQualityObjects, SO2max, SO2min, SO2average, SO2std);

            double avugCO = averageReturn("co", airQualityObjects);
            double avugNO2 = averageReturn("no2", airQualityObjects);
            double avugO3 = averageReturn("o3", airQualityObjects);
            double avugPM10 = averageReturn("pm10", airQualityObjects);
            double avugPM25 = averageReturn("pm25", airQualityObjects);
            double avugSO2 = averageReturn("so2", airQualityObjects);

            averageCoColors(avugCO);
            averageNo2Colors(avugNO2);
            averageO3Colors(avugO3);
            averagePm10Colors(avugPM10);
            averagePm25Colors(avugPM25);
            averageSo2Colors(avugSO2);


        }


    }

    public void averageCoColors(double averageCO) {
        if (averageCO > 0 && averageCO <= 3000) {
            COmainLabel.getStyleClass().add("very-good");
        } else if (averageCO > 3000 && averageCO <= 7000) {
            COmainLabel.getStyleClass().add("good");
        } else if (averageCO > 7000 && averageCO <= 11000) {
            COmainLabel.getStyleClass().add("average");
        } else if (averageCO > 11000 && averageCO <= 15000) {
            COmainLabel.getStyleClass().add("sufficient");
        } else if (averageCO > 15000 && averageCO <= 21000) {
            COmainLabel.getStyleClass().add("bad");
        } else if (averageCO > 21000) {
            COmainLabel.getStyleClass().add("very-bad");
        } else {
            COmainLabel.getStyleClass().add("no-data");
        }
    }

    public void averageNo2Colors(double averageNO2) {
        if (averageNO2 > 0 && averageNO2 <= 41) {
            NO2mainLabel.getStyleClass().add("very-good");
        } else if (averageNO2 > 41 && averageNO2 <= 101) {
            NO2mainLabel.getStyleClass().add("good");
        } else if (averageNO2 > 101 && averageNO2 <= 151) {
            NO2mainLabel.getStyleClass().add("average");
        } else if (averageNO2 > 151 && averageNO2 <= 201) {
            NO2mainLabel.getStyleClass().add("sufficient");
        } else if (averageNO2 > 201 && averageNO2 <= 401) {
            NO2mainLabel.getStyleClass().add("bad");
        } else if (averageNO2 > 401) {
            NO2mainLabel.getStyleClass().add("very-bad");
        } else {
            NO2mainLabel.getStyleClass().add("no-data");
        }
    }

    public void averageO3Colors(double averageO3) {
        if (averageO3 > 0 && averageO3 <= 71) {
            O3mainLabel.getStyleClass().add("very-good");
        } else if (averageO3 > 71 && averageO3 <= 121) {
            O3mainLabel.getStyleClass().add("good");
        } else if (averageO3 > 121 && averageO3 <= 151) {
            O3mainLabel.getStyleClass().add("average");
        } else if (averageO3 > 151 && averageO3 <= 181) {
            O3mainLabel.getStyleClass().add("sufficient");
        } else if (averageO3 > 181 && averageO3 <= 241) {
            O3mainLabel.getStyleClass().add("bad");
        } else if (averageO3 > 241) {
            O3mainLabel.getStyleClass().add("very-bad");
        } else {
            O3mainLabel.getStyleClass().add("no-data");
        }
    }

    public void averagePm10Colors(double averagePM10) {
        if (averagePM10 > 0 && averagePM10 <= 21) {
            PM10mainLabel.getStyleClass().add("very-good");
        } else if (averagePM10 > 21 && averagePM10 <= 61) {
            PM10mainLabel.getStyleClass().add("good");
        } else if (averagePM10 > 61 && averagePM10 <= 101) {
            PM10mainLabel.getStyleClass().add("average");
        } else if (averagePM10 > 101 && averagePM10 <= 141) {
            PM10mainLabel.getStyleClass().add("sufficient");
        } else if (averagePM10 > 141 && averagePM10 <= 201) {
            PM10mainLabel.getStyleClass().add("bad");
        } else if (averagePM10 > 201) {
            PM10mainLabel.getStyleClass().add("very-bad");
        } else {
            PM10mainLabel.getStyleClass().add("no-data");
        }
    }

    public void averagePm25Colors(double averagePM25) {
        if (averagePM25 > 0 && averagePM25 <= 13) {
            PM25mainLabel.getStyleClass().add("very-good");
        } else if (averagePM25 > 13 && averagePM25 <= 37) {
            PM25mainLabel.getStyleClass().add("good");
        } else if (averagePM25 > 37 && averagePM25 <= 61) {
            PM25mainLabel.getStyleClass().add("average");
        } else if (averagePM25 > 61 && averagePM25 <= 85) {
            PM25mainLabel.getStyleClass().add("sufficient");
        } else if (averagePM25 > 85 && averagePM25 <= 121) {
            PM25mainLabel.getStyleClass().add("bad");
        } else if (averagePM25 > 121) {
            PM25mainLabel.getStyleClass().add("very-bad");
        } else {
            PM25mainLabel.getStyleClass().add("no-data");
        }
    }

    public void averageSo2Colors(double averageSO2) {
        if (averageSO2 > 0 && averageSO2 <= 51) {
            SO2mainLabel.getStyleClass().add("very-good");
        } else if (averageSO2 > 51 && averageSO2 <= 101) {
            SO2mainLabel.getStyleClass().add("good");
        } else if (averageSO2 > 101 && averageSO2 <= 201) {
            SO2mainLabel.getStyleClass().add("average");
        } else if (averageSO2 > 201 && averageSO2 <= 351) {
            SO2mainLabel.getStyleClass().add("sufficient");
        } else if (averageSO2 > 351 && averageSO2 <= 501) {
            SO2mainLabel.getStyleClass().add("bad");
        } else if (averageSO2 > 501) {
            SO2mainLabel.getStyleClass().add("very-bad");
        } else {
            SO2mainLabel.getStyleClass().add("no-data");
        }
    }

    void puttingValuesIntoTextFields(String id, AirQualityObjects airQualityObjects, Label maxLabel, Label minLabel, Label avgLabel, Label sdLabel) {

        ArrayList<Double> AllValuesList = putAllValuesIntoLists(id, airQualityObjects); //umieszczenie wszystkich wartości dla danego parametru w liscie tablicowej
        Collections.reverse(AllValuesList); //odwrócenie ArrayListy
        System.out.println(id + " w kazdej godzinie pomiaru: " + AllValuesList);

        double max = maxValue(AllValuesList);
        double min = minValue(AllValuesList);
        double avg = average(AllValuesList);
        double sd = sd(AllValuesList);

        double maxF = new BigDecimal(max).setScale(2, RoundingMode.HALF_UP).doubleValue();
        double minF = new BigDecimal(min).setScale(2, RoundingMode.HALF_UP).doubleValue();
        double avgF = new BigDecimal(avg).setScale(2, RoundingMode.HALF_UP).doubleValue();
        double sdF = new BigDecimal(sd).setScale(2, RoundingMode.HALF_UP).doubleValue();

        maxLabel.setText(String.valueOf(maxF));
        minLabel.setText(String.valueOf(minF));
        avgLabel.setText(String.valueOf(avgF));
        sdLabel.setText(String.valueOf(sdF));
    }

    public static double averageReturn(String id, AirQualityObjects airQualityObjects) {
        ArrayList<Double> AllValuesList = putAllValuesIntoLists(id, airQualityObjects); //umieszczenie wszystkich wartości dla danego parametru w liscie tablicowej
        Collections.reverse(AllValuesList); //odwrócenie ArrayListy

        double avg = average(AllValuesList);
        double avgF = new BigDecimal(avg).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return avgF;
    }

    public static ArrayList<Double> putAllValuesIntoLists(String id, AirQualityObjects airQualityObject) {
        ArrayList<Double> valuesList = new ArrayList<>();
        List<Results> resultsList = airQualityObject.getResults();
        for (int j = 0; j < resultsList.size(); j++) {
            if (resultsList.get(j).getParameter().equals(id))
                valuesList.add(airQualityObject.getResults().get(j).getValue());
        }
        return valuesList;
    }

    public static ArrayList<Double> putValuesIntoListsRelationToTime(String id, AirQualityObjects airQualityObject, int index) {
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

    public static TreeSet<String> uniqueRecords(AirQualityObjects airQualityObject) {
        TreeSet<String> stringsList = new TreeSet<>();
        List<Results> resultsList = airQualityObject.getResults();
        for (int i = 0; i < resultsList.size(); i++) {
            stringsList.add(airQualityObject.getResults().get(i).getDate().getLocal());
        }
        return stringsList;
    }

    public static double maxValue(ArrayList<Double> allValuesList) {
        double max = 0;
        if (!allValuesList.isEmpty()) {
            if (!Collections.max(allValuesList).equals(null)) {
                max = Collections.max(allValuesList);
            }
        }

        return max;
    }

    public static double minValue(ArrayList<Double> allValuesList) {
        double min = 0;
        if (!allValuesList.isEmpty()) {
            if (!Collections.min(allValuesList).equals(null)) {
                min = Collections.min(allValuesList);
            }
        }
        return min;
    }

    public static double average(ArrayList<Double> allValuesList) {
        OptionalDouble average = allValuesList
                .stream()
                .mapToDouble(a -> a)
                .average();
        return average.isPresent() ? average.getAsDouble() : 0;
    }

    public static double sd(ArrayList<Double> allValuesList) {
        double sdValue = 0;
        if (!allValuesList.isEmpty()) {
            // Step 1:
            double mean = average(allValuesList);
            double temp = 0;

            for (int i = 0; i < allValuesList.size(); i++) {
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
        return sdValue;


    }


    @FXML
    void initialize() {
        assert cityTextField != null : "fx:id=\"cityTextField\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert showParametresButton != null : "fx:id=\"showParametresButton\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert saveButton != null : "fx:id=\"saveButton\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert loadButton != null : "fx:id=\"loadButton\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert LineChart != null : "fx:id=\"LineChart\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert COmainLabel != null : "fx:id=\"COmainLabel\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert COmax != null : "fx:id=\"COmax\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert COmin != null : "fx:id=\"COmin\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert COaverage != null : "fx:id=\"COaverage\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert COstd != null : "fx:id=\"COstd\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert NO2mainLabel != null : "fx:id=\"NO2mainLabel\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert NO2max != null : "fx:id=\"NO2max\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert NO2min != null : "fx:id=\"NO2min\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert NO2average != null : "fx:id=\"NO2average\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert NO2std != null : "fx:id=\"NO2std\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert O3mainLabel != null : "fx:id=\"O3mainLabel\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert O3max != null : "fx:id=\"O3max\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert O3min != null : "fx:id=\"O3min\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert O3average != null : "fx:id=\"O3average\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert O3std != null : "fx:id=\"O3std\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert PM10mainLabel != null : "fx:id=\"PM10mainLabel\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert PM10max != null : "fx:id=\"PM10max\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert PM10min != null : "fx:id=\"PM10min\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert PM10average != null : "fx:id=\"PM10average\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert PM10std != null : "fx:id=\"PM10std\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert PM25mainLabel != null : "fx:id=\"PM25mainLabel\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert PM25max != null : "fx:id=\"PM25max\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert PM25min != null : "fx:id=\"PM25min\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert PM25average != null : "fx:id=\"PM25average\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert PM25std != null : "fx:id=\"PM25std\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert SO2mainLabel != null : "fx:id=\"SO2mainLabel\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert SO2max != null : "fx:id=\"SO2max\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert SO2min != null : "fx:id=\"SO2min\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert SO2average != null : "fx:id=\"SO2average\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert SO2std != null : "fx:id=\"SO2std\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert dayLabel != null : "fx:id=\"dayLabel\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert parameterComboBox != null : "fx:id=\"parameterComboBox\" was not injected: check your FXML file 'AirQualityApp.fxml'.";
        assert showChartButton != null : "fx:id=\"showChartButton\" was not injected: check your FXML file 'AirQualityApp.fxml'.";

        parameterComboBox.getItems().addAll("CO", "NO2", "O3", "PM10", "PM25", "SO2");
    }
}

