MAX_REDRAW = 10;
class Controller {

    infoMessageDiv = document.getElementById("info");
    bestPlay = null;
    redrawInARow = 0;

    constructor() {
        this.model = new Model();
        this.drawTiles();
        this.refreshScorePanel();
    }

    waitingForMaster() {
        this.displayInfoMessage("Le maître du jeu est entrain de réfléchir. Veuillez patienter.");
        document.getElementById("playBtn").style.display = "none";
        document.getElementById("resetRackBtn").style.display = "none";
        document.getElementById("loader").style.display = "initial";
    }

    masterIsDone() {

        document.getElementById("playBtn").style.display = "initial";
        document.getElementById("resetRackBtn").style.display = "initial";
        document.getElementById("loader").style.display = "none";
        this.displayInfoMessage("À vous de jouer !");
    }

    searchForBestPlay() {
        this.waitingForMaster();

        setTimeout(() => {
           this.bestPlay = master.getBestPlay();
           if(this.bestPlay == null) {
                //this.displayInfoMessage("Impossible de jouer avec ce tirage.");
                this.resetDraw();
           } else {
               this.redrawInARow = 0;
               this.masterIsDone();
           }
        }, 1);
    }


    resetDraw() {
        this.redrawInARow++;

        resetRackAndBoard();
        this.removeAllTilesFromBoard();
        for(let i = 0; i < this.model.rack.length; i++) {
            this.model.tiles.push(this.model.rack[i]);
            this.model.rack.splice(i, 1);
            i--;
        }
        if(this.drawTiles() == true)
            this.searchForBestPlay();
    }

    refreshScorePanel() {
        document.getElementById("yourscore").innerHTML = this.model.myScore + "" + (this.model.myLastPlay != null ? " (+" + this.model.myLastPlay + ")" : "" );
        document.getElementById("masterscore").innerHTML = this.model.masterScore + "" + (this.model.masterLastPlay != null ? " (+" + this.model.masterLastPlay + ")" : "" );
        document.getElementById("playnumber").innerHTML = this.model.numberOfPlays + "";
        document.getElementById("letternumber").innerHTML = this.model.getNumberOfRemainingLetters() + "";

        if(this.model.numberOfPlays <= sharedGame.opponentScores.length) {
            let opponentTotalScore = getOpponentTotalScore(this.model.numberOfPlays);
            let opponentPlayScore = getOpponentPlayScore(this.model.numberOfPlays - 1);
            document.getElementById("opponentscore").innerHTML = opponentTotalScore + (opponentPlayScore != null ? " (+" + opponentPlayScore + ")" : "");

        }

    }

    displayInfoMessage(msg) {
        this.infoMessageDiv.classList.remove("transparent");
        this.infoMessageDiv.innerHTML = msg;
    }

    removeInfoMessage() {
        this.infoMessageDiv.classList.add("transparent");
        //this.infoMessageDiv.innerText = "";
    }

    addTileToBoard(i, j, l) {
        this.model.add(i, j, l);
    }

    removeTileFromBoard(i, j) {
        this.model.remove(i, j);
    }

    removeAllTilesFromBoard() {
        this.model.removeAllTilesFromBoard();
    }

    play() {
        let error = this.isValid();
        if(!error) {
            let cnt = this.getPoints();
            this.setError("");
            this.masterPlay(cnt, this.bestPlay);
            return cnt;
        } else {
            this.setError(error);
        }

        //if(error)
        //    console.log(error);
        return -1;
    }

    displayCurrentPlayScore(msg) {
        document.getElementById("currentPlayScore").innerHTML = msg;
    }

    removeLatestPlayedTiles() {
       let tiles = document.querySelectorAll("#js-board .tile .playable-tile.latestPlay");
       for(const tile of tiles)
           tile.classList.remove("latestPlay");
    }

    masterPlay(myPlay, masterPlay) {
        this.model.addPlay(myPlay, masterPlay);

        resetRackAndBoard();
        this.removeAllTilesFromBoard();
        let word = masterPlay.word;
        let place = masterPlay.place;
        let j = 0;

        this.removeLatestPlayedTiles();
        this.latestPlay = [...place];
        for(let i = 0; i < place.length; i++) {
            let letter = word.charAt(j);
            if(letter == " ") {
                j++;
                letter += word.charAt(j);
            }
            this.model.board[place[i].row][place[i].column] = letter.toLowerCase();

            j++;
        }

        this.removeLettersFromRack(word);
        //this.refreshScorePanel();
        if(this.drawTiles() == true)
            this.searchForBestPlay();

    }

