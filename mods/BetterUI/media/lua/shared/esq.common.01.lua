local esCommonPlug = {};

esCommonPlug.items = {};
function esCommonPlug.items.destroyItem(item, container)
    if container == nil then container = item:getContainer(); end
    if (container.getType and container:getType() == "floor") then
        item:getWorldItem():removeFromWorld();
        item:getWorldItem():getSquare():transmitRemoveItemFromSquare(item:getWorldItem());
        item:getWorldItem():getSquare():removeWorldObject(item:getWorldItem());
        item:setWorldItem(nil);
    end
    container:Remove(item);
end

function esCommonPlug.items.createItem(item, container, character)
    if container:getType() == "floor" then
        local player = esCommonPlug.player.getPlayerObject(character);
        player:getCurrentSquare():AddWorldInventoryItem(item,
                player:getX() - math.floor(player:getX()),
                player:getY() - math.floor(player:getY()),
                player:getZ() - math.floor(player:getZ()));
    end
    container:AddItem(item);
end

function esCommonPlug.items.getStackUses(itemStack)
    local totalDelta = 0;
    for i = 0, itemStack:size() - 1 do
        totalDelta = totalDelta + itemStack:get(i):getDrainableUsesInt();
    end

    return totalDelta;
end

function esCommonPlug.items.getStackItems(fullType, containers)
    if (containers.size == nil) then
        return containers:FindAll(fullType);
    end

    local itemStack = LuaList:new();
    for c = 0, containers:size() - 1 do
        local container = containers:get(c);
        itemStack:addAll(container:FindAll(fullType));
    end

    return itemStack;
end

function esCommonPlug.items.moveTo(stack, container, player)
    local char = esCommonPlug.player.getPlayerObject(player);
    local transferTime;
    if (isClient()) then
        transferTime = 15;
    end
    for i = 0, stack:size() - 1 do
        local item = stack:get(i);
        local action = ISInventoryTransferAction:new(char, item, item:getContainer(), container, transferTime);
        ISTimedActionQueue.add(action)
    end
end

function esCommonPlug.items.getStackFromSelection(selection)
    local newItemStack = LuaList:new();

    for v, k in pairs(selection) do
        if instanceof(k, "InventoryItem") then
            newItemStack:add(k);
        else
            for x = 2, #k.items do
                if instanceof(k.items[x], "InventoryItem") then
                    newItemStack:add(k.items[x])
                end
            end
        end
    end

    return newItemStack;
end

esCommonPlug.containers = {};
function esCommonPlug.containers.getPlayer(player)
    local char = esCommonPlug.player.getPlayerNumber(player);
    local containerList = ArrayList.new();
    for i,v in ipairs(getPlayerInventory(char).inventoryPane.inventoryPage.backpacks) do
        containerList:add(v.inventory);
    end
    return containerList;
end

function esCommonPlug.containers.getLoot(player)
    local char = esCommonPlug.player.getPlayerNumber(player);
    local containerList = ArrayList.new();
    for i,v in ipairs(getPlayerLoot(char).inventoryPane.inventoryPage.backpacks) do
        containerList:add(v.inventory);
    end
    return containerList;
end

function esCommonPlug.containers.getAll(player)
    local containerList = esCommonPlug.containers.getPlayer(player);
    containerList:addAll(esCommonPlug.containers.getLoot(player));
    return containerList;
end

function esCommonPlug.containers.getItem(container)
    if (container.getContainingItem and container:getContainingItem()) then
        return container:getContainingItem()
    end
end

esCommonPlug.numbers = {};
function esCommonPlug.numbers.round(num, numDecimalPlaces)
    local mult = 10^(numDecimalPlaces or 0);
    return math.floor(num * mult + 0.5) / mult;
end

function esCommonPlug.numbers.padding(number, wholePadding, fractionPadding)
    local wholeNum = string.split(number,"\\.")[1];
    local fractionNum = string.split(number,"\\.")[2];

    while (string.len(wholeNum) < wholePadding) do
        wholeNum = '0' .. wholeNum;
    end

    if (fractionPadding == nil) then
        if (fractionNum ~= nil) then
            return wholeNum..'.'..fractionNum;
        end
        return wholeNum;
    end

    if (fractionNum == nil) then fractionNum = '' end;
    while (string.len(fractionNum) < fractionPadding) do
        fractionNum = fractionNum .. '0';
    end

    return wholeNum..'.'..fractionNum;
