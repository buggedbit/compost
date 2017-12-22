#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/time.h>

int digitlen(unsigned long int l) {
	int length = 0;
	while (l > 0) {
		length++;
		l = l / 10;
	}
	return length;
}

char* ltoa(unsigned long int l)
{
	int length = digitlen(l);
	char* ans = (char *) malloc((length + 1) * sizeof(char));
	ans[length] = '\0';

	while (length > 0) {
		ans[length - 1] = l % 10 + '0';
		l = l / 10;
		length--;
	}
	return ans;
}

char* join(char* a, char* b, char* c, char delimiter) {
	int lena = strlen(a);
	int lenb = strlen(b);
	int lenc = strlen(c);
	int totalLength = lena + lenb + lenc + 2; // 2 delimiters
	char* ans = (char *) malloc((totalLength + 1) * sizeof(char)); // NULL character
	ans[totalLength] = '\0';
	for (int i = 0; i < lena; ++i) {
		ans[i] = a[i];
	}
	ans[lena] = delimiter;
	for (int i = 0; i < lenb; ++i) {
		ans[i + lena + 1] = b[i];
	}
	ans[lena + lenb + 1] = delimiter;
	for (int i = 0; i < lenc; ++i) {
		ans[i + lena + lenb + 2] = c[i];
	}
	return ans;
}

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
		2. If destination already exists but modified timestamp donot match
				Checks existence of augmented destination path (destinationpath.seconds.nanoseconds)
					if that also exists, doesnot copy
					else, copies to augmented destination path (destinationpath.seconds.nanoseconds)
		3. Preserves modified timestamps
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
		struct stat destinationStat;
		if (stat(destinationPath, &destinationStat)) {
			fclose(source);
			fprintf(stderr, "Error while getting status of file : %s\n", destinationPath);
			exit(EXIT_FAILURE);
		}

		char* sourceModifiedSecondsStr = ltoa(sourceStat.st_mtim.tv_sec);
		char* sourceModifiedNanosecondsStr = ltoa(sourceStat.st_mtim.tv_nsec);
		char* augmentedDestinationPath = join(destinationPath, sourceModifiedSecondsStr, sourceModifiedNanosecondsStr, '.');
		free(sourceModifiedSecondsStr);
		free(sourceModifiedNanosecondsStr);

		// if modified timestamps of source and destination are different
		if (sourceStat.st_mtim.tv_sec != destinationStat.st_mtim.tv_sec || sourceStat.st_mtim.tv_nsec != destinationStat.st_mtim.tv_nsec) {
			int augmentedDestinationAlreadyExists = (access(augmentedDestinationPath, F_OK) != -1);
			if (!augmentedDestinationAlreadyExists) {
				// then this is a different file
				// open augmented destination file
				destination = fopen(augmentedDestinationPath, "wb");
				if (destination == NULL) {
					fclose(source);
					free(augmentedDestinationPath);
					fprintf(stderr, "Cannot open destination file : %s\n", destinationPath);
					exit(EXIT_FAILURE);
				}
				// copy data
				if (copy(source, destination)) {
					fclose(source);
					fclose(destination);
					free(augmentedDestinationPath);
					fprintf(stderr, "Error while copying file : %s\n", sourcePath);
					exit(EXIT_FAILURE);
				}
				// close source and destination files
				fclose(destination);

				// set access and modified timestamps of source file to destination file
				if (utimes(augmentedDestinationPath, sourceTimes)) {
					fclose(source);
					free(augmentedDestinationPath);
					fprintf(stderr, "Error while setting timestamps of file : %s\n", destinationPath);
					exit(EXIT_FAILURE);
				}
			}
		}
		free(augmentedDestinationPath);
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
		fclose(destination);

		// set access and modified timestamps of source file to destination file
		if (utimes(destinationPath, sourceTimes)) {
			fclose(source);
			fprintf(stderr, "Error while setting timestamps of file : %s\n", destinationPath);
			exit(EXIT_FAILURE);
		}
	}
	fclose(source);

	return 0;
}
