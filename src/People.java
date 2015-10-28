import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zach on 10/19/15.
 */
public class People {
    public static void main(String[] args) {
        ArrayList<Person> people = new ArrayList();

        String fileContent = readFile("people.csv");
        String[] lines = fileContent.split("\n");

        for (String line : lines) {
            if (line == lines[0])
                continue;

            String[] columns = line.split(",");
            Person person = new Person(Integer.valueOf(columns[0]), columns[1], columns[2], columns[3], columns[4], columns[5]);
            people.add(person);
        }

        Spark.get(
                "/",
                ((request, response) -> {
                    String pageNum = request.queryParams("nextP");
                    int nextP;
                    if (pageNum == null){
                        nextP = 0;
                    }
                    else{
                        nextP = Integer.valueOf(pageNum);
                    }
                    if (!(nextP<people.size())) {
                        Spark.halt(403);
                        }
                    else{
                        ArrayList<Person> tempPeople = new ArrayList(people.subList(nextP, nextP + 20));
                        HashMap m = new HashMap();
                        m.put("people", tempPeople);
                        m.put("newNextP", nextP + 20);
                        return new ModelAndView(m, "people.html");
                    }
                    return new ModelAndView(new HashMap<>(), "people.html");
                }),
                new MustacheTemplateEngine()
        );
    }

    static String readFile(String fileName) {
        File f = new File(fileName);
        try {
            FileReader fr = new FileReader(f);
            int fileSize = (int) f.length();
            char[] fileContent = new char[fileSize];
            fr.read(fileContent);
            return new String(fileContent);
        } catch (Exception e) {
            return null;
        }
    }
}
