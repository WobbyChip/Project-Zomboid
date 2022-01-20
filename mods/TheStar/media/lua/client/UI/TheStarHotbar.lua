---
--- Mod: Weapon Condition Indicator
--- Workshop: https://steamcommunity.com/sharedfiles/filedetails/?id=2619072426
--- Author: NoctisFalco
--- Profile: https://steamcommunity.com/id/NoctisFalco/
---
--- Redistribution of this mod without explicit permission from the original creator is prohibited
--- under any circumstances. This includes, but not limited to, uploading this mod to the Steam Workshop
--- or any other site, distribution as part of another mod or modpack, distribution of modified versions.
--- You are free to do whatever you want with the mod provided you do not upload any part of it anywhere.
---
--- The QualityStar_n.png icons are the property of The Indie Stone.
--- The mod overrides parts of the ISHotbar.lua, ISEquippedItem.lua files by The Indie Stone.
---

TheStar = TheStar or {}

local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small)

function ISHotbar:render()
    if (self.playerNum > 0) or JoypadState.players[self.playerNum + 1] then
        self:setVisible(false);
        -- Don't remove this from the screen, we need update() to call refresh().
        --		self:removeFromUIManager();
    end
    self:drawRectBorderStatic(0, 0, self.width, self.height, self.borderColor.a, self.borderColor.r, self.borderColor.g, self.borderColor.b);

    local mouseOverSlotIndex = self:getSlotIndexAt(self:getMouseX(), self:getMouseY())

    local draggedItem
    if ISMouseDrag.dragging and (mouseOverSlotIndex ~= -1) then
        local dragging = ISInventoryPane.getActualItems(ISMouseDrag.dragging)
        local slot = self.availableSlot[mouseOverSlotIndex]
        for _, item in ipairs(dragging) do
            if self:canBeAttached(slot, item) then
                draggedItem = item
                break
            end
        end
    end

    local slotX = self.margins;

    for i, slot in pairs(self.availableSlot) do
        self:drawRectBorderStatic(slotX, self.margins, self.slotWidth, self.slotHeight, self.borderColor.a, self.borderColor.r, self.borderColor.g, self.borderColor.b);
        local item = self.attachedItems[i]
        if i == mouseOverSlotIndex then
            local r, g, b = 1, 1, 1
            if draggedItem then
                item = draggedItem
            elseif ISMouseDrag.dragging then
                r, g, b = 1, 0, 0
            end
            self:drawRect(slotX, self.margins, self.slotWidth, self.slotHeight, 0.2, r, g, b, 1);
            local slotName = getTextOrNull("IGUI_HotbarAttachment_" .. slot.slotType) or slot.name;
            local textWid = getTextManager():MeasureStringX(UIFont.Small, slotName)
            self:drawText(slotName, slotX + (self.slotWidth - textWid) / 2, 0 - FONT_HGT_SMALL, self.textColor.r, self.textColor.g, self.textColor.b, 1, self.font);
        elseif item == draggedItem then
            item = nil
        end
        if item then
            local isHandWeapon = TheStar.Utils.isHandWeapon(item)
            local isWaterSource = TheStar.Utils.isWaterSource(item)

            local showCondition = true
            -- Show condition of equipped item only
            if TheStar.Options.equippedItemOnly then
                showCondition = item:isEquipped()
            end

            local conditionLevel
            if isHandWeapon or isWaterSource then
                conditionLevel = item:getCondition() / item:getConditionMax()
                if isWaterSource then
                    conditionLevel = item:getUsedDelta()
                end
                if conditionLevel < 0.0 then conditionLevel = 0.0 end
                if conditionLevel > 1.0 then conditionLevel = 1.0 end
            end

            -- Blinker
            if isHandWeapon and TheStar.Options.blinkOnConditionDrop and showCondition then
                if not self.modAttachedItems then self.modAttachedItems = {} end

                local hasConditionDropped = false
                if self.modAttachedItems[item] then
                    hasConditionDropped = conditionLevel < self.modAttachedItems[item]
                end
                self.modAttachedItems[item] = conditionLevel

                if (hasConditionDropped and conditionLevel <= TheStar.Options.blinkCondition)
                        or self.blinkingItem == item then
                    -- Init counter/reset counter every time condition drops
                    if not self.blinkCount or hasConditionDropped then self.blinkCount = 0 end

                    -- Blink
                    if self.blinkCount <= TheStar.Config.BLINK_COUNT_MAX then
                        self.blinkingItem = item

                        if not self.blinkAlpha then self.blinkAlpha = TheStar.Config.BLINK_ALPHA_MAX end

                        if TheStar.Options.showProgressBar then
                            -- Blink the part above the condition
                            -- slotWidth == slotHeight
                            local progressReversed = (self.slotWidth - 2) - math.floor((self.slotWidth - 2) * conditionLevel)
                            if conditionLevel > 0 then progressReversed = math.max(progressReversed, 1) end

                            local blinkColor = TheStar.Utils.getProgressColor(conditionLevel)
                            if TheStar.Options.progressVertical then
                                self:drawRect(slotX + 1, self.margins + 1, self.slotWidth - 2, progressReversed,
                                        self.blinkAlpha, blinkColor.r, blinkColor.g, blinkColor.b)
                            else
                                self:drawRect(slotX + 1 + (self.slotWidth - 2) - progressReversed, self.margins + 1, progressReversed, self.slotHeight - 2,
                                        self.blinkAlpha, blinkColor.r, blinkColor.g, blinkColor.b)
                            end
                        else
                            -- Blink the whole bg
                            self:drawRect(slotX + 1, self.margins + 1, self.slotWidth - 2, self.slotHeight - 2, self.blinkAlpha, 1, 0, 0)
                        end

                        if not self.blinkAlphaIncrease then
                            self.blinkAlpha = self.blinkAlpha - 0.05 * (UIManager.getMillisSinceLastRender() / 22.2)
                            if self.blinkAlpha < 0 then
                                self.blinkAlpha = 0

                                -- Don't increase blinkAlpha on the last blink
                                if self.blinkCount < TheStar.Config.BLINK_COUNT_MAX then
                                    self.blinkAlphaIncrease = true
                                end

                                -- Last blink
                                if self.blinkCount == TheStar.Config.BLINK_COUNT_MAX then
                                    self.blinkingItem = nil
                                end
                            end
                        else
                            self.blinkAlpha = self.blinkAlpha + 0.05 * (UIManager.getMillisSinceLastRender() / 22.2)
                            if self.blinkAlpha > TheStar.Config.BLINK_ALPHA_MAX then
                                self.blinkAlpha = TheStar.Config.BLINK_ALPHA_MAX
                                self.blinkAlphaIncrease = false
                                -- Increase counter
                                self.blinkCount = self.blinkCount + 1
                            end
                        end
                    end
                end
            end

            -- Progress Bar
            if (isHandWeapon or isWaterSource) and TheStar.Options.showProgressBar and showCondition then
                -- slotWidth == slotHeight
                local progress = math.floor((self.slotWidth - 2) * conditionLevel)
                if conditionLevel > 0 then progress = math.max(progress, 1) end

                local progressColor = TheStar.Utils.getProgressColor(conditionLevel, isWaterSource)

                if TheStar.Options.progressVertical then
                    self:drawRect(slotX + 1, self.margins + 1 + self.slotHeight - 2 - progress, self.slotWidth - 2, progress,
                            TheStar.Options.progressBarOpacity, progressColor.r, progressColor.g, progressColor.b)
                else
                    self:drawRect(slotX + 1, self.margins + 1, progress, self.slotHeight - 2,
                            TheStar.Options.progressBarOpacity, progressColor.r, progressColor.g, progressColor.b)
                end
            end

            -- Item Texture
            local tex = item:getTexture()
            self:drawTexture(tex, slotX + (tex:getWidth() / 2), (self.height - tex:getHeight()) / 2, 1, 1, 1, 1)

            -- Weapon Ammo
            if TheStar.Options.showAmmoCountHotbar and isHandWeapon and TheStar.Utils.isRangedWeapon(item) and showCondition then
                local format = item:haveChamber() and "IGUI_TheStar_AmmoCountChambered" or "IGUI_TheStar_AmmoCount"
                local chamber = item:haveChamber() and (item:isRoundChambered() and "1" or "0")
                local textCount = getText(format, item:getCurrentAmmoCount(), item:getMaxAmmo(), chamber)
                self:drawText(textCount, slotX + 3, self.slotHeight + 2 - FONT_HGT_SMALL,
                        self.textColor.r, self.textColor.g, self.textColor.b, self.textColor.a, self.font)
            end

            -- Battery Charge
            if TheStar.Options.showBatteryChargeHotbar and TheStar.Utils.isLightSource(item) and showCondition then
                local battery = TheStar.Utils.round(item:getUsedDelta() * 100) .. "%"
                self:drawText(battery, slotX + 3, self.slotHeight + 2 - FONT_HGT_SMALL,
                        self.textColor.r, self.textColor.g, self.textColor.b, self.textColor.a, self.font)
            end

            -- Icon
            if isHandWeapon and TheStar.Options.showIcon and showCondition then
                local n = math.ceil(conditionLevel * 5)
                local iconTexture = TheStar.Utils.getIconTexture(n)

                self:drawTexture(iconTexture, slotX + self.slotWidth - 3 - iconTexture:getWidth(), self.margins + 3, 1, 1, 1, 1)
            end

            if item:isEquipped() then
                tex = self.equippedItemIcon
                self:drawTexture(tex, slotX + self.slotWidth - tex:getWidth() - 5, self.height - self.margins - tex:getHeight() - 5, 1, 1, 1, 1);
            end
        elseif slot.texture then
            self:drawTexture(slot.texture, slotX + slot.texture:getWidth() / 2, (self.height - slot.texture:getHeight()) / 2, 0.25, 1.0, 1.0, 1.0)
        end

        -- slot index text
        self:drawText(tostring(i), slotX + 3, self.margins + 1, self.textColor.r, self.textColor.g, self.textColor.b, self.textColor.a, self.font);

        slotX = slotX + self.slotWidth + self.slotPad;
    end

    self:updateTooltip()
