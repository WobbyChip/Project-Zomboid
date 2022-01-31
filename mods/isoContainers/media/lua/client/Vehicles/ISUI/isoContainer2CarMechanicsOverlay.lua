local function info()
--this chunk here is hell, i hate it so much, gas tank is a piece of...

ISCarMechanicsOverlay.CarList["Base.isoContainer2"] = {imgPrefix = "isoContainer2_", x=10,y=0};
--
ISCarMechanicsOverlay.PartList["TrunkDoor"] = {img="trunkdoor", vehicles = {"isoContainer2_"}};
ISCarMechanicsOverlay.PartList["TrunkDoor"].vehicles = ISCarMechanicsOverlay.PartList["TrunkDoor"].vehicles or {};
ISCarMechanicsOverlay.PartList["TrunkDoor"].vehicles["isoContainer2_"] = {x=0,y=0,x2=0,y2=0};
--
end


Events.OnInitWorld.Add(info);