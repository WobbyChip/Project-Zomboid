local Functions = require "Skateboard/Functions"
local ISSkateboardTimedAction = require "Skateboard/TimeAction"
local ISVehicleMenuOriginal_FillMenuOutsideVehicle = ISVehicleMenu.FillMenuOutsideVehicle;
local ISUIHandlerOriginal_onKeyStartPressed = ISUIHandler.onKeyStartPressed;
local ISVehicleMenuOriginal_onExit = ISVehicleMenu.onExit;
local ISVehicleMenuOriginal_onHornStart = ISVehicleMenu.onHornStart;
local ISVehicleMenuOriginal_onShowSeatUI = ISVehicleMenu.onShowSeatUI;
local ISVehicleMenuOriginal_onToggleHeater = ISVehicleMenu.onToggleHeater;
local ISVehicleMenuOriginal_onShutOff = ISVehicleMenu.onShutOff;
local ISInventoryPaneContextMenuOriginal_doPlace3DItemOption = ISInventoryPaneContextMenu.doPlace3DItemOption;
local ISInventoryPaneContextMenuOriginal_dropItem = ISInventoryPaneContextMenu.dropItem;

ISVehicleMenu.FillMenuOutsideVehicle = function(player, context, vehicle, test)
	if vehicle:getScriptName() == "Base.Skateboard" then
		context:addOption(getText("ContextMenu_Grab_Skateboard"), player, function(player, vehicle)
			ISTimedActionQueue.add(ISPathFindAction:pathToVehicleAdjacent(getSpecificPlayer(player), vehicle));
			ISTimedActionQueue.add(ISSkateboardTimedAction:grab(getSpecificPlayer(player), vehicle, 50));
		end, vehicle);
	else
		ISVehicleMenuOriginal_FillMenuOutsideVehicle(player, context, vehicle, test);
	end
end

ISUIHandler.onKeyStartPressed = function(key)
	local source = getPlayer(); if not source then return end
	local vehicle = source:getVehicle() or source:getNearVehicle();

	if vehicle ~= nil then
		if vehicle:getScriptName() == "Base.Skateboard" and key == getCore():getKey("Toggle UI") then
			ISUIHandler.toggleUI();
		else
			ISUIHandlerOriginal_onKeyStartPressed(key);
		end
	end
end

ISVehicleMenu.onExit = function(playerObj, seatFrom)
	local vehicle = playerObj:getVehicle();

	if vehicle:getScriptName() ~= "Base.Skateboard" then
		ISVehicleMenuOriginal_onExit(playerObj, seatFrom)
	end
end

ISVehicleMenu.onHornStart = function(playerObj)
	local vehicle = playerObj:getVehicle()
	
	if vehicle:getScriptName() ~= "Base.Skateboard" then
		ISVehicleMenuOriginal_onHornStart(playerObj);
	end
end

ISVehicleMenu.onShowSeatUI = function(playerObj, vehicle)
	local vehicle = ISVehicleMenu.getVehicleToInteractWith(playerObj)

	if vehicle:getScriptName() ~= "Base.Skateboard" then
		ISVehicleMenuOriginal_onShowSeatUI(playerObj, vehicle);
	end
end

ISVehicleMenu.onToggleHeater = function(playerObj)
	local vehicle = playerObj:getVehicle();
	
	if vehicle:getScriptName() ~= "Base.Skateboard" then
		ISVehicleMenuOriginal_onToggleHeater(playerObj);
	end
end

ISVehicleMenu.onShutOff = function(playerObj)
	local vehicle = playerObj:getVehicle()
	
	if vehicle:getScriptName() ~= "Base.Skateboard" then
		ISVehicleMenuOriginal_onShutOff(playerObj)
	end
end

ISInventoryPaneContextMenu.doPlace3DItemOption = function(items, player, context)
	local localItems = ISInventoryPane.getActualItems(items)

	if localItems[1]:getFullType() ~= "Dislaik.Skateboard" or (localItems[1]:getFullType() == "Dislaik.Skateboard" and localItems[1]:isBroken()) then
		ISInventoryPaneContextMenuOriginal_doPlace3DItemOption(items, player, context)
	end
end

