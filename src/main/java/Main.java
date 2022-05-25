import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> employeeListFromCSV = parseCSV(columnMapping, fileName);
        String employeeFromCSV = listToJson(employeeListFromCSV);
        writeString("data.txt", employeeFromCSV);

        List<Employee> employeeListFromXml = parseXML("data.xml");
        String employeeFromXML = listToJson(employeeListFromXml);
        writeString("data2.txt", employeeFromXML);

        String readString = readString("data.txt");
        List<Employee> employeeListFromJson = jsonToList(readString);

        /*Я не совсем понял смысл строки "Полученные экземпляры класса Employee добавляйте в список, который должен быть выведен из метода после его окончания."
        Можно вывести уже после получения списка, или же в самом методе.
         */
        /*for (Employee employee : employeeListFromJson){
            System.out.println(employee);
        }*/

    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> employeeList = null;

        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> columnPositionMappingStrategy = new ColumnPositionMappingStrategy<>();
            columnPositionMappingStrategy.setType(Employee.class);
            columnPositionMappingStrategy.setColumnMapping(columnMapping);

            CsvToBeanBuilder<Employee> csvToBeanBuilder = new CsvToBeanBuilder<>(csvReader);
            CsvToBean<Employee> csvToBean = csvToBeanBuilder
                    .withMappingStrategy(columnPositionMappingStrategy)
                    .build();

            csvToBean.setMappingStrategy(columnPositionMappingStrategy);

            employeeList = csvToBean.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return employeeList;
    }

    public static List<Employee> parseXML(String file) {
        List<Employee> employeeList = new ArrayList<>();
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File(file));

            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("employee");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String id = element.getElementsByTagName("id").item(0).getTextContent();
                    String firstName = element.getElementsByTagName("id").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("id").item(0).getTextContent();
                    String country = element.getElementsByTagName("id").item(0).getTextContent();
                    String age = element.getElementsByTagName("id").item(0).getTextContent();

                    employeeList.add(new Employee(Long.parseLong(id), firstName, lastName, country, Integer.parseInt(age)));

                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return employeeList;
    }

    public static String listToJson(List<Employee> employeeList) {
        Gson gson = new GsonBuilder().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();

        return gson.toJson(employeeList, listType);
    }

    public static List<Employee> jsonToList(String jsonString) {

        Gson gson = new GsonBuilder().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();

        List<Employee> employeeList = gson.fromJson(jsonString, listType);

        for (Employee employee : employeeList){
            System.out.println(employee);
        }

        return employeeList;
    }

    public static void writeString(String path, String file) {
        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readString(String path) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
