require "Vehicle/ISUI/ISVehicleMenu"
function AM2_hasWaterObject(square)	
	for i=0, square:getObjects():size()-1 do
		local v = square:getObjects():get(i);
		if instanceof(v, "IsoObject") and v:getSprite() and v:getSprite():getProperties() and v:getSprite():getProperties():Is(IsoFlagType.water) then
			--print("Water Tile")
			return true;
		end
	end
end

function AM2_isFreeTile( square )
    if not square or square:Is("BlocksPlacement") or square:Is(IsoFlagType.canBeCut) or square:Is("tree") then
        return false;
    end
    return true;
end

function AM2_goodVehicleTile(square)
	--print("Vehicle Tile Test")
	if not square then  square = getPlayer():getSquare() end
	if not square then
		print("No Square")
		return false
	end
	if square:getZ() > 0 then
		print("Is Aboveground")	
		return false
	end
	local solid = square:isSolid()
	if solid then
		print("Solid - ".. tostring(solid))
		return false
	end
	local solidTrans = square:isSolidTrans()
	if solidTrans then
		print("Solid Trans- ".. tostring(solidTrans))
		return false
	end
	if AM2_hasWaterObject(square) then
		print("Water!")
		return false
	end
	if square:getZ() > 0 and not square:hasFloor(true) then
		print("tile test failed - no floor! - " .. tostring(square:hasFloor(true)))	
		return false
	end
	local cell = square:getCell() -- the cell wont change. no need to getWorld():getCell() every step of the loop
	local x, y, z = square:getX(), square:getY(), square:getZ()
	-- local square2 = cell:getGridSquare(x-1, y, z)
	-- if square2 and square:isHoppableTo(square2) then
		-- print("Fence!")
		-- return false
	-- end
	-- local square2 = cell:getGridSquare(x+1, y, z)
	-- if square2 and square:isHoppableTo(square2) then
		-- print("Fence!")
		-- return false
	-- end
	-- local square2 = cell:getGridSquare(x, y-1, z)
	-- if square2 and square:isHoppableTo(square2) then
		-- print("Fence!")
		-- return false
	-- end
	-- local square2 = cell:getGridSquare(x, y+1, z)
	-- if square2 and square:isHoppableTo(square2) then
		-- print("Fence!")
		-- return false
	-- end
	if square:getTree() then
		print("tile test failed - tree intersecting!")
		return false		
	end	
	if square:isVehicleIntersecting() then
		print("tile test failed - vehicle intersecting!")
		return false		
	end	
		-- local wall = square:getWall(true) or square:getWall(false)
		-- local window = square:getWindow(true) or square:getWindow(false)
		-- local door = square:getDoor(true) or square:getDoor(false)
		-- if wall then 
			-- print("No walls!")
			-- return false
		-- end
		-- if window then 
			-- print("No windows!")
			-- return false
		-- end
		-- if door then 
			-- print("No doors!")
			-- return false
		-- end
	-- if square:haveDoor() then
		-- print("No doors 2!")
		-- return false
	-- end
		-- local cell = square:getCell() -- the cell wont change. no need to getWorld():getCell() every step of the loop
		-- local x, y, z = square:getX(), square:getY(), square:getZ()
		-- local square2 = cell:getGridSquare(x-1, y, z)
		-- if square2 and square:getDoorFrameTo(square2) then
			-- print("No Door Frames!")
			-- return false
		-- end
		-- local square2 = cell:getGridSquare(x+1, y, z)
		-- if square2 and square:getDoorFrameTo(square2) then
			-- print("No Door Frames!")
			-- return false
		-- end
		-- local square2 = cell:getGridSquare(x, y-1, z)
		-- if square2 and square:getDoorFrameTo(square2) then
			-- print("No Door Frames!")
			-- return false
		-- end
		-- local square2 = cell:getGridSquare(x, y+1, z)
		-- if square2 and square:getDoorFrameTo(square2) then
			-- print("No Door Frames!")
			-- return false
		-- end
	local stairs = square:HasStairs()
	if stairs then
		print("Stairs - ".. tostring(stairs))
		return false
	end
	local freeT = AM2_isFreeTile(square)
	if freeT == false then
		print("Failed Free Tile - " .. tostring(freeT))	
	end
	--local tag = InventoryItemFactory.CreateItem("Cigarettes");
	--if tag then square:AddWorldInventoryItem(tag, 0.01, 0.01, 0.01) end
	return true 
end
