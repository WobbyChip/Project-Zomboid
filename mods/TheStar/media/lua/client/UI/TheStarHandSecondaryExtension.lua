---
--- Mod: Weapon Condition Indicator
--- Workshop: https://steamcommunity.com/sharedfiles/filedetails/?id=2619072426
--- Author: NoctisFalco
--- Profile: https://steamcommunity.com/id/NoctisFalco/
---
--- Redistribution of this mod without explicit permission from the original creator is prohibited
--- under any circumstances. This includes, but not limited to, uploading this mod to the Steam Workshop
--- or any other site, distribution as part of another mod or modpack, distribution of modified versions.
--- You are free to do whatever you want with the mod provided you do not upload any part of it anywhere.
---
--- The QualityStar_n.png icons are the property of The Indie Stone.
--- The mod overrides parts of the ISHotbar.lua, ISEquippedItem.lua files by The Indie Stone.
---

TheStar = TheStar or {}
TheStar.HandSecondaryExtension = TheStar.HandSecondaryExtension or {}

-- Ticks passed since previous check
local ticksPassed
local handSecondaryExtensionUI

local function initUI()
    if ISEquippedItem.instance then
        handSecondaryExtensionUI = ISEquippedItem.instance.handSecondaryExtensionUI
    end
end

local function hideUI()
    if handSecondaryExtensionUI then
        handSecondaryExtensionUI:setVisible(false)
    end
end

-- ****************
-- Battery

local function getRemainingCharge(item)
    local dayLength = getSandboxOptions():getDayLengthMinutes() / 60
    local usePerMinute = 150 * dayLength / item:getTicksPerEquipUse()

    local minutesLeft = item:getDrainableUsesInt() / usePerMinute * 1.25
    local hours = math.floor(minutesLeft / 60)
    local minutes = math.floor(minutesLeft % 60)
    return getText("Tooltip_TheStar_BatteryCharge", hours, minutes)
end

local function checkBattery(player, item)
    if item and TheStar.Utils.isLightSource(item) and player:isLocalPlayer() then
        local text = TheStar.Utils.round(item:getUsedDelta() * 100) .. "%"
        local tooltip = getRemainingCharge(item)
        handSecondaryExtensionUI:setText(text, tooltip)
        handSecondaryExtensionUI:setVisible(true)
    end
end

local function onTick()
    ticksPassed = ticksPassed + 1
    if (ticksPassed < 50) then return end

    ticksPassed = 0
    checkBattery(getPlayer(), getPlayer():getSecondaryHandItem())
end

local function onEquipSecondary(player, item)
    if not player:isLocalPlayer() then return end

    if item and TheStar.Utils.isLightSource(item) then
        checkBattery(player, item)
        ticksPassed = 0
        Events.OnTick.Add(onTick)
        return
    end

    handSecondaryExtensionUI:setVisible(false)
    Events.OnTick.Remove(onTick)
end

function TheStar.HandSecondaryExtension.init()
    if TheStar.Options.showBatteryChargeEquippedItem then
        initUI()
        Events.OnEquipSecondary.Add(onEquipSecondary)
        --[[
        The OnEquipPrimary event will only be triggered for an item that has been equipped only after the save has loaded,
        therefore the notification will not be displayed for an item that has already been equipped.
        --]]
        if getPlayer() then
            onEquipSecondary(getPlayer(), getPlayer():getSecondaryHandItem())
        end
    else
        hideUI()
        Events.OnEquipSecondary.Remove(onEquipSecondary)
        Events.OnTick.Remove(onTick)
    end
end

local function onCreatePlayer(id)
    if getCore():isDedicated() then return end

    local player = getSpecificPlayer(id)
    if not player or not player:isLocalPlayer() then return end

    TheStar.HandSecondaryExtension.init()
    Events.OnJoypadActivate.Add(initUI)
end

local function onPlayerDeath(player)
    if not player:isLocalPlayer() then return end

    hideUI()
    Events.OnJoypadActivate.Remove(initUI)
    -- Battery
    Events.OnEquipSecondary.Remove(onEquipSecondary)
    Events.OnTick.Remove(onTick)
end

Events.OnCreatePlayer.Add(onCreatePlayer)
Events.OnPlayerDeath.Add(onPlayerDeath)