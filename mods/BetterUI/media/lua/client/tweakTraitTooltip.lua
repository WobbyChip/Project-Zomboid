render_old = ISCharacterScreen.render;
loadTraits_old = ISCharacterScreen.loadTraits;


ISCharacterScreen.loadTraits = function(self)
    for _,image in ipairs(self.traits) do
		self:removeChild(image)
	end
	table.wipe(self.traits);
	self:setDisplayedTraits()
	for _,trait in ipairs(self.displayedTraits) do
		local textImage = ISImage:new(0, 0, trait:getTexture():getWidthOrig(), trait:getTexture():getHeightOrig(), trait:getTexture());
		textImage:initialise();
		textImage:setMouseOverText(trait:getLabel() .. getText(" : <br>") .. trait:getDescription()); -- MODIFIED LINE
		textImage:setVisible(false);
		textImage.trait = trait;
		self:addChild(textImage);
		table.insert(self.traits, textImage);
	end
	self.Strength = self.char:getPerkLevel(Perks.Strength)
	self.Fitness = self.char:getPerkLevel(Perks.Fitness)
end



ISCharacterScreen.render = function(self)
    render_old(self);
    if self.profImage then
        local prof = ProfessionFactory.getProfession(self.char:getDescriptor():getProfession());
        self.profImage:setMouseOverText(prof:getName() .. getText(" : <br>") .. prof:getDescription());
    end 
end