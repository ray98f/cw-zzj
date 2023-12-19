function setCookie(cname,cvalue) {
    localStorage.setItem(cname, cvalue);
}

// 获取 Token
function getCookie(cname) {
    return localStorage.getItem(cname);
}

// 清除 Token
function clearCookie(cname) {
    localStorage.removeItem(cname);
}

function checkToken() {
    var userToken = getCookie("userToken");
    if (userToken != null && userToken !== '') {
        console.log("Welcome again " + userToken);
    } else {
        // location.href = "/zzj/login.html";
        electron.loadPage('./src/login.html')
    }
}

function timeChange(tm){

    var timeStr = tm.padStart(6, "0");
    var newTimeStr = "";
    if(timeStr.length == 6){
        newTimeStr += timeStr.substring(0,2) + ":";
        newTimeStr += timeStr.substring(2,4) + ":";
        newTimeStr += timeStr.substring(4,6);
    }
    return newTimeStr;
}

function trimComma(str){
    str=(str.substring(str.length-1)==',')?str.substring(0,str.length-1):str;
    str=(str.substr(0,1)==',')?str.substr(1):str;
    return str;
}