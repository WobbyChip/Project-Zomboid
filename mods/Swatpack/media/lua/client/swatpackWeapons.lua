-----------------------------------------------------------------------
-- Swat Pack Weapons
-- by relihschneider
-----------------------------------------------------------------------
-- General mod info
local MOD_ID = "Swatpack";
local MOD_NAME = "Swatpack";
local MOD_VERSION = "1.0";
local MOD_AUTHOR = "Relihschneider";
local MOD_DESCRIPTION = "adds new items to the game.";

local debugItems = false;

-- ------------------------------------------------
-- Functions
-- ------------------------------------------------
---
-- Prints out the mod info on startup.
--
local function info()
	print("Mod Loaded: " .. MOD_NAME .. " by " .. MOD_AUTHOR .. " (v" .. MOD_VERSION .. ")");
end

---
-- Add some items to the player's inventory for testing purposes.
--
local function giveItems()
    if debugItems then
        local player = getSpecificPlayer(0);
		
		player:getInventory():AddItem("swatpack.RiotShotgun");
		player:getInventory():AddItem("swatpack.Co2ShortRiotShotgun");
    end
end

-- local function getRandomCondition(_item)
	-- _item:setCondition(ZombRand(_item:getConditionMax())+1);
-- end

-- ------------------------------------------------
-- Game hooks
-- ------------------------------------------------
Events.OnGameBoot.Add(info);
Events.OnGameStart.Add(giveItems);