end

function ISHotbar:updateTooltip()
    if not TheStar.Options.showItemTooltipHotbar then
        if self.tooltipRender then
            self.tooltipRender:removeFromUIManager()
            self.tooltipRender:setVisible(false)
            self.tooltipRender = nil
        end
        return
    end

    if not self:isReallyVisible() then
        return
    end

    local item
    local index = self:getSlotIndexAt(self:getMouseX(), self:getMouseY())
    if index ~= -1 then
        item = self.attachedItems[index]
    end

    if getPlayerContextMenu(self.playerNum) and getPlayerContextMenu(self.playerNum):isAnyVisible()
            or (self.character:getVehicle() and self.character:getVehicle():isDriver(self.character)) then
        item = nil
    end

    if item and self.tooltipRender and item == self.tooltipRender.item and self.tooltipRender:isVisible() then
        return
    end
    if item then
        if self.tooltipRender then
            self.tooltipRender:setItem(item)
            self.tooltipRender:setVisible(true)
            self.tooltipRender:addToUIManager()
            self.tooltipRender:bringToTop()
        else
            self.tooltipRender = ISToolTipInv:new(item)
            self.tooltipRender.backgroundColor.a = 0.7
            self.tooltipRender.followMouse = false
            self.tooltipRender:initialise()
            self.tooltipRender:addToUIManager()
            self.tooltipRender:setVisible(true)
            self.tooltipRender:setOwner(self)
            self.tooltipRender:setCharacter(self.character)
        end

        local x = self:getAbsoluteX() + self.margins + (self.slotWidth + self.slotPad) * (index - 1)
        local y = self:getAbsoluteY() - FONT_HGT_SMALL
        self.tooltipRender.anchorBottomLeft = { x = x, y = y }
    elseif self.tooltipRender and self.tooltipRender:isVisible() then
        self.tooltipRender:removeFromUIManager()
        self.tooltipRender:setVisible(false)
    end
end

if not TheStar.overrideRemoveItem then
    TheStar.overrideRemoveItem = ISHotbar.removeItem
end
function ISHotbar:removeItem(item, doAnim)
    self.blinkingItem = nil
    TheStar.overrideRemoveItem(self, item, doAnim)
end

if not TheStar.overrideAttachItem then
    TheStar.overrideAttachItem = ISHotbar.attachItem
end
function ISHotbar:attachItem(item, slot, slotIndex, slotDef, doAnim)
    if self.modAttachedItems and self.modAttachedItems[item] then
        self.modAttachedItems[item] = nil
    end
    TheStar.overrideAttachItem(self, item, slot, slotIndex, slotDef, doAnim)
end