    removeLettersFromRack(word) {
        for(const char of word) {
            for(let i = 0; i < this.model.rack.length; i++) {
                if(this.model.rack[i] == char) {
                    this.model.rack.splice(i, 1);
                    i--;
                    break;
                }
            }
        }
    }

    setError(error) {
        document.getElementById("error").innerHTML = error;
    }

    isValid() {
        this.invalidWord = null;
        let rack = [...this.model.rack];
        let play = [...this.model.getPlayedLetters()];
        for(const letter of play) {
            const index = rack.indexOf(letter.charAt(0));
            if(index == -1)
                return "Vous ne disposez des lettres nécessaires pour effectuer ce coup";
            rack.splice(index, 1);
        }


        let rows = this.model.getPlayedRows();
        let columns = this.model.getPlayedColumns();
        if(rows.length == 0 && columns.length == 0) {
            return "Veuillez former un mot avec vos tuiles"
        }

        if(rows.length == 1 && columns.length == 1 && this.model.isEmptyBoard())
            return "Deux tuiles minimum doivent être posées lors du premier coup";
        let ok = false;
        let firstPlay = this.model.isEmptyBoard();

        if(rows.length == 1 && columns.length >= 1) {
            if(firstPlay) {
                //first play
                if(rows[0] != this.model.center[0])
                    return "Le premier mot doit obligatoirement être posé au centre du plateau, sur l’étoile";
                if(!columns.includes(this.model.center[1]))
                    return "Le premier mot doit obligatoirement être posé au centre du plateau, sur l’étoile";

            }

            for(let i = 0; i < columns.length; i++)
                if(this.model.board[rows[0]][columns[i]] != null)
                    return "Vos tuiles sont posées sur une case non-libre"; //the tile is not free

            ok = true;
        } else {
            if(rows.length >= 1 && columns.length == 1) {
                if(firstPlay) {
                    //first play
                    if(columns[0] != this.model.center[1])
                        return "Le premier mot doit obligatoirement être posé au centre du plateau, sur l’étoile";
                    if(!rows.includes(this.model.center[0]))
                        return "Le premier mot doit obligatoirement être posé au centre du plateau, sur l’étoile";
                }

                for(let i = 0; i < rows.length; i++)
                    if(this.model.board[rows[i]][columns[0]] != null)
                        return "Vos tuiles sont posées sur une case non-libre"; //the tile is not free
                ok = true;
            }
        }

        if(ok) {
            //conditions respected at this point:
            //1) only one row or column filled
            //2) center filled if first play
            //3) the played tiles are free

            if(!firstPlay) {
                if(!this.isAttached()) {
                    return "Au moins une tuile doit être attachée à un mot existant";
                }
            }

            if(this.getGaps().length > 0)
                return "Il existe des cases libres entre vos tuiles posées"; //some gaps between the played tiles
            else {
                //no gaps
                let words = this.getCreatedWords();
                for(let i = 0; i < words.length; i++) {
                    if(!this.isValidWord(words[i].word)) {
                        this.invalidWord = words[i];
                        return "Le mot '" + words[i].word.toUpperCase() + "' n'existe pas";
                    }
                }

                return null;

            }
        }


        return "Les mots doivent toujours être écrits de gauche à droite ou de haut en bas";


    }

    isValidWord(word) {
        word = word.replaceAll(' ', '');
        return dictionary.has(word.toUpperCase());
    }

    isAttached() {
        let playedColumns = this.model.getPlayedColumns();
        let playedRows = this.model.getPlayedRows();

        if (playedRows.length == 1 && playedColumns.length >= 1) {
            //horizontal word
            let row_num = playedRows[0];
            for (let i = 0; i < playedColumns.length; i++) {
                let col_num = playedColumns[i];

                if (col_num >= 1) {
                    //check left
                    if (this.model.board[row_num][col_num - 1] != null)
                        return true;
                }

                if (col_num < this.model.column_length - 1) {
                    //check right
                    if (this.model.board[row_num][col_num + 1] != null)
                        return true;
                }

                if (row_num >= 1) {
                    //check top
                    if (this.model.board[row_num - 1][col_num] != null)
                        return true;
                }

                if (row_num < this.model.row_length - 1) {
                    //check bottom
                    if (this.model.board[row_num + 1][col_num] != null)
                        return true;
                }


            }
        } else {
            //vertical word
            let col_num = playedColumns[0];
            for (let i = 0; i < playedRows.length; i++) {
                let row_num = playedRows[i];

                if (row_num >= 1) {
                    //check top
                    if (this.model.board[row_num - 1][col_num] != null)
                        return true;
                }

                if (row_num < this.model.row_length - 1) {
                    //check bottom
                    if (this.model.board[row_num + 1][col_num] != null)
                        return true;
                }

                if (col_num >= 1) {
                    //check left
                    if (this.model.board[row_num][col_num - 1] != null)
                        return true;
                }

                if (col_num < this.model.column_length - 1) {
                    //check right
                    if (this.model.board[row_num][col_num + 1] != null)
                        return true;
                }
            }
        }
        return false;
    }


