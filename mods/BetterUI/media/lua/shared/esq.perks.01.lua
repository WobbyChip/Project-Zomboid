local esCommon = require("esq.common.01");
local playerPerks = {};

function playerPerks.getPerksLevel(player, perksEnum)
    local char = esCommon.player.getPlayerObject(player);
    local perkLevel = {};
    local perk = PerkFactory.getPerk(perksEnum);
    if (perk) then
        perkLevel.level = char:getPerkLevel(perksEnum);
        perkLevel.xpNow = char:getXp():getXP(perksEnum) - ISSkillProgressBar.getPreviousXpLvl(perk, perkLevel.level);
        perkLevel.xpNow = esCommon.numbers.round(perkLevel.xpNow, 2);
        perkLevel.toLevel = perk:getXpForLevel(perkLevel.level + 1);
        perkLevel.percent = esCommon.numbers.round((perkLevel.xpNow / perkLevel.toLevel) * 100, 2);
    end
    return perkLevel;
end

function playerPerks.getPerksData(player, query)
    local returnPerks;
    if (query == "Cooking") then
        returnPerks = playerPerks.getPerksLevel(player, Perks.Cooking);
        returnPerks.type = getText("IGUI_perks_Cooking");
    elseif (query == "Carpentry") then
        returnPerks = playerPerks.getPerksLevel(player, Perks.Woodwork);
        returnPerks.type = getText("IGUI_perks_Carpentry");
    elseif (query == "Farming") then
        returnPerks = playerPerks.getPerksLevel(player, Perks.Farming);
        returnPerks.type = getText("IGUI_perks_Farming");
    elseif (query == "FirstAid") then
        returnPerks = playerPerks.getPerksLevel(player, Perks.Doctor);
        returnPerks.type = getText("IGUI_perks_Doctor");
    elseif (query == "Electricity") then
        returnPerks = playerPerks.getPerksLevel(player, Perks.Electricity);
        returnPerks.type = getText("IGUI_perks_Electricity");
    elseif (query == "MetalWelding") then
        returnPerks = playerPerks.getPerksLevel(player, Perks.MetalWelding);
        returnPerks.type = getText("IGUI_perks_Metalworking");
    elseif (query == "Fishing") then
        returnPerks = playerPerks.getPerksLevel(player, Perks.Fishing);
        returnPerks.type = getText("IGUI_perks_Fishing");
    elseif (query == "Trapping") then
        returnPerks = playerPerks.getPerksLevel(player, Perks.Trapping);
        returnPerks.type = getText("IGUI_perks_Trapping");
    elseif (query == "Foraging") then
        returnPerks = playerPerks.getPerksLevel(player, Perks.PlantScavenging);
        returnPerks.type = getText("IGUI_perks_Foraging");
    elseif (query == "Tailoring") then
        returnPerks = playerPerks.getPerksLevel(player, Perks.Tailoring);
        returnPerks.type = getText("IGUI_perks_Tailoring");
    elseif (query == "Mechanics") then
        returnPerks = playerPerks.getPerksLevel(player, Perks.Mechanics);
        returnPerks.type = getText("IGUI_perks_Mechanics");
    end

    if (query:contains("Axe")) then
        returnPerks = playerPerks.getPerksLevel(player, Perks.Axe);
        returnPerks.type = getText("IGUI_perks_Axe");
    elseif (query:contains("Spear")) then
        returnPerks = playerPerks.getPerksLevel(player, Perks.Spear);
        returnPerks.type = getText("IGUI_perks_Spear");
    elseif (query:contains("SmallBlunt")) then
        returnPerks = playerPerks.getPerksLevel(player, Perks.SmallBlunt);
        returnPerks.type = getText("IGUI_perks_SmallBlunt");
    elseif (query:contains("Blunt")) then
        returnPerks = playerPerks.getPerksLevel(player, Perks.Blunt);
        returnPerks.type = getText("IGUI_perks_Blunt");
    elseif (query:contains("SmallBlade")) then
        returnPerks = playerPerks.getPerksLevel(player, Perks.SmallBlade);
        returnPerks.type = getText("IGUI_perks_SmallBlade");
    elseif (query:contains("LongBlade")) then
        returnPerks = playerPerks.getPerksLevel(player, Perks.LongBlade);
        returnPerks.type = getText("IGUI_perks_LongBlade");
    elseif (query:contains("Aiming")) then
        returnPerks = playerPerks.getPerksLevel(player, Perks.Aiming);
        returnPerks.type = getText("IGUI_perks_Aiming");
    elseif (query:contains("Reloading")) then
        returnPerks = playerPerks.getPerksLevel(player, Perks.Reloading);
        returnPerks.type = getText("IGUI_perks_Reloading");
    end

    return returnPerks;
end

return playerPerks;

