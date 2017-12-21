#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/*

Basic copy
Validates source path and destination path
	1. Asserts that they are different
Overwrites destination file if already exists

*/

int main(int argc, char *argv[])
{
	// argument validation
	if (argc != 3) {
		fprintf(stderr, "Usage: %s <source-file-path> <destination-file-path>\n", argv[0]);
		exit(EXIT_FAILURE);
	}
	char *sourcePath = argv[1];
	char *destinationPath = argv[2];
	// source path and destination path cannot be equal
	if (strcmp(sourcePath, destinationPath) == 0) {
		fprintf(stderr, "source path and destination path cannot be same\n");
		exit(EXIT_FAILURE);		
	}
	
	FILE *source, *destination;

	source = fopen(sourcePath, "rb");
	if (source == NULL) {
		fprintf(stderr, "Cannot open source file : %s\n", sourcePath);
		exit(EXIT_FAILURE);
	}

	destination = fopen(destinationPath, "wb");
	if (destination == NULL) {
		fclose(source);
		fprintf(stderr, "Cannot open destination file : %s\n", destinationPath);
		exit(EXIT_FAILURE);
	}

	unsigned char buf[256];
	size_t size;
	while ((size = fread(buf, 1, sizeof(buf), source)) > 0) {
		fwrite(buf, 1, size, destination);
	}

	if (ferror(source) || !feof(source)) {
		fclose(source);
		fclose(destination);
		fprintf(stderr, "Error while copying file : %s\n", sourcePath);
		exit(EXIT_FAILURE);
	}

	fclose(source);
	fclose(destination);

	return 0;
}
