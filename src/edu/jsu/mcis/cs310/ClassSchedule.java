package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class ClassSchedule {
    
    private final String CSV_FILENAME = "jsu_sp24_v1.csv";
    private final String JSON_FILENAME = "jsu_sp24_v1.json";
    
    private final String CRN_COL_HEADER = "crn";
    private final String SUBJECT_COL_HEADER = "subject";
    private final String NUM_COL_HEADER = "num";
    private final String DESCRIPTION_COL_HEADER = "description";
    private final String SECTION_COL_HEADER = "section";
    private final String TYPE_COL_HEADER = "type";
    private final String CREDITS_COL_HEADER = "credits";
    private final String START_COL_HEADER = "start";
    private final String END_COL_HEADER = "end";
    private final String DAYS_COL_HEADER = "days";
    private final String WHERE_COL_HEADER = "where";
    private final String SCHEDULE_COL_HEADER = "schedule";
    private final String INSTRUCTOR_COL_HEADER = "instructor";
    private final String SUBJECTID_COL_HEADER = "subjectid";
    
    public String convertCsvToJsonString(List<String[]> csv) {
        
        // Create JSON Containers
        JsonObject json = new JsonObject();
            
        JsonObject scheduletypeMap = new JsonObject();
        JsonObject subjectMap = new JsonObject();
        JsonObject courseMap = new JsonObject();
        JsonArray sectionList = new JsonArray();
            
        // Iterator to loop through CSV rows
        Iterator<String[]> iterator = csv.iterator();

        // Getting the header row from the CSV data
        String[] headers = iterator.next();

        // Creating a mapping of header names to their respective indices
        HashMap<String, Integer> headerList = new HashMap<>();
        for (int i = 0; i < headers.length; ++i) {
            headerList.put(headers[i], i);
        }
        
        String jsonString = null;

        // Process each row using the iterator
        while (iterator.hasNext()) {
            String[] analyzingRow = iterator.next();

            // Retrieve data from the row using column headers
            String crnString = analyzingRow[headerList.get(CRN_COL_HEADER)];
            Integer crn = Integer.valueOf(crnString);
            String subject = analyzingRow[headerList.get(SUBJECT_COL_HEADER)];
            String num = analyzingRow[headerList.get(NUM_COL_HEADER)];
            String description = analyzingRow[headerList.get(DESCRIPTION_COL_HEADER)];
            String section = analyzingRow[headerList.get(SECTION_COL_HEADER)];
            String type = analyzingRow[headerList.get(TYPE_COL_HEADER)];
            String creditsString = analyzingRow[headerList.get(CREDITS_COL_HEADER)];
            Integer credits = Integer.valueOf(creditsString);
            String start = analyzingRow[headerList.get(START_COL_HEADER)];
            String end = analyzingRow[headerList.get(END_COL_HEADER)];
            String days = analyzingRow[headerList.get(DAYS_COL_HEADER)];
            String where = analyzingRow[headerList.get(WHERE_COL_HEADER)];
            String schedule = analyzingRow[headerList.get(SCHEDULE_COL_HEADER)];
            String instructor = analyzingRow[headerList.get(INSTRUCTOR_COL_HEADER)];    
            String[] courseParts = num.split("\\s+", 2);
            String trimmednum1 = courseParts[0]; // This will contain "ACC", as an example of splitting 
            String trimmednum2 = courseParts.length > 1 ?    courseParts[1] : ""; // This will contain "200",  as an example of splitting

            // Next I populated the courseMap
            JsonObject populatedcourseMap = new JsonObject();
            populatedcourseMap.put(SUBJECTID_COL_HEADER, trimmednum1);
            populatedcourseMap.put(NUM_COL_HEADER, trimmednum2);
            populatedcourseMap.put(DESCRIPTION_COL_HEADER, description);
            populatedcourseMap.put(CREDITS_COL_HEADER, credits);

            courseMap.put(num,populatedcourseMap);

            // Splitting instructors by comma and create a list
            List<String> instructorList = Arrays.asList(instructor.split(", "));

            // Next I will populate the sectionList
            JsonObject populatedsectionList = new JsonObject();
            populatedsectionList.put(CRN_COL_HEADER, crn);
            populatedsectionList.put(SUBJECTID_COL_HEADER, trimmednum1);
            populatedsectionList.put(NUM_COL_HEADER, trimmednum2);
            populatedsectionList.put(SECTION_COL_HEADER, section);
            populatedsectionList.put(TYPE_COL_HEADER, type);
            populatedsectionList.put(START_COL_HEADER, start);
            populatedsectionList.put(END_COL_HEADER, end);
            populatedsectionList.put(DAYS_COL_HEADER, days);
            populatedsectionList.put(WHERE_COL_HEADER, where);
            populatedsectionList.put(INSTRUCTOR_COL_HEADER, instructorList);

            sectionList.add(populatedsectionList);  

            scheduletypeMap.put(type, schedule);
            subjectMap.put(trimmednum1, subject);
            
            //  Adding elements to the JSON object
            json.put("scheduletype", scheduletypeMap);
            json.put("subject", subjectMap);
            json.put("course", courseMap);
            json.put("section", sectionList);
            
            jsonString = Jsoner.serialize(json);
            
        }
        return jsonString;
    }
           
    public String convertJsonToCsvString(JsonObject json) {
        
        // Extracting JSON Objects 
        JsonObject scheduletype = (JsonObject) json.get("scheduletype");
        JsonObject subjects = (JsonObject) json.get("subject");
        JsonObject courses = (JsonObject) json.get("course");
        JsonArray sections = (JsonArray) json.get("section");

        // Holding csv files as a string
        String csvString;
        
        // Initializing StringWriter and storing CSV data
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        
        // CSV header
        csvWriter.writeNext(new String[]{CRN_COL_HEADER, SUBJECT_COL_HEADER, NUM_COL_HEADER, DESCRIPTION_COL_HEADER, SECTION_COL_HEADER, TYPE_COL_HEADER, CREDITS_COL_HEADER, START_COL_HEADER, END_COL_HEADER, DAYS_COL_HEADER, WHERE_COL_HEADER, SCHEDULE_COL_HEADER, INSTRUCTOR_COL_HEADER});
        
        
        // Extracting details from JSON data
        for (Object sectionObj : sections) {
            JsonObject section = (JsonObject) sectionObj;
            String crn = String.valueOf(section.get(CRN_COL_HEADER));
            String subjectID = (String) section.get(SUBJECTID_COL_HEADER);
            String subject = (String) subjects.get(subjectID);
            String justnum = (String) section.get(NUM_COL_HEADER);
            String num = subjectID + " " + justnum;
            JsonObject innercourse = (JsonObject) courses.get(num);
            
            String description = (String) innercourse.get(DESCRIPTION_COL_HEADER);
            String sectionId = (String) section.get(SECTION_COL_HEADER);
            String type = (String) section.get(TYPE_COL_HEADER);
            BigDecimal creditsValue = (BigDecimal) innercourse.get(CREDITS_COL_HEADER);
            String credits = creditsValue.toString();
            String start = (String) section.get(START_COL_HEADER);
            String end = (String) section.get(END_COL_HEADER);
            String days = (String) section.get(DAYS_COL_HEADER);
            String where = (String) section.get(WHERE_COL_HEADER);
            String schedule = (String) scheduletype.get(type); 
            JsonArray instructorsArray = (JsonArray) section.get(INSTRUCTOR_COL_HEADER);
            
            List<String> instructorsList = new ArrayList<>();
            for (Object instructorObj : instructorsArray) {
                instructorsList.add(instructorObj.toString());
            }
            
            // Assigning to to the variable instructor
            String instructor = String.join(", ", instructorsList);
            
            // Write CSV row
            csvWriter.writeNext(new String[]{crn, subject, num, description, sectionId, type, credits, start, end, days, where, schedule, instructor});
        }
            // Assignment to the variable csvString
            csvString = writer.toString();

            return csvString;
    }
    
    public JsonObject getJson() {
        
        JsonObject json = getJson(getInputFileData(JSON_FILENAME));
        return json;
        
    }
    
    public JsonObject getJson(String input) {
        
        JsonObject json = null;
        
        try {
            json = (JsonObject)Jsoner.deserialize(input);
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return json;
        
    }
    
    public List<String[]> getCsv() {
        
        List<String[]> csv = getCsv(getInputFileData(CSV_FILENAME));
        return csv;
        
    }
    
    public List<String[]> getCsv(String input) {
        
        List<String[]> csv = null;
        
        try {
            
            CSVReader reader = new CSVReaderBuilder(new StringReader(input)).withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build();
            csv = reader.readAll();
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return csv;
        
    }
    
    public String getCsvString(List<String[]> csv) {
        
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        
        csvWriter.writeAll(csv);
        
        return writer.toString();
        
    }
    
    private String getInputFileData(String filename) {
        
        StringBuilder buffer = new StringBuilder();
        String line;
        
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        
        try {
        
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("resources" + File.separator + filename)));

            while((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return buffer.toString();
        
    }
    
}