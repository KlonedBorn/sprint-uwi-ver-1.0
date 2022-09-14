package sprint;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalTime;
public class App 
{
    static String weekdays[] =  new String[] {
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday"
    };
    /**
     * Command line argument flags.
     * -f | --path : path to excel file
     * -s | --sheet : the name of the sheet to read from
     * -l | --list : a comma seperated list of courses to filter the excel file by.
     * -u | --show : print faculty names.
     * Long formats might not be working as yet
     * @param args
     */
    public static void main( String[] args )
    {
        joptsimple.OptionParser optparse= new joptsimple.OptionParser(){
            {
                accepts("f").withRequiredArg().ofType(String.class).withValuesSeparatedBy(File.pathSeparatorChar);
                accepts("s").withRequiredArg().ofType(String.class).defaultsTo("v1 - Timetable ");
                accepts("l").withOptionalArg().ofType(String.class).defaultsTo("CODE1001");
                accepts("u");
                accepts("h");
                accepts("e").withOptionalArg().ofType(String.class).defaultsTo("NO_FILE");
                accepts("v").withOptionalArg().ofType(Boolean.class).defaultsTo(true);
                /*              
                accepts("file").withRequiredArg().ofType(String.class).withValuesSeparatedBy(File.pathSeparatorChar);
                accepts("sheet").withRequiredArg().ofType(String.class).defaultsTo("v1 - Timetable ");
                accepts("list").withOptionalArg().ofType(String.class);
                accepts("show");
                accepts("help");
                accepts("export");
                accepts("verbose");
                */
            }
        };
        OptionSet optset = optparse.parse(args);
        Map<OptionSpec<?>,List<?>> optMap = optset.asMap();
        String excelFilePath ="", excelSheetName="", exportFilePath = "";
        boolean bShowFaculty = false, bVerbose = false;
        List<String> courseCodes=Arrays.asList("Nothing");
        for( OptionSpec<?> opt : optMap.keySet() ) {
            String optstring = opt.toString();
            switch(optstring){
                case "[f]" : case "[file]" : {
                    excelFilePath = (String)optMap.get(opt).get(0);
                } break;
                case "[s]" : case "[sheet]" : {
                    excelSheetName = (String)optMap.get(opt).get(0);
                } break;
                case "[u]" : case "[show]" : {
                    if( !optset.hasArgument(opt) ) continue;
                    bShowFaculty = true;
                } break;
                case "[h]" : case "[help]" : {
                    if( !optset.hasArgument(opt) ) continue;
                    try {
                        FileInputStream helpFileStream = new FileInputStream( new File("src\\main\\java\\sprint\\help.txt") );   
                        Scanner fsc = new Scanner(helpFileStream);
                        while(fsc.hasNextLine()){
                            System.out.println(fsc.nextLine());
                        }
                        helpFileStream.close();
                        fsc.close();
                    } catch (IOException fe) {
                        System.err.println("ERROR: help documentation could not be loaded");
                        System.exit(-1);
                    }
                } break;
                case "[l]" : case "[list]" : {
                    if( !optset.hasArgument(opt) ) continue;
                    String codes[] = ((String)optMap.get(opt).get(0)).split(",");
                    courseCodes = Arrays.asList(codes);
                } break;
                case "[e]" : {
                    exportFilePath = (String)optMap.get(opt).get(0);
                } break;
                case "[v]" : {
                    bVerbose = (Boolean)optMap.get(opt).get(0);
                } break;
            }
        }
        ArrayList<Course> courseList = new ArrayList<>();
        String coursePattern = courseCodes.stream().collect(Collectors.joining("|"));
        try {
            FileInputStream excelFileStream = new FileInputStream(excelFilePath);
            Workbook wb = new XSSFWorkbook(excelFileStream);
            Sheet sh = wb.getSheet(excelSheetName);
            Iterator<Row> rowIt = sh.iterator();
            String prevWeekDay = "" , prevFaculty = "";
            while(rowIt.hasNext()) {
                Iterator<Cell> cellIt = rowIt.next().iterator();
                while(cellIt.hasNext()) {
                    Cell cell = cellIt.next();
                    String content = "";
                    try{
                        content = cell.getStringCellValue().strip();
                    }catch(Exception e){continue;};
                    if( !content.isBlank() ){
                        int intOff = cell.getColumnIndex() - 2; 
                        // is content a weekday?
                        if( content.matches("(?:Mon|Tues|Wednes|Thurs|Fri)day") )
                            prevWeekDay = content;
                        // is content a faculty?
                        if( content.matches("So(?:SCAI|BM|HE|HBS)"))
                            prevFaculty = content;
                        // is content a course?
                        if( content.matches("(?:"+coursePattern+").*") ){
                            int hours = 7 + (int)Math.floor(intOff * 0.5);
                            int minutes = 420 + (intOff * 30);
                            LocalTime time = new LocalTime( hours , minutes % 60);
                            courseList.add(new Course(getWeekDay(prevWeekDay),time,prevFaculty,content));
                        }
                    }
                }
            }
            wb.close();
        } catch (IOException e) { e.printStackTrace();};
        Collections.sort(courseList,new Course());
        PrintStream courseOut = null;
        try {
            if(exportFilePath != "NO_FILE") courseOut = new PrintStream(new File(exportFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            courseOut = null;
        }
        for(int wd = 0 ; wd < weekdays.length ; wd++) {
            boolean bHeadWritten = false;
            for( Course c : courseList ) {
                if(!bHeadWritten)
                {
                    if(bVerbose) 
                        System.out.println(weekdays[wd]);
                    if(courseOut != null) 
                        courseOut.println(weekdays[wd]);
                    bHeadWritten = true;
                }
                if( c.getWeekday() == wd ){
                    String line = "\t" + (bShowFaculty?c.getFaculty():"") + c.getTime().toString("hh:mm aa ") + " - " + c.getInfo();
                    if(bVerbose) 
                        System.out.println(line);
                    if(courseOut != null) 
                        courseOut.println(line);
                }
            }
        }
    }
    static public int getWeekDay(String d){
        
        for(int i = 0 ; i <weekdays.length;i++){
            if( weekdays[i].equalsIgnoreCase(d) ) return i;
        }
        return -1;
    }

    static public class Course implements Comparator<Course> {
        private String faculty, info;
        private int weekday;
        private LocalTime time;
        public Course(){}
        public Course(int weekday, LocalTime time, String faculty, String info) {
            this.weekday = weekday;
            this.time = time;
            this.faculty = faculty;
            this.info = info;
        }
        public int getWeekday() {
            return weekday;
        }

        public void setWeekday(int weekday) {
            this.weekday = weekday;
        }

        public LocalTime getTime() {
            return time;
        }
        public void setTime(LocalTime time) {
            this.time = time;
        }

        public String getFaculty() {
            return faculty;
        }

        public void setFaculty(String faculty) {
            this.faculty = faculty;
        }
        
        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
        @Override
        public int compare(Course o1, Course o2) {
            return (o1.getWeekday() * 10000 + o1.getTime().get(DateTimeFieldType.minuteOfDay())) - (o2.getWeekday() * 10000 + o2.getTime().get(DateTimeFieldType.minuteOfDay()));
        }
    }
}