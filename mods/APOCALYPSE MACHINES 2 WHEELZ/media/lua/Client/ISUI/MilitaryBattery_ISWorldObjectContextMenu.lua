require "ISUI/ISWorldObjectContextMenu"


-- local function predicateNotBroken(item)
	-- return not item:isBroken()
-- end

-- local function predicateNotEmpty(item)
	-- return item:getUsedDelta() > 0
-- end

local function predicateNotFull(item)
	return item:getUsedDelta() < 1
end


local function onCarBatteryCharger_Activate(carBatteryCharger, playerObj)
	if luautils.walkAdj(playerObj, carBatteryCharger:getSquare()) then
		ISTimedActionQueue.add(ISActivateCarBatteryChargerAction:new(playerObj, carBatteryCharger, true, 50))
	end
end

local function onCarBatteryCharger_Deactivate(carBatteryCharger, playerObj)
	if luautils.walkAdj(playerObj, carBatteryCharger:getSquare()) then
		ISTimedActionQueue.add(ISActivateCarBatteryChargerAction:new(playerObj, carBatteryCharger, false, 50))
	end
end

local function onCarBatteryCharger_ConnectBattery(carBatteryCharger, playerObj, battery)
	if luautils.walkAdj(playerObj, carBatteryCharger:getSquare()) then
		ISWorldObjectContextMenu.transferIfNeeded(playerObj, battery)
		ISTimedActionQueue.add(ISConnectCarBatteryToChargerAction:new(playerObj, carBatteryCharger, battery, 100))
	end
end

local function onCarBatteryCharger_RemoveBattery(carBatteryCharger, playerObj)
	if luautils.walkAdj(playerObj, carBatteryCharger:getSquare()) then
		ISTimedActionQueue.add(ISRemoveCarBatteryFromChargerAction:new(playerObj, carBatteryCharger, 100))
	end
end

local function onCarBatteryCharger_Take(carBatteryCharger, playerObj)
	if luautils.walkAdj(playerObj, carBatteryCharger:getSquare()) then
		ISTimedActionQueue.add(ISTakeCarBatteryChargerAction:new(playerObj, carBatteryCharger, 50))
	end
end

function ISWorldObjectContextMenu.handleCarBatteryCharger(test, context, worldobjects, playerObj, playerInv)
	if test == true then return true end
	if carBatteryCharger:getBattery() then
		if carBatteryCharger:isActivated() then
			context:addOption(getText("ContextMenu_Turn_Off"), carBatteryCharger, onCarBatteryCharger_Deactivate, playerObj)
		else
			local option = context:addOption(getText("ContextMenu_Turn_On"), carBatteryCharger, onCarBatteryCharger_Activate, playerObj)
			if not (carBatteryCharger:getSquare():haveElectricity() or
					(GameTime:getInstance():getNightsSurvived() < SandboxVars.ElecShutModifier and carBatteryCharger:getSquare():getRoom())) then
				option.notAvailable = true
				option.toolTip = ISWorldObjectContextMenu.addToolTip()
				option.toolTip:setVisible(false)
				option.toolTip.description = getText("IGUI_RadioRequiresPowerNearby")
			end
		end
		local label = getText("ContextMenu_CarBatteryCharger_RemoveBattery").." (" ..  math.floor(carBatteryCharger:getBattery():getUsedDelta() * 100) .. "%)"
		context:addOption(label, carBatteryCharger, onCarBatteryCharger_RemoveBattery, playerObj)
	else
		local batteryList = playerInv:getAllTypeEvalRecurse("CarBattery1", predicateNotFull)
		playerInv:getAllTypeEvalRecurse("CarBattery2", predicateNotFull, batteryList)
		playerInv:getAllTypeEvalRecurse("CarBattery3", predicateNotFull, batteryList)
		playerInv:getAllTypeEvalRecurse("CarBattery8", predicateNotFull, batteryList)
		playerInv:getAllTypeEvalRecurse("CycleBattery", predicateNotFull, batteryList)
		playerInv:getAllTypeEvalRecurse("CordlessDrillBattery", predicateNotFull, batteryList)
		--print("Battery List " .. tostring(batteryList))
		if not batteryList:isEmpty() then
			local chargeOption = context:addOption(getText("ContextMenu_CarBatteryCharger_ConnectBattery"))
			local subMenuCharge = context:getNew(context)
			context:addSubMenu(chargeOption, subMenuCharge)
			local done = false
			for i=1,batteryList:size() do
				local battery = batteryList:get(i-1)
				if battery:getUsedDelta() < 1 then
					local label = battery:getName() .. " (" ..  math.floor(battery:getUsedDelta() * 100) .. "%)"
					subMenuCharge:addOption(label, carBatteryCharger, onCarBatteryCharger_ConnectBattery, playerObj, battery)
					done = true
				end
			end
			if not done then context:removeLastOption() end
		end
		context:addOption(getText("ContextMenu_CarBatteryCharger_Take"), carBatteryCharger, onCarBatteryCharger_Take, playerObj)
	end
end
