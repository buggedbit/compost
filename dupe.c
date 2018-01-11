#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <dirent.h>
#include <sys/types.h>

#define MAX_PATH_LENGTH 4096

#define COPY_BUFFER_SIZE 4096

#define SAME_SOURCE_AND_DESTINATION 1
#define CANNOT_OPEN_FILE 2
#define CANNOT_GET_STATUS_OF_FILE 3
#define CANNOT_SET_TIMESTAMPS_OF_FILE 4
#define ERROR_WHILE_COPY 5

int digitlen(unsigned long int l)
{
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

char* join(const char* a, const char* b, const char* c, char delimiter)
{
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
	unsigned char buf[COPY_BUFFER_SIZE];
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
		returns 0 on successful duplication
		else returns status code
*/
int dupef(const char* sourcePath, const char* destinationPath)
{
	// source path and destination path cannot be equal
	if (strcmp(sourcePath, destinationPath) == 0) {
		fprintf(stderr, "Source path and Destination path cannot be same\n");
		return SAME_SOURCE_AND_DESTINATION;		
	}
	
	FILE *source, *destination;

	source = fopen(sourcePath, "rb");
	if (source == NULL) {
		fprintf(stderr, "Cannot open source file : %s\n", sourcePath);
		return CANNOT_OPEN_FILE;
	}
	// get timestamps of source
	struct stat sourceStat;
	if (stat(sourcePath, &sourceStat)) {
		fclose(source);
		fprintf(stderr, "Cannot get status of file : %s\n", sourcePath);
		return CANNOT_GET_STATUS_OF_FILE;
	}
	// precision is cutdown to microsec because of struct timeval can handle only upto microseconds
	long int sa_sec = sourceStat.st_atim.tv_sec;						// source access sec
	long int sa_usec = sourceStat.st_atim.tv_nsec / 1000;		// source acccess micro seconds
	long int sm_sec = sourceStat.st_mtim.tv_sec;						// source modified sec
	long int sm_usec = sourceStat.st_mtim.tv_nsec / 1000;		// source modified micro seconds

	struct timeval sourceTimes[2];
	sourceTimes[0].tv_sec = sa_sec;
	sourceTimes[0].tv_usec = sa_usec;
	sourceTimes[1].tv_sec = sm_sec;
	sourceTimes[1].tv_usec = sm_usec;

	// check if destination file already exists
	int destinationAlreadyExists = (access(destinationPath, F_OK) != -1);

	if (destinationAlreadyExists) {
		struct stat destinationStat;
		if (stat(destinationPath, &destinationStat)) {
			fclose(source);
			fprintf(stderr, "Cannot get status of file : %s\n", destinationPath);
			return CANNOT_GET_STATUS_OF_FILE;
		}
		// precision is cutdown to microsec because of struct timeval can handle only upto microseconds
		long int dm_sec = destinationStat.st_mtim.tv_sec;
		long int dm_usec = destinationStat.st_mtim.tv_nsec / 1000;

		char* sourceModifiedSecondsStr = ltoa(sm_sec);
		char* sourceModifiedNanosecondsStr = ltoa(sm_usec);
		char* augmentedDestinationPath = join(destinationPath, sourceModifiedSecondsStr, sourceModifiedNanosecondsStr, '.');
		free(sourceModifiedSecondsStr);
		free(sourceModifiedNanosecondsStr);

		// if modified timestamps of source and destination are different
		if (sm_sec != dm_sec || sm_usec != dm_usec) {
			int augmentedDestinationAlreadyExists = (access(augmentedDestinationPath, F_OK) != -1);
			if (!augmentedDestinationAlreadyExists) {
				// then this is a different file
				// open augmented destination file
				destination = fopen(augmentedDestinationPath, "wb");
				if (destination == NULL) {
					fclose(source);
					free(augmentedDestinationPath);
					fprintf(stderr, "Cannot open destination file : %s\n", augmentedDestinationPath);
					return CANNOT_OPEN_FILE;
				}
				// copy data
				if (copy(source, destination)) {
					fclose(source);
					fclose(destination);
					free(augmentedDestinationPath);
					fprintf(stderr, "Error while copying file : %s\n", sourcePath);
					return ERROR_WHILE_COPY;
				}
				// close source and destination files
				fclose(destination);

				// set access and modified timestamps of source file to destination file
				if (utimes(augmentedDestinationPath, sourceTimes)) {
					fclose(source);
					free(augmentedDestinationPath);
					fprintf(stderr, "Cannot set timestamps of file : %s\n", augmentedDestinationPath);
					return CANNOT_SET_TIMESTAMPS_OF_FILE;
				}
				printf("Conflict resolved using augmented timestamp method\n\t%s\t->\t%s\n", sourcePath, augmentedDestinationPath);
			} else {
				printf("Dupe already exists for : %s\t->\t%s\n", sourcePath, augmentedDestinationPath);
			}
		} else {
			printf("Dupe already exists for : %s\t->\t%s\n", sourcePath, destinationPath);
		}
		free(augmentedDestinationPath);
	} else {
		// open destination file
		destination = fopen(destinationPath, "wb");
		if (destination == NULL) {
			fclose(source);
			fprintf(stderr, "Cannot open destination file : %s\n", destinationPath);
			return CANNOT_OPEN_FILE;
		}
		// copy data
		if (copy(source, destination)) {
			fclose(source);
			fclose(destination);
			fprintf(stderr, "Error while copying file : %s\n", sourcePath);
			return ERROR_WHILE_COPY;
		}
		// close source and destination files
		fclose(destination);

		// set access and modified timestamps of source file to destination file
		if (utimes(destinationPath, sourceTimes)) {
			fclose(source);
			fprintf(stderr, "Cannot set timestamps of file : %s\n", destinationPath);
			return CANNOT_SET_TIMESTAMPS_OF_FILE;
		}
	}
	fclose(source);
	return 0;
}

void dupe(const char *sourceDir, const char *destinationDir)
{
	DIR *sourceDIR;
	struct dirent *dirEntry;

	if ((sourceDIR = opendir(sourceDir)) == NULL) {
		printf ("Cannot open directory: %s\n", sourceDir);
		return;
	}

	char srcPath[MAX_PATH_LENGTH];
	char destPath[MAX_PATH_LENGTH];
	struct stat st;
	while ((dirEntry = readdir(sourceDIR)) != NULL) {
		snprintf(srcPath, sizeof(srcPath), "%s/%s", sourceDir, dirEntry->d_name);
		snprintf(destPath, sizeof(destPath), "%s/%s", destinationDir, dirEntry->d_name);

		if (dirEntry->d_type == DT_DIR) {
			// dirEntry is a directory
			if (strcmp(dirEntry->d_name, ".") == 0 || strcmp(dirEntry->d_name, "..") == 0) {
				continue;
			}
			if (stat(destPath, &st) == -1) {
				if (mkdir(destPath, S_IRWXU | S_IRWXG | S_IROTH)) {
					fprintf(stderr, "Cannot create directory %s\n", destPath);
					return;
				}
				if (stat(srcPath, &st)) {
					fprintf(stderr, "Cannot get status of directory %s\n", srcPath);
					return;
				}
				if (chmod(destPath, st.st_mode)) {
					fprintf(stderr, "Cannot set permissions to directory %s\n", destPath);
					return;
				}
			}
			dupe(srcPath, destPath);
		} else {
			// dirEntry is a file		
			if (dupef(srcPath, destPath)) {
				fprintf(stderr, "**Some error has occured while duping %s\n", srcPath);
			}
		}
	}
	closedir(sourceDIR);
}

int main(int argc, char *argv[])
{
	// argument validation
	if (argc != 3) {
		fprintf(stderr, "Usage: %s <source-directory> <destination-directory>\n", argv[0]);
		exit(EXIT_FAILURE);
	}

	char *sourceDir = argv[1];
	char *destinationDir = argv[2];

	// removing additional / at the end if it exists
	if (sourceDir[strlen(sourceDir) - 1] == '/') {
		sourceDir[strlen(sourceDir) - 1] = '\0';
	}
	if (destinationDir[strlen(destinationDir) - 1] == '/') {
		destinationDir[strlen(destinationDir) - 1] = '\0';
	}

	DIR *dir;

	if ((dir = opendir(sourceDir)) == NULL) {
		fprintf(stderr, "Cannot open directory: %s\n", sourceDir);
		exit(EXIT_FAILURE);
	}
	closedir(dir);

	if ((dir = opendir(destinationDir)) == NULL) {
		fprintf(stderr, "Cannot open directory: %s\n", destinationDir);
		exit(EXIT_FAILURE);
	}
	closedir(dir);

	dupe(sourceDir, destinationDir);

	return 0;
}
