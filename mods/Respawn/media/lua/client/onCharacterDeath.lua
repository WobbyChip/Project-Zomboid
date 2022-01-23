function onCharacterDeath(character)
    --This will be only true in multiplayer
    if isClient() then return end

    if instanceof(character, "IsoPlayer") then
        if SandboxVars.keepInventory then
            saveEquipItems(character);
        end

        savePlayer(character);

        if SandboxVars.keepInventory then
            clearInventory(character);
        end
    end
end

Events.OnCharacterDeath.Add(onCharacterDeath);