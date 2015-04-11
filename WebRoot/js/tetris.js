//21*25
var nHNumber=21;
var nVNumber=25;
var nSizeWidth = 10;
var nSizeHeigth = 6;
var nMaxCom = 7;
var nScore = 0;
var nGameLevel = 500;
var GameCanvas = document.getElementById("Game-Canvas");
var GameCanvasContext = GameCanvas.getContext("2d");
var BackgroundAudioPlayer = document.getElementById("Background-AudioPlayer");
var ScoreAudioPlayer = document.getElementById("Score-AudioPlayer");
var GameOverAudioPlayer = document.getElementById("GameOver-AudioPlayer");
var rushBlock = new RusBlock();
var nGameStatus = 1;
var WINDOW_WIDTH =800;
var WINDOW_HEIGHT=100;
var RADIUS=1;
var MARGIN_TOP=0;
var MARGIN_LEFT=30;

const endTime = new Date();

var currentTime=0

var balls = [];
var colors=["#33B5E5","#0099CC","#9933CC","#AA66CC","#99CC00","#669900","#FFBB33","#FF8800","#FF4444","#CC0000"];
function GameStart() {
	endTime.setTime(endTime.getTime() + 60000);
    GameCanvasContext.clearRect(0, 0, nHNumber * nSizeWidth, nVNumber * nSizeHeigth);
    nGameStatus = 1;
    rushBlock.NewNextCom();
    rushBlock.NextComToCurrentCom();
    rushBlock.NewNextCom();
    nGameLevel = document.getElementById("Select-Game-Level").value;//��ȡ��Ϸ�Ѷ�
    BackgroundAudioPlayer.load();
    GameOverAudioPlayer.pause();
    BackgroundAudioPlayer.play();
    GameTimer();
}



function GameEnd() {
    BackgroundAudioPlayer.pause();
    GameOverAudioPlayer.load();
    GameOverAudioPlayer.play();
    nGameStatus = 0;
	for(var i=0;i<nHNumber;i++)
	{
		for(var  j=0;j<nVNumber;j++)
		{
			rushBlock.aState[i][j]=0;
		}
	}
}


function GameTimer() {
    var nDimension = rushBlock.CurrentCom.nDimesion;
    if (rushBlock.CanDown(1)) {
        
        rushBlock.InvalidateRect(rushBlock.ptIndex, nDimension);
        
        rushBlock.ptIndex.Y++;
        
    }
    else {
        for (var i = 0; i < nDimension * nDimension; i++) {
            if (rushBlock.CurrentCom.ptrArray[i] == 1) {
                var xCoordinate = rushBlock.ptIndex.X + i % nDimension;
                var yCoordinate = rushBlock.ptIndex.Y + (i - (i % nDimension)) / nDimension;
                rushBlock.aState[xCoordinate][yCoordinate] = 1;
            }
        }
        rushBlock.InvalidateRect();
        rushBlock.Disappear();
        if (rushBlock.CheckFail()) {
            rushBlock.nCurrentComID = -1;
            GameEnd();
        }
        else {
            rushBlock.NextComToCurrentCom();
            rushBlock.NewNextCom();
        }
    }
    DrawGame();
    if (nGameStatus)
        setTimeout("GameTimer()", nGameLevel);
}