    getCreatedWords() {
        let words = [];
        let playedColumns = this.model.getPlayedColumns();
        let playedRows = this.model.getPlayedRows();

        if(playedRows.length == 1) {
            //row
            let start = playedColumns[0];
            let end = playedColumns[playedColumns.length - 1];
            let word = "";
            let letters = [];
            for(let i = start; i <= end; i++) {
                if(playedColumns.includes(i)) {
                    word += this.getActualLetter(this.model.play[playedRows[0]][i]);
                    letters.push(
                        {'letter': this.getActualTile(this.model.play[playedRows[0]][i]),
                        'coordinates' : [playedRows[0],i],
                        'rack' : 'true'}
                        )
                }
                if(this.model.board[playedRows[0]][i] != null) {
                    word += this.getActualLetter(this.model.board[playedRows[0]][i]);
                    letters.push(
                        {'letter': this.getActualTile(this.model.board[playedRows[0]][i]),
                            'coordinates' : [playedRows[0],i],
                            'rack' : 'false'}
                            );
                }
            }

            for(let i = start - 1; i >= 0; i--) {
                //left
                if(this.model.board[playedRows[0]][i] != null) {
                    word = this.getActualLetter(this.model.board[playedRows[0]][i]) + word;
                    letters.unshift(
                        {'letter': this.getActualTile(this.model.board[playedRows[0]][i]),
                            'coordinates' : [playedRows[0],i],
                            'rack' : 'false'});
                }
                else
                    break;
            }

            for(let i = end + 1; i < this.model.column_length; i++) {
                //right
                if(this.model.board[playedRows[0]][i] != null) {
                    word = word + this.getActualLetter(this.model.board[playedRows[0]][i]);
                    letters.push(
                        {'letter': this.getActualTile(this.model.board[playedRows[0]][i]),
                            'coordinates' : [playedRows[0],i],
                            'rack' : 'false'}
                    );
                }
                else
                    break;
            }

            if(word.length > 1)
                words.push({'word' : word, 'letters' : letters});

            //vertical words
            for(let i = 0; i < playedColumns.length; i++) {
                let word = this.getActualLetter(this.model.play[playedRows[0]][playedColumns[i]]);
                let letters = [];
                letters.push(
                    {'letter': this.getActualTile(this.model.play[playedRows[0]][playedColumns[i]]),
                        'coordinates' : [playedRows[0],playedColumns[i]],
                        'rack' : 'true'}
                );
                for(let j = playedRows[0] - 1; j > 0; j--) {
                    if(this.model.board[j][playedColumns[i]] != null) {
                        word = this.getActualLetter(this.model.board[j][playedColumns[i]]) + word;
                        letters.unshift(
                            {'letter': this.getActualTile(this.model.board[j][playedColumns[i]]),
                                'coordinates' : [j,playedColumns[i]],
                                'rack' : 'false'});
                    }
                    else
                        break;
                }

                for(let j = playedRows[0] + 1; j < this.model.row_length; j++) {
                    if(this.model.board[j][playedColumns[i]] != null) {
                        word += this.getActualLetter(this.model.board[j][playedColumns[i]]);
                        letters.push(
                            {'letter': this.getActualTile(this.model.board[j][playedColumns[i]]),
                                'coordinates' : [j,playedColumns[i]],
                                'rack' : 'false'}
                        );
                    }
                    else
                        break;
                }
                if(word.length > 1)
                    words.push({'word' : word, 'letters' : letters});
            }


        } else {
            //columns
            let start = playedRows[0];
            let end = playedRows[playedRows.length - 1];
            let word = "";
            let letters = [];
            for(let i = start; i <= end; i++) {
                if (playedRows.includes(i)) {
                    word += this.getActualLetter(this.model.play[i][playedColumns[0]]);
                    letters.push(
                        {'letter': this.getActualTile(this.model.play[i][playedColumns[0]]),
                            'coordinates' : [i,playedColumns[0]],
                            'rack' : 'true'}
                    );
                }
                if (this.model.board[i][playedColumns[0]] != null) {
                    word += this.getActualLetter(this.model.board[i][playedColumns[0]]);
                    letters.push(
                        {'letter': this.getActualTile(this.model.board[i][playedColumns[0]]),
                            'coordinates' : [i,playedColumns[0]],
                            'rack' : 'false'}
                    );
                }
            }

            for(let i = start - 1; i >= 0; i--) {
                //bottom
                if(this.model.board[i][playedColumns[0]] != null) {
                    word = this.getActualLetter(this.model.board[i][playedColumns[0]]) + word;
                    letters.unshift(
                        {'letter': this.getActualTile(this.model.board[i][playedColumns[0]]),
                            'coordinates' : [i,playedColumns[0]],
                            'rack' : 'false'});
                }
                else
                    break;
            }

            for(let i = end + 1; i < this.model.row_length; i++) {
                //top
                if(this.model.board[i][playedColumns[0]] != null) {
                    word = word + this.getActualLetter(this.model.board[i][playedColumns[0]]);
                    letters.push(
                        {'letter': this.getActualTile(this.model.board[i][playedColumns[0]]),
                            'coordinates' : [i,playedColumns[0]],
                            'rack' : 'false'});
                }
                else
                    break;
            }

            if(word.length > 1)
                words.push({'word' : word, 'letters' : letters});

            //horizontal words
            for(let i = 0; i < playedRows.length; i++) {
                let word = this.getActualLetter(this.model.play[playedRows[i]][playedColumns[0]]);
                let letters = [];
                letters.push(
                    {'letter': this.getActualTile(this.model.play[playedRows[i]][playedColumns[0]]),
                        'coordinates' : [playedRows[i],playedColumns[0]],
                        'rack' : 'true'}
                );
                for(let j = playedColumns[0] - 1; j > 0; j--) {
                    if(this.model.board[playedRows[i]][j] != null) {
                        word = this.getActualLetter(this.model.board[playedRows[i]][j]) + word;
                        letters.unshift(
                            {'letter': this.getActualTile(this.model.board[playedRows[i]][j]),
                                'coordinates' : [playedRows[i],j],
                                'rack' : 'false'});
                    }
                    else
                        break;
                }

                for(let j = playedColumns[0] + 1; j < this.model.row_length; j++) {
                    if(this.model.board[playedRows[i]][j] != null) {
                        word += this.getActualLetter(this.model.board[playedRows[i]][j]);
                        letters.push(
                            {'letter': this.getActualTile(this.model.board[playedRows[i]][j]),
                                'coordinates' : [playedRows[i],j],
                                'rack' : 'false'});
                    }
                    else
                        break;
                }
                if(word.length > 1)
                    words.push({'word' : word, 'letters' : letters});
            }
        }

        return words;
    }

