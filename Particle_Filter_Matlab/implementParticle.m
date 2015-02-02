img = imread('new_hallway_map.jpg');
img = img(:,:,1);
binImg = zeros(size(img));
binImg(img > 150) = 1;
actualLocs = find(binImg);
[x,y] = ind2sub(size(img),actualLocs);
startLocRow = 3442;
startLocCol = 3091;
movements = [50,29,30,50,73,84,56,38,29,16,62,81,35];
bearings = [0,-10, 20, -15, 20, -8, -10, -5, 10, -3, 10, -7, -5];
curBearing = 0;
listLocs = [startLocRow startLocCol];
curRow = startLocRow;
curCol = startLocCol;
for i=1:length(movements)
    curBearing = curBearing+bearings(i);
    curRow = curRow + movements(i)*sind(curBearing);
    curCol = curCol + movements(i)*cosd(curBearing);
    listLocs = [listLocs; curRow curCol];
end
%imshow(img);
%hold on
predLocs=[];
for j=1:10000
    [x,y] = ind2sub(size(img), actualLocs(int64(rand*length(actualLocs))));
    predLocs = [predLocs;x y curBearing];
end
for i=1:length(listLocs)
    clf;
    imshow(img);
    hold on
    for k=1:1000
        plot(predLocs(k,2),predLocs(k,1),'bo'); 
    end
    for j = 1:i
        plot(listLocs(j,2),listLocs(j,1),'rx'); 
    end
    predLocs2 = [];
    for k=1:1000
        newBearing = int64(predLocs(k,3)+bearings(i));
        newRow = int64(predLocs(k,1)+movements(i)*sind(double(newBearing)));
        newCol = int64(predLocs(k,2)+movements(i)*cosd(double(newBearing)));
        if(newRow<1)
            newRow=1;
        end
        if(newRow>size(binImg,1))
            newRow = size(binImg,1);
        end
        if(newCol<1)
            newCol=1;
        end
        if(newCol>size(binImg,2))
            newCol = size(binImg,2);
        end
        if(binImg(newRow,newCol))
            predLocs2 = [predLocs2; newRow newCol newBearing];
        end
    end
    predLocs=[];
    for r=1:1000
        randInd = int64(rand*(length(predLocs2)));
        if(randInd<1)
           randInd = 1; 
        end
        predLocs = [predLocs; predLocs2(randInd,:)];
    end
    pause
end