function DrawGame() {
    
    GameCanvasContext.moveTo(nHNumber*nSizeWidth,0);
    GameCanvasContext.lineTo(nHNumber*nSizeWidth,nVNumber*nSizeHeigth);
    GameCanvasContext.stroke();


    
    GameCanvasContext.fillStyle = "blue";
    for (var i = 0; i < nHNumber; i++) {
        for (var j = 0; j < nVNumber; j++) {
            if (rushBlock.aState[i][j] == 1) {
                GameCanvasContext.fillRect(i * nSizeWidth, j * nSizeHeigth, nSizeWidth, nSizeHeigth);
            }
        }
    }
    GameCanvasContext.fillStyle = colors[Math.floor(Math.random()*colors.length)];
    
    if (rushBlock.CurrentCom.nComID >= 0) {
        var nDimension = rushBlock.CurrentCom.nDimesion;
        for (var i = 0; i < nDimension * nDimension; i++) {
            if(rushBlock.CurrentCom.ptrArray[i] == 1)
            {
                var xCoordinate = rushBlock.ptIndex.X + i % nDimension;
                var yCoordinate = rushBlock.ptIndex.Y + (i - (i %nDimension)) / nDimension;
                GameCanvasContext.fillRect(xCoordinate * nSizeWidth, yCoordinate * nSizeHeigth, nSizeWidth, nSizeHeigth);
            }
        }
    }
    
    var nNextComDimenion = rushBlock.NextCom.nDimesion;
    GameCanvasContext.clearRect((nHNumber+3)*nSizeWidth,10*nSizeHeigth,4*nSizeWidth,4*nSizeHeigth);
    for (var i = 0; i < nNextComDimenion * nNextComDimenion; i++) {
        if (rushBlock.NextCom.ptrArray[i] == 1) {
            var xCoordinate = nHNumber + i % nNextComDimenion+3;
            var yCoordinate = 10 + (i - i % nNextComDimenion) / nNextComDimenion;
            GameCanvasContext.fillRect(xCoordinate * nSizeWidth, yCoordinate * nSizeHeigth, nSizeWidth, nSizeHeigth);
        }
    }
}

function tagComponet() {
    this.nComID = null;
    this.nDimesion = null;
    this.ptrArray = null;
}

