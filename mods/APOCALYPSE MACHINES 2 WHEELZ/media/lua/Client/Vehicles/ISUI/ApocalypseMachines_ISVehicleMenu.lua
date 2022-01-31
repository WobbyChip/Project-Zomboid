require "Vehicles/ISUI/ISVehicleMenu"


function ISVehicleMenu.onToggleHeater(playerObj)
	local playerNum = playerObj:getPlayerNum()
	if not ISVehicleMenu.acui then
		ISVehicleMenu.acui = {}
	end
	local ui = ISVehicleMenu.acui[playerNum]
	if not ui or ui.character ~= playerObj then
		ui = ISVehicleACUI:new(0,0,playerObj)
		if not ui then return false end
		ui:initialise()
		ui:instantiate()
		ISVehicleMenu.acui[playerNum] = ui
	end
	if ui:isReallyVisible() then
		ui:removeFromUIManager()
		if JoypadState.players[playerNum+1] then
			setJoypadFocus(playerNum, nil)
		end
	else
		ui:setVehicle(playerObj:getVehicle())
		ui:addToUIManager()
		if JoypadState.players[playerNum+1] then
			JoypadState.players[playerNum+1].focus = ui
		end
	end
end
