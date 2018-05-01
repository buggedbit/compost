function truncate(string, maxLength) {
    let label;
    if (string.length > maxLength) {
        label = string.substring(0, maxLength) + '...';
    }
    else {
        label = string;
    }
    return label;
}
