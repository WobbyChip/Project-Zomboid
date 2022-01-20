local esCommon = require("esq.common.01");
local esTooltips = {};

function esTooltips.getTextWidth(text)
    if (text == "<TAB>") then return 6 end;
    -- remove tags
    if (text.gsub) then
        text = text:gsub("<R>", "");
        text = text:gsub("<G>", "");
        text = text:gsub("<B>", "");
        text = text:gsub("<Y>", "");
        text = text:gsub("<O>", "");
        text = text:gsub("<A>", "");
        text = text:gsub("<I>", "");
        text = text:gsub("<BG>", "");
        text = text:gsub("<BD>", "");
    end
    return getTextManager():MeasureStringX(esTooltips.getFontSize(), tostring(text or ""));
end

function esTooltips.getFontSize()
    local ttFontSize = getCore():getOptionTooltipFont();
    if (ttFontSize == 'Small') then return UIFont.Small end
    if (ttFontSize == 'Medium') then return UIFont.Medium end
    return UIFont.Large;
end

function esTooltips.getLongestWidth(sampleTexts)
    local longLength = 0;
    for k,v in pairs(sampleTexts) do
        local textWidth = esTooltips.getTextWidth(v);
        if (textWidth > longLength) then
            longLength = textWidth;
        end
    end

    return longLength;
end

function esTooltips:setMinWidth(column)
    local minColumn = {};
    for k,v in pairs(self.data) do
        table.insert(minColumn, v[column]);
    end

    local minColumnWidth = self.config.rightMargin + self.config.leftMargin + esTooltips.getLongestWidth(minColumn);
    if (self.config.lastPosX < minColumnWidth) then
        self.config.lastPosX = minColumnWidth;
    end
end

function esTooltips:hideDefault()
    local mx = getMouseX() + 24;
    local my = getMouseY() + 24;
    if not self.tooltip.followMouse then
        mx = self.tooltip.anchorBottomLeft.x
        my = self.tooltip.anchorBottomLeft.y
    end
    local myCore = getCore();
    local maxX = myCore:getScreenWidth();
    local maxY = myCore:getScreenHeight();
    self.tooltip:setX(math.max(0, math.min(mx + 11, maxX - 1)));
    self.tooltip:setY(math.max(0, math.min(my, maxY - 1)));
    self.tooltip:setWidth(1);
    self.tooltip:setHeight(1);
end

function esTooltips.getColor(text)
    local colors = esCommon.volume.getColors();

    local color = colors.active;
    if (text:contains("<R>")) then
        color = colors.red;
        text = text:gsub("<R>","");

    elseif (text:contains("<G>")) then
        color = colors.green;
        text = text:gsub("<G>","");

    elseif (text:contains("<B>")) then
        color = colors.blue;
        text = text:gsub("<B>","");

    elseif (text:contains("<Y>")) then
        color = colors.yellow;
        text = text:gsub("<Y>","");

    elseif (text:contains("<O>")) then
        color = colors.orange;
        text = text:gsub("<O>","");

    elseif (text:contains("<A>")) then
        color = colors.active;
        text = text:gsub("<A>","");

    elseif (text:contains("<I>")) then
        color = colors.inActive;
        text = text:gsub("<I>","");

    elseif (text:contains("<BG>")) then
        color = colors.backgroundColor;
        text = text:gsub("<BG>","");

    elseif (text:contains("<BD>")) then
        color = colors.borderColor;
        text = text:gsub("<BD>","");
    end

    text = text:gsub("<TAB>","");
    return color, text;
end

