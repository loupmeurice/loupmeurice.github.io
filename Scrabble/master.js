class Master {
    constructor(model, controller) {
        this.model = model;
        this.controller = controller;
    }

    isWordExtension(place) {
        if(place.length == 1)
            return true;

        let first =  place[0];
        let last = place[place.length - 1];

        if(first.row == last.row) {
            //horizontal word
            if(first.column > 0 && this.model.board[first.row][first.column - 1] != null) //check left
                return true;
            if(last.column < this.model.column_length - 1 && this.model.board[last.row][last.column + 1] != null) //check right
                return true;

            if((last.column - first.column) + 1 > place.length) //letter in between
                return true;

        } else {
            //vertical word
            if(first.row > 0 && this.model.board[first.row - 1][first.column] != null) //check top
                return true;
            if(last.row < this.model.row_length - 1 && this.model.board[last.row + 1][last.column] != null) //check bottom
                return true;

            if((last.row - first.row) + 1 > place.length) //letter in between
                return true;
        }

        return false;

    }



    getBestPlay() {
        let places = this.getAvailablePlaces(this.model.rack.length);
        let map = new Map();
        for(const place of places) {
            let size = place.length;
            let arr = map.get(size);
            if(arr == null) {
                arr = new Set();
                map.set(size, arr);
            }
            arr.add(JSON.stringify(place));
        }

        let map2 = new Map();
        for(const key of map.keys()) {
            let arr = []
            for(const place of map.get(key)) {
                let w = JSON.parse(place);
                w.ext = this.isWordExtension(w);
                arr.push(w);
            }
            map2.set(key, arr);
        }
        map = map2;

        let unexistingPossibleWords = new Map();
        let existingPossibleWords = new Map();
        let count = 0;
        for(const key of map.keys()) {
            let words = this.getAllPossibleWords(key, "", this.model.rack);
            count += words.size;
            let unexarr = [];
            let exarr = [];
            for(const word of words) {
                let exists = this.controller.isValidWordWithOrWithoutEscapingWhiteSpaces(word, false);
				if(exists == true || exists == false) {
					//no white letters
					exarr.push({word: word, exists: exists});
				} else {
					//white letters
					if(exists.existingWords.length > 0) {
						//there is, at least, one existing word
						exarr.push({word: word, exists: exists.existingWords});
					}
					
					if(exists.unexistingWords.length > 0) {
						//there is, at least, one unexisting word
						unexarr.push({word: word, exists: exists.unexistingWords});
					}
				}
            }
            existingPossibleWords.set(key, exarr);
            unexistingPossibleWords.set(key, unexarr);
        }


        let max = 0;
        let maxPlay = null;
        let check = 0;
		let log = false;
        for(const key of map.keys()) {
            for(const place of map.get(key)) {
				log = (place.length == 4 && place[0].row == 8 && place[0].column == 4 && place[1].column == 4);
				
                let array = [];

                let existingWords = existingPossibleWords.get(key);
				
                for(const w of existingWords) {

                    if(w.exists.length) {
                        //words with white letter(s)
                        for(const w2 of w.exists) {
                            array.push({word: w2, exists: true});
                        }
                    } else
                        array.push(w); //words without white letters

                }

                if(place.ext == true) {
                    let unexistingWords = unexistingPossibleWords.get(key);
					
                    for(const w of unexistingWords) {
                        if (w.exists.length) {
                            //words with white letter(s)
                            for (const w2 of w.exists) {
                                array.push({word: w2, exists: false});
                            }
                        } else
                            array.push(w);
                    }
                }
				

                for (const word of array) {

                    this.model.removeAllTilesFromBoard();

                    check++;
                    let i = 0;
                    for(const tile of place) {
                        let letter = word.word[i];
                        if(letter == " ") {
                            i++;
                            letter = " " + word.word[i];
                        }

                        this.model.play[tile.row][tile.column] = letter;
                        i++;

                    }

                    let points = -1;
                    let error = controller.isValid();
                    if(!error)
                        points = controller.getPoints();
                    if(points > max) {
                        max = points;
                        maxPlay = {place: place, word: word.word, points: points};
                    }
                }
            }
        }

        this.model.removeAllTilesFromBoard();

        return maxPlay;

    }

    getAllPossibleWords(length, word, remainingLetters) {
        if(word.length == length)
            return [word];
        if(remainingLetters.length == 0)
            return [];
        let res = new Set();
        for(let i = 0; i < remainingLetters.length; i++) {
            let letter = remainingLetters[i];
            let remaining = [...remainingLetters];
            remaining.splice(i, 1);

            let word2 = word + letter;
            for(const w of this.getAllPossibleWords(length, word2, remaining))
                res.add(w);
        }

        return res;

    }

    getAvailablePlaces(max_nb_tiles) {
        let res = [];
        if(max_nb_tiles <= 0)
            return new Set(res);

        if(this.model.isEmptyBoard()) {
            let i = this.model.center[0];
            let j = this.model.center[1];
            res.push([{row: i, column: j}]);
            let left = this.getPlaces(max_nb_tiles, "L", [{row: i, column: j}], {
                row: i,
                column: j
            }, {row: i, column: j}, true);
            let right = this.getPlaces(max_nb_tiles, "R", [{row: i, column: j}], {
                row: i,
                column: j
            }, {row: i, column: j}, true);
            let top = this.getPlaces(max_nb_tiles, "T", [{row: i, column: j}], {
                row: i,
                column: j
            }, {row: i, column: j}, true);
            let bottom = this.getPlaces(max_nb_tiles, "B", [{row: i, column: j}], {
                row: i,
                column: j
            }, {row: i, column: j}, true);

            res = res.concat(left.concat(right).concat(top).concat(bottom));
        } else {
            for (let i = 0; i < this.model.row_length; i++) {
                for (let j = 0; j < this.model.column_length; j++) {
                    if (this.model.board[i][j] == null) {
                        let attached = false;

                        if (i > 0 && this.model.board[i - 1][j] != null)
                            attached = true; // left neighbour

                        if (i < this.model.row_length - 1 && this.model.board[i + 1][j] != null)
                            attached = true; // right neighbour

                        if (j > 0 && this.model.board[i][j - 1] != null)
                            attached = true; // top neighbour

                        if (j < this.model.column_length - 1 && this.model.board[i][j + 1] != null)
                            attached = true; // bottom neighbour

                        if (attached) {
                            res.push([{row: i, column: j}]);
                            let left = this.getPlaces(max_nb_tiles, "L", [{row: i, column: j}], {
                                row: i,
                                column: j
                            }, {row: i, column: j}, true);
                            let right = this.getPlaces(max_nb_tiles, "R", [{row: i, column: j}], {
                                row: i,
                                column: j
                            }, {row: i, column: j}, true);
                            let top = this.getPlaces(max_nb_tiles, "T", [{row: i, column: j}], {
                                row: i,
                                column: j
                            }, {row: i, column: j}, true);
                            let bottom = this.getPlaces(max_nb_tiles, "B", [{row: i, column: j}], {
                                row: i,
                                column: j
                            }, {row: i, column: j}, true);

                            res = res.concat(left.concat(right).concat(top).concat(bottom));
                        }


                    }
                }
            }
        }

        let res2 = [];
        for(const place of res) {
            if(this.isPossiblePlace(place) == true) {
                res2.push(place);
            }
        }

        return new Set(res2);
    }


    isPossiblePlace(place) {


        for(const tile of place) {
            let word;

            if(place.length == 1 || place[0].row == place[place.length - 1].row)
                word = this.getVerticalWord(tile);
            else
                word = this.getHorizontalWord(tile);

            if(word != null) {
                let possible = false;
                for(const rackTile of this.model.rack) {
                    let word2 = word.replace(" ", rackTile);
                    let valid = this.controller.isValidWordWithOrWithoutEscapingWhiteSpaces(word2, false);
                    if(valid == true || (valid.existingWords && valid.existingWords.length > 0)) {
                        possible = true;
                        break;
                    }
                }
                if(possible == false) {
                    return false;
                }
            }
        }


        return true;
    }

    getHorizontalWord(tile) {
        let res = " ";
        for(let i = tile.column - 1; i >= 0; i--) {
            let letter = this.model.board[tile.row][i];
            if(letter != null)
                res = letter.trim() + res;
            else
                break;
        }

        for(let i = tile.column + 1; i < this.model.column_length; i++) {
            let letter = this.model.board[tile.row][i];
            if(letter != null)
                res += letter.trim();
            else
                break;
        }

        if(res == " ")
            return null;
        return res;
    }

    getVerticalWord(tile) {
        let res = " ";
        for(let i = tile.row - 1; i >= 0; i--) {
            let letter = this.model.board[i][tile.column];
            if(letter != null)
                res = letter.trim() + res;
            else
                break;
        }

        for(let i = tile.row + 1; i < this.model.row_length; i++) {
            let letter = this.model.board[i][tile.column];
            if(letter != null)
                res += letter.trim();
            else
                break;
        }

        if(res == " ")
            return null;
        return res;
    }


    getFreeTiles(word) {
        let res = [];
        for(const w of word)
            if(w.free == true)
                res.push(w);
        return res;
    }

    getPlaces(max_nb_tiles, direction, currentWord, firstTile, lastTile, firstRound) {
        let res = [];
        if(currentWord.length == max_nb_tiles)
            return res;

        let first_row = firstTile.row;
        let first_column = firstTile.column;
        let last_row = lastTile.row;
        let last_column = lastTile.column;
        let neighbour1, neighbour2 = null;

        switch (direction) {
            case "L":
                if(first_column > 0)
                    neighbour1 = {row: first_row, column: first_column -1};
                break;
            case "R":
                if(last_column < this.model.column_length - 1)
                    neighbour2 = {row: last_row, column: last_column + 1};
                if(!firstRound && first_column > 0)
                    neighbour1 = {row: first_row, column: first_column -1};
                break;
            case "T":
                if(first_row > 0)
                    neighbour1 = {row: first_row - 1, column: first_column};
                break;
            case "B":
                if(last_row < this.model.row_length - 1)
                    neighbour2 = {row: last_row + 1, column: last_column};
                if(!firstRound && first_row > 0)
                    neighbour1 = {row: first_row - 1, column: first_column};
                break;
            default:
                break;
        }

        if(neighbour1 != null) {
            let copy = [...currentWord];
            if(this.model.board[neighbour1.row][neighbour1.column] == null) {
                copy.unshift(neighbour1);
                res.push(copy);
            }
            let res2 = this.getPlaces(max_nb_tiles, direction, copy, neighbour1, lastTile, false);
            res = res.concat(res2);
        }

        if(neighbour2 != null) {
            let copy = [...currentWord];
            if(this.model.board[neighbour2.row][neighbour2.column] == null) {
                copy.push(neighbour2);
                res.push(copy);
            }
            let res2 = this.getPlaces(max_nb_tiles, direction, copy, firstTile, neighbour2, false);
            res = res.concat(res2);
        }

        return res;

    }

    getPlaces2(max_nb_tiles, direction, currentWord, lastVisitedTile) {
        let row = lastVisitedTile.row;
        let column = lastVisitedTile.column;
        let res = [];
        if(currentWord.length == max_nb_tiles)
            return res;

        let recursive = true;
        let neighbour = null;
        switch (direction) {
            case "L":
                if(column > 0)
                   neighbour = {row: row, column: column -1};
                else
                    recursive = false;
                break;
            case "R":
                if(column < this.model.column_length - 1)
                    neighbour = {row: row, column: column + 1};
                else
                    recursive = false;
                break;
            case "T":
                if(row > 0)
                    neighbour = {row: row - 1, column: column};
                else
                    recursive = false;
                break;
            case "B":
                if(row < this.model.row_length - 1)
                    neighbour = {row: row + 1, column: column};
                else
                    recursive = false;
                break;
            default:
                recursive = false;
                break;
        }

        if(neighbour != null) {
            let copy = [...currentWord];

            if(this.model.board[neighbour.row][neighbour.column] == null) {
                copy.push(neighbour);
                res.push(copy);
            }

            if(recursive == true) {
                let res2 = this.getPlaces(max_nb_tiles, direction, copy, neighbour);
                res = res.concat(res2);
            }
        }

        return res;

    }

}
