%% returns croppable boder with padding of the image of size imageSize
%  isUniformPadding is a boolean
%     if true, pads with same value on all four sides
%     else,    pads with max possible paddings on the four sides
function border = getborder(bbs, padding, imageSize, isUniformPadding)
	if length(bbs) == 0
		border = [];
		return;
	end
	% bb of bbs
	x1 = min(bbs(:, 1));
	y1 = min(bbs(:, 2));
	x2 = max(bbs(:, 3) + bbs(:, 1));
	y2 = max(bbs(:, 4) + bbs(:, 2));
	% padding
	pdx1 = min(padding, x1);
	pdy1 = min(padding, y1);
	pdx2 = max(min(padding, imageSize(2) - x2), 0);
	pdy2 = max(min(padding, imageSize(1) - y2), 0);
	if isUniformPadding == true
		% uniform padding
		padding = min([pdx1, pdy1, pdx2, pdy2]);
		x1 = x1 - padding;
		y1 = y1 - padding;
		x2 = x2 + padding;
		y2 = y2 + padding;		
	else
		% adaptive padding
		x1 = x1 - pdx1;
		y1 = y1 - pdy1;
		x2 = x2 + pdx2;
		y2 = y2 + pdy2;
	end
	
	border = [x1, y1, x2 - x1, y2 - y1];
