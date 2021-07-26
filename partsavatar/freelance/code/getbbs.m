%% Returns biggest n(= min(bbLimit, noBoundingBoxes)) of an image given it's logical segmented image(J)
function bbs = getbbs(J, areaTh, bbLimit)
	regionProps = regionprops(J, 'BoundingBox', 'Area');
	dimRegionProps = size(regionProps);
	if dimRegionProps(1) == 0
		bbs = [];
		return;
	end
	% sort region props based on area
	cells = struct2cell(regionProps);
	keyCells = cells(1, :, 1);
	keyMatrix = cell2mat(keyCells); 
	keyMatrix = squeeze(keyMatrix); 
	[sortedAreas, sortedIndex] = sort(keyMatrix, 'descend'); 
	sortedRegionProps = regionProps(sortedIndex);
	% filter out small bounding boxes
	excludedRPs = sortedAreas < (sortedAreas(1) * areaTh);
	sortedRegionProps(excludedRPs) = [];
	% limit bounding boxes to given limit param
	sortedBBs = cat(1, sortedRegionProps.BoundingBox);
	dimSortedBBs = size(sortedBBs);
	noSortedBBs = dimSortedBBs(1);
	if bbLimit == 0
		bbLimit = noSortedBBs;
	else
		bbLimit = min(bbLimit, noSortedBBs);
	end
	filteredBBs = sortedBBs(1 : bbLimit, :);
	bbs = filteredBBs;
