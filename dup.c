#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/time.h>

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
	Duplicate a file
		1. Asserts source path and destination path are different
		2. Doesnot copy if destination already exists
		3. Preserves access and modified timestamps
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
	// get timestamps of source
	struct stat sourceStat;
	if (stat(sourcePath, &sourceStat)) {
		fclose(source);
		fprintf(stderr, "Error while getting status of file : %s\n", sourcePath);
		exit(EXIT_FAILURE);
	}
	struct timeval sourceTimes[2];
	sourceTimes[0].tv_sec = sourceStat.st_atim.tv_sec;
	sourceTimes[0].tv_usec = sourceStat.st_atim.tv_nsec / 1000;
	sourceTimes[1].tv_sec = sourceStat.st_mtim.tv_sec;
	sourceTimes[1].tv_usec = sourceStat.st_mtim.tv_nsec / 1000;

	// check if destination file already exists
	int destinationAlreadyExists = (access(destinationPath, F_OK) != -1);

	if (destinationAlreadyExists) {
		// struct stat destinationStat;
		// if (stat(destinationPath, &destinationStat)) {
		// 	fclose(source);
		// 	fclose(destination);
		// 	fprintf(stderr, "Error while getting status of file : %s\n", destinationPath);
		// 	exit(EXIT_FAILURE);
		// }
		fclose(source);
	} else {
		// open destination file
		destination = fopen(destinationPath, "wb");
		if (destination == NULL) {
			fclose(source);
			fprintf(stderr, "Cannot open destination file : %s\n", destinationPath);
			exit(EXIT_FAILURE);
		}
		// copy data
		if (copy(source, destination)) {
			fclose(source);
			fclose(destination);
			fprintf(stderr, "Error while copying file : %s\n", sourcePath);
			exit(EXIT_FAILURE);
		}
		// close source and destination files
		fclose(source);
		fclose(destination);

		// set access and modified timestamps of source file to destination file
		if (utimes(destinationPath, sourceTimes)) {
			fprintf(stderr, "Error while setting timestamps of file : %s\n", destinationPath);
			exit(EXIT_FAILURE);
		}
	}

	return 0;
}
