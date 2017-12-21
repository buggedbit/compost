#include <stdio.h>
#include <stdlib.h>

/*

Basic copy
Overwrites destination file if already exists

*/

int main(int argc, char *argv[])
{
	// argument validation
	if (argc != 3) {
		printf("Usage: %s <source-file-path> <destination-file-path>\n", argv[0]);
		exit(EXIT_FAILURE);
	}

	char *sourcePath = argv[1];
	char *destinationPath = argv[2];
	FILE *source, *destination;

	source = fopen(sourcePath, "rb");
	if (source == NULL) {
		printf("Cannot open source file : %s\n", sourcePath);
		exit(EXIT_FAILURE);
	}
	// get file size
	fseek(source, 0, SEEK_END);
	int fileSize = ftell(source);
	fseek(source, 0, SEEK_SET);
	
	destination = fopen(destinationPath, "w");
	if (destination == NULL) {
		fclose(source);
		printf("Cannot open destination file : %s\n", destinationPath);
		exit(EXIT_FAILURE);
	}

	for (int i = 0; i < fileSize; ++i) {
		fputc(fgetc(source), destination);
	}

	fclose(source);
	fclose(destination);

	return 0;
}
