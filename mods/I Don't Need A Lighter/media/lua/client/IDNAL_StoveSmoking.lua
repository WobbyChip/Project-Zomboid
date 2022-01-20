--I Don't Need A Lighter Mod by Fingbel

local StoveSmoking = {}

local function LightCigOnStove(player, context, worldObjects, _test)


	local player = getSpecificPlayer(player);
	print (player)
	local inventory = player:getInventory();
	local smokables = CheckInventoryForCigarette(player)
	ContextDrawing(player, context, whatIsUnderTheMouse(worldObjects), smokables)
end

Events.OnFillWorldObjectContextMenu.Add(LightCigOnStove)

--This function is responsible for the drawing of the context depending on the smokable array size
function ContextDrawing(player, context, stove, smokables)

	if stove == nill then return end

	--If we do not have any smokable, let draw a fake smoke context menu and make it unavailable
	if smokables == nil then 
		local foo = context:addOption(getText('ContextMenu_Smoke'), player, stove)
		foo.notAvailable = true
		return

	--If we have only one smokable type in the array 
	elseif getTableSize(smokables) == 1 then 
		context:addOption(getText('ContextMenu_Smoke') .."  ".. smokables[0]:getDisplayName(), player, OnStoveSmoking, stove, smokables[0])
	return
	end

	--We have more than on type, we need to draw a sub-menu
	local smokeOption = context:addOption(getText('ContextMenu_Smoke'), stove, nil);		
	local subMenu = ISContextMenu:getNew(context)
	for i=0,getTableSize(smokables) -1 do				
		subMenu:addOption(smokables[i]:getDisplayName(), player, OnStoveSmoking, stove, smokables[i])
		context:addSubMenu(smokeOption, subMenu);
	end
end
	
function OnStoveSmoking(_player, stove, _cigarette) 
	--Do we need to transfer cigarette from a bag first ? 
	if luautils.walkAdj(_player, stove:getSquare(), true) then 
		if _cigarette:getContainer() ~= _player:getInventory() then
			ISTimedActionQueue.add(ISInventoryTransferAction:new (_player,  _cigarette, _cigarette:getContainer(), _player:getInventory(), 5))
		end
	end
	 
	--Let's light what we've selected
	local time
	if luautils.walkAdj(_player, stove:getSquare(), true) then 
		if instanceof(stove, 'IsoStove') and not stove:isMicrowave() then ISTimedActionQueue.add(IsStoveLighting:new (_player, stove, _cigarette, 300))
		elseif instanceof(stove, 'IsoStove') and stove:isMicrowave() then ISTimedActionQueue.add(IsStoveLighting:new (_player, stove, _cigarette, 2000)) 
		elseif instanceof(stove,'IsoFireplace') and stove:isLit() then ISTimedActionQueue.add(IsStoveLighting:new (_player, stove, _cigarette, 200)) 
		elseif instanceof(stove,'IsoBarbecue') and stove:isLit() then ISTimedActionQueue.add(IsStoveLighting:new (_player, stove, _cigarette, 275)) 
		elseif instanceof(stove, "IsoObject") and stove:getSpriteName() == "camping_01_5" then ISTimedActionQueue.add(IsStoveLighting:new (_player, stove, _cigarette, 200)) 
		elseif instanceof(stove, "IsoFire") then ISTimedActionQueue.add(IsStoveLighting:new (_player, stove, _cigarette, 10)) end
	end

	--Now it's lit, let's smoke it
	if luautils.walkAdj(_player, stove:getSquare(), true) then 
		
		ISTimedActionQueue.add(IsStoveSmoking:new(_player, stove, _cigarette, 460))
	end
end

function whatIsUnderTheMouse (worldObjects)
	for i,stove in ipairs(worldObjects) do
	--did we clicked a lit stove?
		if instanceof(stove, 'IsoStove') and not stove:isMicrowave() then return stove		
	--did we clicked a microwave?
		elseif instanceof(stove, 'IsoStove') and stove:isMicrowave() then return stove		
	--did we clicked a lit fireplace ?
		elseif instanceof(stove,'IsoFireplace') and stove:isLit() then return stove										
	--did we clicked a lit barbecue ?
		elseif instanceof(stove,'IsoBarbecue') and stove:isLit() then return stove									
	--did we clicked a Campfire ? We check the sprite directly to check if the campfire is lit or not
		elseif instanceof(stove, "IsoObject") and stove:getSpriteName() == "camping_01_5" then return stove						
	--did we clicked on a Fire ? You mad man THIS ONE IS BROKEN, IsoFire is not picked up
		elseif instanceof(stove, "IsoFire") then return stove end
	return nil 
	end
end