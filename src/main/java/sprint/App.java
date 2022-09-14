package sprint;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * Hello world!
 */
public class App 
{
    /**
     * Command line argument flags.
     * -f | --path : path to excel file
     * -s | --sheet : the name of the sheet to read from
     * -l | --list : a comma seperated list of courses to filter the excel file by.
     * -u | --show : print faculty names.
     * @param args
     */
    public static void main( String[] args )
    {
        joptsimple.OptionParser optparse= new joptsimple.OptionParser(){
            {
                accepts("f").withRequiredArg().ofType(String.class).withValuesSeparatedBy(File.pathSeparatorChar);
                accepts("s").withRequiredArg().ofType(String.class).defaultsTo("v1 - Timetable ");
                accepts("l").withOptionalArg().ofType(String.class);
                accepts("u");
                accepts("h");
                /*
                accepts("file").withRequiredArg().required();
                accepts("sheet").withOptionalArg().defaultsTo("v1 - Timetable ");
                accepts("list");
                accepts("show"); 
                accepts("help");
                */
            }
        };
        Map<OptionSpec<?>,List<?>> optMap = optparse.parse(args).asMap();
        String excelFilePath ="", excelSheetName="";
        boolean bShowFaculty = false;
        List<String> courseCodes=Arrays.asList("Nothing");
        for( OptionSpec<?> opt : optMap.keySet() ) {
            String optstring = opt.toString();
            switch(optstring){
                case "[f]" : {
                    excelFilePath = (String)optMap.get(opt).get(0);
                } break;
                case "[s]" : {
                    excelSheetName = (String)optMap.get(opt).get(0);
                } break;
                case "[u]" : {
                    bShowFaculty = true;
                } break;
                case "[h]" : {
                    System.out.println("Print help");
                } break;
                case "[l]" : {
                    String codes[] = ((String)optMap.get(opt).get(0)).split(",");
                    courseCodes = Arrays.asList(codes);
                } break;
            }
        }
    }
}
