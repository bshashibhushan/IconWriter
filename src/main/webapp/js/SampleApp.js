﻿var WebTWAIN;
var ua;
var em = "";
var seed;
var objEmessage;
var re;
var strre;
var ileft, itop, iright, ibottom;
var timeout;
var Resolution, InterpolationMethod, txt_fileNameforSave, txt_fileName;
var CurrentImage, TotalImage, MultiPageTIFF_save, MultiPagePDF_save, MultiPageTIFF, MultiPagePDF, PreviewMode, pNoScanner;
var TWAINPlugInMSI, TWAINx64CAB, TWAINCAB;
window.onload = Pageonload;
//====================Page Onload  Start==================//

function Pageonload() {

    var strObjectFF = GetEventString(); 
    strObjectFF += " class='divcontrol' pluginspage='DynamicWebTWAIN/DynamicWebTwain.xpi'></embed>";

    var strObject = GetObject();
		
	var obj = document.getElementById("maindivIElimit");
    obj.style.display = "none";
	
    if (ExplorerType() == "IE" && navigator.userAgent.indexOf("Win64") != -1 && navigator.userAgent.indexOf("x64") != -1) {
        strObject = "<object id='mainDynamicWebTwainIE' codebase='DynamicWebTWAIN/DynamicWebTWAINx64.cab#version=8,0' class='divcontrol' " + "classid='clsid:FFC6F181-A5CF-4ec4-A441-093D7134FBF2' viewastext> " + strObject;
        var objDivx64 = document.getElementById("maindivIEx64");
        objDivx64.style.display = "inline";
        objDivx64.innerHTML = strObject;
        var obj = document.getElementById("mainControlPluginNotInstalled");
        obj.style.display = "none";
        WebTWAIN = document.getElementById("mainDynamicWebTwainIE");
        document.getElementById("64bitsamplesource").style.display = "inline";
        document.getElementById("32bitsamplesource").style.display = "none";
		var obj = document.getElementById("maindivIElimit");
        obj.style.display = "none";
    }
    else if (ExplorerType() == "IE" && (navigator.userAgent.indexOf("Win64") == -1 || navigator.userAgent.indexOf("x64") == -1)) {
        strObject = "<object id='mainDynamicWebTwainIE' codebase='DynamicWebTWAIN/DynamicWebTWAIN.cab#version=8,0' class='divcontrol' " + "classid='clsid:FFC6F181-A5CF-4ec4-A441-093D7134FBF2' viewastext> " + strObject;
        var objDivx86 = document.getElementById("maindivIEx86");
        objDivx86.style.display = "inline";
        objDivx86.innerHTML = strObject;
        var obj = document.getElementById("mainControlPluginNotInstalled");
        obj.style.display = "none";
        WebTWAIN = document.getElementById("mainDynamicWebTwainIE");
		var obj = document.getElementById("maindivIElimit");
        obj.style.display = "none";
    }
	else if (ExplorerType() == "notIE" ){
		var objDivFF = document.getElementById("mainControlPlugin");
        objDivFF.innerHTML = strObjectFF;
		var obj = document.getElementById("maindivIE");
        obj.style.display = "none";
		var obj = document.getElementById("maindivIElimit");
        obj.style.display = "none";
        WebTWAIN = document.getElementById("mainDynamicWebTWAINnotIE");
	}
    else{
        var obj = document.getElementById("mainControlPluginNotInstalled");
        obj.style.display = "none";
		var obj = document.getElementById("maindivIE");
        obj.style.display = "none";
		var obj = document.getElementById("maindivIElimit");
        obj.style.display = "none";
    }
    
    CurrentImage = document.getElementById("CurrentImage");
    CurrentImage.value = "";
    TotalImage = document.getElementById("TotalImage");
    TotalImage.value = "0";

    seed = setInterval(ControlDetect, 500);
}

