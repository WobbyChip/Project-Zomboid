require "Vehicle/Vehicles"

function AM2_TransferBag(cont1, cont2)

	--local player_Inventory = player:getInventory();
	local movedItems = {} 
	--local dItem;
	--local texture
	
	--for i = 0, (item:size()-1) do 
		--dItem = item:get(i); 
		--if dItem:getCategory() == "Container" then 
			--dInv = dItem:getInventory(); 
			--dInv = cont1
			--newInv= cont2
			cont1Items = cont1:getItems()
			if cont1Items:size() >= 1 then 
				for i = 0, (cont1Items:size()-1) do
					item = cont1Items:get(i);
					table.insert(movedItems, item) 
				end
			end
		--end
	--end
	
	for i, k in ipairs(movedItems) do
		cont1:Remove(k) 
		cont2:AddItem(k)
	end
	local num = getPlayer():getPlayerNum()
	local pdata = getPlayerData(num)
	pdata.lootInventory:refreshBackpacks();
	pdata.playerInventory:refreshBackpacks();
	--getPlayer():getInventory():refreshBackpacks()
end

function Vehicles.Update.AME_GasTank(vehicle, part, elapsedMinutes)
	--print("Electric Gas Tank Updated")
	--print("Part Tank " .. tostring(part))
	--if not vehicle:getBattery():getInventoryItem() then return end
	local invItem = part:getInventoryItem();
	if not invItem then return; end
	
	local amount = part:getContainerCapacity()
	--amount = 0
	local battery = vehicle:getBattery()
	
	if battery and battery:getInventoryItem() then
		amount = math.floor(battery:getInventoryItem():getUsedDelta() * amount)
	end
	if not battery or not battery:getInventoryItem() then
		amount = 0
	end

	part:setContainerContentAmount(amount, false, true)
	--amount = part:getContainerContentAmount();
	-- local precision = (amount < 0.5) and 2 or 1
	-- if VehicleUtils.compareFloats(amountOld, amount, precision) then
		-- vehicle:transmitPartModData(part)
	-- end
	local part = vehicle:getBattery()
	--print("Part Battery " .. tostring(part))




	local invItem = part:getInventoryItem();
	if not invItem then return; end
	--if not part:getUsedDelta() then print("False") return false end
	local amount = part:getInventoryItem():getUsedDelta()
	local chargeOld = part:getInventoryItem():getUsedDelta()
	if elapsedMinutes > 0 and amount > 0 and vehicle:isEngineRunning() then
		local amountOld = amount
		-- calcul how much gas is used, based mainly on engine speed, engine quality & mass.
		--local gasMultiplier = 90000;
		local gasMultiplier = 90000000;

		-- if quality is 60, we do: 100 - 60 = 40; 40/2 = 20; 20/100=0.2; 0.2+1 = 1.2 : our multiplier;
		local qualityMultiplier = ((100 - vehicle:getEngineQuality()) / 200) + 1;
		local massMultiplier =  ((math.abs(1000 - vehicle:getScript():getMass())) / 300) + 1;
		-- the closer we are to change shift, the less we consume gas
		local speedToNextTransmission = ((vehicle:getMaxSpeed() / vehicle:getScript():getGearRatioCount()) * 0.71) * vehicle:getTransmissionNumber();
		local speedMultiplier = (speedToNextTransmission - vehicle:getCurrentSpeedKmHour()) * 350;
		-- if vehicle is stopped, we half the value of gas consummed
		if math.floor(vehicle:getCurrentSpeedKmHour()) > 0 then
			gasMultiplier = gasMultiplier / qualityMultiplier / massMultiplier;
		else
			gasMultiplier = (gasMultiplier / qualityMultiplier) * 2;
			speedMultiplier = 1;
		end
		-- we're at max gear, cap general gas consumption
		if speedMultiplier < 800 then
			speedMultiplier = 800;
		end
		
	
--		local engineSpeed = math.min(vehicle:getEngineSpeed(), 6000)
--		local engineSpeedCalc = 6000 - engineSpeed;

--		local newAmount = engineSpeedCalc / gasMultiplier;
		local newAmount = (speedMultiplier / gasMultiplier)  * SandboxVars.CarGasConsumption;
		newAmount =  newAmount * (vehicle:getEngineSpeed()/2500.0);
		amount = amount - elapsedMinutes * newAmount;
	
		-- if your gas tank is in bad condition, you can simply lose fuel
		if part:getCondition() < 70 then
			if ZombRand(part:getCondition() * 2) == 0 then
				amount = amount - 0.01;
			end
		end
	
		if amount ~= amountOld then
			part:getInventoryItem():setUsedDelta(amount)
			if VehicleUtils.compareFloats(amountOld, amount, 2) then
				vehicle:transmitPartUsedDelta(part)
			end
		end
	end
