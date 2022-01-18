function onCharacterDeath(chacrter)
    if SandboxVars.keepInventory and instanceof(chacrter, "IsoPlayer") then
        saveEquipItems(chacrter);
    end
end

Events.OnCharacterDeath.Add(onCharacterDeath);