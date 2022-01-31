require "Vehicle/ISUI/ISVehicleMenu"


local oldFillMenuOutsideVehicle =ISVehicleMenu.FillMenuOutsideVehicle
function ISVehicleMenu.FillMenuOutsideVehicle(player, context, vehicle, test)
	local playerObj = getSpecificPlayer(player)
    oldFillMenuOutsideVehicle(player, context, vehicle, test)
	--print("TEST 1")	
	if vehicle:getScriptName():contains("Commodore") then --or isAdmin()
		--print("TEST 2")
           -- context:addOption(getText("ContextMenu_TurnMoveBike"), playerObj, AM2_SFDriveFunctions_turnBike, vehicle)
			return
    end
   if vehicle:getScriptName():contains("AM2_") or vehicle:getScriptName():contains("AME_") then --or isAdmin()
		--print("TEST 2")
		if not vehicle:getScriptName():contains("Commodore") then
            context:addOption(getText("ContextMenu_TurnMoveBike"), playerObj, AM2_SFDriveFunctions_turnBike, vehicle)
		end
			
    end
	-- local square = playerObj:getSquare()
   -- if vehicle:getScriptName():contains("AME_") and AM2_goodVehicleTile(square) then --or isAdmin() --ADD SQUARE BLOCKED CHECKS!
		-- --print("TEST 2")
            -- context:addOption(getText("ContextMenu_PullDevice"), playerObj, AM2_PullDevice, vehicle)
			
    -- end
end

local old_ISVehicleMenu_showRadialMenuOutside = ISVehicleMenu.showRadialMenuOutside
function ISVehicleMenu.showRadialMenuOutside(playerObj)
	 old_ISVehicleMenu_showRadialMenuOutside(playerObj)
	 local playerIndex = playerObj:getPlayerNum()
	local menu = getPlayerRadialMenu(playerIndex)
	 local vehicle = ISVehicleMenu.getVehicleToInteractWith(playerObj)
	 if not vehicle then return end
	 if not vehicle:getScriptName():contains("AM2_") and not vehicle:getScriptName():contains("AME_") then --or isAdmin()
		--print("TEST 2")
           -- context:addOption(getText("ContextMenu_TurnMoveBike"), playerObj, AM2_SFDriveFunctions_turnBike, vehicle)
			return
    end
	if vehicle:getScriptName():contains("Commodore") then --or isAdmin()
		--print("TEST 2")
           -- context:addOption(getText("ContextMenu_TurnMoveBike"), playerObj, AM2_SFDriveFunctions_turnBike, vehicle)
			return
    end
	 
			--local menu = getPlayerRadialMenu(player:getPlayerNum())
			if menu then
				menu:addSlice(getText("ContextMenu_TurnMoveBike"), getTexture("media/textures/AM2_bike1.png"), AM2_SFDriveFunctions_turnBike, playerObj, vehicle)
			end
	-- local square = playerObj:getSquare()
   -- if vehicle:getScriptName():contains("AME_") and AM2_goodVehicleTile(square) then --or isAdmin()
		-- --print("TEST 2")
            -- context:addOption(getText("ContextMenu_PullDevice"),
			-- getTexture("media/textures/AM2_bike1.png"),
			-- AM2_PullDevice,
			-- playerObj,
			-- vehicle)
			
    -- end
end