end



function Vehicles.Update.AME_Battery(vehicle, part, elapsedMinutes)
	--print("Update Electric Battery")
	if part:getInventoryItem() then
		local chargeOld = part:getInventoryItem():getUsedDelta()
		local charge = chargeOld
		-- Starting the engine drains the battery
		local engineStarted = vehicle:isEngineRunning()
		if engineStarted and not part:getModData().engineStarted then
			charge = charge - 0.025
		end
		part:getModData().engineStarted = engineStarted
		-- Running the engine charges the battery

		-- Having a generator & the engine not running charge the battery
		if not vehicle:isEngineRunning() and vehicle:getSquare() and vehicle:getSquare():haveElectricity() then
			charge = math.min(charge + elapsedMinutes * 0.001, 1.0)
		end
		if charge ~= chargeOld then
			part:getInventoryItem():setUsedDelta(charge)
			if VehicleUtils.compareFloats(chargeOld, charge, 2) then
				vehicle:transmitPartUsedDelta(part)
			end
		end
	end
	-- Hack, there's no Lightbar part.
	Vehicles.Update.Lightbar(vehicle, part, elapsedMinutes)
end





function Vehicles.CheckEngine.AME_GasTank(vehicle, part)
	--print("Electric Gas Tank Checked")
	local part = vehicle:getBattery()
	if not part:getInventoryItem() then return false end
	--return true
	local amount = math.floor(part:getInventoryItem():getUsedDelta() * 100 )
	--print(tostring(amount))
	return part:getInventoryItem() and amount > 0
end

function Vehicles.ContainerAccess.AME_TruckBedOpen(vehicle, part, chr)
	--print("TEST")
	--if chr:getVehicle() then return false end
	if chr:getVehicle() == vehicle then
		--print("true")
		return true
	elseif chr:getVehicle() then
		--print("false")
		return false 	
	elseif not vehicle:isInArea(part:getArea(), chr) then
		--print("false")
		return false
	end
		--print("true")
	return true
end
function Vehicles.ContainerAccess.AME_TruckBedOpen2(vehicle, part, chr)
	--print("TEST")
	--if chr:getVehicle() then return false end
	if not part:getInventoryItem() then return false end
	
	if chr:getVehicle() == vehicle then
		--print("true")
		return true
	elseif chr:getVehicle() then
		--print("false")
		return false 	
	elseif not vehicle:isInArea("TruckBed", chr) then
		--print("false")
		return false
	end
		--print("true")
	return true
end




function Vehicles.InstallTest.Bagrack(vehicle, part, chr)
	--print("TEST!!!!!!!!!!")
	if ISVehicleMechanics.cheat then return true; end
	local keyvalues = part:getTable("install")
	if not keyvalues then return false end
	if part:getInventoryItem() then return false end
	--if not part:getItemType() or part:getItemType():isEmpty() then return false end
	local typeToItem = VehicleUtils.getItems_Empty(chr:getPlayerNum())
	if keyvalues.requireInstalled then
		local split = keyvalues.requireInstalled:split(";");
		for i,v in ipairs(split) do
			if not vehicle:getPartById(v) or not vehicle:getPartById(v):getInventoryItem() then return false; end
		end
	end
	if not VehicleUtils.testProfession(chr, keyvalues.professions) then return false end
	-- allow all perk, but calculate success/failure risk
--	if not VehicleUtils.testPerks(chr, keyvalues.skills) then return false end
	if not VehicleUtils.testRecipes(chr, keyvalues.recipes) then return false end
	if not VehicleUtils.testTraits(chr, keyvalues.traits) then return false end
	if not VehicleUtils.testItems(chr, keyvalues.items, typeToItem) then return false end
	-- if doing mechanics on this part require key but player doesn't have it, we'll check that door or windows aren't unlocked also
	--if VehicleUtils.RequiredKeyNotFound(part, chr) then
		--return false;
	--end
	return true
end



-- function VehicleUtils.testItems_Empty(chr, items, typeToItem)
	-- if not items then return true end
	-- for _,item in pairs(items) do
		-- if not typeToItem[item.type] then return false end
		-- if item then
			-- print("Item " .. tostring(item.type))
		
		-- end
		-- -- and item:getCategory()
		-- -- and item:getCategory() == "Container" then
			-- -- if item:getInventory():isEmpty() then return false end
		-- -- end
		-- if item.count then
		-- end
	-- end
	-- return true
-- end



