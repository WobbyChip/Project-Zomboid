local esPerks = require("esq.perks.01");
local esCommon = require("esq.common.01");
local esInfoOptions = require("es.info.options");

local baseInvTooltipRender = ISToolTipInv.render;
function ISToolTipInv:render()
    baseInvTooltipRender(self);

    if (esInfoOptions.getOption("booksOn") and (self.item:getCategory() == "Literature" or
            self.item:getScriptItem():getRecordedMediaCat())) then
        local fontSize = UIFont.Large;
        local ttFontSize = getCore():getOptionTooltipFont();
        if (ttFontSize == 'Small') then fontSize = UIFont.Small end
        if (ttFontSize == 'Medium') then fontSize = UIFont.Medium end
        local lineHeight = getTextManager():getFontFromEnum(fontSize):getLineHeight();
        local lineY = self.tooltip:getHeight();
        local statusColor = esCommon.volume.getColors().active;

        local character = esCommon.player.getPlayerObject(self.tooltip:getCharacter());
        local mediaCat = getZomboidRadio():getRecordedMedia();
        local isMedia = mediaCat:getCategories():contains(self.item:getScriptItem():getRecordedMediaCat());

        local perksSkill, perkSkillText, skillBook, bookStart, bookEnd;
        if (self.item.getSkillTrained and self.item:getSkillTrained()) then
            perksSkill = esPerks.getPerksData(character, self.item:getSkillTrained());
            if (perksSkill) then
                perkSkillText = perksSkill.type .. " " .. getText("IGUI_PlayerStats_Level") .. ": " .. perksSkill.level .. " (" .. perksSkill.percent .. "%)";
                skillBook = SkillBook[self.item:getSkillTrained()]
                bookStart = self.item:getMaxLevelTrained() - self.item:getNumLevelsTrained();
                bookEnd = self.item:getMaxLevelTrained();
            end
        end

        local inventory = getText("IGUI_InventoryTooltip") .. ": ";
        local itemsFound = character:getInventory():getAllTypeRecurse(self.item:getFullType());
        local itemIndex = ArrayList.new()
        for i = 0, itemsFound:size() - 1 do
            itemIndex:add(itemsFound:get(i):getDisplayName());
        end

        if (itemsFound:size() > 0) then
            if (isMedia) then
                if itemIndex:contains(self.item:getDisplayName()) then
                    inventory = inventory .. getText("UI_Yes");
                else
                    inventory = inventory .. getText("UI_No");
                end
            else
                inventory = inventory .. getText("UI_Yes");
            end
        else
            inventory = inventory .. getText("UI_No");
        end

        if skillBook then
            local playerSkill = perksSkill.level;
            self.item:setTooltip("\n\n");
            lineY = lineY - (lineHeight * 3);
            local readPercent = (character:getAlreadyReadPages(self.item:getFullType()) / self.item:getNumberOfPages()) * 100;
            if (readPercent == 100) then
                statusColor = esCommon.volume.getColors().inActive;
            elseif (playerSkill >= bookEnd) then
                statusColor = esCommon.volume.getColors().inActive;

            elseif (bookStart <= playerSkill and playerSkill < bookEnd) then
                statusColor = esCommon.volume.getColors().green;

            elseif (playerSkill < bookStart) then
                statusColor = esCommon.volume.getColors().orange;
            end

            self:drawText(inventory, 15, lineY, statusColor.r, statusColor.g, statusColor.b, statusColor.a, fontSize);
            self:drawText(perkSkillText, 15, lineY + lineHeight, statusColor.r, statusColor.g, statusColor.b, statusColor.a, fontSize);

        elseif (self.item.getTeachedRecipes and self.item:getTeachedRecipes()) then
            self.item:setTooltip("\n");
            lineY = lineY - (lineHeight * 2);
            if character:getAlreadyReadBook():contains(self.item:getFullType()) then
                statusColor = esCommon.volume.getColors().inActive;
            elseif self.item:getTeachedRecipes() and character:getKnownRecipes():containsAll(self.item:getTeachedRecipes()) then
                statusColor = esCommon.volume.getColors().inActive;
            elseif self.item:getTeachedRecipes() then
                statusColor = esCommon.volume.getColors().green;
            end

            self:drawText(inventory, 15, lineY, statusColor.r, statusColor.g, statusColor.b, statusColor.a, fontSize);

        elseif (isMedia) then
            self.item:setTooltip("\n");
            lineY = lineY - (lineHeight * 2);
            statusColor = esCommon.volume.getColors().green;
            if self.item:getMediaData() ~= nil and mediaCat:hasListenedToAll(self.character, self.item:getMediaData()) then
                statusColor = esCommon.volume.getColors().inActive;
            end

            self:drawText(inventory, 15, lineY, statusColor.r, statusColor.g, statusColor.b, statusColor.a, fontSize);
        else
            self.item:setTooltip("\n");
            lineY = lineY - (lineHeight * 2);
            local itemCount = getText("IGUI_InventoryTooltip") .. ": (" .. itemsFound:size() .. ")";
            self:drawText(itemCount, 15, lineY, statusColor.r, statusColor.g, statusColor.b, statusColor.a, fontSize);
        end

    end

end