end

function esCommonPlug.numbers.roll(percent)
    if (percent < 1) then
        return (percent * 100) >= ZombRand(100);
    end
    return percent >= ZombRand(100);
end

esCommonPlug.volume = {};
function esCommonPlug.volume.getColors()
    local colors = {
        red = { r = 0.63, g = 0.10, b = 0.10, a = 1.0 },
        green = { r = 0.41, g = 0.80, b = 0.41, a = 0.9 },
        blue = { r = 0.55, g = 0.55, b = 0.87, a = 0.9 },
        yellow = { r = 0.84, g = 0.78, b = 0.30, a = 0.9 },
        orange = { r = 0.79, g = 0.44, b = 0.19, a = 0.9 },
        active = { r = 1, b = 1, g = 1, a = 0.8 },
        inActive = { r = 0.5, b = 0.5, g = 0.5, a = 0.8 },
        backgroundColor = { r = 0, g = 0, b = 0, a = 0.7 },
        borderColor = { r = 0.4, g = 0.4, b = 0.4, a = 1.0 },
    }
    return colors;
end

function esCommonPlug.volume.getRGB(percentage)
    if (percentage > 1) then percentage = percentage/100 end;
    local rgb = {};
    local colors = esCommonPlug.volume.getColors();

    if (percentage < 0.20) then
        rgb.green = colors.red;
        rgb.red = colors.green;
    elseif (percentage >= 0.20 and percentage < 0.40) then
        rgb.green = colors.orange;
        rgb.red = colors.blue;
    elseif (percentage >= 0.40 and percentage < 0.60) then
        rgb.green = colors.yellow;
        rgb.red = colors.yellow;
    elseif (percentage >= 0.60 and percentage < 0.80) then
        rgb.green = colors.blue;
        rgb.red = colors.orange;
    elseif (percentage >= 0.80) then
        rgb.green = colors.green;
        rgb.red = colors.red;
    end

    return rgb;
end

function esCommonPlug.volume.getRGBTag(percentage)
    local rgb = esCommonPlug.volume.getRGB(percentage);

    return {
        ["green"] = "<RGB:" .. rgb.green.r .. "," .. rgb.green.g .. "," .. rgb.green.b .. ">",
        ["red"] = "<RGB:" .. rgb.red.r .. "," .. rgb.red.g .. "," .. rgb.red.b .. ">",
    }
end

esCommonPlug.modData = {}
function esCommonPlug.modData.get(item, key)
    if (item == nil) then return nil end;

    local modData = item:getModData();
    if (key ~= nil) then
        return modData[key];
    end
    return modData;
end

function esCommonPlug.modData.set(item, key, value)
    if (item == nil) then return nil end;
    item:getModData()[key] = value;
end

function esCommonPlug.modData.jsonGet(item, key)
    if (item == nil) then return nil end;
    local json = require "json";
    local jsonData = item:getModData()[key] or json.stringify({});
    return json.parse(jsonData);
end

function esCommonPlug.modData.jsonSet(item, tableValue, key)
    if (item == nil) then return nil end;
    local json = require "json";
    item:getModData()[key] = json.stringify(tableValue);
end

esCommonPlug.player = {};
function esCommonPlug.player.getPlayerNumber(player)
    if (player == nil) then
        return 0;
    elseif (type(player) == "number") then
        return player;
    else
        return player:getPlayerNum();
    end
end

function esCommonPlug.player.getPlayerObject(player)
    if (player == nil) then
        return getSpecificPlayer(0);
    elseif (type(player) == "number") then
        return getSpecificPlayer(player);
    else
        return player;
    end
end

esCommonPlug.utils = {};
function esCommonPlug.utils.sizeOf(array)
    local nCount=0;
    for v,k in pairs(array) do
        nCount = nCount + 1;
    end
    return nCount;
end

function esCommonPlug.utils.split(string, delimiter)
    local result = {};
    for match in (string..delimiter):gmatch("(.-)"..delimiter) do
        table.insert(result, match);
    end
    return result;
end

function esCommonPlug.utils.trim(s)
    return (string.gsub(s, "^%s*(.-)%s*$", "%1"))
end

return esCommonPlug;