-- function VehicleUtils.getItems_Empty(playerNum)
	-- local containers = VehicleUtils.getContainers(playerNum)
	-- local typeToItem = {}
	-- for _,container in ipairs(containers) do
		-- for i=1,container:getItems():size() do
			-- local item = container:getItems():get(i-1)			
			-- local condition = item:getCondition()
			-- local container = item:getCategory() == "Container"
			-- local empty = true
			-- if container then
				-- local inv = item:getInventory()
				-- if inv:isEmpty() then
					-- --print(tostring(item:getType()) .. " is empty!")
					-- empty = true
				-- else
					-- --print(tostring(item:getType()) .. " is not empty!")
					-- empty = nil
					-- condition = 0
				-- end
			-- end
			-- --if ZombRand(0,2) == 0 then condition = 0 end
			-- --if container and not empty then condition = 0 end
			-- if condition > 0 and empty then
				-- --print("Item : " .. tostring(item:getType()) .. ", is Container? " .. tostring(container)  .. ", Empty? " .. tostring(empty))
				-- --print("Adding " .. tostring(item:getType()))
				-- typeToItem[item:getFullType()] = typeToItem[item:getFullType()] or {}
				-- table.insert(typeToItem[item:getFullType()], item)
				-- -- This isn't needed for Radios any longer.  There was a bug setting
				-- -- the item type to Radio.worldSprite, but that no longer happens.
				-- if instanceof(item, "Moveable") and item:getWorldSprite() then
					-- local fullType = item:getScriptItem():getFullName()
					-- if fullType ~= item:getFullType() then
						-- typeToItem[fullType] = typeToItem[fullType] or {}
						-- table.insert(typeToItem[fullType], item)
					-- end
				-- end
			-- end
		-- end
	-- end
	-- return typeToItem
-- end

function Vehicles.InstallComplete.Bagrack(vehicle, part)
	--print("Install Bagrack")
	
	AM2_TransferBag(part:getInventoryItem():getInventory(), part:getItemContainer())
	local player = getPlayer()
	local inv = player:getInventory()
	local hand = player:getPrimaryHandItem()
	inv:Remove(hand)
	player:setPrimaryHandItem(nil)
	renderBagrack(part)
end

function Vehicles.Create.Bagrack(vehicle, part)
	--print("Create Bagrack")
	if ZombRand(2) == 0 then
		local invItem = VehicleUtils.createPartInventoryItem(part);
	end
	renderBagrack(part)
end

function Vehicles.Init.Bagrack(vehicle, part)
	--print("Init Bagrack")	
	renderBagrack(part)	
end

function Vehicles.Update.Bagrack(vehicle, part)
	--print("Update Bagrack")	
	-- local item = part:getInventoryItem()
	-- if not item then return false end
	-- local capacity = item:getCapacity()
	-- part:setContainerCapacity(capacity)
	renderBagrack(part)	
end


function Vehicles.UninstallTest.Bagrack(vehicle, part, chr)
	--print("Uninstall Bagrack")
	local cont = part:getItemContainer()
	--print("CONT " .. tostring(cont))
	local empty = cont:isEmpty()
	--print("Empty " .. tostring(empty))
	--if not cont:isEmpty() then return false end
	if ISVehicleMechanics.cheat then return true; end
	local keyvalues = part:getTable("uninstall")
	if not keyvalues then return false end
	if not part:getInventoryItem() then return false end
	if not part:getItemType() or part:getItemType():isEmpty() then return false end
	local typeToItem = VehicleUtils.getItems(chr:getPlayerNum())
	if keyvalues.requireUninstalled and (vehicle:getPartById(keyvalues.requireUninstalled) and vehicle:getPartById(keyvalues.requireUninstalled):getInventoryItem()) then
		return false;
	end
	if not VehicleUtils.testProfession(chr, keyvalues.professions) then return false end
	-- allow all perk, but calculate success/failure risk
--	if not VehicleUtils.testPerks(chr, keyvalues.skills) then return false end
	if not VehicleUtils.testRecipes(chr, keyvalues.recipes) then return false end
	if not VehicleUtils.testTraits(chr, keyvalues.traits) then return false end
	if not VehicleUtils.testItems(chr, keyvalues.items, typeToItem) then return false end
	if keyvalues.requireEmpty and round(part:getContainerContentAmount(), 3) > 0 then return false end
	local seatNumber = part:getContainerSeatNumber()
	local seatOccupied = (seatNumber ~= -1) and vehicle:isSeatOccupied(seatNumber)
	if keyvalues.requireEmpty and seatOccupied then return false end
	-- if doing mechanics on this part require key but player doesn't have it, we'll check that door or windows aren't unlocked also
	--if VehicleUtils.RequiredKeyNotFound(part, chr) then
		--return false
	--end
	return true