    getActualLetter(letter) {
        if(letter.length == 1)
            return letter;

        return letter.charAt(1);
    }

    getActualTile(letter) {
        if(letter.length == 1)
            return letter;

        return letter.charAt(0);
    }



    getGaps() {
        let res = [];

        let playedColumns = this.model.getPlayedColumns();
        let playedRows = this.model.getPlayedRows();

        if(playedRows.length == 1) {
            //row
            let start = playedColumns[0];
            let end = playedColumns[playedColumns.length - 1];
            for(let i = start + 1; i < end; i++) {
                if(!playedColumns.includes(i) && this.model.board[playedRows[0]][i] == null)
                    res.push([playedRows[0], i])
            }

        } else {
            //columns
            let start = playedRows[0];
            let end = playedRows[playedRows.length - 1];
            for(let i = start + 1; i < end; i++)
                if(!playedRows.includes(i) && this.model.board[i][playedColumns[0]] == null)
                    res.push([i, playedColumns[0]])
        }

        return res;
    }

    drawTiles() {
        if(this.isGameOver()) {
            this.refreshView();
            this.displayInfoMessage("La partie est terminée!");
            this.finalizeGame();
            return false;
        } else {

            if(isSharedGame() == true) {

                //reset the rack
                for(let i = 0; i < this.model.rack.length; i++) {
                    this.model.tiles.push(this.model.rack[i]);
                    this.model.rack.splice(i, 1);
                    i--;
                }

                let arr = sharedGame.drawnTiles[this.model.numberOfPlays];
                for(const tile of arr) {
                    this.model.rack.push(tile);
                    this.model.tiles.splice(this.model.tiles.indexOf(tile), 1)
                }



            } else {
                for (let i = this.model.rack.length; i < this.model.nb_of_rack_riles && this.model.tiles.length; i++) {
                    let index = Math.floor(Math.random() * this.model.tiles.length);
                    this.model.rack.push(this.model.tiles[index]);
                    this.model.tiles.splice(index, 1);
                }
            }
            this.refreshView();
            return true;
        }
    }