function esTooltips:init()
    local columns = {};
    local columnsX = {};
    local pointerPos = self.config.leftMargin;

    for k, v in pairs(self.config.directions) do
        local x = {};
        for i,a in pairs(self.data) do
            if (esCommon.utils.sizeOf(a) > 1) then
                table.insert(x, a[k]);
            end
        end

        columns[k] = esTooltips.getLongestWidth(x);
    end

    for k,v in pairs(self.config.directions) do
        if (v == "R" or v == "r") then
            columnsX[k] = pointerPos;
            pointerPos = pointerPos + columns[k];
        else
            columnsX[k] = pointerPos + columns[k];
            pointerPos = columnsX[k];
        end
    end

    self.config.lastPosX = self.config.rightMargin + pointerPos;
    self.config.columns = columnsX;
end

function esTooltips:new(tooltip, data, options)
    local param = options or {};
    local o = {};
    setmetatable(o, self);
    self.__index = self;
    o.tooltip = tooltip;
    o.data = data;
    o.tooltipY = 0;

    o.config = {};
    o.config.tooltipCardinal = param.tooltipCardinal or "S";
    o.config.leftMargin = param.leftMargin or 15;
    o.config.rightMargin = param.rightMargin or 15;
    o.config.directions = param.directions or { "R" };
    o.config.lineHeight = getTextManager():getFontFromEnum(esTooltips.getFontSize()):getLineHeight();

    return o;
end

function esTooltips:drawToolTip()
    local itemHeight = self.config.lineHeight;
    local tooltipX = self.tooltip:getWidth();
    local newTooltipX = self.config.lastPosX;
    local BGColor, t = esTooltips.getColor("<BG>");
    local BDColor, t = esTooltips.getColor("<BD>");

    if (self.config.tooltipCardinal == "S") then
        if (tooltipX < newTooltipX) then
            tooltipX = newTooltipX + 15;
        end

        self.tooltipY = self.tooltip:getHeight();
        if (self.tooltip:getY() + self.tooltipY + (itemHeight * (#self.data + 1)) > getCore():getScreenHeight()) then
            self.tooltipY = 1 + self.tooltip:getHeight() - (self.tooltip:getHeight() + (itemHeight * (#self.data + 1)));
            self.tooltip:setHeight(self.tooltipY - (itemHeight * (#self.data + 1)));
        else
            self.tooltip:setHeight(self.tooltipY + (itemHeight * (#self.data + 1)));
        end

        self.tooltip:setX(self.tooltip:getX());
    else -- "East"
        self.tooltipY = 1;
        tooltipX = self.config.lastPosX;

        if (self.tooltip:getX() + tooltipX + self.tooltip:getWidth() > getCore():getScreenWidth()) then
            self.tooltip:setX(self.tooltip:getX() - tooltipX);
        else
            self.tooltip:setX(self.tooltip:getX() + self.tooltip:getWidth());
        end

        -- adjust Y
        if (self.tooltip:getY() + self.tooltipY + (itemHeight * (#self.data + 1)) > getCore():getScreenHeight()) then
            self.tooltipY = 1 + self.tooltip:getHeight() - (itemHeight * (#self.data + 1));
        end

    end

    self.tooltip:drawRect(0, self.tooltipY - 1, tooltipX, itemHeight * (#self.data + 1), BGColor.a, BGColor.r, BGColor.g, BGColor.b);
    self.tooltip:drawRectBorder(0, self.tooltipY - 1, tooltipX, itemHeight * (#self.data + 1), BDColor.a, BDColor.r, BDColor.g, BDColor.b);
end

function esTooltips:drawData()
    local lineY = self.tooltipY + (self.config.lineHeight / 3);

    for k, v in pairs(self.data) do
        for i, val in pairs(v) do
            local color, textPrint = esTooltips.getColor(tostring(val));
            if (self.config.directions[i] == 'R') then
                self.tooltip:drawText(textPrint, self.config.columns[i], lineY, color.r, color.g, color.b, color.a, esTooltips.getFontSize());
            else
                self.tooltip:drawTextRight(textPrint, self.config.columns[i], lineY, color.r, color.g, color.b, color.a, esTooltips.getFontSize());
            end
        end
        lineY = lineY + self.config.lineHeight;
    end
end

return esTooltips;