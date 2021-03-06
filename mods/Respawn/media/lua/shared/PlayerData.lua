-- [-] Belts for some reason don't load in correct order
-- [-] Which causes attachment failure, and player need to put them back manually
-- [-] Basicly If you put Belt and then Holster add attachments and then respawn
-- [-] After respawn belt order will be different and attachments removed

-- [+] setWornItem will cause error if respawn with overweight invenotry > 50
-- [+] ItemContainer#Capacity is public simply modify it and then set back
-- [+] or use isUnlimitedCarry and setUnlimitedCarry

-- [?] and check if you die with overweight invenotry
-- [?] will items drop on the ground

-- [+] Primary and secondary items drop on MP
-- [+] This is cause client drops items and sends packet before event
-- [+] updateWhileDead()

-- [+] Corpse duplication, second player is seeing two corpses
-- [-] Some weird body part, Grab Base.Wound_*, client side error
-- [-] Probaly not even my error

Respawn = {};

--Save player

function savePlayer(player)
    savePlayerLevels(player);
    savePlayerBoosts(player);
    savePlayerBooks(player);
    savePlayerMultipliers(player);
    savePlayerInventory(player);
    saveRespawnLocation(player);
    savePlayerModel(player);
    saveNutrition(player);

    Respawn.Traits = player:getTraits();
    Respawn.Profession = player:getDescriptor():getProfession();
    Respawn.Recipes = player:getKnownRecipes();
    Respawn.ZombieKills = player:getZombieKills();
end

function savePlayerLevels(player)
    Respawn.Xp = {};
    Respawn.Levels = {};
    local perks = PerkFactory.PerkList;

    for i = 0, perks:size()-1 do
        local perk = perks:get(i);

        Respawn.Levels[perk] = player:getPerkLevel(perk);
        Respawn.Xp[perk] = player:getXp():getXP(perk);
    end
end

function savePlayerBoosts(player)
    Respawn.Boosts = {};
    local perks = PerkFactory.PerkList;
    local boosts = player:getXp();

    for i = 0, perks:size() - 1 do
        local perk = perks:get(i);
        Respawn.Boosts[perk] = boosts:getPerkBoost(perk);
    end
end

function savePlayerBooks(player)
    Respawn.SkillBooks = {};
    local items = getAllItems();

    for i = 0, items:size()-1 do
        if items:get(i):getTypeString() == "Literature" then
            local item = items:get(i):InstanceItem(items:get(i):getName());
            if item ~= nil and item:IsLiterature() and item:getNumberOfPages() > 0 then
                Respawn.SkillBooks[item:getFullType()] = player:getAlreadyReadPages(item:getFullType());
            end
        end
    end
end

function savePlayerMultipliers(player)
    Respawn.Multipliers = {};
    local perks = PerkFactory.PerkList;

    for i = 0, perks:size()-1 do
        local perk = perks:get(i);
        Respawn.Multipliers[perk] = player:getXp():getMultiplier(perk);
    end
end

function savePlayerInventory(player)
    local WornItems = player:getWornItems();
    Respawn.WornItems = {};
    
    for i = 0, WornItems:size()-1 do
        Respawn.WornItems[i] = WornItems:get(i);
    end

    local AttachedItems = player:getAttachedItems();
    Respawn.AttachedItems = {};
    
    for i = 0, AttachedItems:size()-1 do
        Respawn.AttachedItems[i] = AttachedItems:get(i);
    end

	Respawn.Items = player:getInventory():getItems():clone();
end

function saveRespawnLocation(player)
    local pModData = player:getModData();
    Respawn.X = pModData.RespawnX;
    Respawn.Y = pModData.RespawnY;
    Respawn.Z = pModData.RespawnZ;
end

function saveEquipItems(player)
    if SandboxVars.Respawn.dropHeavyItems then
        player:dropHeavyItems();
    end

    Respawn.PrimaryHandItem = player:getPrimaryHandItem();
    player:setPrimaryHandItem(nil);

    Respawn.SecondaryHandItem = player:getSecondaryHandItem();
    player:setSecondaryHandItem(nil);
end

function savePlayerModel(player)
    Respawn.Visual = {};
    Respawn.Visual.SkinTexture = player:getVisual():getSkinTextureIndex();
    Respawn.Visual.SkinTextureName = player:getVisual():getSkinTexture();
    Respawn.Visual.NonAttachedHair = player:getVisual():getNonAttachedHair();
    Respawn.Visual.BodyHair = player:getVisual():getBodyHairIndex();
    Respawn.Visual.Outfit = player:getVisual():getOutfit();
    Respawn.Visual.HairModel = player:getVisual():getHairModel();
    Respawn.Visual.BeardModel = player:getVisual():getBeardModel();
    Respawn.Visual.SkinColor = player:getVisual():getSkinColor();
    Respawn.Visual.HairColor = player:getVisual():getHairColor();
    Respawn.Visual.BeardColor = player:getVisual():getBeardColor();

    if Respawn.Visual.Outfit then
        Respawn.Visual.Outfit = Respawn.Visual.Outfit:clone();
    end

    Respawn.Descriptor = {};
    Respawn.Descriptor.Age = player:getAge();
    Respawn.Descriptor.Female = player:isFemale();
    Respawn.Descriptor.Forename = player:getDescriptor():getForename();
    Respawn.Descriptor.Surname = player:getDescriptor():getSurname();
