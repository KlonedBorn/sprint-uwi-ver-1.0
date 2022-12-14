# UWI - Sprint
Sprint is a Java application that filters the UWI-FIC official timetable by using a student's registered course codes.

## Usage
To invoke Sprint, edit the argument list inside of ['launch.json'](.vscode\launch.json) then start a task using launch.json.Futhermore, calls to Sprint can be made using the following code snippet in the terminal.
```console
    java -jar ./sprint.jar -f=path-to-timetable -l=list-of-course-codes -e=path-to-destination
```
## Documentation
### usage:
    Sprint reads a .xlsx file containing the date, faculty & time for each courses' lecture & tutorial sessions are. A selected list of course codes are used to filter these courses out of the schedule.  
N.B - As of (Wednesday 14 September, 2022) long formats are not implemented yet.
### options:
   ```markdown
        -f, --path path to .xlsx file to read from. Must have read permissions.
            e.g. -f="Schedule.xlsx"

        -s, --sheet the name of the sheet to read from the .xlsx file. Default is "v1 - Timetable"
            e.g. --sheet="Timetable"

        -l, --list a list of course codes to be extracted from the schedule. Must be comma seperated with no spaces.
            e.g. -l=COMP2351,foun1301,PhYc4951

        -u, --show prints the course's faculty as a header.
            e.g. -u

        -h, --help invokes the help documentation of software.
            e.g. --help

        -e, --export writes the output to a file.
            e.g. -e="kyle-king.txt"

        -v, --verbose print filtered courses to console.
            e.g. -v
   ```        

Apache License, Version 2.0, Copyright 2022 Kyle King