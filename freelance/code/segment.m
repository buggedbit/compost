% returns segmented logical image (uses thresholding technique)
% If I is grayscale, GrayTh is used as threshold
% If I is RGB image, RedTh, GreenTh, BlueTh are used as thresholds
function J = segment(I, RedTh, GreenTh, BlueTh, GrayTh)
	dimensions = size(I);
	sizeOfDimensions = size(dimensions);
	noDimensions = sizeOfDimensions(2);
	% Grayscale image
	if noDimensions == 1
		J = graysegment(I, GrayTh);
	% RGB image
	elseif noDimensions == 3
		J = rgbsegment(I, RedTh, GreenTh, BlueTh);
	else
		J = [];
	end
