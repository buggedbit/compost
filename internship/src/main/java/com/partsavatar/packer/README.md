# OrderAllocation Packing Algorithm #

This algorihtm aims at optimizing the packing of a given order of items in some given box, trying to solve the famous 3D [bin packing problem](https://en.wikipedia.org/wiki/Bin_packing_problem). 

## Approach	##
#### Forward Propagation ####

The idea is to place the largest available item of the order in the left bottom behind corner of the box. Then fill the region above the placed item recursively. When an item is placed at left bottom behind corner of a box, this process creates L shaped unused surfaces. These are filled by back propagation.

#### Backward Propagation ####

Traverse backward along the unused L shaped surfaces, divide each unused surface into two rectangular surfaces, and fill these using forward propagation.


## BruteForce Algorithm (Slow) ##

This algorithm involves in using all orientations of the parts to check if a part fits in the given space.

## Best Orientation Algorithm (Fast) ##

This algorithm fills the part be trying to heuristically get the best orientation.
