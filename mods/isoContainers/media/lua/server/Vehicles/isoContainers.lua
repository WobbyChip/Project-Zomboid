--***********************************************************
--**                          KI5                          **
--***********************************************************

local distributionTable = VehicleDistributions[1]

VehicleDistributions.mccoy = {
	
	TruckBed = VehicleDistributions.McCoyTruckBed;
}

VehicleDistributions.fosoil = {
	
	TruckBed = VehicleDistributions.FossoilTruckBed;
}

distributionTable["isoContainer2"] = { Normal = VehicleDistributions.mccoy; }
distributionTable["isoContainer3tanker"] = { Normal = VehicleDistributions.fosoil; }