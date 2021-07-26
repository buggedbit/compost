% returns segmented logical image (uses thresholding technique)
% I must be grayscale image
function J = rgbsegment(I, GrayTh)
	% Threshold segmentation
	IBW = im2bw(I, GrayTh);
	IComplement = imcomplement(IBW);
	IFilled = imfill(IComplement, 'holes');
	J = IFilled;