end

function saveNutrition(player)
    Respawn.Nutrition = {};
    Respawn.Nutrition.Calories = player:getNutrition():getCalories();
    Respawn.Nutrition.Proteins = player:getNutrition():getProteins();
    Respawn.Nutrition.Lipids = player:getNutrition():getLipids();
    Respawn.Nutrition.Carbohydrates = player:getNutrition():getCarbohydrates();
    Respawn.Nutrition.Weight = player:getNutrition():getWeight();
end

--Load player

function loadPlayer(player)
    clearInventory(player);

    if SandboxVars.Respawn.keepInventory then
        loadPlayerInventory(player);
    end

    if SandboxVars.Respawn.keepLevels then
        loadPlayerLevels(player);
    end

    if SandboxVars.Respawn.keepBooks then
        loadPlayerBooks(player);
        loadPlayerMultipliers(player);
    end

    if SandboxVars.Respawn.keepRecipes then
        loadPlayerRecipes(player);
    end

    if SandboxVars.Respawn.keepNutrition then
        loadNutrition(player);
    end

    loadPlayerModel(player);
    loadPlayerBoosts(player);
    loadPlayerTraits(player);
    player:setZombieKills(Respawn.ZombieKills);
end

function loadPlayerLevels(player)
    for perk, level in pairs(Respawn.Levels or {}) do
        local perkLevel = player:getPerkLevel(perk);

        while perkLevel > 0 do
            player:LoseLevel(perk);
            perkLevel = perkLevel-1;
        end

        player:getXp():setXPToLevel(perk, 0);
        player:getXp():AddXP(perk, Respawn.Xp[perk], true, true, false, false);
    end
end

function loadPlayerBoosts(player)
    local prof = ProfessionFactory.getProfession(Respawn.Profession);

    for perk, boost in pairs(Respawn.Boosts or {}) do
        prof:addXPBoost(perk, boost);
    end

    player:getDescriptor():setProfessionSkills(prof);
    player:getDescriptor():setProfession(Respawn.Profession);
end

function loadPlayerTraits(player)
    player:getTraits():clear();

    for i = 0, Respawn.Traits:size()-1 do
        player:getTraits():add(Respawn.Traits:get(i));
    end
end

function loadPlayerBooks(player)
    for book, pages in pairs(Respawn.SkillBooks or {}) do
        player:setAlreadyReadPages(book, pages);
    end
end

function loadPlayerMultipliers(player)
    for perk, amount in pairs(Respawn.Multipliers or {}) do
        player:getXp():addXpMultiplier(perk, amount, 0, 10);
    end
end

function loadPlayerRecipes(player)
    for i = 0, Respawn.Recipes:size()-1 do
        player:learnRecipe(Respawn.Recipes:get(i));
    end
end

function loadPlayerInventory(player)
    local isUnlimitedCarry = player:isUnlimitedCarry();
    player:setUnlimitedCarry(true);

    --Assign new player's container to old items
    for i = 0, Respawn.Items:size()-1 do
        Respawn.Items:get(i):setEquipParent(player);
        Respawn.Items:get(i):setContainer(player:getInventory());
    end

    --Set items
	player:getInventory():setItems(Respawn.Items);
    local Belt = nil;
    local BeltExtra = nil;

    --Set back worn items, clothes, belts, etc
    for _, WornItem in pairs(Respawn.WornItems or {}) do
        player:getWornItems():setItem(WornItem:getLocation(), WornItem:getItem());

        if WornItem:getLocation() == "Belt" then
            Belt = WornItem;
        end
        
        if WornItem:getLocation() == "BeltExtra" then
            BeltExtra = WornItem;
        end
    end

    --Set back attached items, items in belts
    for _, AttachedItem in pairs(Respawn.AttachedItems or {}) do
        player:getAttachedItems():setItem(AttachedItem:getLocation(), AttachedItem:getItem());
    end

    --Wear belts manually
    if Belt then
        player:setWornItem(Belt:getLocation(), Belt:getItem());
    end

    if BeltExtra then
        player:setWornItem(BeltExtra:getLocation(), BeltExtra:getItem());
    end

    --Set items in primary and secondary hands
    player:setPrimaryHandItem(Respawn.PrimaryHandItem);
    player:setSecondaryHandItem(Respawn.SecondaryHandItem);
    player:setUnlimitedCarry(isUnlimitedCarry);
    player:update();

	if isClient() then
		triggerEvent("OnClothingUpdated", player);
	end
