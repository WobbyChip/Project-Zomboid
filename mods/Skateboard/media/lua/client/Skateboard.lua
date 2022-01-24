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
			ISTimedActionQueue.add(ISPathFindAction:pathToVehicleAdjacent(getSpecificPlayer(player), vehicle))
			ISTimedActionQueue.add(ISSkateboardTimedAction:grab(getSpecificPlayer(player), vehicle, 50))
		end, vehicle);
	else
		ISVehicleMenuOriginal_FillMenuOutsideVehicle(player, context, vehicle, test)
	end
end

ISUIHandler.onKeyStartPressed = function(key)
	local source = getPlayer(); if not source then return end
	local vehicle = source:getVehicle() or source:getNearVehicle()

	if vehicle ~= nil then
		if vehicle:getScriptName() == "Base.Skateboard" and key == getCore():getKey("Toggle UI") then
			ISUIHandler.toggleUI();
		else
			ISUIHandlerOriginal_onKeyStartPressed(key);
		end
	end
end

ISVehicleMenu.onExit = function(playerObj, seatFrom)
	local vehicle = playerObj:getVehicle()

	if vehicle:getScriptName() ~= "Base.Skateboard" then
		ISVehicleMenuOriginal_onExit(playerObj, seatFrom)
	end
end

ISVehicleMenu.onHornStart = function(playerObj)
	local vehicle = playerObj:getVehicle()
	
	if vehicle:getScriptName() ~= "Base.Skateboard" then
		ISVehicleMenuOriginal_onHornStart(playerObj)
	end
end

ISVehicleMenu.onShowSeatUI = function(playerObj, vehicle)
	local vehicle = ISVehicleMenu.getVehicleToInteractWith(playerObj)

	if vehicle:getScriptName() ~= "Base.Skateboard" then
		ISVehicleMenuOriginal_onShowSeatUI(playerObj, vehicle)
	end
end

ISVehicleMenu.onToggleHeater = function(playerObj)
	local vehicle = playerObj:getVehicle()
	
	if vehicle:getScriptName() ~= "Base.Skateboard" then
		ISVehicleMenuOriginal_onToggleHeater(playerObj)
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
			source:SetVariable("vehicleForward", "false")

			if vehicle:getEmitter():isPlaying("SkateboardRide") then
				vehicle:getEmitter():stopSoundByName("SkateboardRide");
			end

		end
	end
end

local onKeyStartPressed = function(key)
	local source = getPlayer(); if not source then return end

	if key == getCore():getKey("Forward") then
		local vehicle = source:getVehicle(); if not vehicle then return end
		
		if vehicle:getScriptName() == "Base.Skateboard" and instanceof(source, 'IsoPlayer') and source:isLocalPlayer() and vehicle:isDriver(source) then
			
			Functions.startSoundRide(source, vehicle, "SkateboardRide", 0.15)

			source:SetVariable("vehicleForward", "true")
		end
	end

	if key == getCore():getKey("Interact") then
		local vehicle = source:getVehicle() or source:getNearVehicle();
		if not vehicle then return end

		if vehicle:getScriptName() == "Base.Skateboard" then
			if Functions.isInsideVehicle(source) then
				vehicle:exit(source);

				if vehicle:getEmitter():isPlaying("SkateboardRide") then
					vehicle:getEmitter():stopSoundByName("SkateboardRide");
				end

				vehicle:setHotwired(false);
				source:SetVariable("vehicleScriptName", "")
				source:SetVariable("vehicleForward", "false")

				if vehicle:getCurrentSpeedKmHour() > 15 or vehicle:getCurrentSpeedKmHour() < -15 then 
					Functions.bump(source)
				end
			else
				local Vdist = math.sqrt(((source:getX() - vehicle:getX())^2) + ((source:getY() - vehicle:getY())^2));

				if Vdist < 0.8 then
					local field = getClassField(vehicle, 62)
					local fieldVal = getClassFieldVal(vehicle, field)

					if isClient() then
						sendClientCommand(source, "Skateboard", "SetFrontDurability", {
							vehicleId = vehicle:getId()
						})
					else
						vehicle:getModData().currentFrontEndDurability = fieldVal
					end
					
					vehicle:enter(0, source);
					vehicle:setHotwired(true)
					vehicle:engineDoRunning()
					source:SetVariable("vehicleScriptName", vehicle:getScriptName())
				end
			end
		end
	end
end

local onTick = function(tick)
	local source = getPlayer(); if not source then return end
	local vehicle = source:getVehicle(); if not vehicle then return end

	if vehicle:getScriptName() == "Base.Skateboard" then

		if getPlayerVehicleDashboard(source:getPlayerNum()).vehicle ~= nil then
			getPlayerVehicleDashboard(source:getPlayerNum()):setVehicle(nil)
			vehicle:setHotwired(true)
			vehicle:engineDoRunning()

			if not source:containsVariable("vehicleScriptName") then
				source:SetVariable("vehicleScriptName", vehicle:getScriptName())
			end
		end

		if source:containsVariable("vehicleForward") then
			if source:getVariableBoolean("vehicleForward") and not vehicle:getEmitter():isPlaying("SkateboardRide") then
				Functions.startSoundRide(source, vehicle, "SkateboardRide", 0.2)
			end
		end

		if not isClient() then
			local field = getClassField(vehicle, 62)
			local fieldVal = getClassFieldVal(vehicle, field)

			if vehicle:getModData().currentFrontEndDurability == nil then
				vehicle:getModData().currentFrontEndDurability = fieldVal
			end

			if vehicle:getModData().currentFrontEndDurability ~= fieldVal then
				vehicle:getModData().currentFrontEndDurability = fieldVal

				Functions.bumpVehicle(source, vehicle, "SkateboardRide")
			end
		end
	end
end

Events.OnTick.Add(onTick)
Events.OnExitVehicle.Add(onExitVehicle);
Events.OnKeyStartPressed.Add(onKeyStartPressed);
Events.OnKeyPressed.Add(onKeyPressed);