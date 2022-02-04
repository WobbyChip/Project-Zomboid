function OnEat_Antibodies(food, player, percent)
	player:getBodyDamage():setInfected(false);
	player:getBodyDamage():setInfectionMortalityDuration(-1);
	player:getBodyDamage():setInfectionTime(-1);
	player:getBodyDamage():setInfectionLevel(0);
	local parts = player:getBodyDamage():getBodyParts();

    for i = 0, parts:size()-1 do
        if parts:get(i):bitten() or (parts:get(i):getBiteTime() > 0) then
            parts:get(i):setBiteTime(0);
            parts:get(i):SetBitten(false);
            parts:get(i):SetInfected(false);
            local health = getHealth_Antibodies(player)-90;
            setHealth_Antibodies(player, health);
            break;
        end
    end
end

function getHealth_Antibodies(player)
    return player:getBodyDamage():getOverallBodyHealth();
end

function setHealth_Antibodies(player, health)
    local parts = player:getBodyDamage():getBodyParts();
    local health = 80+(20*health/100);

    for i = 0, parts:size()-1 do
        parts:get(i):SetHealth(health);
    end
end