function RusBlock() {
   
    this.nCurrentComID = null;
    this.aState = new Array(nHNumber);
    for (var i = 0; i < nHNumber; i++) {
        this.aState[i] = new Array(nVNumber);
        for (var j = 0; j < nVNumber; j++)
            this.aState[i][j] = 0;
    }

    
    this.aComponets = new Array(nMaxCom);
    for (var i = 0; i < nMaxCom; i++)
        this.aComponets[i] = new tagComponet();
    
    
    this.aComponets[0].nComID=0;
    this.aComponets[0].nDimesion=2;
    this.aComponets[0].ptrArray=new Array(4);
    for(var i=0;i<4;i++)
    {
		this.aComponets[0].ptrArray[i]=1;
    }
        // 1 1
        // 1 1


        //1
	this.aComponets[1].nComID=1;
    this.aComponets[1].nDimesion=3;
    this.aComponets[1].ptrArray=new Array(9);
    this.aComponets[1].ptrArray[0]=0;
    this.aComponets[1].ptrArray[1]=1;
    this.aComponets[1].ptrArray[2]=0;
    this.aComponets[1].ptrArray[3]=1;
    this.aComponets[1].ptrArray[4]=1;
    this.aComponets[1].ptrArray[5]=1;
    this.aComponets[1].ptrArray[6]=0;
    this.aComponets[1].ptrArray[7]=0;
    this.aComponets[1].ptrArray[8]=0;
    // 0 1 0
    // 1 1 1
    // 0 0 0

    //2
    this.aComponets[2].nComID=2;
    this.aComponets[2].nDimesion=3;
    this.aComponets[2].ptrArray = new Array(9);
    this.aComponets[2].ptrArray[0]=1;
    this.aComponets[2].ptrArray[1]=0;
    this.aComponets[2].ptrArray[2]=0;
    this.aComponets[2].ptrArray[3]=1;
    this.aComponets[2].ptrArray[4]=1;
    this.aComponets[2].ptrArray[5]=0;
    this.aComponets[2].ptrArray[6]=0;
    this.aComponets[2].ptrArray[7] = 1;
    this.aComponets[2].ptrArray[8]=0;
    // 1 0 0
    // 1 1 0
    // 0 1 0


    //3
    this.aComponets[3].nComID=3;
    this.aComponets[3].nDimesion=3;
    this.aComponets[3].ptrArray = new Array(9);
    this.aComponets[3].ptrArray[0]=0;
    this.aComponets[3].ptrArray[1]=0;
    this.aComponets[3].ptrArray[2]=1;
    this.aComponets[3].ptrArray[3]=0;
    this.aComponets[3].ptrArray[4]=1;
    this.aComponets[3].ptrArray[5]=1;
    this.aComponets[3].ptrArray[6]=0;
    this.aComponets[3].ptrArray[7]=1;
    this.aComponets[3].ptrArray[8]=0;
    // 0 0 1
    // 0 1 1
    // 0 1 0


    //4
    this.aComponets[4].nComID=4;
    this.aComponets[4].nDimesion=3;
    this.aComponets[4].ptrArray = new Array(9);
    this.aComponets[4].ptrArray[0]=1;
    this.aComponets[4].ptrArray[1]=0;
    this.aComponets[4].ptrArray[2]=0;
    this.aComponets[4].ptrArray[3]=1;
    this.aComponets[4].ptrArray[4]=1;
    this.aComponets[4].ptrArray[5]=1;
    this.aComponets[4].ptrArray[6]=0;
    this.aComponets[4].ptrArray[7]=0;
    this.aComponets[4].ptrArray[8]=0;
    // 1 0 0
    // 1 1 1
    // 0 0 0


    //5
    this.aComponets[5].nComID=5;
    this.aComponets[5].nDimesion=3;
    this.aComponets[5].ptrArray = new Array(9);
    this.aComponets[5].ptrArray[0]=0;
    this.aComponets[5].ptrArray[1]=0;
    this.aComponets[5].ptrArray[2]=1;
    this.aComponets[5].ptrArray[3]=1;
    this.aComponets[5].ptrArray[4]=1;
    this.aComponets[5].ptrArray[5]=1;
    this.aComponets[5].ptrArray[6]=0;
    this.aComponets[5].ptrArray[7]=0;
    this.aComponets[5].ptrArray[8]=0;
    // 0 0 1
    // 1 1 1
    // 0 0 0

    //6
    this.aComponets[6].nComID=6;
    this.aComponets[6].nDimesion=4;
    this.aComponets[6].ptrArray = new Array(16);
    this.aComponets[6].ptrArray[0]=0;
    this.aComponets[6].ptrArray[1]=0;
    this.aComponets[6].ptrArray[2]=0;
    this.aComponets[6].ptrArray[3]=1;
    this.aComponets[6].ptrArray[4]=0;
    this.aComponets[6].ptrArray[5]=0;
    this.aComponets[6].ptrArray[6]=0;
    this.aComponets[6].ptrArray[7]=1;
    this.aComponets[6].ptrArray[8]=0;
    this.aComponets[6].ptrArray[9]=0;
    this.aComponets[6].ptrArray[10]=0;
    this.aComponets[6].ptrArray[11]=1;
    this.aComponets[6].ptrArray[12]=0;
    this.aComponets[6].ptrArray[13]=0;
    this.aComponets[6].ptrArray[14]=0;
    this.aComponets[6].ptrArray[15]=1;
    //0 0 0 1
    //0 0 0 1 
    //0 0 0 1
    //0 0 0 1
    this.CurrentCom = new tagComponet();
    this.NextCom = new tagComponet();
    this.ptIndex = new Point(0,0);
    


    
    this.NewNextCom = function () {
        var nComID = Math.round(Math.random() * 6);
        this.NextCom.nComID = nComID;
        var nDimension = this.aComponets[nComID].nDimesion;
        this.NextCom.nDimesion = nDimension;
        this.NextCom.ptrArray = new Array(nDimension * nDimension);
        for (var i = 0; i < nDimension * nDimension; i++) {
            this.NextCom.ptrArray[i] = this.aComponets[nComID].ptrArray[i];
        }
    }


    this.NextComToCurrentCom = function () {
        this.CurrentCom.nComID = this.NextCom.nComID;
        this.nCurrentComID = this.CurrentCom.nComID;
        this.CurrentCom.nDimesion = this.NextCom.nDimesion;
        var nDimension = this.CurrentCom.nDimesion;
        this.CurrentCom.ptrArray = new Array(nDimension * nDimension);
        for (var i = 0; i < nDimension * nDimension; i++) {
            this.CurrentCom.ptrArray[i] = this.NextCom.ptrArray[i];
        }
        this.ptIndex.X = 9;
        this.ptIndex.Y = -1;
        
        if (this.CanNew()==false) {
            this.nCurrentComID = -1;
            GameEnd();
        }
    }
    
    this.CanDown = function (nNumber) {
        var bDown = true;
        var ptNewIndex = new Point(this.ptIndex.X, this.ptIndex.Y);
        ptNewIndex.Y+=nNumber;
        var nDimension = this.CurrentCom.nDimesion;
        for (var i = 0; i < nDimension * nDimension; i++) {
            if (this.CurrentCom.ptrArray[i] == 1) {
                var xCoordinate = ptNewIndex.X + i % nDimension;
                var yCoordinate = ptNewIndex.Y + (i - (i % nDimension)) / nDimension;
                if (yCoordinate >= nVNumber || this.aState[xCoordinate][yCoordinate] == 1) {
                    bDown = false;
                }
            }
        }
        ptNewIndex = null;
        return bDown;
    }
    
    this.Left = function () {
        var bLeft = true;
        var nDimension = this.CurrentCom.nDimesion;
        var ptNewPoint = new Point(this.ptIndex.X,this.ptIndex.Y);
        ptNewPoint.X--;
        for (var i = 0; i < nDimension * nDimension; i++) {
            if (this.CurrentCom.ptrArray[i] == 1) {
                var xCoordinate = ptNewPoint.X + i % nDimension;
                var yCoordinate = ptNewPoint.Y + (i - (i % nDimension)) / nDimension;;
                if (xCoordinate <0 || this.aState[xCoordinate][yCoordinate] == 1) {
                    bLeft = false;
                }
            }
        }
        ptNewPoint = null;
        if (bLeft)
            this.ptIndex.X--;
    }
    
    this.Right = function () {
        var bRight = true;
        var nDimension = this.CurrentCom.nDimesion;
        var ptNewIndex = new Point(this.ptIndex.X, this.ptIndex.Y);
        ptNewIndex.X++;
        for (var i = 0; i < nDimension * nDimension; i++) {
            if (this.CurrentCom.ptrArray[i] == 1) {
                var xCoordinate = ptNewIndex.X + i % nDimension;
                var yCoordinate = ptNewIndex.Y + (i - (i % nDimension)) / nDimension;
                if (xCoordinate>=nHNumber|| this.aState[xCoordinate][yCoordinate] == 1) {
                    bRight = false;
                }
            }
        }
        ptNewIndex = null;
        if (bRight) {
            this.ptIndex.X++;
        }
    }
    
    this.Rotate = function () {
        var bRotate = true;
        var nDimension = this.CurrentCom.nDimesion;
        var ptNewIndex = new Point(this.ptIndex.X, this.ptIndex.Y);
        var ptrNewCom = new Array(nDimension * nDimension);
        for(var i=0;i<nDimension*nDimension;i++)
        {
            var row = (i-i%nDimension) / nDimension;//��
            var column = i % nDimension;//��
            var newIndex=column * nDimension + (nDimension - row - 1);
            ptrNewCom[newIndex] = rushBlock.CurrentCom.ptrArray[i];//����:Ŀ��[��][ά��-��-1]=Դ[��][��]
            if (ptrNewCom[newIndex] == 1) {
                var xCoordinate = ptNewIndex .X+ newIndex % nDimension;
                var yCoordinate = ptNewIndex.Y +( newIndex - newIndex % nDimension )/ nDimension;
                if (xCoordinate < 0 || this.aState[xCoordinate][yCoordinate] == 1 || xCoordinate >= nHNumber || yCoordinate >= nVNumber) {
                    bRotate = false;
                }
            }
        }
        if (bRotate) {
            for (var i = 0; i < nDimension * nDimension; i++) {
                this.CurrentCom.ptrArray[i] = ptrNewCom[i];
            }
        }
        ptNewIndex = null;
        ptrNewCom = null;
    }


    this.Accelerate = function () {
        if (this.CanDown(3)) {
            this.ptIndex.Y += 3;
        }
    }
    
    this.CanNew = function () {
        var bNew = true;
        var nDimension = this.CurrentCom.nDimesion;
        var ptNewIndex = new Point(this.ptIndex.X, this.ptIndex.Y);
        for (var i = 0; i < nDimension * nDimension; i++) {
            if (this.CurrentCom.ptrArray[i] == 1) {
                var xCoordinate = ptNewIndex.X + i % nDimension;
                var yCoordinate = ptNewIndex.Y +( i - i % nDimension) / nDimension;
                if (this.aState[xCoordinate][yCoordinate] == 1) {//����ס
                    bNew = false;
                }
            }
        }
        ptNewIndex = null;
        return bNew;
    }
    
    this.Disappear = function () {
        var nLine = 0;
        for (var i = nVNumber - 1; i >= 0; i--) {
            var bLine = true;
            for (var j = 0; j < nHNumber; j++) {
                if (this.aState[j][i] == 0)
                    bLine = false;
            }

            if (bLine) {
                nLine++;
                for (var j = i; j > 0; j--) {
                    for (var k = 0; k < nHNumber; k++) {
                        this.aState[k][j] = this.aState[k][j - 1];
                    }
                }
                for (var j = 0; j < nHNumber; j++) {
                    this.aState[j][0] = 0;
                }
                i++;
                GameCanvasContext.clearRect(0,0,nHNumber*nSizeWidth,nVNumber*nSizeHeigth);
            }
        }
        if (nLine) {
            ScoreAudioPlayer.play();
            nScore += nLine * 21;
            document.getElementById("Game-Score").innerText=nScore;
        }

       
    }
    
    this.InvalidateRect = function () {
        GameCanvasContext.clearRect(this.ptIndex.X*nSizeWidth-1,this.ptIndex.Y*nSizeHeigth-1,(this.CurrentCom.nDimesion)*nSizeWidth+1.5,(this.CurrentCom.nDimesion)*nSizeWidth+1);
    }
    
    this.CheckFail = function () {
        var bEnd = false;
        
        for (var i = 0; i < nHNumber; i++) {
            if (this.aState[i][0] == 1) {
                bEnd = true;
            }
        }
        return bEnd;
    }
    
}


