#include "dupef.h"
#include <dirent.h>

void dupe(const char *sourceDir, const char *destinationDir)
{
	DIR *sourceDIR;
	struct dirent *dirEntry;

	if ((sourceDIR = opendir(sourceDir)) == NULL) {
		printf ("Cannot open directory: %s\n", sourceDir);
		return;
	}

	char srcPath[PATH_MAX];
	char destPath[PATH_MAX];
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
	if (argc == 3) {
		char sourceDir[PATH_MAX];
		char destinationDir[PATH_MAX];
		if (realpath(argv[1], sourceDir) == NULL) {
			fprintf(stderr, "Cannot resolve realpath directory: %s\n", argv[1]);
			exit(EXIT_FAILURE);		
		}
		if (realpath(argv[2], destinationDir) == NULL) {
			fprintf(stderr, "Cannot resolve realpath directory: %s\n", argv[2]);
			exit(EXIT_FAILURE);
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

	} else if (argc == 4 && strcmp(argv[1], "-f") == 0) {
		char *sourceFile = argv[2];
		char *destinationFile = argv[3];
		
		if (dupef(sourceFile, destinationFile)) {
			fprintf(stderr, "**Some error has occured while duping %s\n", sourceFile);
		}

	} else {
		fprintf(stderr, "Usage: %s <source-directory> <destination-directory>\n", argv[0]);
		fprintf(stderr, "Usage: %s -f <source-file-path> <destination-file-path>\n", argv[0]);
		exit(EXIT_FAILURE);		
	}

	return 0;
}
