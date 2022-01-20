---
--- Mod: Weapon Condition Indicator
--- Workshop: https://steamcommunity.com/sharedfiles/filedetails/?id=2619072426
--- Author: NoctisFalco
--- Profile: https://steamcommunity.com/id/NoctisFalco/
---
--- Redistribution of this mod without explicit permission from the original creator is prohibited
--- under any circumstances. This includes, but not limited to, uploading this mod to the Steam Workshop
--- or any other site, distribution as part of another mod or modpack, distribution of modified versions.
--- You are free to add the mod to your Steam Workshop Collection or to do whatever you want with the mod
--- provided you do not upload any part of it anywhere.
---
--- The QualityStar_n.png icons are the property of The Indie Stone.
--- The mod contains parts of the ISHotbar.lua, ISEquippedItem.lua files by The Indie Stone.
---

require('ISUI/ISUIElement')

UIHandMainExtension = ISUIElement:derive("UIHandMainExtension")

function UIHandMainExtension:initialise()
    ISUIElement.initialise(self)
end

function UIHandMainExtension:render()
    local textWidth = getTextManager():MeasureStringX(UIFont.Small, self.text)
    local textHeight = getTextManager():MeasureStringY(UIFont.Small, self.text)

    self.width = self.textureLeft:getWidth() + self.textureRight:getWidth() + textWidth
    self.height = self.textureLeft:getHeight()

    local textX = self.textureLeft:getWidth() + self.marginLeft
    local textureMiddleWidth = (self.marginLeft + textWidth) - (self.textureRight:getWidth() - self.marginRight)
    local textureRightX = self.textureLeft:getWidth() + textureMiddleWidth

    if self.changeBackground then
        self:drawTexture(self.textureLeftOverlay, 0, 0, 1, self.bgColorReload.r, self.bgColorReload.g, self.bgColorReload.b)
        self:drawRect(self.textureLeft:getWidth(), 0, textureMiddleWidth, self.height, 1, self.bgColorReload.r, self.bgColorReload.g, self.bgColorReload.b)
        self:drawTexture(self.textureRightOverlay, textureRightX, 0, 1, self.bgColorReload.r, self.bgColorReload.g, self.bgColorReload.b)
    else
        self:drawTexture(self.textureLeft, 0, 0, 1, 1, 1, 1)
        self:drawRect(self.textureLeft:getWidth(), 0, textureMiddleWidth, self.height, self.bgColor.a, self.bgColor.r, self.bgColor.g, self.bgColor.b)
        self:drawTexture(self.textureRight, textureRightX, 0, 1, 1, 1, 1)
    end

    self:drawText(self.text, textX, (self.height - textHeight) / 2, self.textColor.r, self.textColor.g, self.textColor.b, self.textColor.a, UIFont.Small)
    --self:drawTextCentre(self.text, self.width / 2, (self.height - textHeight) / 2, self.textColor.r, self.textColor.g, self.textColor.b, self.textColor.a, UIFont.Small)

    -- blinker
    if self.blink then
        if not self.blinkAlpha then self.blinkAlpha = self.blinkAlphaMax end

        self:drawTexture(self.textureLeftOverlay, 0, 0, self.blinkAlpha, self.blinkColor.r, self.blinkColor.g, self.blinkColor.b)
        self:drawRect(self.textureLeft:getWidth(), 0, textureMiddleWidth, self.height, self.blinkAlpha, self.blinkColor.r, self.blinkColor.g, self.blinkColor.b)
        self:drawTexture(self.textureRightOverlay, textureRightX, 0, self.blinkAlpha, self.blinkColor.r, self.blinkColor.g, self.blinkColor.b)
        if not self.blinkAlphaIncrease then
            self.blinkAlpha = self.blinkAlpha - 0.05 * (UIManager.getMillisSinceLastRender() / 55.5)
            if self.blinkAlpha < self.blinkAlphaMin then
                self.blinkAlpha = self.blinkAlphaMin
                self.blinkAlphaIncrease = true
            end
        else
            self.blinkAlpha = self.blinkAlpha + 0.05 * (UIManager.getMillisSinceLastRender() / 55.5)
            if self.blinkAlpha > self.blinkAlphaMax then
                self.blinkAlpha = self.blinkAlphaMax
                self.blinkAlphaIncrease = false
            end
        end
    end

    self:setWidth(self.width)
    self:setHeight(self.height)

    self:updateTooltip()
end

function UIHandMainExtension:updateTooltip()
    if self:isMouseOver() and self.tooltip then
        local text = self.tooltip
        if not self.tooltipUI then
            self.tooltipUI = ISToolTip:new()
            self.tooltipUI.followMouse = false
            self.tooltipUI:setOwner(self)
            self.tooltipUI:setVisible(false)
            self.tooltipUI:setAlwaysOnTop(true)
        end
        if not self.tooltipUI:getIsVisible() then
            self.tooltipUI:addToUIManager()
            self.tooltipUI:setVisible(true)
        end
        self.tooltipUI.description = text
        --self.tooltipUI:setDesiredPosition(getMouseX(), self:getAbsoluteY() + self:getHeight() + 8)
        self.tooltipUI:setDesiredPosition(self:getAbsoluteX() + 4 + 8, self:getAbsoluteY() + self:getHeight() + 8)
    else
        if self.tooltipUI and self.tooltipUI:getIsVisible() then
            self.tooltipUI:setVisible(false)
            self.tooltipUI:removeFromUIManager()
        end
    end
end

function UIHandMainExtension:setText(text, tooltip)
    self.text = text
    self.tooltip = tooltip
end

function UIHandMainExtension:setBlink(blink)
    self.blink = blink
end

function UIHandMainExtension:setChangeBackground(changeBackground)
    self.changeBackground = changeBackground
end

function UIHandMainExtension:isMouseOver()
    local mouseX = getMouseX()
    local x = self:getAbsoluteX() + self.textureLeft:getWidth()
    if mouseX >= x and mouseX <= x + self.width then
        local mouseY = getMouseY()
        local y = self:getAbsoluteY()
        if mouseY >= y and mouseY <= y + self.height then
            return true
        end
    end
    return false
end

function UIHandMainExtension:new(x, y)
    local o = ISUIElement:new(x, y, 0, 0) -- width = 0, height = 0
    setmetatable(o, self)
    self.__index = self
    o.x = x
    o.y = y
    o.width = 0
    o.height = 0
    o.text = ""
    o.tooltip = nil
    o.marginLeft = 5
    o.marginRight = 8
    o.textureLeft = getTexture("media/ui/HandMain_Extension_Left.png")
    o.textureRight = getTexture("media/ui/HandMain_Extension_Right.png")
    o.textureLeftOverlay = getTexture("media/ui/HandMain_Extension_Overlay_Left.png")
    o.textureRightOverlay = getTexture("media/ui/HandMain_Extension_Overlay_Right.png")
    o.blinkAlphaMin = 0.1
    o.blinkAlphaMax = 0.5
    o.blinkColor = TheStar.Config.COLOR_RED
    o.bgColor = { a = 1, r = 0.0985, g = 0.0985, b = 0.0985 } -- 25, 25, 25
    o.bgColorReload = { a = 1, r = 0.5, g = 0, b = 0 }
    o.textColor = { a = 0.9, r = 0.8, g = 0.8, b = 0.8 }
    return o
end