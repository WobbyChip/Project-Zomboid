ISWorldMenuElements = ISWorldMenuElements or {}

function ISWorldMenuElements.TurnOffTVRadioFromContextMenu_Toggle()
	local self = ISMenuElement.new()

	function self.init()
	end

	function self.createMenu(_data)
		for _,item in ipairs(_data.objects) do
			if instanceof(item, "IsoWaveSignal") then
				local deviceData = item:getDeviceData()
				local caption = nil
				if deviceData:getIsTurnedOn() then
					caption = getText("ContextMenu_Turn_Off")
				else
					caption = getText("ContextMenu_Turn_On")
				end
				local option = _data.context:addOption(caption, _data, self.toggle, item)
				option.notAvailable = (not deviceData:canBePoweredHere())
				if option.notAvailable then
					local toolTip = ISToolTip:new()
					toolTip:initialise()
					toolTip:setVisible(false)
					toolTip:setName(deviceData:getDeviceName())
					toolTip.maxLineWidth = 512
					toolTip.description = getText("IGUI_RadioRequiresPowerNearby")
					option.toolTip = toolTip
				end
			end
		end
	end

	function self.toggle(_data, item)
		if		instanceof(item, "InventoryItem")
			or  (instanceof(item, "VehiclePart")
			 and _data.player:getVehicle() == item:getVehicle())
			or  (instanceof(item, "IsoObject")
			 and item:getSquare() ~= nil
			 and luautils.walkAdj(_data.player, item:getSquare(), false)) then
			ISTimedActionQueue.add(ISRadioAction:new("ToggleOnOff", _data.player, item))
		end
	end

	return self
end


ISInventoryMenuElements = ISInventoryMenuElements or {};

function ISInventoryMenuElements.TurnOffTVRadioFromContextMenu_Toggle()
	local self = ISMenuElement.new()
	self.inventoryMenu = ISContextManager.getInstance().getInventoryMenu()

	function self.init()
	end

	function self.createMenu(item)
		if		(instanceof(item, "Radio")
			 or  instanceof(item, "IsoWaveSignal"))
			and item:getContainer() == self.inventoryMenu.inventory
			and self.inventoryMenu.player:isEquipped(item) then
			local deviceData = item:getDeviceData()
			local caption = nil
			if deviceData:getIsTurnedOn() then
				caption = getText("ContextMenu_Turn_Off")
			else
				caption = getText("ContextMenu_Turn_On")
			end
			local option = self.inventoryMenu.context:addOption(caption, self.inventoryMenu, self.toggle, item)
			option.notAvailable = ((deviceData:getIsBatteryPowered()
							 and    deviceData:getPower() <= 0)
							or     (not deviceData:getIsBatteryPowered()
							 and    not deviceData:canBePoweredHere()))
			if option.notAvailable then
				local toolTip = ISToolTip:new()
				toolTip:initialise()
				toolTip:setVisible(false)
				toolTip:setName(deviceData:getDeviceName())
				toolTip.maxLineWidth = 512
				toolTip.description = getText("IGUI_RadioRequiresPowerNearby")
				option.toolTip = toolTip
			end
		end
	end

	function self.toggle(inventoryMenu, item)
		ISTimedActionQueue.add(ISRadioAction:new("ToggleOnOff", inventoryMenu.player, item))
	end

	return self
end