    finalizeGame() {
        let btn = document.getElementById("resetRackBtn");
        btn.style.display = "none";

        btn = document.getElementById("playBtn");
        btn.style.display = "none";

        btn = document.getElementById("shareBtn");
        btn.style.display = "initial";

    }

    shareGame() {
        let name = null;
        while(name == null || name.length == 0) {
            name = prompt("Pour partager cette partie, veuillez introduire votre nom", "Votre nom");
            if(name)
                name = name.trim();
        }

        let game = new SharedGame(name, this.model.myPlays, this.model.myRacks);
        let sharedUrl = getSharedURL(game);
        this.displayInfoMessage("<a href='" + sharedUrl + "' target='_blank'>Lien de partage</a>");
        alert("Le lien de partage a été généré et se trouve dans votre boite de dialogue. Vous pouvez dorénavant l'envoyer à vos amis !");
    }

    isGameOver() {
        if(this.redrawInARow >= MAX_REDRAW) {
            return true;
        }


        if(this.model.rack.length + this.model.tiles.length <= 1) {
            return true;
        }

        let arr = [...this.model.rack].concat([...this.model.tiles]);
        if(this.hasOnlyConsonants(arr) || this.hasOnlyVowels(arr)) {
            return true;
        }
        return false;
    }


    hasOnlyConsonants(letters) {
        let vowels = ["A", "E", "I", "O", "U", "Y", " "];
        for(const letter of letters)
            if(vowels.includes(letter.toUpperCase()))
                return false;
        return true;
    }

    hasOnlyVowels(letters) {
        let vowels = ["A", "E", "I", "O", "U"];
        for(const letter of letters)
            if(vowels.includes(letter.toUpperCase()) == false)
                return false;
        return true;
    }

    refreshView() {
        //refreshing rack
        let rack = document.querySelector(".rack");
        for(let i = 0; i < this.model.rack.length; i++) {
            let tile = rack.querySelector(".draggable[position='" + i + "'] .playable-tile");
            tile.setAttribute("data-letter", this.model.rack[i].toLowerCase());
        }

        for(let i = this.model.rack.length; i < this.model.nb_of_rack_riles; i++) {
            let tile = rack.querySelector(".draggable[position='" + i + "'] .playable-tile");
            tile.classList.add("transparent");
        }

        //refreshing board
        let board = document.getElementById("js-board");
        for(let i = 0; i < this.model.row_length; i++)
            for(let j = 0; j < this.model.column_length; j++) {
                if(this.model.board[i][j] != null) {
                    let tile = board.querySelector(".tile[data-row='" + i + "'][data-col='" + j + "'] .playable-tile");
                    tile.setAttribute("data-letter", this.model.board[i][j])
                    tile.removeAttribute("position");
                    tile.parentNode.classList.remove("invisible");
                    tile.parentNode.parentNode.setAttribute("free", "false");
                }
            }

        if(this.latestPlay != null) {
            for(const place of this.latestPlay) {
                document.querySelector("#js-board .tile[data-row='" + place.row + "'][data-col='" + place.column + "'] .playable-tile").classList.add("latestPlay");
            }
        }

        this.refreshScorePanel();
    }

    getPoints() {
        let words = this.getCreatedWords();
        let total = 0;
        for(const word of words) {
            let count = 0;
            let factor = 1;
            for(const letter of word.letters) {
                let points = this.model.getLetterPoint(letter['letter']);
                if(letter['rack'] == 'true') {
                    let bonus = this.model.getBonus(letter['coordinates']);
                    if(bonus != null) {
                        switch (bonus) {
                            case MT:
                                factor = factor * 3;
                                break;
                            case MD:
                                factor = factor * 2;
                                break;
                            case LD:
                                points = points * 2;
                                break;
                            case LT:
                                points = points * 3;
                                break;
                        }
                    }

                }
                count += points;
            }
            total += (count * factor);
        }

        let nbOfLetters = 0;
        for(const row of this.model.play)
            for(const cell of row)
                if(cell != null)
                    nbOfLetters++;
        if(nbOfLetters == SCRABBLE_LENGTH)
            total += SCRABBLE_PRIME;
        return total;
    }



}