function Point(x, y) {
    this.X= x;
    this.Y= y;
}


function Action(event) {
    var nDimension = rushBlock.CurrentCom.nDimesion;
    rushBlock.InvalidateRect();
    switch (event.keyCode) {
        case 37://left
            rushBlock.Left();
            break;
        case 38://up->rotate 
            rushBlock.Rotate();
            break;
        case 39://right
            rushBlock.Right();
                break;
        case 40:
            rushBlock.Accelerate();
            break;
    }
    GameCanvasContext.fillStyle = colors[Math.floor(Math.random()*colors.length)];
  
    for (var i = 0; i < nDimension * nDimension; i++) {
        if (rushBlock.CurrentCom.ptrArray[i] == 1) {
            var xCoordinate = rushBlock.ptIndex.X + i % nDimension;
            var yCoordinate = rushBlock.ptIndex.Y + (i - i % nDimension) / nDimension;
            GameCanvasContext.fillRect(xCoordinate*nSizeWidth,yCoordinate*nSizeHeigth,nSizeWidth,nSizeHeigth);
        }
    }
}




function getCurrentTime(){
	 var curTime = new Date();
	 var ret = endTime.getTime()-curTime.getTime()
	 ret =Math.round(ret/1000)
	 return ret >= 0 ? ret: 0;
}

