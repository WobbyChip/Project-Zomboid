

TrolleyList = {
"TMC.TrolleyContainer", 
"TMC.CartContainer", 
"TMC.TrolleyContainer2",
"TMC.CartContainer2",
}

local seatNameTable = {"SeatFrontLeft", "SeatFrontRight", "SeatMiddleLeft", "SeatMiddleRight", "SeatRearLeft", "SeatRearRight"}

function onEquipTrolleyTick()
    local playersSum = getNumActivePlayers()
	for playerNum = 0, playersSum - 1 do
		local playerObj = getSpecificPlayer(playerNum)
		if playerObj then
			-- Выбрасывание лишней тележки
			local playerInv = playerObj:getInventory()
			if (playerInv:getItemCount(TrolleyList[1]) + 
					playerInv:getItemCount(TrolleyList[2]) + 
					playerInv:getItemCount(TrolleyList[3]) + 
					playerInv:getItemCount(TrolleyList[4])) > 1 then
				-- print("MORE Trolley")
				for i = 1, #TrolleyList do
					trolForDrop = nil
					itemsArray = playerInv:getItemsFromType(TrolleyList[i])
					-- itemsArray = playerInv:getItemsFromType("SAD")
					-- print(itemsArray:size())
					if itemsArray:size() > 0 then
						if not (playerObj:getPrimaryHandItem() == itemsArray:get(0)) then
							trolForDrop = itemsArray:get(0)
						elseif itemsArray:size() > 1 then
							trolForDrop = itemsArray:get(1)
						end
						if trolForDrop then
							-- print("trolForDrop")
							playerInv:Remove(trolForDrop)
							playerObj:removeFromHands(trolForDrop)
							local dropX,dropY,dropZ = ISInventoryTransferAction.GetDropItemOffset(playerObj, playerObj:getCurrentSquare(), trolForDrop)
							playerObj:getCurrentSquare():AddWorldInventoryItem(trolForDrop, dropX, dropY, dropZ)
							break
						end
					end
				end
			elseif (playerInv:getItemCount(TrolleyList[1]) == 1 or 
					playerInv:getItemCount(TrolleyList[2]) == 1 or 
					playerInv:getItemCount(TrolleyList[3]) == 1 or 
					playerInv:getItemCount(TrolleyList[4]) == 1) then
				for i = 1, #TrolleyList do
					trolForEquip = playerInv:getFirstType(TrolleyList[i])
					if trolForEquip then
						-- Замена на другой контейнер
						trolCont = trolForEquip:getItemContainer()
						if i <= (#TrolleyList / 2) and not trolCont:isEmpty() then
							playerInv:Remove(trolForEquip)
							trolForEquip = playerInv:AddItem(TrolleyList[i + #TrolleyList / 2])
							trolForEquip:setItemContainer(trolCont)
						elseif i > (#TrolleyList / 2) and trolCont:isEmpty() then
							playerInv:Remove(trolForEquip)
							trolForEquip = playerInv:AddItem(TrolleyList[i - #TrolleyList / 2])
							trolForEquip:setItemContainer(trolCont)
						end
						-- Тележка сразу берется в руки
						if playerObj:getPrimaryHandItem() ~= trolForEquip then
							playerObj:setPrimaryHandItem(trolForEquip)
							playerObj:setSecondaryHandItem(trolForEquip)
							getPlayerData(playerObj:getPlayerNum()).playerInventory:refreshBackpacks();
						end
						break
					end
				end
			end
			
			if playerObj:getVariableString("righthandmask") == "holdingtrolleyright" then
				if not (playerObj:getCurrentState() == IdleState.instance() or 
						playerObj:getCurrentState() == PlayerAimState.instance()) then
					local sqr = playerObj:getSquare()
					local trol = playerObj:getPrimaryHandItem()
					playerObj:getInventory():Remove(trol)
					local pdata = getPlayerData(playerObj:getPlayerNum());
					if pdata ~= nil then
						pdata.playerInventory:refreshBackpacks();
						pdata.lootInventory:refreshBackpacks();
					end
					playerObj:setPrimaryHandItem(nil);
					playerObj:setSecondaryHandItem(nil);
					sqr:AddWorldInventoryItem(trol, 0, 0, 0);
				end
				-- Выбрасывание тележки в машине
				if playerObj:getVehicle() then
					local vehicle = playerObj:getVehicle()
					local areaCenter = vehicle:getAreaCenter(seatNameTable[vehicle:getSeat(playerObj)+1])
					-- print(areaCenter)
					if areaCenter then 
						local sqr = getCell():getGridSquare(areaCenter:getX(), areaCenter:getY(), vehicle:getZ())
						local trol = playerObj:getPrimaryHandItem()
						playerObj:getInventory():Remove(trol)
						local pdata = getPlayerData(playerObj:getPlayerNum());
						if pdata ~= nil then
							pdata.playerInventory:refreshBackpacks();
							pdata.lootInventory:refreshBackpacks();
						end
						playerObj:setPrimaryHandItem(nil);
						playerObj:setSecondaryHandItem(nil);
						sqr:AddWorldInventoryItem(trol, 0, 0, 0);
					end
				end
			end
		end
		-- print(playerObj:getCurrentState())
    end
end


function ISWorldObjectContextMenu.getWorldObjectsOnSquares(squares, worldObjects)
	for _,square in ipairs(squares) do
		local squareObjects = square:getWorldObjects()
		for i=1,squareObjects:size() do
			local worldObject = squareObjects:get(i-1)
			table.insert(worldObjects, worldObject)
		end
	end
end



ISWorldObjectContextMenu.equipTrolley = function(playerObj, WItem)
    if WItem:getSquare() and luautils.walkAdj(playerObj, WItem:getSquare()) then
		if playerObj:getPrimaryHandItem() then
			ISTimedActionQueue.add(ISUnequipAction:new(playerObj, playerObj:getPrimaryHandItem(), 50));
		end
		if playerObj:getSecondaryHandItem() and playerObj:getSecondaryHandItem() ~= playerObj:getPrimaryHandItem() then
			ISTimedActionQueue.add(ISUnequipAction:new(playerObj, playerObj:getSecondaryHandItem(), 50));
		end
		-- local time = ISWorldObjectContextMenu.grabItemTime(playerObj, WItem)
		ISTimedActionQueue.add(ISTakeTrolley:new(playerObj, WItem, 1))
	end
end

local oldForceDropHeavyItem = isForceDropHeavyItem
function isForceDropHeavyItem(item)
	if item and (item:getFullType() == TrolleyList[1] or item:getFullType() == TrolleyList[2] or item:getFullType() == TrolleyList[3] or item:getFullType() == TrolleyList[4]) then
		return true
	else
		return oldForceDropHeavyItem(item)
	end
end

-- Позволяет подбирать полностью заполненную тележку
function TrolleyOnFillWorldObjectContextMenu(player, context, worldobjects, test)
	local playerObj = getSpecificPlayer(player)
	local squares = {}
	local doneSquare = {}
	for i,v in ipairs(worldobjects) do
		if v:getSquare() and not doneSquare[v:getSquare()] then
			doneSquare[v:getSquare()] = true
			table.insert(squares, v:getSquare())
		end
	end
	if #squares == 0 then return false end
	
	local worldObjects = {}
	if JoypadState.players[player+1] then
		for _,square in ipairs(squares) do
			for i=1,square:getWorldObjects():size() do
				local worldObject = square:getWorldObjects():get(i-1)
				table.insert(worldObjects, worldObject)
			end
		end
	else
		local squares2 = {}
		for k,v in pairs(squares) do
			squares2[k] = v
		end
		local radius = 1
		for _,square in ipairs(squares2) do
			ISWorldObjectContextMenu.getSquaresInRadius(square:getX(), square:getY(), square:getZ(), radius, doneSquare, squares)
		end
		ISWorldObjectContextMenu.getWorldObjectsOnSquares(squares, worldObjects)
	end
	if #worldObjects == 0 then return false end
	for _,worldObject in ipairs(worldObjects) do
		local trolleyName = worldObject:getItem():getFullType()
		if (trolleyName == TrolleyList[1]) or (trolleyName == TrolleyList[2])
			or (trolleyName == TrolleyList[3]) or (trolleyName == TrolleyList[4]) then
			local old_option_update = context:getOptionFromName(getText("ContextMenu_Grab"))
			if old_option_update then
				context:updateOptionTsar(old_option_update.id, getText("ContextMenu_GrabTrolley"), playerObj, ISWorldObjectContextMenu.equipTrolley, worldObject)
				return
			end				
		end
	end
end

-- Разрешает брать заполненные тележки в руки из инвентаря
local function trolleyUpdateInventoryOptions(player, context, items)
	local playerObj = getSpecificPlayer(player)
	local item = items[1];
	if not instanceof(items[1], "InventoryItem") then
		item = items[1].items[1];
	end
	if item and (item:getFullType() == TrolleyList[1] or item:getFullType() == TrolleyList[2] or item:getFullType() == TrolleyList[3] or item:getFullType() == TrolleyList[4]) then
		if item:getWorldItem() then
			local old_option_update = context:getOptionFromName(getText("ContextMenu_Equip_Two_Hands"))
			if old_option_update then
				context:updateOptionTsar(old_option_update.id, getText("ContextMenu_Equip_Two_Hands"), playerObj, ISWorldObjectContextMenu.equipTrolley, item:getWorldItem())
				return
			end	
		end
	end
end

local function trolleyBlockBuildOptions(player, context, worldobjects, test)
	local playerObj = getSpecificPlayer(player)
	if playerObj:getVariableString("righthandmask") == "holdingtrolleyright" then
		context:removeOptionTsar(context:getOptionFromName(getText("ContextMenu_MetalWelding")))
		context:removeOptionTsar(context:getOptionFromName(getText("ContextMenu_Build")))
	end
end

Events.OnFillWorldObjectContextMenu.Add(trolleyBlockBuildOptions)
Events.OnFillInventoryObjectContextMenu.Add(trolleyUpdateInventoryOptions);
Events.OnFillWorldObjectContextMenu.Add(TrolleyOnFillWorldObjectContextMenu);
Events.OnTick.Add(onEquipTrolleyTick);
