#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>

/**
return 0 on success, 1 on error
*/
int copy(FILE *source, FILE *destination)
{
	unsigned char buf[256];
	size_t size;
	while ((size = fread(buf, 1, sizeof(buf), source)) > 0) {
		fwrite(buf, 1, size, destination);
	}

	if (ferror(source) || !feof(source)) {
		return 1;
	}
	return 0;
}

/**
Basic Duplicate
Validates source path and destination path
	1. Asserts that they are different
Doesnot copy if destination already exists
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

	int destinationAlreadyExists = (access(destinationPath, F_OK) != -1);

	if (destinationAlreadyExists) {
		// struct stat sourceStat;
		// if (stat(sourcePath, &sourceStat)) {
		// 	fclose(source);
		// 	fclose(destination);
		// 	fprintf(stderr, "Error while getting status of file : %s\n", sourcePath);
		// 	exit(EXIT_FAILURE);
		// }
		// struct stat destinationStat;
		// if (stat(destinationPath, &destinationStat)) {
		// 	fclose(source);
		// 	fclose(destination);
		// 	fprintf(stderr, "Error while getting status of file : %s\n", destinationPath);
		// 	exit(EXIT_FAILURE);
		// }
		fclose(source);
	} else {
		destination = fopen(destinationPath, "wb");
		if (destination == NULL) {
			fclose(source);
			fprintf(stderr, "Cannot open destination file : %s\n", destinationPath);
			exit(EXIT_FAILURE);
		}
		if (copy(source, destination)) {
			fclose(source);
			fclose(destination);
			fprintf(stderr, "Error while copying file : %s\n", sourcePath);
			exit(EXIT_FAILURE);
		}
		
		fclose(source);
		fclose(destination);
	}

	return 0;
}
