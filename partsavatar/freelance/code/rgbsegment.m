% returns segmented logical image (uses thresholding technique)
% I must be RGB image
% RedTh, GreenTh, BlueTh are thresholds for R G B respectively
function J = rgbsegment(I, RedTh, GreenTh, BlueTh)
	IRedChannel = I(:, :, 1);
	IGreenChannel = I(:, :, 2);
	IBlueChannel = I(:, :, 3);

	% Threshold segmentation
	RedBw = im2bw(IRedChannel, RedTh);
	BlueBW = im2bw(IBlueChannel, BlueTh);
	GreenBW = im2bw(IGreenChannel, GreenTh);
	IBW = (RedBw & GreenBW & BlueBW);
	IComplement = imcomplement(IBW);
	IFilled = imfill(IComplement, 'holes');
	J = IFilled;
