ZombiesZoneDefinition = ZombiesZoneDefinition or {};


ZombiesZoneDefinition.Police = {
	Swat = {
		name="Swat",
		chance=15,
		gender="male",
		beardStyles="null:80",
	},
	Police = {
		name="Police",
		chance=20,
	},

	OfficeWorkerSkirt = {
		name="OfficeWorkerSkirt",
		chance=10,
		gender="female",
	},
	OfficeWorker = {
		name="OfficeWorker",
		chance=10,
		gender="male",
		beardStyles="null:80",
	},
	AntiBomb = {
		name="AntiBomb",
		chance=2,
		gender="male",
		beardStyles="null:80",
	},
}


ZombiesZoneDefinition.Prison = {
	-- Gonna force male zombies in prison
	maleChance = 80,
	Doctor = {
		name="Doctor",
		chance=2,
	},
	Doctor2 = {
		name="Doctor",
		chance=20,
		room="medicalstorage",
	},
	Nurse = {
		name="Nurse",
		chance=30,
		room="medicalstorage",
	},
	Priest = {
		name="Priest",
		chance=2,
		gender="male",
		room="cafeteria;classroom",
	},
	Waiter_Diner = {
		name="Waiter_Diner",
		chance=2,
	},
	PrisonGuard = {
		name="PrisonGuard",
		chance=20,
		gender="male",
	},
	OfficeWorkerSkirt = {
		name="OfficeWorkerSkirt",
		gender="female",
		chance=30,
		room="office",
	},
	OfficeWorker = {
		name="OfficeWorker",
		gender="male",
		chance=30,
		room="office",
		beardStyles="null:80",
	},
	Security = {
		name="PrisonGuard",
		gender="male",
		chance=100,
		room="security",
	},
	Inmate = {
		name="Inmate",
		chance=76,
		gender="male",
		room="prisoncells;hall;cafeteria;classroom;laundry;janitor",
	},
	-- this one is used for lower chance of inmate in some rooms
	InmateLowerZone = {
		name="Inmate",
		chance=30,
		gender="male",
		room="bathroom;kitchen;medicalstorage;library",
	},
	Naked = {
		name="Naked",
		chance=50,
		gender="male",
		room="bathroom",
	},
	Cook_Generic = {
		name="Cook_Generic",
		chance=30,
		gender="male",
		room="kitchen",
	},
	AntiRiot = {
		name="AntiRiot",
		chance=10,
		gender="male",
		beardStyles="null:80",
	},
}
