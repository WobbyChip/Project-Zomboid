const http = require('http');
const express = require('express');
const socketio = require('socket.io');
const child_process = require('child_process');
const isOnline = require('is-online');
const publicIp = require('qiao-get-ip');
const fs  = require('fs');
const path = require('path');

var config = {
    serverIni: 'servertest.ini',
    gameDir: `./game`,
    memory: '8g',
    port: 5000,

    buffer_lines: 1000,
    buffer_size: 0,
    buffer: new String(),

    shutdown: false,
    output: true,
}

config.commandline = `"${config.gameDir}/ProjectZomboidServer.bat"`;

const app = express();
app.use(express.static('website', {index: false}));
const server = http.createServer(app);
const io = socketio(server);

var ProccessStdin;
process.title = 'Project Zomboid Server';
process.chdir(__dirname + '/..');

(async () => {
    await SetupSettings();

    var startFile = fs.readFileSync('./runtime/ProjectZomboidServer.bat', 'utf8').toString();
    startFile = startFile.replace(/%memory%/g, config.memory);
    fs.writeFileSync(`${config.gameDir}/ProjectZomboidServer.bat`, startFile);

    StartProccess();

    //Start server
    server.listen(config.port, IPV4Address(), function() {
        console.log(`Listening on ${IPV4Address()}:${config.port}`);
    });
})();



//When someone visit webpage
app.get('/', async function(req, res) {
    if (IPV4Address() == '127.0.0.1') {
        return res.sendFile(__dirname + '/website/index.html');
    }

    var ip = req.headers['x-forwarded-for'] || req.socket.remoteAddress;

    if (await publicIp.getIp() == ip) {
        res.sendFile(__dirname + '/website/index.html');
    } else {
        res.statusCode = 401;
        res.setHeader('WWW-Authenticate', 'Basic realm="example"');
        res.end('Access denied');
    }
});

function PrintOutput(data) {
    config.buffer += data;

    if (config.output) {
        process.stdout.write(data);
    }
}

function PrintCommand(data) {
    ProccessStdin.write(data + '\n');
    config.buffer += '>' + data + '\n';

    if (config.output) {
        process.stdout.write('>' + data + '\n');
    }
}

//When someone connects
io.sockets.on('connection', socket => {
    //Send auto offline data
    socket.emit('autooffline', config.auto_offline);

    //Send text from console
    socket.emit('buffer', config.buffer.split('\n').slice(-config.buffer_lines).join('\n'));

    //When update recives
    socket.on('update', () => {
        if (config.buffer_size == config.buffer.length) {
            return;
        }

        config.buffer_size = config.buffer.length;
        socket.emit('buffer', config.buffer.split('\n').slice(-config.buffer_lines).join('\n'));
    });

    //When sendtext recives
    socket.on('sendtext', data => {  
        PrintCommand(data);
    });

    //When shutdown recives
    socket.on('shutdown', () => {
        config.shutdown = true;
        ProccessStdin.write('quit\n');
    });

    //When restart recives
    socket.on('restart', () => {
        ProccessStdin.write('quit\n');
    });

    //When clipboard recives
    socket.on('clipboard', () => {
        socket.emit('clipboard', config.buffer);
    });
})


//======================================================================================================
//Bunch of different functions==========================================================================
//======================================================================================================


//Wait function
async function Wait(milleseconds) {
	return new Promise(resolve => setTimeout(resolve, milleseconds))
}


//Get IPV4 Address
function IPV4Address() {
    var address, ifaces = require('os').networkInterfaces();
    for (var dev in ifaces) {
        ifaces[dev].filter((details) => details.family === 'IPv4' && details.internal === false ? address = details.address: undefined);
    }

    return address;
}


//Setup some settings
async function SetupSettings() {
    while (!await isOnline()) {
        Wait(1000);
    }

    var mods = [];
    var directories = fs.readdirSync('./mods', { withFileTypes: true });
    directories = directories.filter(e => e.isDirectory()).map(e => e.name);

    for (let dir of directories) { 
        mods.push(getProperties(`./mods/${dir}/mod.info`, 'id='));
    }

    //Setup properties
    setProperties(`./Server/${config.serverIni}`, 'Mods=', mods.join(';'));
}


//Set value in properties file
function setProperties(filename, name, value) {
    var properties = fs.readFileSync(filename, 'utf8').toString().split('\n');

    for (var i = 0; i < properties.length; i++) {
        if (properties[i].startsWith(name)) {
            properties[i] = name + value + '\r';
        }
    }

    fs.writeFileSync(filename, properties.join('\n'));
}


//Get value in properties file
function getProperties(filename, name) {
    var properties = fs.readFileSync(filename, 'utf8').toString().split('\n');

    for (var i = 0; i < properties.length; i++) {
        if (properties[i].startsWith(name)) {
            var value = properties[i].split('=');
            value.shift();
            return value.join('').trim();
        }
    }
 
    return '';
}


//Start server process
function StartProccess() {
    var ChildProccess = child_process.exec(config.commandline, {encoding: "UTF-8"});
    ProccessStdin = ChildProccess.stdin;

    //When application exits
    ChildProccess.on('close', async () => {
        if (config.shutdown) {
            config.buffer += 'Server closed.';
            child_process.exec('shutdown.exe -s -t 5');
            return;
        }

        RestartProccess();
    });

    //Output data to console and string variable
    ChildProccess.stdout.on('data', (data) => {
        PrintOutput(data);
    });

    //Output errors to console and string variable
    ChildProccess.stderr.on('data', (data) => {
        PrintOutput(data);
    });
}


//Restart server process
async function RestartProccess() {
    PrintOutput('Waiting for 5 seconds!\n');
    await Wait(5000);

    try {
        fs.unlinkSync('server-console.txt');
    } catch(e) {
        return RestartProccess();
    }

    config.buffer = '';
    console.clear();
    await SetupSettings();
    StartProccess();
}