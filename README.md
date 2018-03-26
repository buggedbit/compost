Dupe is a tool to easily and safely pool your photos, videos and files from different devices to a backup device avoiding unncessary duplicates

### Compilation

``gcc -Wall dupe.c -o dupe``

### Usage

``./dupe srcdir/ dstdir/``
 
``./dupe -f srcfile dstdir/dstfile``

For example, if src has 3 files of which 2 already exist in dst dir, only 1 new file is copied

For ``-f`` option, the dstfile name has to be given i.e. ``./dupe -f srcfile destinationdir/`` will not automatically create a ``dstfile`` in ``dstdir``
