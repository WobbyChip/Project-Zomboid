local esTooltips = require("esq.tooltip.01"):new();
local esInfoOptions = require("es.info.options");
local esCommon = require("esq.common.01");
local esInfoClothing = {};

function esInfoClothing.isClothing(item)
    return item ~= nil and item:getBodyLocation() ~= "";
end

function esInfoClothing.getEquipped(selectedItem)
    local wornItems = getPlayer():getWornItems();
    local bodyLocationGroup = wornItems:getBodyLocationGroup();

    for i = 1, wornItems:size() - 1 do
        local wornItem = wornItems:get(i);
        local item = wornItem:getItem();

        if (selectedItem:getBodyLocation() == wornItem:getLocation()
                or bodyLocationGroup:isExclusive(wornItem:getLocation(), selectedItem:getBodyLocation())) then
            return item;
        end
    end

    return nil;
end

function esInfoClothing.fetchData(selectedItem)
    if (not selectedItem) then return nil end;
    local itemData = {};

    itemData["condition"] = {
        getText("Tooltip_weapon_Condition"),
        selectedItem:getCondition() .. "/" .. selectedItem:getConditionMax()
    };
    itemData["insulate"] = {
        getText("Tooltip_item_Insulation"),
        esCommon.numbers.round((selectedItem.getInsulation and selectedItem:getInsulation() or 0) * 100, 2)
    };
    itemData["wind"] = {
        getText("Tooltip_item_Windresist"),
        esCommon.numbers.round((selectedItem.getWindresistance and selectedItem:getWindresistance() or 0) * 100, 2)
    };
    itemData["water"] = {
        getText("Tooltip_item_Waterresist"),
        esCommon.numbers.round((selectedItem.getWaterResistance and selectedItem:getWaterResistance() or 0) * 100, 2)
    };
    itemData["bite"] = {
        getText("Tooltip_BiteDefense"),
        esCommon.numbers.round((selectedItem.getBiteDefense and selectedItem:getBiteDefense() or 0), 2)
    };
    itemData["scratch"] = {
        getText("Tooltip_ScratchDefense"),
        esCommon.numbers.round((selectedItem.getScratchDefense and selectedItem:getScratchDefense() or 0), 2)
    };
    if (selectedItem:getCategory() ~= "Container") then
        itemData["run"] = {
            getText("Tooltip_RunSpeedModifier"),
            esCommon.numbers.round((selectedItem.getRunSpeedModifier and selectedItem:getRunSpeedModifier() or 1), 2)
        };
        itemData["combat"] = {
            getText("Tooltip_CombatSpeedModifier"),
            esCommon.numbers.round((selectedItem.getCombatSpeedModifier and selectedItem:getCombatSpeedModifier() or 1), 2)
        };
    end
    itemData["bloody"] = {
        getText("Tooltip_clothing_bloody"),
        esCommon.numbers.round((selectedItem.getBloodLevel and selectedItem:getBloodLevel() or 0), 2)
    };
    itemData["dirty"] = {
        getText("Tooltip_clothing_dirty"),
        esCommon.numbers.round((selectedItem.getDirtyness and selectedItem:getDirtyness() or 0), 2)
    };
    itemData["wet"] = {
        getText("Tooltip_Wetness"),
        esCommon.numbers.round((selectedItem.getWetness and selectedItem:getWetness() or 0), 2)
    };
    itemData["capacity"] = {
        getText("Tooltip_container_Capacity"),
        esCommon.numbers.round((selectedItem.getCapacity and selectedItem:getCapacity() or 0), 2)
    };
    itemData["reduction"] = {
        getText("Tooltip_container_Weight_Reduction"),
        esCommon.numbers.round((selectedItem.getWeightReduction and selectedItem:getWeightReduction() or 0), 2)
    };
    if (selectedItem:getFabricType()) then
        local patchMat = ClothingRecipesDefinitions["FabricType"][selectedItem:getFabricType()].material;
        if (patchMat) then
            local material = "<A>";
            if (patchMat == "Base.DenimStrips") then
                material = "<B>";
            elseif (patchMat == "Base.LeatherStrips") then
                material = "<O>";
            end
            itemData["fabric"] = {
                material .. getText("ContextMenu_PatchHole") .. ": " .. getScriptManager():FindItem(patchMat):getDisplayName();
            };
        end
    end
    itemData["weight"] = {
        getText("Tooltip_item_Weight"),
        esCommon.numbers.round((selectedItem.getEquippedWeight and selectedItem:getEquippedWeight() or 0), 2) .. " ( "
                .. esCommon.numbers.round((selectedItem.getActualWeight and selectedItem:getActualWeight() or 0), 2) .. " ) "
    };

    if (selectedItem:getFullType() == "Base.AmmoStrap_Bullets") then
        selectedItem:setTooltip("Tooltip_AmmoStrap");
    end
    if (selectedItem.getTooltip and selectedItem:getTooltip() ~= nil) then
        itemData["description"] = { getText(selectedItem:getTooltip()) };
    end

    return itemData;
