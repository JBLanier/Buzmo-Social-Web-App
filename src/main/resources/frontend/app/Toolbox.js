export function CSVSplit(csv, delim=",") {
    csv = csv.split(delim);
    if (csv.length == 1 && csv[0] === "") {
        return [];
    }
    return csv;
}