end

function loadRespawnLocation(player)
    if (Respawn.X and Respawn.Y and Respawn.Z) then
        player:setX(Respawn.X);
        player:setY(Respawn.Y);
        player:setZ(Respawn.Z);
    end;
end

function loadPlayerModel(player)
    if Respawn.Visual then
        player:getVisual():setSkinTextureIndex(Respawn.Visual.SkinTexture)
        player:getVisual():setSkinTextureName(Respawn.Visual.SkinTextureName);
        player:getVisual():setNonAttachedHair(Respawn.Visual.NonAttachedHair);
        player:getVisual():setBodyHairIndex(Respawn.Visual.BodyHair);
        player:getVisual():setOutfit(Respawn.Visual.Outfit);
        player:getVisual():setHairModel(Respawn.Visual.HairModel);
        player:getVisual():setBeardModel(Respawn.Visual.BeardModel);
        player:getVisual():setSkinColor(Respawn.Visual.SkinColor);
        player:getVisual():setHairColor(Respawn.Visual.HairColor);
        player:getVisual():setBeardColor(Respawn.Visual.BeardColor);
    end

    if Respawn.Descriptor then
        player:setAge(Respawn.Descriptor.Age);
        player:setFemale(Respawn.Descriptor.Female);
        player:getDescriptor():setFemale(Respawn.Descriptor.Female);
        player:getDescriptor():setForename(Respawn.Descriptor.Forename);
        player:getDescriptor():setSurname(Respawn.Descriptor.Surname);
    end
end

function loadNutrition(player)
    player:getNutrition():setCalories(Respawn.Nutrition.Calories);
    player:getNutrition():setProteins(Respawn.Nutrition.Proteins);
    player:getNutrition():setLipids(Respawn.Nutrition.Lipids);
    player:getNutrition():setCarbohydrates(Respawn.Nutrition.Carbohydrates);
    player:getNutrition():setWeight(Respawn.Nutrition.Weight);
end

--Other

function clearInventory(player)
    local isUnlimitedCarry = player:isUnlimitedCarry();
    player:setUnlimitedCarry(true);

    --Need to unequip default equped items
    local WornItems = {};

    --Remove item while looping will cause an error
    for i = 0, player:getWornItems():size()-1 do
        WornItems[i] = player:getWornItems():get(i):getItem();
    end

    --Unequip worn items
    for i, WornItem in pairs(WornItems or {}) do
        player:removeWornItem(WornItem);
    end

	player:getWornItems():clear(); --Worn items, like clothes and belt
    player:getAttachedItems():clear(); --Attched items, like attached to belt
    player:setPrimaryHandItem(nil);
    player:setSecondaryHandItem(nil);

	local playerInventory = player:getInventory();
	playerInventory:getItems():clear();
	playerInventory:removeAllItems();
	player:setInventory(playerInventory);
    player:setUnlimitedCarry(isUnlimitedCarry);
	player:update();

	if isClient() then
		triggerEvent("OnClothingUpdated", player);
	end
end

function setPlayerRespawn(player)
    local pModData = player:getModData();
	pModData.RespawnX = player:getX();
	pModData.RespawnY = player:getY();
	pModData.RespawnZ = player:getZ();
end

function removePlayerRespawn(player)
    local pModData = player:getModData();
	pModData.RespawnX = nil;
	pModData.RespawnY = nil;
	pModData.RespawnZ = nil;
end

function getPlayerRespawn(player)
    local pModData = player:getModData();
    return {x=pModData.RespawnX, y=pModData.RespawnY, z=pModData.RespawnZ};
end

function setRespawnRegion(player, region)
    local spawn = region.points[player:getDescriptor():getProfession()];
    if not spawn then spawn = region.points["unemployed"] end

    if spawn then
        local randSpawnPoint = spawn[(ZombRand(#spawn) + 1)];
        getWorld():setLuaSpawnCellX(randSpawnPoint.worldX);
        getWorld():setLuaSpawnCellY(randSpawnPoint.worldY);
        getWorld():setLuaPosX(randSpawnPoint.posX);
        getWorld():setLuaPosY(randSpawnPoint.posY);
        getWorld():setLuaPosZ(randSpawnPoint.posZ or 0);
    end
end

function setHealth(player, health)
    local parts = player:getBodyDamage():getBodyParts();
    local health = 80+(20*health/100);

    for i = 0, parts:size()-1 do
        parts:get(i):SetHealth(health);
    end
end