function update(){
	var nextShowTime = getCurrentTime();
	
	var nextHours = parseInt(nextShowTime/3600)
	var nextMinutes = parseInt((nextShowTime - nextHours*3600)/60)
	var nextSeconds = nextShowTime % 60;
	
	var curHours = parseInt(currentTime/3600)
	var curMinutes = parseInt((currentTime - curHours*3600)/60)
	var curSeconds = currentTime % 60;
	
	if(curSeconds!=nextSeconds){
		if(parseInt(curHours/10)!=parseInt(nextHours/10)){
			addBalls(MARGIN_LEFT, MARGIN_TOP, parseInt(curHours/10));
		}
		if(parseInt(curHours%10)!=parseInt(nextHours%10)){ 
			addBalls(MARGIN_LEFT + 15*(RADIUS+1), MARGIN_TOP, parseInt(curHours%10));
		}
		if(parseInt(curMinutes/10)!=parseInt(nextMinutes/10)){
			addBalls(MARGIN_LEFT + 39*(RADIUS+1), MARGIN_TOP, parseInt(curHours/10));
		}
		if(parseInt(curMinutes%10)!=parseInt(nextMinutes%10)){
			addBalls(MARGIN_LEFT + 54*(RADIUS+1), MARGIN_TOP, parseInt(curHours%10));
		}
		if(parseInt(curSeconds/10)!=parseInt(nextSeconds/10)){
			addBalls(MARGIN_LEFT + 78*(RADIUS+1), MARGIN_TOP, parseInt(curHours/10));
		}
		if(parseInt(curSeconds%10)!=parseInt(nextSeconds%10)){
			addBalls(MARGIN_LEFT + 93*(RADIUS+1), MARGIN_TOP, parseInt(curHours%10));
		}
		currentTime = nextShowTime;
	}
	updateBalls();
}

