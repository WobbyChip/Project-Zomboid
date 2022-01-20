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
TheStar.Notifier = TheStar.Notifier or {}
TheStar.Notifier.Condition = TheStar.Notifier.Condition or {}
TheStar.Notifier.ItemFall = TheStar.Notifier.ItemFall or {}

-- ****************
-- Condition

-- Ticks passed since previous condition check
local ticksPassed
local previousConditionLevel

function TheStar.Notifier.Condition.checkCondition(player, item)
    if item and TheStar.Utils.isHandWeapon(item) and player:isLocalPlayer() then
        local conditionLevel = item:getCondition() / item:getConditionMax()
        if conditionLevel < 0.0 then conditionLevel = 0.0 end
        if conditionLevel > 1.0 then conditionLevel = 1.0 end

        if not previousConditionLevel then previousConditionLevel = conditionLevel end
        -- Item was repaired
        if conditionLevel > previousConditionLevel then previousConditionLevel = conditionLevel end
        if conditionLevel < previousConditionLevel and conditionLevel <= TheStar.Options.overheadNotificationConditionLevel then
            previousConditionLevel = conditionLevel
            HaloTextHelper.addTextWithArrow(player, item:getDisplayName(), false, HaloTextHelper.getColorRed())
        end
    end
end

function TheStar.Notifier.Condition.onTick()
    ticksPassed = ticksPassed + 1
    if (ticksPassed < 20) then return end

    ticksPassed = 0
    TheStar.Notifier.Condition.checkCondition(getPlayer(), getPlayer():getPrimaryHandItem())
end

function TheStar.Notifier.Condition.onEquipPrimary(player, item)
    if not player:isLocalPlayer() then return end

    if item and TheStar.Utils.isHandWeapon(item) then
        TheStar.Notifier.Condition.checkCondition(player, item)
        ticksPassed = 0
        Events.OnTick.Add(TheStar.Notifier.Condition.onTick)
        return
    end

    previousConditionLevel = nil
    Events.OnTick.Remove(TheStar.Notifier.Condition.onTick)
end

function TheStar.Notifier.Condition.init()
    if TheStar.Options.showOverheadNotification then
        Events.OnEquipPrimary.Add(TheStar.Notifier.Condition.onEquipPrimary)
        --[[
        The OnEquipPrimary event will only be triggered for an item that has been equipped only after the save has loaded,
        therefore the notification will not be displayed for an item that has already been equipped.
        --]]
        if getPlayer() then
            TheStar.Notifier.Condition.onEquipPrimary(getPlayer(), getPlayer():getPrimaryHandItem())
        end
    else
        previousConditionLevel = nil
        Events.OnEquipPrimary.Remove(TheStar.Notifier.Condition.onEquipPrimary)
        Events.OnTick.Remove(TheStar.Notifier.Condition.onTick)
    end
end

-- ****************
-- OnItemFall

function TheStar.Notifier.ItemFall.onItemFall(item)
    local player = getPlayer()
    if TheStar.Utils.isHandWeapon(item) and player:isLocalPlayer() and player:isAlive() then
        HaloTextHelper.addText(player, getText("IGUI_TheStar_Notification_ItemFall", item:getDisplayName()), HaloTextHelper.getColorRed())
    end
end

function TheStar.Notifier.ItemFall.init()
    -- Was added in Build 41.64
    if getCore():getGameVersion():getMinor() < 64 then return end

    if TheStar.Options.showOverheadNotificationItemFall then
        Events.onItemFall.Add(TheStar.Notifier.ItemFall.onItemFall)
    else
        Events.onItemFall.Remove(TheStar.Notifier.ItemFall.onItemFall)
    end
end

function TheStar.Notifier.init()
    TheStar.Notifier.Condition.init()
    TheStar.Notifier.ItemFall.init()
end

Events.OnGameStart.Add(TheStar.Notifier.init)