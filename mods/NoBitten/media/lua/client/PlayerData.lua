function removeBitten(player, counter)
    local parts = player:getBodyDamage():getBodyParts();

    for i = 0, parts:size()-1 do
        if parts:get(i):bitten() or (parts:get(i):getBiteTime() > 0) then
            parts:get(i):setBiteTime(0);
            parts:get(i):SetBitten(false);
            parts:get(i):SetInfected(false);
            removeHealth(player, SandboxVars.bittenHealthPenalty);
            removeBitten(player, counter+1);
            return;
        end
    end

    if counter > 0 then
        player:getBodyDamage():setInfected(false);
        player:getBodyDamage():setInfectionMortalityDuration(-1);
        player:getBodyDamage():setInfectionTime(-1);
        player:getBodyDamage():setInfectionLevel(0);
    end
end

function removeHealth(player, health)
    local health = getHealth(player)-health;
    setHealth(player, health);
end

function getHealth(player)
    return player:getBodyDamage():getOverallBodyHealth();
end

function setHealth(player, health)
    local parts = player:getBodyDamage():getBodyParts();
    local health = 80+(20*health/100);

    for i = 0, parts:size()-1 do
        parts:get(i):SetHealth(health);
    end
end