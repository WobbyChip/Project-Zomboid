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

UIHandSecondaryExtension = ISUIElement:derive("UIHandSecondaryExtension")

function UIHandSecondaryExtension:initialise()
    ISUIElement.initialise(self)
end

function UIHandSecondaryExtension:render()
    local textWidth = getTextManager():MeasureStringX(UIFont.Small, self.text)
    local textHeight = getTextManager():MeasureStringY(UIFont.Small, self.text)

    self.width = self.textureLeft:getWidth() + self.textureRight:getWidth() + textWidth
    self.height = self.textureLeft:getHeight()

    local textX = self.textureLeft:getWidth() + self.marginLeft
    local textureMiddleWidth = (self.marginLeft + textWidth) - (self.textureRight:getWidth() - self.marginRight)
    local textureRightX = self.textureLeft:getWidth() + textureMiddleWidth

    self:drawTexture(self.textureLeft, 0, 0, 1, 1, 1, 1)
    self:drawRect(self.textureLeft:getWidth(), 0, textureMiddleWidth, self.height, self.bgColor.a, self.bgColor.r, self.bgColor.g, self.bgColor.b)
    self:drawTexture(self.textureRight, textureRightX, 0, 1, 1, 1, 1)

    self:drawText(self.text, textX, (self.height - textHeight) / 2, self.textColor.r, self.textColor.g, self.textColor.b, self.textColor.a, UIFont.Small)
    --self:drawTextCentre(self.text, self.width / 2, (self.height - textHeight) / 2, self.textColor.r, self.textColor.g, self.textColor.b, self.textColor.a, UIFont.Small)

    self:setWidth(self.width)
    self:setHeight(self.height)

    self:updateTooltip()
end

function UIHandSecondaryExtension:updateTooltip()
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
        self.tooltipUI:setDesiredPosition(getMouseX(), self:getAbsoluteY() + self:getHeight() + 8)
        self.tooltipUI:setDesiredPosition(self:getAbsoluteX() + 4 + 6 + 8, self:getAbsoluteY() + self:getHeight() + 8)
    else
        if self.tooltipUI and self.tooltipUI:getIsVisible() then
            self.tooltipUI:setVisible(false)
            self.tooltipUI:removeFromUIManager()
        end
    end
end

function UIHandSecondaryExtension:setText(text, tooltip)
    self.text = text
    self.tooltip = tooltip
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

function UIHandSecondaryExtension:new(x, y)
    local o = ISUIElement:new(x, y, 0, 0) -- width = 0, height = 0
    setmetatable(o, self)
    self.__index = self
    o.x = x
    o.y = y
    o.width = 0
    o.height = 0
    o.text = ""
    o.marginLeft = 5
    o.marginRight = 8
    o.textureLeft = getTexture("media/ui/HandSecondary_Extension_Left.png")
    o.textureRight = getTexture("media/ui/HandSecondary_Extension_Right.png")
    o.bgColor = { a = 1, r = 0.0985, g = 0.0985, b = 0.0985 } -- 25, 25, 25
    o.textColor = { a = 0.9, r = 0.8, g = 0.8, b = 0.8 }
    return o
end