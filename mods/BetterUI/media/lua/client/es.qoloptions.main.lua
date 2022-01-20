esQolModOptions = esQolModOptions or {};
local json = require "json";
local esQolOptions = {};

esQolOptions.isMP = false;
esQolOptions.adminUsers = "MonsterSauce SpicySauce";

function esQolOptions.isAdmin(character)
    if (esQolOptions.adminUsers:contains(character:getUsername())) then
        return true;
    end
    return false;
end

function esQolOptions.getSettings(player)
    local allPapers = player:getInventory():FindAll("Base.SheetPaper2");
    local toTrash = LuaList:new();
    local modSettings;
    for i = 0, allPapers:size() - 1 do
        local paper = allPapers:get(i);
        if (paper:getModData()["esQoL.info.settings"]) then
            if (paper:getModData()["esQoL.info.settings"] > getTimestampMs()) then
                toTrash:add(paper);
            elseif (not modSettings) then
                modSettings = paper;
            elseif (modSettings:getModData()["esQoL.info.settings"] < paper:getModData()["esQoL.info.settings"]) then
                toTrash:add(modSettings);
                modSettings = paper;
            else
                toTrash:add(paper);
            end
        end
    end

    for i = 0, toTrash:size() - 1 do
        player:getInventory():Remove(toTrash:get(i));
    end

    if (modSettings) then
        return json.parse(tostring(modSettings:seePage(1)));
    end
    return nil;
end

function esQolOptions.setSettings()
    local sdf = SimpleDateFormat.new("dd-MM-yyyy hh:mm:ss");
    local timeStamped = getTimestampMs();

    for p = 0, getNumActivePlayers() do
        local sheetPaper = InventoryItemFactory.CreateItem("Base.SheetPaper2");
        local player = getSpecificPlayer(p);
        local modData = sheetPaper:getModData();

        if (player) then
            modData["esQoL.info.settings"] = timeStamped;
            sheetPaper:setName("QoL Info Settings [" .. sdf:format(timeStamped) .. "]");
            sheetPaper:addPage(1, json.stringify(esQolModOptions.options));
            sheetPaper:setLockedBy("player"..timeStamped);
            player:getInventory():AddItem(sheetPaper);
        end
    end
end

return esQolOptions;