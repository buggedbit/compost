% improves contrast of input image
% I can be a grayscale or RGB image
function J = improvecontrast(I)
	dimensions = size(I);
	sizeOfDimensions = size(dimensions);
	noDimensions = sizeOfDimensions(2);
	% Grayscale image
	if noDimensions == 1
		J = imadjust(I);
	% RGB image
	elseif noDimensions == 3
		J = imadjust(I, stretchlim(I));
	else
		J = I;
	end
