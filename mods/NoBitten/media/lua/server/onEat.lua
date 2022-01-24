function OnEat_Antibodies(food, player, percent)
	player:getBodyDamage():setInfected(false);
	player:getBodyDamage():setInfectionMortalityDuration(-1);
	player:getBodyDamage():setInfectionTime(-1);
	player:getBodyDamage():setInfectionLevel(0);
	local parts = player:getBodyDamage():getBodyParts();

    for i = 0, parts:size()-1 do
        if parts:get(i):bitten() then
            parts:get(i):setBiteTime(0);
            parts:get(i):SetBitten(false);
            parts:get(i):SetInfected(false);
            setHealth_Antibodies(player, 5);
            break;
        end
    end
end

function setHealth_Antibodies(player, health)
    local parts = player:getBodyDamage():getBodyParts();
    local health = 80+(20*health/100);

    for i = 0, parts:size()-1 do
        parts:get(i):SetHealth(health);
    end
end