function updateBalls(){
	for(var i = 0; i<balls.length;i++){
		balls[i].x+=balls[i].vx;
		balls[i].y+=balls[i].vy;
		balls[i].y+=balls[i].g;
		
		if(balls[i].y >=WINDOW_HEIGHT-RADIUS){
			balls[i].y = WINDOW_HEIGHT - RADIUS;
			balls[i].vy= - balls[i].vy * 0.55;
		}
	}
}

function addBalls(x, y, num){
	for(var i =0; i<digit[num].length; i++){
		for(var j=0; j<digit[num][i].length; j++){
			if(digit[num][i][j]==1){
				var Balls={
					x:x+j*2*(RADIUS+1)+(RADIUS+1),
					y:y+i*2*(RADIUS+1)+(RADIUS+1),
					g:1.5+Math.random(),
					vx:Math.pow(-1, Math.ceil(Math.random()*1000))*4,
					vy:5,
					color:colors[Math.floor(Math.random()*colors.length)]
				}
				
				balls.push(Balls);
			}
		}
	}
}

function render(cxt){
	cxt.clearRect(0,0,WINDOW_WIDTH,WINDOW_HEIGHT);
	
	var hours = parseInt(currentTime/3600);
	var minutes = parseInt((currentTime - hours*3600)/60);
	var seconds = currentTime % 60;
	
	renderDigit(MARGIN_LEFT, MARGIN_TOP, parseInt(hours/10), cxt);
	renderDigit(MARGIN_LEFT + 15 *(RADIUS+1), MARGIN_TOP, parseInt(hours%10), cxt);
	renderDigit(MARGIN_LEFT + 30 *(RADIUS+1), MARGIN_TOP, 10, cxt);
	renderDigit(MARGIN_LEFT + 39 *(RADIUS+1), MARGIN_TOP, parseInt(minutes/10), cxt);
	renderDigit(MARGIN_LEFT + 54 *(RADIUS+1), MARGIN_TOP, parseInt(minutes%10), cxt);
	renderDigit(MARGIN_LEFT + 69 *(RADIUS+1), MARGIN_TOP, 10, cxt);
	renderDigit(MARGIN_LEFT + 78 *(RADIUS+1), MARGIN_TOP, parseInt(seconds/10), cxt);
	renderDigit(MARGIN_LEFT + 93 *(RADIUS+1), MARGIN_TOP, parseInt(seconds%10), cxt);
	
	for(var i =0; i< balls.length; i++){
		cxt.fillStyle=balls[i].color;
		 
		cxt.beginPath();
		cxt.arc(balls[i].x, balls[i].y, RADIUS, 0, 2*Math.PI, true);
		cxt.closePath();
		
		cxt.fill();
	}
	
}

function renderDigit(x, y, num, cxt){
	cxt.fillStyle = "rgb(0,102,153)";
	
	for(var i = 0; i<digit[num].length;i++){
		for(var j=0; j<digit[num][i].length; j++){
			if(digit[num][i][j]==1){
				cxt.beginPath();
				cxt.arc(x+j*2*(RADIUS+1)+RADIUS+1, y+i*2*(RADIUS+1)+RADIUS+1, RADIUS, 0, 2* Math.PI);
				cxt.closePath()
				
				cxt.fill()
			}
		}
	}
}