end

function Vehicles.UninstallComplete.Bagrack(vehicle, part, item)

	AM2_TransferBag(part:getItemContainer(), item:getInventory())
	local inv = getPlayer():getInventory()
	inv:AddItem("Base.Rope")

end


function renderBagrack(part)
	--print("Render bagrack!")
	local item = part:getInventoryItem()
	if not item then return false end
	local capacity = item:getCapacity()
	part:setContainerCapacity(capacity)

	local iType = item:getType()
	if not iType then return false end
	local tChoice = item:getVisual():getTextureChoice()
	--print("tChoice " .. tostring(tChoice))	
	
		-- part:setModelVisible("schoolbag_back", false)
		-- part:setModelVisible("schoolbag_blue", false)
		-- part:setModelVisible("schoolbag_spiffo", false)
		-- part:setModelVisible("duffel", false)
		-- part:setModelVisible("bighiking_green", false)
		-- part:setModelVisible("bighiking_red", false)
		-- part:setModelVisible("bighiking_blue", false)
		-- part:setModelVisible("hiking_blue", false)
		-- part:setModelVisible("hiking_green", false)
		-- part:setModelVisible("hiking_red", false)
		-- part:setModelVisible("alice", false)
		-- part:setModelVisible("alice_army", false)	
	
	
	if iType == "Bag_Schoolbag" then
		if tChoice == 1 then
			--print("Blue Bag!")
		part:setModelVisible("schoolbag_black", false)
		part:setModelVisible("schoolbag_spiffo", false)
		
			part:setModelVisible("schoolbag_blue", true)	
		elseif tChoice ==0 then
			--print("Black Bag!")
		part:setModelVisible("schoolbag_blue", false)
		part:setModelVisible("schoolbag_spiffo", false)
		
			part:setModelVisible("schoolbag_black", true)			
		elseif tChoice == 2 then
			--print("Spiffo Bag!")
		part:setModelVisible("schoolbag_black", false)
		part:setModelVisible("schoolbag_blue", false)
		
			part:setModelVisible("schoolbag_spiffo", true)		
		end
	else	
		part:setModelVisible("schoolbag_black", false)
		part:setModelVisible("schoolbag_blue", false)
		
		part:setModelVisible("schoolbag_spiffo", false)
	end
		
	if iType == "Bag_BigHikingBag" then
		if tChoice == 0 then
			--print("Blue Bag!")
		part:setModelVisible("bighiking_green", false)
		part:setModelVisible("bighiking_red", false)
		
			part:setModelVisible("bighiking_blue", true)
		elseif tChoice ==1 then
			--print("Green Bag!")
		part:setModelVisible("bighiking_blue", false)
		part:setModelVisible("bighiking_red", false)
		
			part:setModelVisible("bighiking_green", true)		
		elseif tChoice == 2 then
			--print("Red Bag!")
		part:setModelVisible("bighiking_blue", false)
		part:setModelVisible("bighiking_green", false)
		
			part:setModelVisible("bighiking_red", true)	
		end
	else
		part:setModelVisible("bighiking_blue", false)
		part:setModelVisible("bighiking_green", false)		
		part:setModelVisible("bighiking_red", false)
	end
	
	if iType:contains("DuffelBag") then
		part:setModelVisible("duffel", true)
	else
		part:setModelVisible("duffel", false)
	end
	if iType == "Bag_NormalHikingBag" then
		if tChoice == 0 then
			--print("Blue Bag!")
		part:setModelVisible("hiking_green", false)
		part:setModelVisible("hiking_red", false)
		
			part:setModelVisible("hiking_blue", true)
		elseif tChoice ==1 then
			--print("Green Bag!")
		part:setModelVisible("hiking_blue", false)
		part:setModelVisible("hiking_red", false)
		
			part:setModelVisible("hiking_green", true)		
		elseif tChoice == 2 then
			--print("Red Bag!")
		part:setModelVisible("hiking_blue", false)
		part:setModelVisible("hiking_green", false)
		
			part:setModelVisible("hiking_red", true)	
		end
	else
		part:setModelVisible("hiking_blue", false)
		part:setModelVisible("hiking_green", false)
		
		part:setModelVisible("hiking_red", false)
	end
	if iType == "Bag_SurvivorBag" or iType == "Bag_ALICEpack" then
		part:setModelVisible("alice", true)
	else
		part:setModelVisible("alice", false)
	end
	if iType == "Bag_ALICEpack_Army" then
		part:setModelVisible("alice_army", true)
	else
		part:setModelVisible("alice_army", false)
	end
	
end