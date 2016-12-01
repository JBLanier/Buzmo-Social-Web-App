export function CSVSplit(csv, delim=",") {
    csv = csv.split(delim);
    if (csv.length == 1 && csv[0] === "") {
        return [];
    }
    return csv;
}

export function UTCToString(utc) {
    let options = {
        weekday: "long", year: "numeric", month: "short",
        day: "numeric", hour: "2-digit", minute: "2-digit"
    };

    return new Date(utc).toLocaleTimeString("en-us", options); // The 0 there is the key, which sets the date to the epoch
}

export function uniq(a) {
    if (!a.isArray) {
        return a;
    }
    var seen = {};
    return a.filter(function(item) {
        return seen.hasOwnProperty(item) ? false : (seen[item] = true);
    });
}