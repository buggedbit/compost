# dupe

## description
- A simple tool to easily pool your files from different sources to a single destination avoiding unnecessary duplicates.
    - It uses paths and timestamps to detect duplicates.
    - Therefore it does not gurante the existence of all files from all sources in the destination.
    - But this works in most cases.
- The intented use case is to pool media from different devices to a backup device.

## roadmap
- [x] Supports file level and directory level duplication.
- [x] Prints log of operations done on each file.
- [ ] Use md5 hash.

## code
- Code is written in `C`.

## documentation
- The documentation of code is itself.

## usage
- To compile use `gcc -Wall dupe.c -o dupe`.
- To pool from source dir to destination dir use `./dupe srcdir/ dstdir/`.
    - For example, if src has 3 files of which 2 already exist in dst dir, only 1 new file is copied.
- To pool from source file to destination file use `./dupe -f srcfile dstdir/dstfile`.
    - The dstfile name has to be given i.e. `./dupe -f srcfile dstdir/` will not automatically create a `dstdir/srcfile`.