end

function esInfoClothing.getClothingData(selectedItem)
    local itemInfo, itemRow = {};
    local itemStats = esInfoClothing.fetchData(selectedItem);
    local eqItem = esInfoClothing.getEquipped(selectedItem);

    if (eqItem == selectedItem) then eqItem = nil end;
    local eqItemStats = esInfoClothing.fetchData(eqItem)

    itemRow = { selectedItem:getDisplayName(), "<TAB>", "", "<TAB>" };
    if (eqItem) then itemRow[5] = eqItem:getDisplayName() end;
    table.insert(itemInfo, itemRow);

    if (selectedItem:isEquipped()) then
        itemRow = { "(" .. getText("Tooltip_item_Equipped") ..")", "", "", "" };
        if (eqItem and eqItem:isEquipped()) then itemRow[5] = "(" .. getText("Tooltip_item_Equipped") ..")" end;
        table.insert(itemInfo, itemRow);
    elseif (eqItem and eqItem:isEquipped()) then
        itemRow = { "", "", "", "" };
        if (eqItem and eqItem:isEquipped()) then itemRow[5] = "(" .. getText("Tooltip_item_Equipped") ..")" end;
        table.insert(itemInfo, itemRow);
    end

    table.insert(itemInfo, { "" });

    itemRow = { itemStats["weight"][1], "", tostring(itemStats["weight"][2]), " " };
    if (eqItem ~= nil) then itemRow[5] = tostring(eqItemStats["weight"][2]) end
    table.insert(itemInfo, itemRow);

    itemRow = { itemStats["condition"][1], "", tostring(itemStats["condition"][2]), " " };
    if (eqItem ~= nil) then itemRow[5] = tostring(eqItemStats["condition"][2]) end
    table.insert(itemInfo, itemRow);

    if (itemStats["capacity"] and itemStats["capacity"][2] > 0) then
        itemRow = { itemStats["capacity"][1], "", tostring(itemStats["capacity"][2]), " " };
        if (eqItem ~= nil) then itemRow[5] = tostring(eqItemStats["capacity"][2]) end
        table.insert(itemInfo, itemRow);

        itemRow = { itemStats["reduction"][1], "", tostring(itemStats["reduction"][2]), " " };
        if (eqItem ~= nil) then itemRow[5] = tostring(eqItemStats["reduction"][2]) end
        table.insert(itemInfo, itemRow);
    end

    --  insulate, wind, water, bite, scratch, bloody, dirty,
    if (itemStats["insulate"][2] > 0 or eqItemStats and eqItemStats["insulate"][2] > 0) then
        itemRow = { itemStats["insulate"][1], "", tostring(itemStats["insulate"][2]), " " };
        if (eqItem ~= nil) then itemRow[5] = tostring(eqItemStats["insulate"][2]) end
        table.insert(itemInfo, itemRow);
    end

    if (itemStats["wind"][2] > 0 or (eqItem and eqItemStats["wind"][2] > 0)) then
        itemRow = { itemStats["wind"][1] };
        local colorA = "";
        local colorB = "";
        if (selectedItem:isEquipped() and eqItem == nil) then
        elseif (eqItem ~= nil) then
            if (itemStats["wind"][2] > eqItemStats["wind"][2]) then
                colorA = "<G>";
                colorB = "<R>";
            elseif (itemStats["wind"][2] < eqItemStats["wind"][2]) then
                colorB = "<G>";
                colorA = "<R>";
            end
        else
            colorA = "<G>";
            colorB = "<A>";
        end
        itemRow[3] = colorA .. tostring(itemStats["wind"][2]) .. " %";
        if (eqItem ~= nil) then
            itemRow[5] = colorB .. tostring(eqItemStats["wind"][2]) .. " %";
        end
        table.insert(itemInfo, itemRow);
    end
    if (itemStats["water"][2] > 0 or (eqItem and eqItemStats["water"][2] > 0)) then
        itemRow = { itemStats["water"][1] };
        local colorA = "";
        local colorB = "";
        if (selectedItem:isEquipped() and eqItem == nil) then
        elseif (eqItem ~= nil) then
            if (itemStats["water"][2] > eqItemStats["water"][2]) then
                colorA = "<G>";
                colorB = "<R>";
            elseif (itemStats["water"][2] < eqItemStats["water"][2]) then
                colorB = "<G>";
                colorA = "<R>";
            end
        else
            colorA = "<G>";
            colorB = "<A>";
        end
        itemRow[3] = colorA .. tostring(itemStats["water"][2]) .. " %";
        if (eqItem ~= nil) then
            itemRow[5] = colorB .. tostring(eqItemStats["water"][2]) .. " %";
        end
        table.insert(itemInfo, itemRow);
    end
    if (itemStats["bloody"][2] > 0 or (eqItem and eqItemStats["bloody"][2] > 0)) then
        itemRow = { itemStats["bloody"][1], "", tostring(itemStats["bloody"][2]) .. " %" };
        if (eqItem ~= nil) then
            itemRow[5] = tostring(eqItemStats["bloody"][2]) .. " %";
        end
        table.insert(itemInfo, itemRow);
    end
    if (itemStats["dirty"][2] > 0 or (eqItem and eqItemStats["dirty"][2] > 0)) then
        itemRow = { itemStats["dirty"][1], "", tostring(itemStats["dirty"][2]) .. " %" };
        if (eqItem ~= nil) then
            itemRow[5] = tostring(eqItemStats["dirty"][2]) .. " %";
        end
        table.insert(itemInfo, itemRow);
    end
    if (itemStats["wet"][2] > 0 or (eqItem and eqItemStats["wet"][2] > 0)) then
        itemRow = { itemStats["wet"][1], "", tostring(itemStats["wet"][2]) .. " %" };
        if (eqItem ~= nil) then
            itemRow[5] = tostring(eqItemStats["wet"][2]) .. " %";
        end
        table.insert(itemInfo, itemRow);
    end

    --  scratch, run, combat, description
    if (itemStats["bite"][2] > 0 or (eqItem and eqItemStats["bite"][2] > 0)) then
        itemRow = { itemStats["bite"][1] };
        local colorA = "";
        local colorB = "";
        if (selectedItem:isEquipped() and eqItem == nil) then
        elseif (eqItem ~= nil) then
            if (itemStats["bite"][2] > eqItemStats["bite"][2]) then
                colorA = "<G>";
                colorB = "<R>";
            elseif (itemStats["bite"][2] < eqItemStats["bite"][2]) then
                colorB = "<G>";
                colorA = "<R>";
            end
        else
            colorA = "<G>";
            colorB = "<A>";
        end
        itemRow[3] = colorA .. tostring(itemStats["bite"][2]) .. " %";
        if (eqItem ~= nil) then
            itemRow[5] = colorB .. tostring(eqItemStats["bite"][2]) .. " %";
        end
        table.insert(itemInfo, itemRow);
    end
    if (itemStats["scratch"][2] > 0 or (eqItem and eqItemStats["scratch"][2] > 0)) then
        itemRow = { itemStats["scratch"][1] };
        local colorA = "";
        local colorB = "";
        if (selectedItem:isEquipped() and eqItem == nil) then
        elseif (eqItem ~= nil) then
            if (itemStats["scratch"][2] > eqItemStats["scratch"][2]) then
                colorA = "<G>";
                colorB = "<R>";
            elseif (itemStats["scratch"][2] < eqItemStats["scratch"][2]) then
                colorB = "<G>";
                colorA = "<R>";
            end
        else
            colorA = "<G>";
            colorB = "<A>";
        end
        itemRow[3] = colorA .. tostring(itemStats["scratch"][2]) .. " %";
        if (eqItem ~= nil) then
            itemRow[5] = colorB .. tostring(eqItemStats["scratch"][2]) .. " %";
        end
        table.insert(itemInfo, itemRow);
    end
    if ((itemStats["run"] and itemStats["run"][2] ~= 1) or (eqItem and eqItem["run"] and eqItemStats["run"][2] ~= 1)) then
        local colorA = "";
        local colorB = "";
        if (eqItem ~= nil and itemStats["run"][2] ~= eqItemStats["run"][2]) then
            if (itemStats["run"][2] > eqItemStats["run"][2]) then
                colorA = "<G>";
                colorB = "<R>";
            else
                colorB = "<G>";
                colorA = "<R>";
            end
        end
        itemRow = { itemStats["run"][1], "", colorA ..tostring(itemStats["run"][2]), "" };
        if (eqItem ~= nil) then
            itemRow[5] = colorB ..tostring(eqItemStats["run"][2]);
        end
        table.insert(itemInfo, itemRow);
    end
    if ((itemStats["combat"] and itemStats["combat"][2] ~= 1) or (eqItem and eqItem["combat"] and eqItemStats["combat"][2] ~= 1)) then
        local colorA = "";
        local colorB = "";
        if (eqItem ~= nil and itemStats["combat"][2] ~= eqItemStats["combat"][2]) then
            if (itemStats["combat"][2] > eqItemStats["combat"][2]) then
                colorA = "<G>";
                colorB = "<R>";
            else
                colorB = "<G>";
                colorA = "<R>";
            end
        end
        itemRow = { itemStats["combat"][1], "", colorA ..tostring(itemStats["combat"][2]), "" };
        if (eqItem ~= nil) then
            itemRow[5] = colorB ..tostring(eqItemStats["combat"][2]);
        end
        table.insert(itemInfo, itemRow);
    end
    if (itemStats["description"] ~= nil) then
        itemRow = { itemStats["description"][1] };
        if (eqItem ~= nil and eqItemStats["description"][1] ~= nil) then
            itemRow[5] = tostring(eqItemStats["description"][1])
        end
        table.insert(itemInfo, itemRow);
    end
    if (itemStats["fabric"] ~= nil or (eqItem ~= nil and eqItemStats["fabric"] ~= nil)) then
        itemRow = {};
        if (itemStats["fabric"] ~= nil) then
            itemRow[1] = itemStats["fabric"][1];
        end
        if (eqItem ~= nil and eqItemStats["fabric"] ~= nil) then
            itemRow[4] = "";
            itemRow[5] = eqItemStats["fabric"][1];
        end
        table.insert(itemInfo, itemRow);
    end

    return itemInfo;
end

local baseInvTooltipRender = ISToolTipInv.render;
function ISToolTipInv:render()
    if esInfoOptions.getOption("clotheOn") and (not ISContextMenu.instance or not ISContextMenu.instance.visibleCheck) then
        if (not esInfoClothing.isClothing(self.item)) then
            return baseInvTooltipRender(self);
        end

        local clothesData = esInfoClothing.getClothingData(self.item);
        local cardinal = esInfoOptions.getOption("clotheCardinal");

        local tooltipOptions = {}
        tooltipOptions.tooltipCardinal = cardinal;
        tooltipOptions.directions = { "R", "R", "R", "R", "R" };

        local newTooltip = esTooltips:new(self, clothesData, tooltipOptions);

        newTooltip:init();
        newTooltip:setMinWidth(1);
        newTooltip:drawToolTip();
        newTooltip:drawData();

        if(cardinal == "O") then
            newTooltip:hideDefault();
            return;
        end

    end

    return baseInvTooltipRender(self);
end