ISInventoryPaneContextMenu.dropItem = function(item, player)
	if item:getFullType() == "Dislaik.Skateboard" and not item:isBroken() then
		local source = getSpecificPlayer(player)

		if source:isHandItem(item) then
			ISTimedActionQueue.add(ISUnequipAction:new(source, item, 50));
		end

		ISInventoryPaneContextMenu.transferIfNeeded(source, item)
		ISTimedActionQueue.add(ISSkateboardTimedAction:drop(source, item, 50))
	else
		ISInventoryPaneContextMenuOriginal_dropItem(item, player)
	end
end

local onKeyPressed = function(key)
	local source = getPlayer(); if not source then return end

	if key == getCore():getKey("Forward") then
		local vehicle = source:getVehicle(); if not vehicle then return end
		
		if vehicle:getScriptName() == "Base.Skateboard" and instanceof(source, 'IsoPlayer') and source:isLocalPlayer() and vehicle:isDriver(source) then
			source:SetVariable("VehicleForward", "false")

			if isClient() then
				sendClientCommand(source, "Skateboard", "VehicleForward", {
					forward = false
				})
			end

			Functions.endSoundRide(vehicle, "SkateboardRide")
		end
	end
end

local onKeyStartPressed = function(key)
	local source = getPlayer(); if not source then return end

	if key == getCore():getKey("Interact") then
		local vehicle = source:getVehicle() or source:getNearVehicle();
		if not vehicle then return end

		if vehicle:getScriptName() == "Base.Skateboard" then
			if Functions.isInsideVehicle(source) then
				vehicle:exit(source);
				vehicle:setHotwired(false);

				source:SetVariable("VehicleScriptName", "")
				source:SetVariable("VehicleForward", "false")

				if isClient() then
					sendClientCommand(source, "Skateboard", "PlayerExit", {})
				end

				Functions.endSoundRide(vehicle, "SkateboardRide")

				if vehicle:getCurrentSpeedKmHour() > 15 then
					Functions.bump(source, "FallForward")
				end
			else
				local Vdist = math.sqrt(((source:getX() - vehicle:getX())^2) + ((source:getY() - vehicle:getY())^2));

				if Vdist < 0.8 and vehicle:getDriver() == nil then
					source:SetVariable("VehicleScriptName", vehicle:getScriptName())

					if isClient() then
						sendClientCommand(source, "Skateboard", "PlayerEnter", {
							vehicleId = vehicle:getId()
						})
					else
						vehicle:getModData().currentFrontEndDurability = Functions.currentFrontEndDurability(vehicle)
					end

					vehicle:enter(0, source);
					vehicle:setHotwired(true)
					vehicle:engineDoRunning()
				end
			end
		end
	end

	if key == getCore():getKey("Forward") then
		local vehicle = source:getVehicle(); if not vehicle then return end
		
		if vehicle:getScriptName() == "Base.Skateboard" and instanceof(source, 'IsoPlayer') and source:isLocalPlayer() and vehicle:isDriver(source) then
			
			source:SetVariable("VehicleForward", "true")

			if isClient() then
				sendClientCommand(source, "Skateboard", "VehicleForward", {
					forward = true
				})
			end

			Functions.startSoundRide(vehicle, "SkateboardRide", 0.1)

		end
	end
end


local everyTenMinutes = function()
	local source = getPlayer(); if not source then return end
	local vehicle = source:getVehicle();

	if vehicle and vehicle:getScriptName() == "Base.Skateboard" then
		if vehicle:getCurrentSpeedKmHour() ~= 0 then
			local random = ZombRand(100);
			local nimbleLevel = source:getPerkLevel(Perks.Nimble);

			if nimbleLevel == 0 then
				if random < 30 then
					Functions.bumpVehicle(source, "Random", "SkateboardRide")
				end
			elseif nimbleLevel == 1 then
				if random < 25 then
					Functions.bumpVehicle(source, "Random", "SkateboardRide")
				end
			elseif nimbleLevel == 2 then
				if random < 20 then
					Functions.bumpVehicle(source, "Random", "SkateboardRide")
				end
			elseif nimbleLevel == 3 then
				if random < 15 then
					Functions.bumpVehicle(source, "Random", "SkateboardRide")
				end
			elseif nimbleLevel == 4 then
				if random < 10 then
					Functions.bumpVehicle(source, "Random", "SkateboardRide")
				end
			end
			
			if nimbleLevel ~= 10 then
				source:getXp():AddXP(Perks.Nimble, 1);
			end
		end
	end
end

