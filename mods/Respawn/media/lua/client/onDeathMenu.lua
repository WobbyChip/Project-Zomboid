o = {};

local FONT_HGT_LARGE = getTextManager():getFontHeight(UIFont.Large);

function ISPostDeathUI_render(ui)
	ISPanelJoypad.render(ui)
	if ui.quitToDesktopDialog and ui.quitToDesktopDialog:isReallyVisible() then
		return
	end
	if ui.waitOver and ui.textY > -50 then
		local y = ui.textY
		local fontHgt = getTextManager():getFontFromEnum(UIFont.Large):getLineHeight()
		for _,line in ipairs(ui.lines) do
			local bgWidth = getTextManager():MeasureStringX(UIFont.Large, line) + 8 * 2
			ui:drawRect(ui.screenX + (ui.screenWidth - bgWidth) / 2 - ui:getAbsoluteX(), y - ui:getAbsoluteY(),
				bgWidth, FONT_HGT_LARGE, 0.5, 0.0, 0.0, 0.0)
			getTextManager():DrawStringCentre(UIFont.Large, ui.screenX + ui.screenWidth / 2, y, line, 1, 1, 1, 1)
			y = y + fontHgt + 2
		end
		if getTimestamp() > ui.timeOfDeath + 6 then
			ui.textY = ui.textY - 0.5 * (UIManager.getMillisSinceLastRender() / 33.3)
		end
	end
end

function inject_render()
    if ISPostDeathUI.instance[o.playerNum] then
        ISPostDeathUI_render(ISPostDeathUI.instance[o.playerNum]);
    else return end

    local seconds = math.floor(os.difftime(o.cooldown, os.time()));
    
    if seconds > 0 then
        if ISPostDeathUI.instance[o.playerNum] then
            ISPostDeathUI.instance[o.playerNum].buttonQuit:setTitle("RESPAWN ("..seconds..")");
        end
    else
        if ISPostDeathUI.instance[o.playerNum] then
            ISPostDeathUI.instance[o.playerNum].buttonQuit:setTitle("RESPAWN");
            ISPostDeathUI.instance[o.playerNum].buttonQuit:setOnClick(onRespawn);
            ISPostDeathUI.instance[o.playerNum].render = ISPostDeathUI.instance[o.playerNum].saved_render;
        end
    end
end

function onPlayerDeath(player)
    o.playerNum = player:getPlayerNum();
    o.cooldown = os.time() + SandboxVars.respawnCooldown + 4;

    if ISPostDeathUI.instance[o.playerNum] then
        ISPostDeathUI.instance[o.playerNum].buttonQuit:setOnClick(nil);
        ISPostDeathUI.instance[o.playerNum].saved_render = ISPostDeathUI.instance[o.playerNum].render;
        ISPostDeathUI.instance[o.playerNum].render = inject_render;
    end
end

function onRespawn(target)
    if MainScreen.instance:isReallyVisible() then return end
    target:setVisible(false);

    setPlayerMouse(nil); --This spawns new player
    loadPlayer(getPlayer());

    --Fix this, for some reason not updating and working
    local health =  getPlayer():getBodyDamage():getOverallBodyHealth();
    health = health*((100-SandboxVars.healthPenaltyPercentage)/100);
    getPlayer():getBodyDamage():setOverallBodyHealth(health);

    if ISPostDeathUI.instance[o.playerNum] then
        ISPostDeathUI.instance[o.playerNum]:removeFromUIManager();
        ISPostDeathUI.instance[o.playerNum] = nil;
    end
end

Events.OnPlayerDeath.Add(onPlayerDeath);