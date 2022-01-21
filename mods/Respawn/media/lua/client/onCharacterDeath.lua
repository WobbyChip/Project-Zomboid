function onCharacterDeath(character)
    if SandboxVars.keepInventory and instanceof(character, "IsoPlayer") then
        saveEquipItems(character);
    end
end

Events.OnCharacterDeath.Add(onCharacterDeath);