local onZombieUpdate = function(zombie)
	local source = getPlayer(); if not source then return end
	local vehicle = source:getVehicle();

	if vehicle and vehicle:getScriptName() == "Base.Skateboard" then
		local bodyDamage = source:getBodyDamage();

		if vehicle:getDistanceSq(zombie) < 0.6 then
			if vehicle:getCurrentSpeedKmHour() > 7 then
				Functions.bumpVehicle(source, "Random", "SkateboardRide")
			else
				if not zombie:isCrawling() and not zombie:isOnFloor() then
					if not zombie:getVariableBoolean("AttackDidDamage") and zombie:getHitReaction() ~= "ZombieGrab" then
						zombie:setHitReaction("ZombieGrab");
						bodyDamage:AddRandomDamageFromZombie(zombie, nil);
					end
				end
			end
		end
	end
end


local onTick = function(tick)
	local source = getPlayer(); if not source then return end
	local vehicle = source:getVehicle();

	if vehicle and vehicle:getScriptName() == "Base.Skateboard" then
		local vehicleWorldPos = vehicle:getWorldPos(Vector3f.new(0, 0, 0), Vector3f.new(0, 0, 0))
		local gridSquare = getSquare(vehicleWorldPos:x(), vehicleWorldPos:y(), vehicleWorldPos:z())
		local currentFrontDurability = Functions.currentFrontEndDurability(vehicle)
		local stats = source:getStats();
		local vehicleScriptFile = string.split(string.split(vehicle:getScript():getFileName(), "/")[5], "\\.")[1]

		if getPlayerVehicleDashboard(source:getPlayerNum()).vehicle ~= nil then
			getPlayerVehicleDashboard(source:getPlayerNum()):setVehicle(nil)
			vehicle:setHotwired(true)
			vehicle:engineDoRunning()

			if not source:containsVariable("VehicleScriptName") then
				source:SetVariable("VehicleScriptName", vehicle:getScriptName())
			end

			vehicle:getModData().currentFrontEndDurability = currentFrontDurability
		end
		
		if instanceof(gridSquare:getDeadBody(), "IsoDeadBody") then
			if vehicle:getCurrentSpeedKmHour() > 7 then
				Functions.bumpVehicle(source, "FallForward", "SkateboardRide")
			end
		end

		if vehicle:isDoingOffroad() then
			if vehicle:getCurrentSpeedKmHour() > 7 then
				Functions.bumpVehicle(source, "FallForward", "SkateboardRide")
			end
		end

		if vehicle:getCurrentSpeedKmHour() < -10 then
			Functions.bumpVehicle(source, "FallBackwards", "SkateboardRide")
		end

		if vehicle:getCurrentSpeedKmHour() == 0 then
			Functions.endSoundRide(vehicle, "SkateboardRide")
		end

		if vehicle:getCurrentSpeedKmHour() ~= 0 then
			if stats:getEndurance() > 0.05 then
				stats:setEndurance(stats:getEndurance() - 0.0001);

				if stats:getEndurance() > 0.27 then
					if vehicleScriptFile == "Vehicle_SkateboardSlow" then
						vehicle:setScript("Base.Skateboard")
					end
				else
					if vehicleScriptFile == "Vehicle_Skateboard" then
						vehicle:setScript("Base.Skateboard_Slow")
						vehicle:setScriptName("Base.Skateboard")
					end
				end
			end
		end

		if vehicle:isRegulator() then
			vehicle:setRegulator(false);
			vehicle:setRegulatorSpeed(0)
		end

		if source:containsVariable("VehicleForward") then
			if source:getVariableBoolean("VehicleForward") and not vehicle:getEmitter():isPlaying("SkateboardRide") then
				Functions.startSoundRide(vehicle, "SkateboardRide", 0.1)
			end
		end

		if not isClient() then

			if vehicle:getModData().currentFrontEndDurability ~= currentFrontDurability then
				vehicle:getModData().currentFrontEndDurability = currentFrontDurability

				Functions.bumpVehicle(source, "FallForward", "SkateboardRide")
			end
		end
	end
end

Events.OnKeyStartPressed.Add(onKeyStartPressed);
Events.OnKeyPressed.Add(onKeyPressed);
Events.EveryTenMinutes.Add(everyTenMinutes);
Events.OnZombieUpdate.Add(onZombieUpdate);
Events.OnTick.Add(onTick)