function ControlDetect() {
    if (WebTWAIN.ErrorCode == 0) {
        pause();
		
		if (ExplorerType() == "notIE" ){
			document.getElementById("mainControlPluginNotInstalled").style.display = "none";
            document.getElementById("mainControlPlugin").style.display = "inline";
		}
		
        var i;
        document.getElementById("source").options.length = 0;
        WebTWAIN.OpenSourceManager();

        for (i = 0; i < WebTWAIN.SourceCount; i++) {
            document.getElementById("source").options.add(new Option(WebTWAIN.SourceNameItems(i), i));
        }
        WebTWAIN.MaxImagesInBuffer = 4096;
        WebTWAIN.MouseShape = false;
        
        AddResolution();

        AddInterpolationMethod();

        SetFileName();

        document.getElementById("ADF").checked = true;
        SetMultiPageInfo(); 

        AddPreviewMode();

        SetNoScanner();
        
        objEmessage = document.getElementById("emessage");

        re = /^\d+$/;
        strre = /^\w+$/;

        ileft = 0;
        itop = 0;
        iright = 0;
        ibottom = 0;

        SetShowOrCloseLoadImage();
        
        var allinputs = document.getElementsByTagName("input");
        var j = 0;
        for (var i = 0; i < allinputs.length; i++) {
            if (allinputs[i].type == "text") {
                allinputs[i].onkeyup = function () {
                    if (event.keyCode != 37 && event.keyCode != 39) value = value.replace(/\D/g, '');
                }
            }
        }
        objEmessage.ondblclick = function () {
            em = "";
            this.innerHTML = "";
        }
        if (ExplorerType() == "IE") {
            GetIEEventString();
        }
        
        DoWithNoSource();
    }
    else {
        if (navigator.userAgent.indexOf("Macintosh") != -1) {
            document.getElementById("MACmainControlNotInstalled").style.display = "inline";
            document.getElementById("mainControlPluginNotInstalled").style.display = "none";
            document.getElementById("mainControlInstalled").style.display = "none";
        }
        else if (ua.match(/chrome\/([\d.]+)/) || ua.match(/opera.([\d.]+)/) || ua.match(/version\/([\d.]+).*safari/)) {
            document.getElementById("mainControlPluginNotInstalled").style.display = "inline";
            document.getElementById("mainControlPlugin").style.display = "none";
            document.getElementById("MACmainControlNotInstalled").style.display = "none";
        }
    }
    timeout = setTimeout(function(){},10);
}
//====================Page Onload End====================//


//====================Preview Group Start====================//
function slPreviewMode() {
    WebTWAIN.SetViewMode(parseInt(document.getElementById("PreviewMode").selectedIndex + 1), parseInt(document.getElementById("PreviewMode").selectedIndex + 1));
    if (document.getElementById("PreviewMode").selectedIndex != 0) {
        WebTWAIN.MouseShape = true;
    }
    else {
        WebTWAIN.MouseShape = false;
    }
}
//====================Preview Group End====================//

//====================Get Image Group Start=====================//

/*-----------------Load Image---------------------*/
function btnLoad_onclick() {
	if(location.pathname.lastIndexOf('\\')>1)
		WebTWAIN.LoadImage(location.pathname.substring(1, location.pathname.lastIndexOf('\\')).replace(/%20/g," ") + "\\Images\\twain_associate.pdf");
	else
		WebTWAIN.LoadImage(location.pathname.substring(1, location.pathname.lastIndexOf('/')).replace(/%20/g," ") + "/Images/twain_associate.pdf");
    UpdatePageInfo();
    if (WebTWAIN.SourceCount != 0) {
        clearTimeout(timeout);
        timeout = setTimeout(function () {
            document.getElementById("tblLoadImage").style.visibility = "hidden";
        }, 1000);
    }
}

//====================Get Image Group End=====================//

//====================Edit Image Group Start=====================//

/*----------------------Crop Method---------------------*/

function btnCropOK_onclick() {
    btnCropOKInner();
}

/*----------------Change Image Size--------------------*/
function btnChangeImageSizeOK_onclick() {
    btnChangeImageSizeOKInner();
}

function uploadimages() {
//alert('kk');
    WebTWAIN.IfShowFileDialog = true;
	//alert(WebTWAIN.ErrorCode);
	WebTWAIN.LoadImage("C:\\scan\\test.jpg");

}


//====================Edit Image Group End==================//

/*-----------------Upload Image Group---------------------*/
function btnUpload_onclick(){

    var CurrentPathName = unescape(location.pathname); // get current PathName in plain ASCII	
    var CurrentPath = CurrentPathName.substring(0, CurrentPathName.lastIndexOf("/") + 1);
    var strActionPage = CurrentPath + "SaveToDB.aspx"; //the ActionPage's file path
    var redirectURLifOK = CurrentPath + "online_demo_list.aspx";

    btnUploadInner("www.dynamsoft.com", 80, strActionPage, redirectURLifOK);
   
    if (CheckErrorString()) {
        window.location = redirectURLifOK;
    }	
}