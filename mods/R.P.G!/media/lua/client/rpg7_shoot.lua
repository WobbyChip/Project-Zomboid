local rpg7_Radius = 7;
local rpg7_FireChance = 0;
local rpg7_TickPerAnimation = 2;
local rpg7_AnimationMaxFrames = 47;
local rpg7_AnimationDealy = -120;

-- debug stuff, disable in actual mod.
function RPG7_DebugCheck(keynum)
	if keynum == Keyboard.KEY_O then
		local pSquare = getPlayer():getCurrentSquare();
		getPlayer():Say("Debug");
		RPG7_StartRPG7FX(getPlayer():getCurrentSquare());
	end
end

-- Call start the FX
function RPG7_StartRPG7FX(sSquare)
	if getPlayer() == nil then
		return
	end
	local spawnSquare = getCell():getGridSquare(sSquare:getX()+5, sSquare:getY()+6, sSquare:getZ());
	if spawnSquare then
		getPlayer():getModData().rpg7FXSquare = spawnSquare;
		getPlayer():getModData().rpg7FXtick = rpg7_AnimationDealy;
		getPlayer():getModData().rpg7FXSwitch = true;
	else
		print("Spawn FX failed, square not valid");
	end
end

-- Check if there is FX need to be updated.
function RPG7_CheckUpdateFX()
	if getPlayer() == nil then
		return
	end
	if getPlayer():getModData().rpg7FXSwitch then
		if getPlayer():getModData().rpg7FXtick >= rpg7_AnimationMaxFrames * rpg7_TickPerAnimation then
			getPlayer():getModData().rpg7FXSquare:removeAllWorldObjects();
			getPlayer():getModData().rpg7FXtick = rpg7_AnimationDealy;
			getPlayer():getModData().rpg7FXSwitch = false;
		else
			if getPlayer():getModData().rpg7FXtick >= 0 then
				if getPlayer():getModData().rpg7FXtick % rpg7_TickPerAnimation == 0 then
					if getPlayer():getModData().rpg7FXSquare == nil then
						return
					end
					local aFrame = getPlayer():getModData().rpg7FXtick / rpg7_TickPerAnimation;
					local FXSquare = getPlayer():getModData().rpg7FXSquare;
					local SSquare = getPlayer():getModData().rpg7FXCenterSquare;
					RPG7_ReplaceFXItem(aFrame, getPlayer(), FXSquare , SSquare);
				end
			end
			getPlayer():getModData().rpg7FXtick = getPlayer():getModData().rpg7FXtick + 1;
		end
	end
end


-- Update the FX item. should only get called by server or in SP.
function RPG7_ReplaceFXItem(frame, fUser, FXSquare, soundSquare)
	local fSquare = FXSquare;
	if fSquare == nil then
		print("FX Square doesn't exist");
		return
	end
	fSquare:removeAllWorldObjects();
	local fItem = ScriptManager.instance:getItem("Base.modFX_RPG7");
  if fItem then
  	local fItemIconName = "explosionSFX"..tostring(frame);
  	fItem:DoParam("Icon = "..fItemIconName);
  else
  	print("Can't find the FX item in the script");
  end
	fSquare:AddWorldInventoryItem("Base.modFX_RPG7", 0.5, 0.5, 0);
	if frame == 0 then
		local sound = getSoundManager(): PlayWorldSound("modRPG7ExplosionSound", soundSquare, 0, 4, 1.0, false);
		sound:setVolume(0.7);
	end
	if frame == 3 then
		local eRange, rRange = RPG7_CalculateExplosionTiles(soundSquare);
		RPG7_StartFires(eRange, rRange);
		RPG7_KillAll(fUser, eRange);
	end
end

-- RPG7 hit a character
function RPG7_OnRPG7Hit(attacker, receiver, handWeapon, damage)
	if attacker == nil then
		return
	end
	if handWeapon:getWeaponSprite() == "modRPG7" then
		if receiver ~= nil then
			getPlayer():getModData().rpg7FXCenterSquare = receiver:getCurrentSquare();
			RPG7_StartRPG7FX(receiver:getCurrentSquare());
		end
	end
end

-- Calculate RPG7 effect tiles.
function RPG7_CalculateExplosionTiles(targetSquare)
	local effectSquares = {};
	local rangeSquares = {};
	local targetX = targetSquare:getX();
	local targetY = targetSquare:getY();
	local targetZ = targetSquare:getZ();
	for x = targetX - rpg7_Radius , targetX + rpg7_Radius do
		for y = targetY - rpg7_Radius , targetY + rpg7_Radius do
			local currentSquare = getCell():getOrCreateGridSquare(x, y, targetZ);
			if currentSquare ~= nil then
				local fRadius =  math.pow(rpg7_Radius, 2) * 1.0;
				local fRange = math.pow(x-targetX, 2) + math.pow(y-targetY, 2);
				if fRange < fRadius then
					table.insert (effectSquares, currentSquare);
					table.insert (rangeSquares, (fRadius - fRange) / fRadius );
				end
			end
		end
	end
	return effectSquares, rangeSquares
end

-- RPG7 kills all in the tiles.
function RPG7_KillAll(RPGUser, tableSquares)
	if tableSquares == nil then
		return
	end
	for k,v in ipairs(tableSquares) do
		if v ~= nil then
			for i=v:getMovingObjects():size(),1,-1 do
					local kTarget = v:getMovingObjects():get(i-1);
					if instanceof(kTarget, "IsoZombie") then
						kTarget:Kill(RPGUser);
					end
			end
			for i=v:getMovingObjects():size(),1,-1 do
					local kTarget = v:getMovingObjects():get(i-1);
					if instanceof(kTarget, "IsoPlayer") then
						kTarget:Kill(RPGUser);
					end
			end
		end
	end
end

-- RPG7 starts fire in the tiles.
function RPG7_StartFires(eTableSquares, rTableSquares)
	if eTableSquares == nil then
		return
	end
	for k,v in ipairs(eTableSquares) do
		if v ~= nil then
			v:Burn(true);
			if ZombRand(100) < rpg7_FireChance + 100 * rTableSquares[k] then
				v:smoke();
				v:StartFire();
			end
		end
	end
end

-- make sure all ModData variables are declared.
function RPG7_Setup()
		if getPlayer() == nil then
				return
		end
		if getPlayer():getModData().rpg7FXtick == nil then
				getPlayer():getModData().rpg7FXtick = rpg7_AnimationDealy;
		end
		if getPlayer():getModData().rpg7FXSwitch == nil then
				getPlayer():getModData().rpg7FXSwitch = false;
		end
		if getPlayer():getModData().rpg7FXSquare == nil then
				getPlayer():getModData().rpg7FXSquare = getPlayer():getCurrentSquare();
		end
		if getPlayer():getModData().rpg7FXCenterSquare == nil then
				getPlayer():getModData().rpg7FXCenterSquare = getPlayer():getCurrentSquare();
		end
end
	
Events.OnGameStart.Add(RPG7_Setup);
Events.OnWeaponHitCharacter.Add(RPG7_OnRPG7Hit);
Events.OnTick.Add(RPG7_CheckUpdateFX);
--Events.OnKeyPressed.Add(RPG7_DebugCheck);