const encrypt = (text) => {
    const passphrase = '123';
    return CryptoJS.AES.encrypt(text, passphrase).toString();
};

const decrypt = (ciphertext) => {
    const passphrase = '123';
    const bytes = CryptoJS.AES.decrypt(ciphertext, passphrase);
    const originalText = bytes.toString(CryptoJS.enc.Utf8);
    return originalText;
};

function getOpponentTotalScore(play) {
    let res = 0;
    for(let i = 0; i < play; i++)
        res += sharedGame.opponentScores[i];
    return res;
}

function isSharedGame() {
    return sharedGame.opponentName != null;
}

function getOpponentPlayScore(play) {
    let res = null;

    if(play >= 0 && play <= sharedGame.opponentScores.length - 1)
        res = sharedGame.opponentScores[play];

    return res;
}

function getSharedURL(game) {
    let encrypted = encrypt(JSON.stringify(game));
    let myUrlWithParams = new URL(document.URL);
    if(myUrlWithParams.searchParams.get("game") == null)
        myUrlWithParams.searchParams.append("game", encrypted);
    else
        myUrlWithParams.searchParams.set("game", encrypted);
    return myUrlWithParams.href;
}



//sharedGame = new SharedGame("Loup", [0], [0]);
//let url = new URL(document.URL);
//url.searchParams.append("game", encrypt(JSON.stringify(sharedGame)));
//console.log(url.href);

urlParams = new URLSearchParams(window.location.search);
encryptedSharedGame = urlParams.get('game');
sharedGame = new SharedGame(null, [], []);
if(encryptedSharedGame) {
    try {
        sharedGame = JSON.parse(decrypt(encryptedSharedGame));
        document.getElementById("opponentName").innerHTML = "Score de " + sharedGame.opponentName + ":";
        document.getElementById("opponent").style.display = "initial";
    } catch(error) {
        alert("Cette partie partagée semble corrompue. Impossible de la démarrer.");
    }
}


//if(sharedGame == null) {
//    console.log("null");
//    sharedGame = new SharedGame();
//}



//let encrypted = encrypt(JSON.stringify(sharedGame));
//const myUrlWithParams = new URL(document.URL);

//myUrlWithParams.searchParams.append("game", encrypted);
//console.log(myUrlWithParams.href);

//let decrypted = decrypt(encrypted);
//console.log(JSON.parse(decrypted));
