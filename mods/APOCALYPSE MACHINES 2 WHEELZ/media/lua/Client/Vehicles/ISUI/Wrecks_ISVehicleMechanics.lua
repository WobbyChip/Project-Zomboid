--
-- Created by IntelliJ IDEA.
-- User: RJ
-- Date: 22/09/2017
-- Time: 11:06
-- To change this template use File | Settings | File Templates.
--
require "Vehicles/ISUI/ISVehicleMechanics"

local old_ISVehicleMechanics_doMenuTooltip = ISVehicleMechanics.doMenuTooltip
function ISVehicleMechanics:doMenuTooltip(part, option, lua, name)
	if name then
		local item = InventoryItemFactory.CreateItem(name)
		if not item then return false end
	end
	old_ISVehicleMechanics_doMenuTooltip(self, part, option, lua, name)
	
end

