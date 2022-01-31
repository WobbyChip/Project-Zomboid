--
-- Created by IntelliJ IDEA.
-- User: RJ
-- Date: 22/09/2017
-- Time: 11:06
-- To change this template use File | Settings | File Templates.
--
require "Vehicles/ISUI/ISVehicleMechanics"


local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small)
local FONT_HGT_MEDIUM = getTextManager():getFontHeight(UIFont.Medium)

local old_ISVehicleMechanics_doMenuTooltip = ISVehicleMechanics.doMenuTooltip

function ISVehicleMechanics:doMenuTooltip(part, option, lua, name)
	local vehicle = part:getVehicle();
	
	if not vehicle:getScriptName():contains("AM2_") then old_ISVehicleMechanics_doMenuTooltip(self, part, option, lua, name) end
	
	local tooltip = ISToolTip:new();
	tooltip:initialise();
	tooltip:setVisible(false);
	tooltip.description = getText("Tooltip_craft_Needs") .. " : <LINE>";
	option.toolTip = tooltip;
	local keyvalues = part:getTable(lua);
	
	-- repair engines tooltip
	if lua == "takeengineparts" then
		local rgb = " <RGB:1,1,1>";
		local addedTxt = "";
		if part:getCondition() < 10 then
			rgb = " <RGB:1,0,0>";
			addedTxt = "/10";
			tooltip.description = tooltip.description .. rgb .. " " .. getText("Tooltip_Vehicle_EngineCondition", part:getCondition() .. addedTxt) .. " <LINE>";
		end
		rgb = " <RGB:1,1,1>";
		if self.chr:getPerkLevel(Perks.Mechanics) < part:getVehicle():getScript():getEngineRepairLevel() then
			rgb = " <RGB:1,0,0>";
		end
		tooltip.description = tooltip.description .. rgb .. getText("IGUI_perks_Mechanics") .. " " .. self.chr:getPerkLevel(Perks.Mechanics) .. "/" .. part:getVehicle():getScript():getEngineRepairLevel() .. " <LINE>";
		rgb = " <RGB:1,1,1>";
		local item = InventoryItemFactory.CreateItem("Base.Wrench");
		if not self.chr:getInventory():contains("Wrench") then
			tooltip.description = tooltip.description .. " <RGB:1,0,0>" .. item:getDisplayName() .. " 0/1 <LINE>";
		else
			tooltip.description = tooltip.description .. " <RGB:1,1,1>" .. item:getDisplayName() .. " 1/1 <LINE>";
		end
		
		tooltip.description = tooltip.description .. " <RGB:1,0,0> " .. getText("Tooltip_vehicle_TakeEnginePartsWarning");
	end
	if lua == "repairengine" then
		local rgb = " <RGB:1,1,1>";
		local addedTxt = "";
		if part:getCondition() >= 100 then
			tooltip.description = tooltip.description .. " <RGB:1,0,0> " .. getText("Tooltip_Vehicle_EngineCondition", part:getCondition()) .. " <LINE>";
		end
		rgb = " <RGB:1,1,1>";
		if self.chr:getPerkLevel(Perks.Mechanics) < part:getVehicle():getScript():getEngineRepairLevel() then
			rgb = " <RGB:1,0,0>";
		end
		tooltip.description = tooltip.description .. rgb .. getText("IGUI_perks_Mechanics") .. " " .. self.chr:getPerkLevel(Perks.Mechanics) .. "/" .. part:getVehicle():getScript():getEngineRepairLevel() .. " <LINE>";
		rgb = " <RGB:1,1,1>";
		local item = InventoryItemFactory.CreateItem("Base.Wrench");
		if not self.chr:getInventory():contains("Wrench") then
			tooltip.description = tooltip.description .. " <RGB:1,0,0>" .. item:getDisplayName() .. " 0/1 <LINE>";
		else
			tooltip.description = tooltip.description .. " <RGB:1,1,1>" .. item:getDisplayName() .. " 1/1 <LINE>";
		end
		local item = InventoryItemFactory.CreateItem("Base.EngineParts");
		if not self.chr:getInventory():contains("EngineParts") then
			tooltip.description = tooltip.description .. " <RGB:1,0,0>" .. item:getDisplayName() .. " 0/1 <LINE>";
		else
			tooltip.description = tooltip.description .. " <RGB:1,1,1>" .. item:getDisplayName() .. " <LINE>";
		end
	end
	if lua == "configheadlight" then
		local rgb = " <RGB:1,1,1>";
		tooltip.description = tooltip.description .. " <RGB:1,1,1> " .. getText("IGUI_HeadlightFocusing") .. ": " .. part:getLight():getFocusing() .. " <LINE>";
		--tooltip.description = tooltip.description .. " <RGB:1,0,0> Destination: " .. part:getLight():getDistanization() .. " <LINE>";
		--tooltip.description = tooltip.description .. " <RGB:1,0,0> Intensity: " .. part:getLight():getIntensity() .. " <LINE>";
		--rgb = " <RGB:1,1,1>";
		--local item = InventoryItemFactory.CreateItem("Base.Spanner");
		--if not self.chr:getInventory():contains("Spanner") then
		--	tooltip.description = tooltip.description .. " <RGB:1,0,0>" .. item:getDisplayName() .. " 0/1 <LINE>";
		--else
		--	tooltip.description = tooltip.description .. " <RGB:1,1,1>" .. item:getDisplayName() .. " 1/1 <LINE>";
		--end
		rgb = " <RGB:1,1,1>";
		if self.chr:getPerkLevel(Perks.Mechanics) < part:getVehicle():getScript():getHeadlightConfigLevel() then
			rgb = " <RGB:1,0,0>";
		end
		tooltip.description = tooltip.description .. rgb .. " Mechanic Skill: " .. self.chr:getPerkLevel(Perks.Mechanics) .. "/" .. part:getVehicle():getScript():getHeadlightConfigLevel() .. " <LINE>";
	end

	-- do you need the key to operate
	if VehicleUtils.RequiredKeyNotFound(part, self.chr) then
		tooltip.description = tooltip.description .. " <RGB:1,0,0> " .. getText("Tooltip_vehicle_keyRequired") .. " <LINE>";
	end
	
	if not keyvalues then return; end
	--	if not part:getInventoryItem() then return; end
	if not part:getItemType() then return; end
	local typeToItem = VehicleUtils.getItems(self.playerNum);
	-- first do items required
	if name then
		local item = InventoryItemFactory.CreateItem(name);
		if not typeToItem[name] then
			tooltip.description = tooltip.description .. " <RGB:1,0,0>" .. item:getDisplayName() .. " 0/1 <LINE>";
		else
			tooltip.description = tooltip.description .. " <RGB:1,1,1>" .. item:getDisplayName() .. " 1/1 <LINE>";
		end
	end
	if keyvalues.items then	for i,v in pairs(keyvalues.items) do
		local itemName = InventoryItemFactory.CreateItem(v.type);
		if itemName then
			itemName = itemName:getName();
		else
			itemName = v.type;
		end
		local keep = "";
		--		if v.keep then keep = "Keep "; end
		if not typeToItem[v.type] then
			tooltip.description = tooltip.description .. " <RGB:1,0,0>" .. keep .. itemName .. " 0/1 <LINE>";
		else
			tooltip.description = tooltip.description .. " <RGB:1,1,1>" .. keep .. itemName .. " 1/1 <LINE>";
		end
	end end
	-- recipes
	if keyvalues.recipes and keyvalues.recipes ~= "" then
		for _,recipe in ipairs(keyvalues.recipes:split(";")) do
			if not self.chr:isRecipeKnown(recipe) then
				tooltip.description = tooltip.description .. " <RGB:1,0,0> " .. getText("Tooltip_vehicle_requireRecipe", getRecipeDisplayName(recipe)) .. " <LINE>";
			else
				tooltip.description = tooltip.description .. " <RGB:1,1,1> " .. getText("Tooltip_vehicle_requireRecipe", getRecipeDisplayName(recipe)) .. " <LINE>";
			end
		end
	end
	-- uninstall stuff
	if keyvalues.requireUninstalled and (vehicle:getPartById(keyvalues.requireUninstalled) and vehicle:getPartById(keyvalues.requireUninstalled):getInventoryItem()) then
		tooltip.description = tooltip.description .. " <RGB:1,0,0> " .. getText("Tooltip_vehicle_requireUnistalled", getText("IGUI_VehiclePart" .. keyvalues.requireUninstalled)) .. " <LINE>";
	end
	local seatNumber = part:getContainerSeatNumber()
	local seatOccupied = (seatNumber ~= -1) and vehicle:isSeatOccupied(seatNumber)
	if keyvalues.requireEmpty and (round(part:getContainerContentAmount(), 3) > 0 or seatOccupied) then
		tooltip.description = tooltip.description .. " <RGB:1,0,0> " .. getText("Tooltip_vehicle_needempty", getText("IGUI_VehiclePart" .. part:getId())) .. " <LINE> ";
	end
	-- install stuff
	if keyvalues.requireInstalled then
		local split = keyvalues.requireInstalled:split(";");
		for i,v in ipairs(split) do
			if not vehicle:getPartById(v) or not vehicle:getPartById(v):getInventoryItem() then
				tooltip.description = tooltip.description .. " <RGB:1,0,0> " .. getText("Tooltip_vehicle_requireInstalled", getText("IGUI_VehiclePart" .. v)) .. " <LINE>";
			end
		end
	end
	-- now required skill
	local perks = keyvalues.skills;
	if perks and perks ~= "" then
		for _,perk in ipairs(perks:split(";")) do
			local name,level = VehicleUtils.split(perk, ":")
			local rgb = " <RGB:1,1,1> ";
			tooltip.description = tooltip.description .. rgb .. getText("Tooltip_vehicle_recommendedSkill", getText("IGUI_perks_" .. name), self.chr:getPerkLevel(Perks.FromString(name)) .. "/" .. level) .. " <LINE> <LINE>";
		end
	end
	-- install/uninstall success/failure chances
	local perks = keyvalues.skills;
	local success, failure = VehicleUtils.calculateInstallationSuccess(perks, self.chr);
	if success < 100 and failure > 0 then
		local colorSuccess = "<GREEN>";
		if success < 65 then
			colorSuccess = "<ORANGE>";
		end
		if success < 25 then
			colorSuccess = "<RED>";
		end
		local colorFailure = "<GREEN>";
		if failure > 30 then
			colorFailure = "<ORANGE>";
		end
		if failure > 60 then
			colorFailure = "<RED>";
		end
		tooltip.description = tooltip.description .. colorSuccess .. getText("Tooltip_chanceSuccess") .. " " .. success .. "% <LINE> " .. colorFailure .. getText("Tooltip_chanceFailure") .. " " .. failure .. "%";
	end
	if part:getItemType() and not part:getItemType():isEmpty() then
		if part:getInventoryItem() then
			local fixingList = FixingManager.getFixes(part:getInventoryItem());
			if not part:getScriptPart():isRepairMechanic() and not fixingList:isEmpty() then
				tooltip.description = tooltip.description .. " <LINE> <RGB:1,1,1>" .. getText("Tooltip_RepairableUninstalled");
			end
		end
	end
end
